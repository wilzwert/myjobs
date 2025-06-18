package com.wilzwert.myjobs.core.domain.model.attachment.event.integration;


import com.wilzwert.myjobs.core.domain.model.attachment.AttachmentId;
import com.wilzwert.myjobs.core.domain.model.job.JobId;
import com.wilzwert.myjobs.core.domain.shared.event.integration.IntegrationEventId;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Wilhelm Zwertvaegher
 * Date:18/06/2025
 * Time:09:42
 */

public class AttachmentDeletedEventTest {

    @Test
    void testAttachmentDeletedEventTest() {
        IntegrationEventId eventId = IntegrationEventId.generate();
        JobId jobId = JobId.generate();
        AttachmentId attachmentId = AttachmentId.generate();
        Instant now = Instant.now();
        AttachmentDeletedEvent event = new AttachmentDeletedEvent(eventId, jobId, attachmentId);
        assertEquals(eventId, event.getId());
        assertEquals(jobId, event.getJobId());
        assertEquals(attachmentId, event.getAttachmentId());
        assertTrue(event.getOccurredAt().equals(now) || event.getOccurredAt().isAfter(now));
    }

    @Test
    void testAttachmentDeletedEventTest2() {
        IntegrationEventId eventId = IntegrationEventId.generate();
        JobId jobId = JobId.generate();
        AttachmentId attachmentId = AttachmentId.generate();
        Instant now = Instant.now();
        AttachmentDeletedEvent event = new AttachmentDeletedEvent(eventId, now, jobId, attachmentId);
        assertEquals(eventId, event.getId());
        assertEquals(jobId, event.getJobId());
        assertEquals(attachmentId, event.getAttachmentId());
        assertEquals(now, event.getOccurredAt());
    }
}