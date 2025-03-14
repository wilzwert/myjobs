package com.wilzwert.myapps.infrastructure.security.service;

import com.wilzwert.myapps.domain.model.User;
import com.wilzwert.myapps.infrastructure.security.model.RefreshToken;

import java.util.Optional;

/**
 * This service handles Refresh Tokens creation, retrieval and verifying
 * @author Wilhelm Zwertvaegher
 * Date:15/11/2024
 * Time:09:16
 */
public interface RefreshTokenService {

    Optional<RefreshToken> findByToken(final String token);

    RefreshToken createRefreshToken(User user);

    boolean verifyExpiration(RefreshToken refreshToken);
}
