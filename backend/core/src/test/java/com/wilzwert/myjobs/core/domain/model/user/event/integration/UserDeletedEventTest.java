package com.wilzwert.myjobs.core.domain.model.user.event.integration;


import com.wilzwert.myjobs.core.domain.model.user.UserId;
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
public class UserDeletedEventTest {

    @Test
    void testUserDeletedEvent() {
        IntegrationEventId eventId = IntegrationEventId.generate();
        UserId userId = UserId.generate();
        Instant now = Instant.now();
        UserDeletedEvent event = new UserDeletedEvent(eventId, userId);
        assertEquals(eventId, event.getId());
        assertEquals(userId, event.getUserId());
        assertTrue(event.getOccurredAt().equals(now) || event.getOccurredAt().isAfter(now));
    }

    @Test
    void testUserDeletedEvent2() {
        IntegrationEventId eventId = IntegrationEventId.generate();
        UserId userId = UserId.generate();
        Instant now = Instant.now();
        UserDeletedEvent event = new UserDeletedEvent(eventId, now, userId);
        assertEquals(eventId, event.getId());
        assertEquals(userId, event.getUserId());
        assertEquals(now, event.getOccurredAt());
    }
}