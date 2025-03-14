package com.wilzwert.myapps.infrastructure.security.repository;


import com.wilzwert.myapps.infrastructure.security.model.RefreshToken;

import java.util.Optional;

/**
 * @author Wilhelm Zwertvaegher
 * Date:14/03/2025
 * Time:11:22
 */
public interface RefreshTokenRepository {
    Optional<RefreshToken> findByToken(String token);
    RefreshToken save(RefreshToken token);
    void delete(RefreshToken token);
}
