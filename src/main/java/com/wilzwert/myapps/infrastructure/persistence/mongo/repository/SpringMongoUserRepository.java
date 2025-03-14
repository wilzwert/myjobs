package com.wilzwert.myapps.infrastructure.persistence.mongo.repository;


import com.wilzwert.myapps.infrastructure.persistence.mongo.entity.MongoUser;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author Wilhelm Zwertvaegher
 * Date:12/03/2025
 * Time:15:50
 */
@Repository
public interface SpringMongoUserRepository extends MongoRepository<MongoUser, String> {
    Optional<MongoUser> findByEmail(String email);

}