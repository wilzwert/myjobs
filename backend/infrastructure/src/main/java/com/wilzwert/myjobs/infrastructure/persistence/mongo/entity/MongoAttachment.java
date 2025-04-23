package com.wilzwert.myjobs.infrastructure.persistence.mongo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.Instant;
import java.util.UUID;

/**
 * @author Wilhelm Zwertvaegher
 * Date:12/03/2025
 * Time:16:06
 */
@Document(collection = "jobs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MongoAttachment {
    @Id
    @MongoId
    private UUID id;

    private String name;

    private String fileId;

    private String filename;

    @Field(name = "content_type")
    private String contentType;

    @Field(name = "created_at")
    @CreatedDate
    private Instant createdAt;

    @Field(name = "updated_at")
    @LastModifiedDate
    private Instant updatedAt;
}

