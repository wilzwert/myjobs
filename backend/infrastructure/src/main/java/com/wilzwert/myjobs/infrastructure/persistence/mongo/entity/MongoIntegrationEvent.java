package com.wilzwert.myjobs.infrastructure.persistence.mongo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.UUID;

/**
 * @author Wilhelm Zwertvaegher
 */
@Document(collection = "integration_events")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MongoIntegrationEvent {
    @Id
    private UUID id;

    @Field(name = "occurred_at")
    private Instant occurredAt;

    private EventStatus status;

    private String type;

    private String payload;
}

