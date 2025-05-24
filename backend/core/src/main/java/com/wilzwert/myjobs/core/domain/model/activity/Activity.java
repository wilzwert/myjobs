package com.wilzwert.myjobs.core.domain.model.activity;

import com.wilzwert.myjobs.core.domain.shared.exception.ValidationException;
import com.wilzwert.myjobs.core.domain.model.DomainEntity;
import com.wilzwert.myjobs.core.domain.shared.validation.ErrorCode;
import com.wilzwert.myjobs.core.domain.shared.validation.ValidationErrors;
import com.wilzwert.myjobs.core.domain.shared.validation.Validator;

import java.time.Instant;

/**
 * @author Wilhelm Zwertvaegher
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

    private ValidationErrors validate() {
        return  new Validator()
                .requireNotEmpty("id", id)
                .require("type", () -> type != null,  ErrorCode.FIELD_CANNOT_BE_EMPTY)
                .getErrors();
    }

    public Activity(ActivityId id, ActivityType type, String comment, Instant createdAt, Instant updatedAt) {
        this.id = id != null ? id : ActivityId.generate();
        this.type = type;
        this.comment = comment != null ? comment : "";
        this.createdAt = createdAt != null ? createdAt : Instant.now();
        this.updatedAt = updatedAt != null ? updatedAt : Instant.now();

        ValidationErrors errors = validate();
        if(errors.hasErrors()) {
            throw new ValidationException(errors);
        }

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
