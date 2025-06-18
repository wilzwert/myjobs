package com.wilzwert.myjobs.infrastructure.utility;


import com.wilzwert.myjobs.infrastructure.persistence.mongo.entity.EventStatus;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.entity.MongoIntegrationEvent;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.repository.MongoIntegrationEventRepository;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Wilhelm Zwertvaegher
 * Date:18/06/2025
 * Time:08:41
 */
public class IntegrationEventUtility {

    private final MongoIntegrationEventRepository integrationEventRepository;

    public IntegrationEventUtility(MongoIntegrationEventRepository integrationEventRepository) {
        this.integrationEventRepository = integrationEventRepository;
    }

    public void assertEventCreated(String eventType, Object findInPayload, Instant minInstant) {
        if(null == minInstant) {
            // by default, gently assume that an event should have been created in the last 30 seconds
            minInstant = Instant.now().minusSeconds(30);
        }
        final Instant compareInstant = minInstant;

        // check an event has been created
        List<MongoIntegrationEvent> events = integrationEventRepository.findByType(eventType);
        List<MongoIntegrationEvent> filteredEvents = events.stream().filter(
                e ->
                        e.getStatus().equals(EventStatus.PENDING)
                        && e.getPayload().contains(findInPayload.toString())
                        && e.getOccurredAt().isAfter(compareInstant)
        ).toList();
        assertThat(filteredEvents).hasSize(1);
        MongoIntegrationEvent event = filteredEvents.getFirst();
        assertThat(event.getStatus()).isEqualTo(EventStatus.PENDING);
        assertThat(event.getPayload()).contains(findInPayload.toString());

        // TODO : return a real deserialized Integration Event for further checks ?
        // it could be very useful, but the downside is that it relies on actual deserialization which MUST be thoroughly tested
    }

    public void assertEventCreated(String eventType, Object findInPayload) {
        assertEventCreated(eventType, findInPayload, null);
    }
}
