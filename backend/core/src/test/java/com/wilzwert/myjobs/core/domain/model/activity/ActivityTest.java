package com.wilzwert.myjobs.core.domain.model.activity;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Wilhelm Zwertvaegher
 * Date:08/04/2025
 * Time:16:45
 */

public class ActivityTest {

    @Test
    public void shouldCreateActivity() {
        Instant now = Instant.now();
        ActivityId activityId = ActivityId.generate();
        Activity activity = Activity.builder()
                .id(activityId)
                .type(ActivityType.CREATION)
                .comment("comment")
                .createdAt(now)
                .updatedAt(now)
                .build();

        assertNotNull(activity);
        assertEquals(activityId, activity.getId());
        assertEquals(ActivityType.CREATION, activity.getType());
        assertEquals("comment", activity.getComment());
        assertEquals(now, activity.getCreatedAt());
        assertEquals(now, activity.getUpdatedAt());
    }
}
