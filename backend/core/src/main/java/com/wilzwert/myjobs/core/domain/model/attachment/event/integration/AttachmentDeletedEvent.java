package com.wilzwert.myjobs.core.domain.model.attachment.event.integration;


import com.wilzwert.myjobs.core.domain.model.attachment.AttachmentId;
import com.wilzwert.myjobs.core.domain.model.job.JobId;
import com.wilzwert.myjobs.core.domain.shared.event.integration.IntegrationEvent;
import com.wilzwert.myjobs.core.domain.shared.event.integration.IntegrationEventId;

import java.time.Instant;

/**
 * @author Wilhelm Zwertvaegher
 * Date:06/06/2025
 * Time:15:56
 */

public final class AttachmentDeletedEvent extends IntegrationEvent {
    private final JobId jobId;
    private final AttachmentId attachmentId;

    public AttachmentDeletedEvent(IntegrationEventId id, JobId jobId, AttachmentId attachmentId) {
        super(id);
        this.jobId = jobId;
        this.attachmentId = attachmentId;
    }

    public AttachmentDeletedEvent(IntegrationEventId id, Instant occurredAt, JobId jobId, AttachmentId attachmentId) {
        super(id, occurredAt);
        this.jobId = jobId;
        this.attachmentId = attachmentId;
    }

    public JobId getJobId() {
        return jobId;
    }

    public AttachmentId getAttachmentId() {
        return attachmentId;
    }
}
