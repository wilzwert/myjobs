package com.wilzwert.myjobs.core.domain.ports.driven.metadata.extractor;

/**
 * @author Wilhelm Zwertvaegher
 * Date:02/04/2025
 * Time:13:53
 */

public record ExtractedMetadata(String title, String company, String description, String profile, String salary, String url) {
    public static class Builder {
        private String title;
        private String company;
        private String description;
        private String profile;
        private String salary;
        private String url;


        public Builder() {
            title = "";
            company = "";
            description = "";
            profile = "";
            salary = "";
            url = "";
        }

        public Builder(String title, String company, String description, String profile, String salary, String url) {
            this.title = title;
            this.company = company;
            this.description = description;
            this.profile = profile;
            this.salary = salary;
            this.url = url;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder company(String company) {
            this.company = company;
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
            System.out.println("SALARY "+salary);
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public ExtractedMetadata build() {
            return new ExtractedMetadata(title, company, description, profile, salary, url);
        }
    }
}