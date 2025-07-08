package com.wilzwert.myjobs.core.domain.model.activity.command;

import com.wilzwert.myjobs.core.domain.model.activity.ActivityType;
import com.wilzwert.myjobs.core.domain.model.job.JobId;
import com.wilzwert.myjobs.core.domain.model.user.UserId;

import java.util.List;

/**
 * @author Wilhelm Zwertvaegher
 */

public record CreateActivitiesCommand(List<CreateActivityCommand> createActivityCommandList, UserId userId, JobId jobId) {
    public static class Builder {
        private List<CreateActivityCommand> createActivityCommandList;
        private UserId userId;
        private JobId jobId;


        public Builder() {}

        public Builder(CreateActivitiesCommand command) {
            this.createActivityCommandList = command.createActivityCommandList();
            this.userId = command.userId();
            this.jobId = command.jobId();
        }

        public CreateActivitiesCommand.Builder commandList(List<CreateActivityCommand> createActivityCommandList) {
            this.createActivityCommandList = createActivityCommandList;
            return this;
        }
        public CreateActivitiesCommand.Builder userId(UserId userId) {
            this.userId = userId;
            return this;
        }

        public CreateActivitiesCommand.Builder jobId(JobId jobId) {
            this.jobId = jobId;
            return this;
        }

        public CreateActivitiesCommand build() {
            return new CreateActivitiesCommand(createActivityCommandList, userId, jobId);
        }
    }
}

