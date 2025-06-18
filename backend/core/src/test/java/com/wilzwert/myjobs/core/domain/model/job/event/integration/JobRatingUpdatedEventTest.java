package com.wilzwert.myjobs.core.domain.model.job.event.integration;


import com.wilzwert.myjobs.core.domain.model.job.JobId;
import com.wilzwert.myjobs.core.domain.model.job.JobRating;
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
public class JobRatingUpdatedEventTest {

    @Test
    void testJobRatingUpdateEvent() {
        IntegrationEventId id = IntegrationEventId.generate();
        JobId jobId = JobId.generate();
        Instant now = Instant.now();
        JobRatingUpdatedEvent event = new JobRatingUpdatedEvent(id, jobId, JobRating.of(3));
        assertEquals(jobId, event.getJobId());
        assertTrue(event.getOccurredAt().equals(now) || event.getOccurredAt().isAfter(now));
        assertEquals(3, event.getJobRating().getValue());
    }

    @Test
    void testJobRatingUpdateEvent2() {
        IntegrationEventId id = IntegrationEventId.generate();
        JobId jobId = JobId.generate();
        Instant now = Instant.now();
        JobRatingUpdatedEvent event = new JobRatingUpdatedEvent(id, now, jobId, JobRating.of(4));
        assertEquals(jobId, event.getJobId());
        assertEquals(now, event.getOccurredAt());
        assertEquals(4, event.getJobRating().getValue());
    }
}