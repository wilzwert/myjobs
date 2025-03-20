package com.wilzwert.myjobs.infrastructure.security.repository;


import com.wilzwert.myjobs.infrastructure.security.model.RefreshToken;

import java.util.Optional;
import java.util.UUID;

/**
 * @author Wilhelm Zwertvaegher
 * Date:14/03/2025
 * Time:11:22
 */
public interface RefreshTokenRepository {
    Optional<RefreshToken> findByToken(String token);
    RefreshToken save(RefreshToken token);
    void delete(RefreshToken token);
    void deleteByUserId(UUID userId);
}
