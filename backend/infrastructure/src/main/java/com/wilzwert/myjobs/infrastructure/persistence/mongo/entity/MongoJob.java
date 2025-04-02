package com.wilzwert.myjobs.infrastructure.persistence.mongo.entity;

import com.wilzwert.myjobs.core.domain.model.JobRating;
import com.wilzwert.myjobs.core.domain.model.JobStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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
@NoArgsConstructor
@AllArgsConstructor
@CompoundIndexes({
        @CompoundIndex(name = "unique_user_job", def = "{'user_id': 1, 'url': 1}", unique = true)
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
    private String company;

    @Field
    private String description;

    @Field
    private String profile;

    @Field
    private String salary;

    @Field
    private JobRating rating;

    @Field(name = "created_at")
    private Instant createdAt;

    @Field(name = "updated_at")
    private Instant updatedAt;

    @Field(name = "user_id")
    private UUID userId;

    @Field(name = "activities")
    private List<MongoActivity> activities = new ArrayList<>();

    @Field(name = "attachments")
    private List<MongoAttachment> attachments = new ArrayList<>();
}

