package com.wilzwert.myjobs.infrastructure.persistence.mongo.service;

import com.wilzwert.myjobs.core.domain.model.user.User;
import com.wilzwert.myjobs.infrastructure.security.configuration.JwtProperties;
import com.wilzwert.myjobs.infrastructure.security.model.RefreshToken;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.entity.MongoRefreshToken;
import com.wilzwert.myjobs.infrastructure.security.repository.RefreshTokenRepository;
import com.wilzwert.myjobs.infrastructure.security.service.JwtService;
import com.wilzwert.myjobs.infrastructure.security.service.RefreshTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * This service handles Refresh Tokens creation, retrieval and verifying
 * @author Wilhelm Zwertvaegher
 */

@Service
@Slf4j
public class MongoRefreshTokenService implements RefreshTokenService {
    private final JwtService jwtService;

    private final RefreshTokenRepository refreshTokenRepository;

    private final JwtProperties jwtProperties;

    public MongoRefreshTokenService(final RefreshTokenRepository mongoRefreshTokenRepository, final JwtService jwtService, final JwtProperties jwtProperties) {
        this.refreshTokenRepository = mongoRefreshTokenRepository;
        this.jwtService = jwtService;
        this.jwtProperties = jwtProperties;
    }

    @Override
    public Optional<RefreshToken> findByToken(final String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Override
    public RefreshToken createRefreshToken(User user) {
        MongoRefreshToken refreshToken = new MongoRefreshToken().setId(UUID.randomUUID()).setUserId(user.getId().value()).setToken(jwtService.generateToken(UUID.randomUUID().toString()));
        refreshToken.setExpiresAt(Instant.now().plusMillis(jwtProperties.getRefreshExpirationTime()*1000));
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

    @Override
    public void deleteRefreshToken(RefreshToken refreshToken) {
        refreshTokenRepository.delete(refreshToken);
    }
}
