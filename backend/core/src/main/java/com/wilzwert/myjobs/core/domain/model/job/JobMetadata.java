package com.wilzwert.myjobs.core.domain.model.job;

/**
 * @author Wilhelm Zwertvaegher
 * Date:13/03/2025
 * Time:11:10
 */

public record JobMetadata(String title, String company, String url, String description, String profile, String salary) {
    public static class Builder {
        private String title;
        private String company;
        private String url;
        private String description;
        private String profile;
        private String salary;

        public Builder() {

        }

        public Builder(JobMetadata metadata) {
            this.title = metadata.title();
            this.company = metadata.company();
            this.url = metadata.url();
            this.description = metadata.description();
            this.profile = metadata.profile();
            this.salary = metadata.salary();
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

        public Builder salary(String salary) {
            this.salary = salary;
            return this;
        }

        public JobMetadata build() {
            return new JobMetadata(title, company, url, description, profile, salary);
        }
    }
}
