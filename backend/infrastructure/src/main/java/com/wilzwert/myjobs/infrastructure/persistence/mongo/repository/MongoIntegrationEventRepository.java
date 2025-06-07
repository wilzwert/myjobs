package com.wilzwert.myjobs.infrastructure.persistence.mongo.repository;

import com.wilzwert.myjobs.infrastructure.persistence.mongo.entity.EventStatus;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.entity.MongoIntegrationEvent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * @author Wilhelm Zwertvaegher
 */
@Repository
public interface MongoIntegrationEventRepository extends MongoRepository<MongoIntegrationEvent, UUID> {
    List<MongoIntegrationEvent> findByType(String type);
    List<MongoIntegrationEvent> findByStatus(EventStatus status);
}