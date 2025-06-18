package com.wilzwert.myjobs.core.domain.model.job.event.integration;


import com.wilzwert.myjobs.core.domain.model.job.JobId;
import com.wilzwert.myjobs.core.domain.model.job.JobStatus;
import com.wilzwert.myjobs.core.domain.shared.event.integration.IntegrationEventId;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Wilhelm Zwertvaegher
 * Date:06/06/2025
 * Time:22:27
 */
public class JobStatusUpdatedEventTest {

    @Test
    void testJobStatusUpdateEvent() {
        IntegrationEventId id = IntegrationEventId.generate();
        JobId jobId = JobId.generate();
        Instant now = Instant.now();
        JobStatusUpdatedEvent event = new JobStatusUpdatedEvent(id, jobId, JobStatus.PENDING);
        assertEquals(jobId, event.getJobId());
        assertTrue(event.getOccurredAt().equals(now) || event.getOccurredAt().isAfter(now));
        assertEquals(JobStatus.PENDING, event.getJobStatus());
    }

    @Test
    void testJobStatusUpdateEvent2() {
        IntegrationEventId id = IntegrationEventId.generate();
        JobId jobId = JobId.generate();
        Instant now = Instant.now();
        JobStatusUpdatedEvent event = new JobStatusUpdatedEvent(id, now, jobId, JobStatus.PENDING);
        assertEquals(jobId, event.getJobId());
        assertEquals(now, event.getOccurredAt());
        assertEquals(JobStatus.PENDING, event.getJobStatus());
    }
}