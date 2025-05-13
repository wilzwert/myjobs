package com.wilzwert.myjobs.core.domain.model.attachment;

import com.wilzwert.myjobs.core.domain.shared.exception.ValidationException;
import com.wilzwert.myjobs.core.domain.model.DomainEntity;
import com.wilzwert.myjobs.core.domain.shared.validation.ValidationErrors;
import com.wilzwert.myjobs.core.domain.shared.validation.Validator;

import java.time.Instant;

/**
 * @author Wilhelm Zwertvaegher
 * Date:12/03/2025
 * Time:15:33
 */
public class Attachment extends DomainEntity<AttachmentId> {
    private final AttachmentId id;

    private final String name;

    private final String fileId;

    private final String filename;

    private final String contentType;

    private final Instant createdAt;

    private final Instant updatedAt;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private AttachmentId id;
        private String name;
        private String fileId;
        private String filename;
        private String contentType;
        private Instant createdAt;
        private Instant updatedAt;

        public Builder() {
        }

        public Builder(Attachment attachment) {
            id = attachment.getId();
            createdAt = attachment.getCreatedAt();
            updatedAt = attachment.getUpdatedAt();
            name = attachment.getName();
            fileId = attachment.getFileId();
            filename = attachment.getFilename();
            contentType = attachment.getContentType();
        }

        public Builder id(AttachmentId id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder fileId(String fileId) {
            this.fileId = fileId;
            return this;
        }

        public Builder filename(String filename) {
            this.filename = filename;
            return this;
        }

        public Builder contentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(Instant updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Attachment build() {
            return new Attachment(id, name, fileId, filename, contentType, createdAt, updatedAt);
        }
    }

    private ValidationErrors validate() {
        return  new Validator()
                .requireNotEmpty("name", name)
                .requireNotEmpty("fileId", fileId)
                .requireNotEmpty("filename", filename)
                .requireNotEmpty("contentType", contentType)
                .getErrors();
    }

    public Attachment(AttachmentId id, String name, String fileId, String filename, String contentType, Instant createdAt, Instant updatedAt) {
        this.id = id != null ? id : AttachmentId.generate();
        this.name = name;
        this.fileId = fileId;
        this.filename = filename;
        this.contentType = contentType;
        this.createdAt = createdAt != null ? createdAt : Instant.now();
        this.updatedAt = updatedAt != null ? updatedAt : Instant.now();

        ValidationErrors validationErrors = validate();
        if(validationErrors.hasErrors()) {
            throw new ValidationException(validationErrors);
        }

    }

    @Override
    public AttachmentId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getFileId() {
        return fileId;
    }

    public String getFilename() {
        return filename;
    }

    public String getContentType() {
        return contentType;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
