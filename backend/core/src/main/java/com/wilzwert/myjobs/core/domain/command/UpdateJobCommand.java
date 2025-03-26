package com.wilzwert.myjobs.core.domain.command;


import com.wilzwert.myjobs.core.domain.model.JobId;
import com.wilzwert.myjobs.core.domain.model.UserId;


/**
 * @author Wilhelm Zwertvaegher
 * Date:13/03/2025
 * Time:11:10
 */

public record UpdateJobCommand(JobId jobId, UserId userId, String title, String company, String url, String description, String profile) {
    public static class Builder {
        private JobId jobId;
        private UserId userId;
        private String title;
        private String company;
        private String url;
        private String description;
        private String profile;

        public Builder(UpdateJobCommand command) {
            this.jobId = command.jobId();
            this.userId = command.userId();
        }

        public UpdateJobCommand.Builder title(String title) {
            this.title = title;
            return this;
        }

        public UpdateJobCommand.Builder company(String company) {
            this.company = company;
            return this;
        }

        public UpdateJobCommand.Builder url(String url) {
            this.url = url;
            return this;
        }

        public UpdateJobCommand.Builder description(String description) {
            this.description = description;
            return this;
        }

        public UpdateJobCommand.Builder profile(String profile) {
            this.profile = profile;
            return this;
        }

        public UpdateJobCommand.Builder userId(UserId userId) {
            this.userId = userId;
            return this;
        }

        public UpdateJobCommand build() {
            return new UpdateJobCommand(jobId, userId, title, company, url, description, profile);
        }
    }
}
