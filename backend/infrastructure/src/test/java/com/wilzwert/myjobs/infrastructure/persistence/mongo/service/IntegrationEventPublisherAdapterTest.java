package com.wilzwert.myjobs.infrastructure.persistence.mongo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.wilzwert.myjobs.core.domain.model.job.JobId;
import com.wilzwert.myjobs.core.domain.model.job.event.integration.*;
import com.wilzwert.myjobs.core.domain.shared.event.integration.IntegrationEvent;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.entity.EventStatus;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.entity.MongoIntegrationEvent;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.repository.MongoIntegrationEventRepository;
import com.wilzwert.myjobs.infrastructure.serialization.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class IntegrationEventPublisherAdapterTest {

    private MongoIntegrationEventRepository repository;
    private ObjectMapper objectMapper;
    private IntegrationEventPublisherAdapter adapter;

    private final UUID jobId = UUID.randomUUID();
    private final UUID eventId = UUID.randomUUID();
    private final Instant now = Instant.parse("2025-06-08T14:00:00Z");

    @BeforeEach
    void setup() {
        repository = mock(MongoIntegrationEventRepository.class);
        objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(JobCreatedEvent.class, new JobCreatedEventDeserializer());
        module.addDeserializer(JobUpdatedEvent.class, new JobUpdatedEventDeserializer());
        module.addDeserializer(JobStatusUpdatedEvent.class, new JobStatusUpdatedEventDeserializer());
        module.addDeserializer(JobRatingUpdatedEvent.class, new JobRatingUpdatedEventDeserializer());
        module.addDeserializer(JobFieldUpdatedEvent.class, new JobFieldUpdatedEventDeserializer());
        objectMapper.registerModule(module);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        adapter = new IntegrationEventPublisherAdapter(repository, objectMapper);
    }

    @Test
    void publish_shouldSaveEventToMongo() {
        JobUpdatedEvent event = new JobUpdatedEvent(
                new IntegrationEventId(eventId),
                now,
                new JobId(jobId)
        );

        adapter.publish(event);

        ArgumentCaptor<MongoIntegrationEvent> captor = ArgumentCaptor.forClass(MongoIntegrationEvent.class);
        verify(repository).save(captor.capture());

        MongoIntegrationEvent saved = captor.getValue();

        assertThat(saved.getId()).isEqualTo(eventId);
        assertThat(saved.getStatus()).isEqualTo(EventStatus.PENDING);
        assertThat(saved.getType()).isEqualTo("JobUpdatedEvent");
        assertThat(saved.getOccurredAt()).isEqualTo(now);
        assertThat(saved.getPayload()).contains("jobId");
    }

    @Test
    void findPending_shouldDeserializeKnownEvents() throws JsonProcessingException {
        JobUpdatedEvent event = new JobUpdatedEvent(new IntegrationEventId(eventId), now, new JobId(jobId));
        String payload = objectMapper.writeValueAsString(event);
        MongoIntegrationEvent mongoEvent = new MongoIntegrationEvent();
        mongoEvent.setId(eventId);
        mongoEvent.setOccurredAt(now);
        mongoEvent.setStatus(EventStatus.PENDING);
        mongoEvent.setType("JobUpdatedEvent");
        mongoEvent.setPayload(payload);

        when(repository.findByStatus(EventStatus.PENDING)).thenReturn(List.of(mongoEvent));

        List<IntegrationEvent> events = adapter.findPending();

        assertThat(events).hasSize(1);
        assertThat(events.getFirst()).isInstanceOf(JobUpdatedEvent.class);
        assertThat(events.getFirst().getId().value()).isEqualTo(eventId);
    }

    @Test
    void findPending_shouldThrowIfTypeUnknown() {
        MongoIntegrationEvent mongoEvent = new MongoIntegrationEvent();
        mongoEvent.setType("UnknownEvent");
        mongoEvent.setPayload("{}");

        when(repository.findByStatus(EventStatus.PENDING)).thenReturn(List.of(mongoEvent));

        assertThatThrownBy(() -> adapter.findPending())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unknown event type");
    }

    @Test
    void markAllAs_shouldUpdateStatus() {
        IntegrationEventId id = new IntegrationEventId(eventId);
        JobUpdatedEvent event = new JobUpdatedEvent(id, now, new JobId(jobId));

        MongoIntegrationEvent mongoEvent = new MongoIntegrationEvent();
        mongoEvent.setId(eventId);
        mongoEvent.setStatus(EventStatus.PENDING);

        when(repository.findAllById(List.of(eventId))).thenReturn(List.of(mongoEvent));

        List<? extends IntegrationEvent> result = adapter.markAllAs(List.of(event), EventStatus.DISPATCHED);

        assertThat((List<JobUpdatedEvent>) result).containsExactly(event);
        verify(repository).saveAll(argThat(events ->
                StreamSupport.stream(events.spliterator(), false).allMatch(e -> e.getStatus() == EventStatus.DISPATCHED)
        ));
    }
}
