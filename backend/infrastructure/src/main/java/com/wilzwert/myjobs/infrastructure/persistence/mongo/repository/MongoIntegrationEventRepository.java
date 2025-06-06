package com.wilzwert.myjobs.infrastructure.persistence.mongo.repository;

import com.wilzwert.myjobs.infrastructure.persistence.mongo.entity.MongoIntegrationEvent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Wilhelm Zwertvaegher
 */
@Repository
public interface MongoIntegrationEventRepository extends MongoRepository<MongoIntegrationEvent, String> {
    List<MongoIntegrationEvent> findByType(String type);
}