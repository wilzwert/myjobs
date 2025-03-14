package com.wilzwert.myapps.infrastructure.persistence.mongo.repository;


import com.wilzwert.myapps.infrastructure.security.model.RefreshToken;
import com.wilzwert.myapps.infrastructure.persistence.mongo.entity.MongoRefreshToken;
import com.wilzwert.myapps.infrastructure.security.repository.RefreshTokenRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author Wilhelm Zwertvaegher
 * Date:14/03/2025
 * Time:11:23
 */

@Repository
public interface MongoRefreshTokenRepository extends RefreshTokenRepository, MongoRepository<MongoRefreshToken, String> {
    @Override
    Optional<RefreshToken> findByToken(String token);
}
