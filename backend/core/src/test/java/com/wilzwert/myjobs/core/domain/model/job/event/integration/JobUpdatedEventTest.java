package com.wilzwert.myjobs.core.domain.model.job.event.integration;


import com.wilzwert.myjobs.core.domain.model.job.JobId;
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
public class JobUpdatedEventTest {

    @Test
    void testJobUpdatedEvent() {
        IntegrationEventId id = IntegrationEventId.generate();
        JobId jobId = JobId.generate();
        Instant now = Instant.now();
        JobUpdatedEvent event = new JobUpdatedEvent(id, jobId);
        assertEquals(id, event.getId());
        assertEquals(jobId, event.getJobId());
        assertTrue(event.getOccurredAt().equals(now) || event.getOccurredAt().isAfter(now));
    }
}