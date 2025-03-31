package com.wilzwert.myjobs.infrastructure.security.jwt;

import com.wilzwert.myjobs.infrastructure.security.model.JwtToken;
import com.wilzwert.myjobs.infrastructure.security.service.CookieService;
import com.wilzwert.myjobs.infrastructure.security.service.JwtService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

/**
 * @author Wilhelm Zwertvaegher
 * Date:14/03/2025
 * Time:13:46
 */
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    private final UserDetailsService userDetailsService;

    private final CookieService cookieService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService, CookieService cookieService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.cookieService = cookieService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            Optional<JwtToken> token = jwtService.extractTokenFromRequest(request);
            if(token.isPresent()) {
                // extract JWT Token with embedded email and try to authenticate the user
                JwtToken jwtToken = token.get();
                String email = jwtToken.getClaims().getSubject();
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (email != null && authentication == null) {
                    UserDetails userDetails = this.userDetailsService.loadUserByUsername(email);
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    // pass authentication to the security context
                    log.info("Token handled, set security context authentication");
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            // security filter chain continues
            filterChain.doFilter(request, response);
        }
        catch (UsernameNotFoundException | JwtException e) {
            log.warn("JWT authentication filter : token or user exception {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().print("auth_error");
            response.addHeader(HttpHeaders.SET_COOKIE, cookieService.revokeAccessTokenCookie().toString());
            response.addHeader(HttpHeaders.SET_COOKIE, cookieService.revokeRefreshTokenCookie().toString());
        }
    }

    @Override
    public boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        // bypass filter if path should remain publicly accessible
        String path = request.getRequestURI();
        return path.matches("/api/auth/(login|register|refresh-token)")
                // || path.matches(apiDocProperties.getApiDocsPath()+"/?.*")
                // || path.matches(apiDocProperties.getSwaggerPath()+"/?.*")
                || path.matches("/swagger-ui/.*");
    }
}
