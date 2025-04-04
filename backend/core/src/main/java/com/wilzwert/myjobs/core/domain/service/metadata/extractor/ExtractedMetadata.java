package com.wilzwert.myjobs.core.domain.service.metadata.extractor;

import java.util.Objects;

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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Builder builder = (Builder) o;
            return Objects.equals(title, builder.title) && Objects.equals(company, builder.company) && Objects.equals(description, builder.description) && Objects.equals(profile, builder.profile) && Objects.equals(salary, builder.salary) && Objects.equals(url, builder.url);
        }

        @Override
        public int hashCode() {
            return Objects.hash(title, company, description, profile, salary, url);
        }

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