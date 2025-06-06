package com.wilzwert.myjobs.core.domain.model.job.command;


import com.wilzwert.myjobs.core.domain.model.job.JobId;
import com.wilzwert.myjobs.core.domain.model.user.UserId;

import java.util.Optional;

/**
 * @author Wilhelm Zwertvaegher
 * Date:05/06/2025
 * Time:08:42
 * A command to update a specific field in a Job
 *
 */

public record UpdateJobFieldCommand(JobId jobId, UserId userId, Field field, String value) implements UpdateJobCommand {
    public enum Field {
        URL,
        TITLE,
        COMPANY,
        DESCRIPTION,
        PROFILE,
        COMMENT,
        SALARY;

        public static Optional<Field> fromString(String input) {
            try {
                return Optional.of(Field.valueOf(input.toUpperCase()));
            } catch (IllegalArgumentException | NullPointerException e) {
                return Optional.empty();
            }
        }
    }



    public static class Builder {
        private JobId jobId;
        private UserId userId;
        private Field field;
        private String value;

        public Builder() {}

        public Builder(UpdateJobFieldCommand command) {
            this.jobId = command.jobId();
            this.userId = command.userId();
            this.field = command.field();
            this.value = command.value();
        }

        public Builder jobId(JobId jobId) {
            this.jobId = jobId;
            return this;
        }

        public Builder userId(UserId userId) {
            this.userId = userId;
            return this;
        }

        public Builder field(Field field) {
            this.field = field;
            return this;
        }

        public Builder value(String value) {
            this.value = value;
            return this;
        }

        public UpdateJobFieldCommand build() {
            return new UpdateJobFieldCommand(jobId, userId, field, value);
        }
    }
}
