package com.wilzwert.myjobs.infrastructure.persistence.mongo.service;

import com.wilzwert.myjobs.core.domain.model.job.JobId;
import com.wilzwert.myjobs.core.domain.model.job.event.integration.*;
import com.wilzwert.myjobs.core.domain.shared.event.integration.IntegrationEvent;
import com.wilzwert.myjobs.infrastructure.configuration.AbstractBaseIntegrationTest;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.entity.EventStatus;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.repository.MongoIntegrationEventRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.fail;

class IntegrationEventPublisherAdapterIT extends AbstractBaseIntegrationTest {

    @Autowired
    private MongoIntegrationEventRepository repository;

    @Autowired
    private IntegrationEventPublisherAdapter underTest;

    private final UUID jobId = UUID.randomUUID();
    private final UUID eventId = UUID.randomUUID();
    private final Instant now = Instant.parse("2025-06-08T14:00:00Z");

    @Test
    void shouldCreateEvent_andRetrieveCreatedEvent() {
        var integrationEventId = new IntegrationEventId(eventId);
        JobUpdatedEvent eventToSave = new JobUpdatedEvent(
                integrationEventId,
                now,
                new JobId(jobId)
        );

        JobUpdatedEvent savedEvent = (JobUpdatedEvent) underTest.publish(eventToSave);

        assertThat(savedEvent.getId().value()).isEqualTo(eventId);
        assertThat(savedEvent.getJobId().value()).isEqualTo(jobId);
        assertThat(savedEvent.getOccurredAt()).isEqualTo(now);

        underTest.findById(integrationEventId)
                .ifPresentOrElse((event) -> {
                        var e = (JobUpdatedEvent)event;
                        assertThat(e.getId()).isEqualTo(integrationEventId);
                        assertThat(e.getJobId().value()).isEqualTo(jobId);
                        assertThat(e.getOccurredAt()).isEqualTo(now);
                    },
                    () -> fail("IntegrationEvent should be retrievable after saving"));

        // manually delete the created event to ensure further tests consistency
        repository.deleteById(eventId);


    }

    @Test
    void findPending_shouldDeserializeAndReturnKnownEvents()  {
        List<IntegrationEvent> pendingEvents = underTest.findPending();
        assertThat(pendingEvents.size()).isEqualTo(5);
    }

    @Test
    void markAllAs_shouldUpdateStatus() {
        List<IntegrationEvent> pendingEvents = underTest.findPending();
        assertThat(pendingEvents.size()).isEqualTo(5);

        underTest.markAllAs(pendingEvents, EventStatus.IN_PROGRESS);
        List<IntegrationEvent> newPendingEvents = underTest.findPending();
        assertThat(newPendingEvents.size()).isEqualTo(0);

        underTest.markAllAs(newPendingEvents, EventStatus.PENDING);
        assertThat(underTest.findPending().size()).isEqualTo(0);
    }
}
