package com.wilzwert.myjobs.core.domain.model;

import java.time.Instant;

/**
 * @author Wilhelm Zwertvaegher
 * Date:12/03/2025
 * Time:15:33
 */
public class Attachment extends DomainEntity<AttachmentId> {
    private final AttachmentId id;

    private final JobId jobId;

    private final String name;

    private final String fileId;

    private final String filename;

    private final String contentType;

    private final Instant createdAt;

    private final Instant updatedAt;


    public Attachment(AttachmentId id, JobId jobId, String name, String fileId, String filename, String contentType, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.jobId = jobId;
        this.name = name;
        this.fileId = fileId;
        this.filename = filename;
        this.contentType = contentType;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @Override
    public AttachmentId getId() {
        return id;
    }

    public JobId getJobId() {
        return jobId;
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
