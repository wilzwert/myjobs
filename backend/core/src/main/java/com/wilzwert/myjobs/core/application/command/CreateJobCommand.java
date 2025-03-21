package com.wilzwert.myjobs.core.application.command;

import com.wilzwert.myjobs.core.domain.model.UserId;

/**
 * @author Wilhelm Zwertvaegher
 * Date:13/03/2025
 * Time:11:10
 */

public record CreateJobCommand(String title, String company, String url, String description, String profile, UserId userId) {
    public static class Builder {
        private String title;
        private String company;
        private String url;
        private String description;
        private String profile;
        private UserId userId;

        public Builder(CreateJobCommand command) {
            this.userId = command.userId();
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder company(String company) {
            this.company = company;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder profile(String profile) {
            this.profile = profile;
            return this;
        }

        public Builder userId(UserId userId) {
            this.userId = userId;
            return this;
        }

        public CreateJobCommand build() {
            return new CreateJobCommand(title, company, url, description, profile, userId);
        }
    }
}
