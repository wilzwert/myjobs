package com.wilzwert.myjobs.core.domain.model.job.event.integration;


import com.wilzwert.myjobs.core.domain.model.job.JobId;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Wilhelm Zwertvaegher
 * Date:06/06/2025
 * Time:22:27
 */
public class JobCreatedEventTest {

    @Test
    void testJobCreatedEvent() {
        IntegrationEventId eventId = IntegrationEventId.generate();
        JobId jobId = JobId.generate();
        Instant now = Instant.now();
        JobCreatedEvent event = new JobCreatedEvent(eventId, jobId);
        assertEquals(eventId, event.getId());
        assertEquals(jobId, event.getJobId());
        assertTrue(event.getOccurredAt().equals(now) || event.getOccurredAt().isAfter(now));
    }
}