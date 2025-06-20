package com.wilzwert.myjobs.core.domain.model.job.event.integration;


import com.wilzwert.myjobs.core.domain.model.job.JobId;
import com.wilzwert.myjobs.core.domain.model.job.command.UpdateJobFieldCommand;
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
public class JobFieldUpdatedEventTest {

    @Test
    void testJobFieldUpdateEvent() {
        IntegrationEventId id = IntegrationEventId.generate();
        JobId jobId = JobId.generate();
        Instant now = Instant.now();
        JobFieldUpdatedEvent event = new JobFieldUpdatedEvent(id, jobId, UpdateJobFieldCommand.Field.COMMENT);
        assertEquals(id, event.getId());
        assertEquals(jobId, event.getJobId());
        assertTrue(event.getOccurredAt().equals(now) || event.getOccurredAt().isAfter(now));
        assertEquals(UpdateJobFieldCommand.Field.COMMENT, event.getField());
    }

    @Test
    void testJobFieldUpdateEvent2() {
        IntegrationEventId id = IntegrationEventId.generate();
        JobId jobId = JobId.generate();
        Instant now = Instant.now();
        JobFieldUpdatedEvent event = new JobFieldUpdatedEvent(id, now, jobId, UpdateJobFieldCommand.Field.COMMENT);
        assertEquals(id, event.getId());
        assertEquals(jobId, event.getJobId());
        assertEquals(now, event.getOccurredAt());
        assertEquals(UpdateJobFieldCommand.Field.COMMENT, event.getField());
    }
}