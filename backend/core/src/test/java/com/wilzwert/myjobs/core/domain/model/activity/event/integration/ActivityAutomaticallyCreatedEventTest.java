package com.wilzwert.myjobs.core.domain.model.activity.event.integration;


import com.wilzwert.myjobs.core.domain.model.activity.ActivityId;
import com.wilzwert.myjobs.core.domain.model.activity.ActivityType;
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

public class ActivityAutomaticallyCreatedEventTest {

    @Test
    void testActivityAutomaticallyCreatedEventTest() {
        IntegrationEventId eventId = IntegrationEventId.generate();
        JobId jobId = JobId.generate();
        ActivityId activityId = ActivityId.generate();
        Instant now = Instant.now();
        ActivityAutomaticallyCreatedEvent event = new ActivityAutomaticallyCreatedEvent(eventId, jobId, activityId, ActivityType.CREATION);
        assertEquals(eventId, event.getId());
        assertEquals(jobId, event.getJobId());
        assertEquals(activityId, event.getActivityId());
        assertEquals(ActivityType.CREATION, event.getActivityType());
        assertTrue(event.getOccurredAt().equals(now) || event.getOccurredAt().isAfter(now));
    }

    @Test
    void testActivityAutomaticallyCreatedEventTest2() {
        IntegrationEventId eventId = IntegrationEventId.generate();
        JobId jobId = JobId.generate();
        ActivityId activityId = ActivityId.generate();
        Instant now = Instant.now();
        ActivityAutomaticallyCreatedEvent event = new ActivityAutomaticallyCreatedEvent(eventId, now, jobId, activityId, ActivityType.CREATION);
        assertEquals(eventId, event.getId());
        assertEquals(jobId, event.getJobId());
        assertEquals(activityId, event.getActivityId());
        assertEquals(ActivityType.CREATION, event.getActivityType());
        assertEquals(now, event.getOccurredAt());
    }
}
