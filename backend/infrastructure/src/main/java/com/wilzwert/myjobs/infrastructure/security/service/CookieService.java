package com.wilzwert.myjobs.infrastructure.security.service;


import com.wilzwert.myjobs.infrastructure.security.configuration.CookieProperties;
import com.wilzwert.myjobs.infrastructure.security.configuration.JwtProperties;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

/**
 * @author Wilhelm Zwertvaegher
 */
@Service
public class CookieService {
    private final CookieProperties cookieProperties;

    private final JwtProperties jwtProperties;

    public CookieService(CookieProperties cookieProperties, JwtProperties jwtProperties) {
        this.cookieProperties = cookieProperties;
        this.jwtProperties = jwtProperties;
    }

    public ResponseCookie createAccessTokenCookie(String accessToken) {
        return createCookie("access_token", accessToken, jwtProperties.getExpirationTime());
    }

    public ResponseCookie createRefreshTokenCookie(String refreshToken) {
        return createCookie("refresh_token", refreshToken, jwtProperties.getRefreshExpirationTime());
    }

    public ResponseCookie revokeAccessTokenCookie() {
        return createCookie("access_token", "", 0);
    }

    public ResponseCookie revokeRefreshTokenCookie() {
        return createCookie("refresh_token", "", 0);
    }

    private ResponseCookie createCookie(String name, String value, long maxAge) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(cookieProperties.isSecure())
                .sameSite(cookieProperties.getSameSite())
                .domain(cookieProperties.getDomain())
                .path(cookieProperties.getPath())
                .maxAge(maxAge)
                .build();
    }
}
