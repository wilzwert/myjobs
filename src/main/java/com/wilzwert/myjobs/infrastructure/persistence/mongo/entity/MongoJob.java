package com.wilzwert.myjobs.infrastructure.persistence.mongo.entity;

import com.wilzwert.myjobs.domain.model.JobStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

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
@CompoundIndexes({
        @CompoundIndex(name = "unique_user_job", def = "{'userId': 1, 'url': 1}", unique = true)
})
public class MongoJob {
    @Id
    private UUID id;

    @Field
    private String url;

    @Field
    private JobStatus status;

    @Field
    private String title;

    @Field
    private String description;

    @Field
    private String profile;

    @Field(name = "created_at")
    @CreatedDate
    private Instant createdAt;

    @Field(name = "updated_at")
    @LastModifiedDate
    private Instant updatedAt;

    @Field(name = "user_id")
    private UUID userId;
}

