package com.wilzwert.myjobs.infrastructure.security.service;


import com.wilzwert.myjobs.infrastructure.security.configuration.JwtProperties;
import com.wilzwert.myjobs.infrastructure.security.model.JwtToken;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;


/**
 * Provides JWT token generation and validation
 * @author Wilhelm Zwertvaegher
 */


@Service
@Slf4j
public class JwtService {
    private final JwtProperties jwtProperties;

    public JwtService(final JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    public Optional<String> getToken(HttpServletRequest request) {
        String token;
        Cookie[] cookies = request.getCookies();

        if(cookies != null) {
            Cookie jwtCookie = Arrays.stream(cookies)
                    .filter(cookie -> "access_token".equals(cookie.getName()))
                    .findFirst()
                    .orElse(null);
            token = jwtCookie != null ? jwtCookie.getValue() : null;
            if(token != null && !token.isEmpty()) {
                return Optional.of(token);
            }
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            token = request.getParameter("token");
            if(token == null || token.isEmpty()) {
                // log.info("Authorization header not found or not compatible with Bearer token");
                return Optional.empty();
            }
        }
        else {
            token = authHeader.substring(7);
        }

        return Optional.of(token);
    }

    /**
     * Extracts a JWT token from the current Request
     * @param request the current request
     * @return the extracted jwt token
     * @throws ExpiredJwtException when JWT token is expired
     * @throws MalformedJwtException when JWT token is malformed
     * @throws IllegalArgumentException when JWT token is illegal
     * @throws UnsupportedJwtException when JWT token is unsupported
     * @throws SignatureException when JWT token's signature is invalid
     */
    public Optional<JwtToken> extractTokenFromRequest(HttpServletRequest request) throws ExpiredJwtException, MalformedJwtException, IllegalArgumentException, UnsupportedJwtException, SignatureException {
        return getToken(request).flatMap(this::parseToken);
    }

    public Optional<JwtToken> parseToken(String token) {
        try {
            Jwt<?, ?> parsedToken = Jwts
                    .parser().verifyWith(getSignInKey()).build().parse(token);
            Claims claims = (Claims) parsedToken.getPayload();
            JwtToken jwtToken = new JwtToken(claims.getSubject(), claims);
            return Optional.of(jwtToken);
        }
        // we only catch different JwtException types to log warning messages
        // the exceptions are then thrown again to be handled by the authentication filter
        catch (ExpiredJwtException e) {
            log.warn("Expired JWT token: {}", e.getMessage());
            throw e;
        }
        catch (MalformedJwtException e) {
            log.warn("Malformed JWT token: {}", e.getMessage());
            throw e;
        }
        catch (SignatureException e) {
            log.warn("Invalid JWT signature: {}", e.getMessage());
            throw e;
        }
        catch (UnsupportedJwtException e) {
            log.warn("Unsupported JWT token: {}", e.getMessage());
            throw e;
        }
        catch (IllegalArgumentException e) {
            log.warn("Empty JWT claims: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Generates a token with a subject
     * @param subject the subject we want to generate the token for (email, username...)
     * @return the JWT Token
     */
    public String generateToken(String subject) {
        return Jwts
                .builder()
                .subject(subject)
                // .claim("authType", authType)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtProperties.getExpirationTime()*1000))
                .signWith(getSignInKey())
                .compact();
    }

    /**
     *
     * @return the signin key
     */
    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecretKey());
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

