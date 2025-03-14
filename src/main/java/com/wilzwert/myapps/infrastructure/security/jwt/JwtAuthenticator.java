package com.wilzwert.myapps.infrastructure.security.jwt;


import com.wilzwert.myapps.domain.model.AuthenticatedUser;
import com.wilzwert.myapps.domain.model.User;
import com.wilzwert.myapps.domain.ports.driven.Authenticator;
import com.wilzwert.myapps.infrastructure.security.model.RefreshToken;
import com.wilzwert.myapps.infrastructure.security.service.JwtService;
import com.wilzwert.myapps.infrastructure.security.service.RefreshTokenService;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

/**
 * @author Wilhelm Zwertvaegher
 * Date:13/03/2025
 * Time:20:18
 */

@Component
public class JwtAuthenticator implements Authenticator {

    private final JwtService jwtService;

    private final RefreshTokenService refreshTokenService;

    public JwtAuthenticator(final JwtService jwtService, final RefreshTokenService refreshTokenService) {
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    public AuthenticatedUser authenticate(User user) throws AuthenticationException {
        String token = jwtService.generateToken(user.getEmail());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
        return new JwtAuthenticatedUser(user.getEmail(), user.getUsername(), token, refreshToken.getToken(), user.getRole());
    }
}