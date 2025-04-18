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

    private final String comment;

    private final Instant createdAt;

    private final Instant updatedAt;

    public static Builder builder() {
        return new Builder();
    }

    public static Builder from(Activity activity) {
        return new Builder(activity);
    }

    public static class Builder {
        private ActivityId id;

        private ActivityType type;

        private String comment;

        private Instant createdAt;

        private Instant updatedAt;

        public Builder() {
            id = ActivityId.generate();
            comment = "";
            createdAt = Instant.now();
            updatedAt = Instant.now();
        }

        public Builder(Activity activity) {
            id = activity.getId();
            createdAt = activity.getCreatedAt();
            updatedAt = activity.getUpdatedAt();
            type = activity.getType();
            comment = activity.getComment();
        }

        public Builder id(ActivityId id) {
            this.id = id;
            return this;
        }

        public Builder type(ActivityType type) {
            this.type = type;
            return this;
        }

        public Builder comment(String comment) {
            this.comment = comment;
            return this;
        }

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(Instant updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Activity build() {
            return new Activity(id, type, comment, createdAt, updatedAt);
        }
    }

    public Activity(ActivityId id, ActivityType type, String comment, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.type = type;
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
