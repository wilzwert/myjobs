package com.wilzwert.myjobs.infrastructure.security.service;

import com.wilzwert.myjobs.core.domain.model.user.UserId;
import com.wilzwert.myjobs.infrastructure.security.configuration.JwtProperties;
import com.wilzwert.myjobs.infrastructure.security.model.JwtToken;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import io.jsonwebtoken.security.WeakKeyException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Key;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

/**
 * @author Wilhelm Zwertvaegher
 */

@ExtendWith(MockitoExtension.class)
@Tag("Security")
public class JwtServiceTest {

    @Mock
    private JwtProperties jwtProperties;

    @InjectMocks
    private JwtService jwtService;

    @Mock
    private HttpServletRequest request;

    private static final String SECRET_KEY = "testSecretWithEnoughBytesToGenerateKeyWithoutThrowingWeakKeyException";

    @BeforeEach
    void setUp() {
        when(jwtProperties.getSecretKey()).thenReturn(SECRET_KEY);
    }

    @Test
    void shouldThrowWeakKeyException() {
        when(jwtProperties.getSecretKey()).thenReturn("weakKey");
        var uuidString = UUID.randomUUID().toString();
        assertThrows(WeakKeyException.class, () -> jwtService.generateToken(uuidString));
    }

    @Test
    void shouldGenerateToken() {
        String token = jwtService.generateToken(UUID.randomUUID().toString());

        assertThat(token).isNotBlank();
        long count = token.chars().filter(ch -> ch == '.').count();
        Assertions.assertThat(count).isEqualTo(2);
    }

    @Test
    void shouldExtractJwtToken() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        Key key = Keys.hmacShaKeyFor(keyBytes);
        long timeMs = System.currentTimeMillis();
        String validToken = Jwts.builder()
                .subject("test@example.com")
                .issuedAt(new Date(timeMs))
                .expiration(new Date(timeMs + 10000))
                .signWith(key)
                .compact();

        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);

        Optional<JwtToken> jwtToken = jwtService.extractTokenFromRequest(request);

        assertThat(jwtToken).isPresent();
        assertThat(jwtToken).map(JwtToken::getSubject).hasValue("test@example.com");
        assertThat(jwtToken).map(JwtToken::getClaims).map(Claims::getSubject).hasValue("test@example.com");
    }

    @Test
    void shouldExtractFromGeneratedToken() {
        when(jwtProperties.getExpirationTime()).thenReturn(600L);
        UserId userId = UserId.generate();
        String idValue = userId.value().toString();

        String token = jwtService.generateToken(idValue);

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        Optional<JwtToken> jwtToken = jwtService.extractTokenFromRequest(request);

        assertNotNull(token);
        assertThat(jwtToken).isPresent();
        assertThat(jwtToken).map(JwtToken::getSubject).hasValue(idValue);
        assertThat(jwtToken).map(JwtToken::getClaims).map(Claims::getSubject).hasValue(idValue);
    }

    @Test
    void shouldReturnEmptyOptionalWhenNoCookieAndNotAuthHeader() {
        reset(jwtProperties);
        when(request.getCookies()).thenReturn(new Cookie[0]);
        when(request.getHeader("Authorization")).thenReturn(null);
        assertThat(jwtService.extractTokenFromRequest(request)).isEmpty();
    }

    @Nested
    class GetTokenTest {
        @Test
        void shouldGetTokenFromCookie() {
            reset(jwtProperties);
            when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("access_token", "token_string")});
            Optional<String> token = jwtService.getToken(request);
            assertThat(token).isPresent().contains("token_string");
        }

        @Test
        void shouldGetTokenFromHeader() {
            reset(jwtProperties);
            when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("access_token", "")});
            when(request.getHeader("Authorization")).thenReturn("Bearer token_string");
            Optional<String> token = jwtService.getToken(request);
            assertThat(token).isPresent().contains("token_string");
        }

        @Test
        void shouldGetTokenFromParam() {
            reset(jwtProperties);
            when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("access_token", "")});
            when(request.getHeader("Authorization")).thenReturn("Bearer");
            when(request.getParameter("token")).thenReturn("token_string");
            Optional<String> token = jwtService.getToken(request);
            assertThat(token).isPresent().contains("token_string");
        }

        @Test
        void shouldGetNoTokenFromRequest() {
            reset(jwtProperties);
            when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("access_token", "")});
            when(request.getHeader("Authorization")).thenReturn("Bearer");
            when(request.getParameter("token")).thenReturn("");
            Optional<String> token = jwtService.getToken(request);
            assertThat(token).isEmpty();
        }
    }

    @Nested
    class ParseTokenTest {

        @Test
        void shouldThrowSignatureException() {
            byte[] keyBytes = Decoders.BASE64.decode("differentTestSecretWithEnoughBytesToGenerateKeyWithoutThrowingWeakKeyException");
            Key key = Keys.hmacShaKeyFor(keyBytes);
            String invalidSignatureToken = Jwts.builder()
                    .subject("testUser")
                    .issuedAt(new Date())
                    .expiration(new Date(System.currentTimeMillis() + 1000))
                    .signWith(key)
                    .compact();
            assertThrows(SignatureException.class, () -> jwtService.parseToken(invalidSignatureToken));
        }

        @Test
        void shouldThrowMalformedJwtException() {
            reset(jwtProperties);
            when(jwtProperties.getSecretKey()).thenReturn(SECRET_KEY);
            String malformedToken = "yJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJicGF3YW4";

            assertThrows(MalformedJwtException.class, () -> jwtService.parseToken(malformedToken));
        }

        @Test
        void shouldThrowExpiredJwtException() {
            reset(jwtProperties);
            when(jwtProperties.getSecretKey()).thenReturn(SECRET_KEY);
            byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
            Key key = Keys.hmacShaKeyFor(keyBytes);
            String expiredToken = Jwts.builder()
                    .subject("testUser")
                    .issuedAt(new Date(System.currentTimeMillis() - 10000000))
                    .expiration(new Date(System.currentTimeMillis() - 1000000))
                    .signWith(key)
                    .compact();

            assertThrows(ExpiredJwtException.class, () -> jwtService.parseToken(expiredToken));
        }

        @Test
        void shouldThrowIllegalArgumentExceptionWhenNull() {
            reset(jwtProperties);
            assertThrows(IllegalArgumentException.class, () -> jwtService.parseToken(null));
        }

        @Test
        void shouldThrowIllegalArgumentException() {
            reset(jwtProperties);
            assertThrows(IllegalArgumentException.class, () -> jwtService.parseToken(""));
        }

        @Test
        void shouldThrowUnsupportedJwtException() {
            String unsupportedToken = Jwts.builder()
                    .subject("testUser")
                    .compact();

            assertThrows(UnsupportedJwtException.class, () -> jwtService.parseToken(unsupportedToken));
        }

        @Test
        void shouldParseJwtToken() {
            byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
            Key key = Keys.hmacShaKeyFor(keyBytes);
            String validToken = Jwts.builder()
                    .subject("test@example.com")
                    .issuedAt(new Date())
                    .expiration(new Date(System.currentTimeMillis() + 1000))
                    .signWith(key)
                    .compact();

            Optional<JwtToken> jwtToken = jwtService.parseToken(validToken);

            assertThat(jwtToken).isPresent().map(JwtToken::getSubject).hasValue("test@example.com");
            assertThat(jwtToken).isPresent().map(JwtToken::getClaims).map(Claims::getSubject).hasValue("test@example.com");
        }

        @Test
        void shouldParseGeneratedToken() {
            when(jwtProperties.getExpirationTime()).thenReturn(600L);
            UserId userId = UserId.generate();
            String idValue = userId.toString();
            String token = jwtService.generateToken(idValue);

            Optional<JwtToken> jwtToken = jwtService.parseToken(token);

            assertNotNull(token);
            assertThat(jwtToken).isPresent().map(JwtToken::getSubject).hasValue(idValue);
            assertThat(jwtToken).isPresent().map(JwtToken::getClaims).map(Claims::getSubject).hasValue(idValue);
        }
    }
}
