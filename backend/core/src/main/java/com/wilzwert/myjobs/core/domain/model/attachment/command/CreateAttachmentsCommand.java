package com.wilzwert.myjobs.core.domain.model.attachment.command;

import com.wilzwert.myjobs.core.domain.model.job.JobId;
import com.wilzwert.myjobs.core.domain.model.user.UserId;

import java.util.List;

/**
 * @author Wilhelm Zwertvaegher
 */

public record CreateAttachmentsCommand(List<CreateAttachmentCommand> createAttachmentCommandList, UserId userId, JobId jobId) {
    public static class Builder {
        private List<CreateAttachmentCommand> createAttachmentCommandList;
        private UserId userId;
        private JobId jobId;


        public Builder() {}

        public Builder(CreateAttachmentsCommand command) {
            this.createAttachmentCommandList = command.createAttachmentCommandList();
            this.userId = command.userId();
            this.jobId = command.jobId();
        }

        public CreateAttachmentsCommand.Builder commandList(List<CreateAttachmentCommand> createAttachmentCommandList) {
            this.createAttachmentCommandList = createAttachmentCommandList;
            return this;
        }
        public CreateAttachmentsCommand.Builder userId(UserId userId) {
            this.userId = userId;
            return this;
        }

        public CreateAttachmentsCommand.Builder jobId(JobId jobId) {
            this.jobId = jobId;
            return this;
        }

        public CreateAttachmentsCommand build() {
            return new CreateAttachmentsCommand(createAttachmentCommandList, userId, jobId);
        }
    }
}