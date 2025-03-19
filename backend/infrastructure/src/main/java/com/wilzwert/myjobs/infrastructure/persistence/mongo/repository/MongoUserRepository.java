package com.wilzwert.myjobs.infrastructure.persistence.mongo.repository;

import com.wilzwert.myjobs.infrastructure.persistence.mongo.entity.MongoUser;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * @author Wilhelm Zwertvaegher
 * Date:12/03/2025
 * Time:15:50
 */
@Repository
public interface MongoUserRepository extends MongoRepository<MongoUser, String> {
    Optional<MongoUser> findByEmail(String email);
    Optional<MongoUser> findByUsername(String username);
    Optional<MongoUser> findByEmailOrUsername(String email, String username);
    Optional<MongoUser> findById(UUID id);

}