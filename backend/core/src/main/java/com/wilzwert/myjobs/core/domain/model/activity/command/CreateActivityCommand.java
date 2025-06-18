package com.wilzwert.myjobs.core.domain.model.activity.command;

import com.wilzwert.myjobs.core.domain.model.activity.ActivityType;
import com.wilzwert.myjobs.core.domain.model.job.JobId;
import com.wilzwert.myjobs.core.domain.model.user.UserId;

/**
 * @author Wilhelm Zwertvaegher
 */

public record CreateActivityCommand(ActivityType activityType, String comment, UserId userId, JobId jobId) {
    public static class Builder {
        private ActivityType activityType;
        private String comment;
        private UserId userId;
        private JobId jobId;


        public Builder() {}

        public Builder(CreateActivityCommand command) {
            this.activityType = command.activityType();
            this.comment = command.comment();
            this.userId = command.userId();
            this.jobId = command.jobId();
        }

        public CreateActivityCommand.Builder comment(String comment) {
            this.comment = comment;
            return this;
        }
        public CreateActivityCommand.Builder userId(UserId userId) {
            this.userId = userId;
            return this;
        }

        public CreateActivityCommand.Builder jobId(JobId jobId) {
            this.jobId = jobId;
            return this;
        }

        public CreateActivityCommand build() {
            return new CreateActivityCommand(activityType, comment, userId, jobId);
        }
    }
}

