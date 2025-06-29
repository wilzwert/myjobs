package com.wilzwert.myjobs.core.domain.model.activity.command;

import com.wilzwert.myjobs.core.domain.model.activity.ActivityType;
import com.wilzwert.myjobs.core.domain.model.job.JobId;
import com.wilzwert.myjobs.core.domain.model.user.UserId;

/**
 * @author Wilhelm Zwertvaegher
 */

public record CreateActivityCommand(ActivityType activityType, String comment) {
    public static class Builder {
        private ActivityType activityType;
        private String comment;

        public Builder() {}

        public Builder(CreateActivityCommand command) {
            this.activityType = command.activityType();
            this.comment = command.comment();
        }

        public CreateActivityCommand.Builder comment(String comment) {
            this.comment = comment;
            return this;
        }

        public CreateActivityCommand build() {
            return new CreateActivityCommand(activityType, comment);
        }
    }
}

