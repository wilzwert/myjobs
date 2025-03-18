package com.wilzwert.myjobs.core.domain.model;

import java.time.Instant;

/**
 * @author Wilhelm Zwertvaegher
 * Date:12/03/2025
 * Time:15:33
 */
public class Activity extends DomainEntity<ActivityId> {
    private final ActivityId id;

    private final ActivityType type;

    private final JobId jobId;

    private final String comment;

    private final Instant createdAt;

    private final Instant updatedAt;


    public Activity(ActivityId id, ActivityType type, JobId jobId, String comment, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.type = type;
        this.jobId = jobId;
        this.comment = comment;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public ActivityId getId() {
        return id;
    }

    public ActivityType getType() {
        return type;
    }

    public String getComment() {
        return comment;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
