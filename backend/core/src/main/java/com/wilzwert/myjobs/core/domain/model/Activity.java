package com.wilzwert.myjobs.core.domain.model;

import java.time.Instant;

/**
 * @author Wilhelm Zwertvaegher
 * Date:12/03/2025
 * Time:15:33
 */
public class Activity {
    private final ActivityId id;

    private final ActivityType type;

    private final Instant createdAt;

    private final Instant updatedAt;


    public Activity(ActivityId id, ActivityType type, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.type = type;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public ActivityId getId() {
        return id;
    }

    public ActivityType getType() {
        return type;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
