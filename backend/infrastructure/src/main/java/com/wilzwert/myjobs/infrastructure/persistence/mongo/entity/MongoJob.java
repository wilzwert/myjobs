package com.wilzwert.myjobs.infrastructure.persistence.mongo.entity;

import com.wilzwert.myjobs.core.domain.model.job.JobRating;
import com.wilzwert.myjobs.core.domain.model.job.JobStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Wilhelm Zwertvaegher
 * Date:12/03/2025
 * Time:16:06
 */

@Document(collection = "jobs")
@Data
@Accessors(chain = true)
@NoArgsConstructor
// @AllArgsConstructor
@CompoundIndexes({
        @CompoundIndex(name = "unique_user_job", def = "{'user_id': 1, 'url': 1}", unique = true)
})
public class MongoJob {
    @Id
    private UUID id;

    private String url;

    private JobStatus status;

    private String title;

    private String company;

    private String description;

    private String profile;

    private String salary;

    private JobRating rating;

    @Field(name = "created_at")
    private Instant createdAt;

    @Field(name = "updated_at")
    private Instant updatedAt;

    @Field(name = "status_updated_at")
    private Instant statusUpdatedAt;

    @Field(name = "user_id")
    private UUID userId;

    @Field(name = "activities")
    private List<MongoActivity> activities = new ArrayList<>();

    @Field(name = "attachments")
    private List<MongoAttachment> attachments = new ArrayList<>();
}