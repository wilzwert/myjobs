package com.wilzwert.myjobs.core.domain.model.job.command;

import com.wilzwert.myjobs.core.domain.model.job.JobStatus;
import com.wilzwert.myjobs.core.domain.model.job.JobStatusMeta;
import com.wilzwert.myjobs.core.domain.model.user.UserId;

/**
 * @author Wilhelm Zwertvaegher
 */

public record GetUserJobsCommand(UserId userId, int page, int itemsPerPage, JobStatus status, JobStatusMeta statusMeta, String sort, String query) {
    public static class Builder {
        private UserId userId;
        private int page;
        private int itemsPerPage;
        private JobStatus status;
        private JobStatusMeta statusMeta;
        private String sort;
        private String query;

        public Builder() {}

        public Builder(GetUserJobsCommand command) {
            this.userId = command.userId();
            this.page = command.page();
            this.itemsPerPage = command.itemsPerPage();
            this.status = command.status();
            this.statusMeta = command.statusMeta();
            this.sort = command.sort();
            this.query = command.query();
        }

        public Builder userId(UserId userId) {
            this.userId = userId;
            return this;
        }

        public Builder page(int page) {
            this.page = page;
            return this;
        }

        public Builder itemsPerPage(int itemsPerPage) {
            this.itemsPerPage = itemsPerPage;
            return this;
        }

        public Builder status(JobStatus status) {
            this.status = status;
            return this;
        }

        public Builder statusMeta(JobStatusMeta statusMeta) {
            this.statusMeta = statusMeta;
            return this;
        }

        public Builder sort(String sort) {
            this.sort = sort;
            return this;
        }

        public Builder query(String query) {
            this.query = query;
            return this;
        }

        public GetUserJobsCommand build() {
            return new GetUserJobsCommand(userId, page, itemsPerPage, status, statusMeta, sort, query);
        }
    }
}