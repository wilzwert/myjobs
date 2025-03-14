package com.wilzwert.myapps.infrastructure.persistence.mongo.service;

import com.wilzwert.myapps.domain.model.User;
import com.wilzwert.myapps.infrastructure.security.model.RefreshToken;
import com.wilzwert.myapps.infrastructure.persistence.mongo.entity.MongoRefreshToken;
import com.wilzwert.myapps.infrastructure.security.repository.RefreshTokenRepository;
import com.wilzwert.myapps.infrastructure.security.service.RefreshTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * This service handles Refresh Tokens creation, retrieval and verifying
 * @author Wilhelm Zwertvaegher
 * Date:15/11/2024
 * Time:09:16
 */

@Service
@Slf4j
public class MongoRefreshTokenService implements RefreshTokenService {

    @Value("${security.jwt.refresh-expiration-time}")
    private long jwtRefreshExpiration;

    private final RefreshTokenRepository refreshTokenRepository;

    public MongoRefreshTokenService(final RefreshTokenRepository mongoRefreshTokenRepository) {
        this.refreshTokenRepository = mongoRefreshTokenRepository;
    }

    @Override
    public Optional<RefreshToken> findByToken(final String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Override
    public RefreshToken createRefreshToken(User user) {
        MongoRefreshToken refreshToken = new MongoRefreshToken().setId(UUID.randomUUID()).setUserId(user.getId()).setToken(UUID.randomUUID().toString());
        log.info("setting expiry to {} (now is {}, expiration is {})", Instant.now().plusMillis(jwtRefreshExpiration), Instant.now(), jwtRefreshExpiration);
        refreshToken.setExpiryDate(Instant.now().plusMillis(jwtRefreshExpiration));
        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public boolean verifyExpiration(RefreshToken refreshToken) {
        if (refreshToken.getExpiresAt().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(refreshToken);
            return false;
        }
        return true;
    }
}
