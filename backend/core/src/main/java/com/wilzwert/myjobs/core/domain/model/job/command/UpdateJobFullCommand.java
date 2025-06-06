package com.wilzwert.myjobs.core.domain.model.job.command;


import com.wilzwert.myjobs.core.domain.model.job.JobId;
import com.wilzwert.myjobs.core.domain.model.user.UserId;


/**
 * @author Wilhelm Zwertvaegher
 */

public record UpdateJobFullCommand(JobId jobId, UserId userId, String title, String company, String url, String description, String profile, String comment, String salary) implements UpdateJobCommand {
    public static class Builder {
        private JobId jobId;
        private UserId userId;
        private String title;
        private String company;
        private String url;
        private String description;
        private String profile;
        private String comment;
        private String salary;

        public Builder() {}

        public Builder(UpdateJobFullCommand command) {
            this.jobId = command.jobId();
            this.userId = command.userId();
            this.title = command.title();
            this.company = command.company();
            this.url = command.url();
            this.description = command.description();
            this.profile = command.profile();
            this.comment = command.comment();
            this.salary = command.salary();
        }

        public UpdateJobFullCommand.Builder jobId(JobId jobId) {
            this.jobId = jobId;
            return this;
        }


        public UpdateJobFullCommand.Builder title(String title) {
            this.title = title;
            return this;
        }

        public UpdateJobFullCommand.Builder company(String company) {
            this.company = company;
            return this;
        }

        public UpdateJobFullCommand.Builder url(String url) {
            this.url = url;
            return this;
        }

        public UpdateJobFullCommand.Builder description(String description) {
            this.description = description;
            return this;
        }

        public UpdateJobFullCommand.Builder profile(String profile) {
            this.profile = profile;
            return this;
        }

        public UpdateJobFullCommand.Builder comment(String comment) {
            this.comment = comment;
            return this;
        }

        public UpdateJobFullCommand.Builder salary(String salary) {
            this.salary = salary;
            return this;
        }

        public UpdateJobFullCommand.Builder userId(UserId userId) {
            this.userId = userId;
            return this;
        }

        public UpdateJobFullCommand build() {
            return new UpdateJobFullCommand(jobId, userId, title, company, url, description, profile, comment, salary);
        }
    }
}
