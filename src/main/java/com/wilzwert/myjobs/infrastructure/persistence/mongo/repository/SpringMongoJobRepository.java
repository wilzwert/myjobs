package com.wilzwert.myjobs.infrastructure.persistence.mongo.repository;


import com.wilzwert.myjobs.infrastructure.persistence.mongo.entity.MongoJob;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author Wilhelm Zwertvaegher
 * Date:12/03/2025
 * Time:15:50
 */
@Repository
public interface SpringMongoJobRepository extends MongoRepository<MongoJob, String> {
    Optional<MongoJob> findById(String id);

}