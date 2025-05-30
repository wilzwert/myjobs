package com.wilzwert.myjobs.infrastructure.security.service;

import com.wilzwert.myjobs.core.domain.model.user.User;
import com.wilzwert.myjobs.infrastructure.security.model.RefreshToken;

import java.util.Optional;

/**
 * This service handles Refresh Tokens creation, retrieval and verifying
 * @author Wilhelm Zwertvaegher
 */
public interface RefreshTokenService {

    Optional<RefreshToken> findByToken(final String token);

    RefreshToken createRefreshToken(User user);

    boolean verifyExpiration(RefreshToken refreshToken);

    void deleteRefreshToken(RefreshToken refreshToken);
}
