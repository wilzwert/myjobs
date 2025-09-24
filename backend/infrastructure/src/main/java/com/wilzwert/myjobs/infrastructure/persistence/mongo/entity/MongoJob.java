package com.wilzwert.myjobs.infrastructure.persistence.mongo.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.wilzwert.myjobs.core.domain.model.job.JobRating;
import com.wilzwert.myjobs.core.domain.model.job.JobStatus;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.job.JobRatingDeserializer;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Wilhelm Zwertvaegher
 */

@Document(collection = "jobs")
@Data
@Accessors(chain = true)
@NoArgsConstructor
@CompoundIndex(name = "unique_user_job", def = "{'user_id': 1, 'url': 1}", unique = true)
@ToString
public class MongoJob {
    @Id
    private UUID id;

    private String url;

    @Indexed
    private JobStatus status;

    @TextIndexed(weight = 5)
    private String title;

    @TextIndexed(weight = 3)
    private String company;

    @TextIndexed(weight = 2)
    private String description;

    private String profile;

    @TextIndexed(weight = 4)
    private String comment;

    private String salary;

    @JsonDeserialize(using = JobRatingDeserializer.class)
    private JobRating rating;

    @Field(name = "created_at")
    private Instant createdAt;

    @Indexed
    @Field(name = "updated_at")
    private Instant updatedAt;

    @Field(name = "status_updated_at")
    private Instant statusUpdatedAt;

    @Field(name = "follow_up_reminder_sent_at")
    private Instant followUpReminderSentAt;

    @Field(name = "user_id")
    private UUID userId;

    @Field(name = "activities")
    private List<MongoActivity> activities = new ArrayList<>();

    @Field(name = "attachments")
    private List<MongoAttachment> attachments = new ArrayList<>();
}