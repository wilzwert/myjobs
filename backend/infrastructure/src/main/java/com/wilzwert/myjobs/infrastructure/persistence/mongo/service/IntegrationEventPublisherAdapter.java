package com.wilzwert.myjobs.infrastructure.persistence.mongo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wilzwert.myjobs.core.domain.model.job.event.integration.*;
import com.wilzwert.myjobs.core.domain.shared.event.integration.IntegrationEvent;
import com.wilzwert.myjobs.core.domain.shared.ports.driven.event.IntegrationEventPublisher;
import com.wilzwert.myjobs.infrastructure.event.IntegrationEventDataManager;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.entity.EventStatus;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.entity.MongoIntegrationEvent;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.repository.MongoIntegrationEventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Wilhelm Zwertvaegher
 * Date:06/06/2025
 * Time:16:18
 */
@Component
@Slf4j
public class IntegrationEventPublisherAdapter implements IntegrationEventPublisher, IntegrationEventDataManager {

    // map used to retrieve actual event classes based on the simple name string stored in mongodb
    // this could (should) be improved by providing some kind of factory and externalizing the map generation to a bean
    // (maybe with componentscan ?)
    private static final Map<String, Class<? extends IntegrationEvent>> EVENT_TYPE_MAP = Map.of(
            JobCreatedEvent.class.getSimpleName(), JobCreatedEvent.class,
            JobUpdatedEvent.class.getSimpleName(), JobUpdatedEvent.class,
            JobStatusUpdatedEvent.class.getSimpleName(), JobStatusUpdatedEvent.class,
            JobRatingUpdatedEvent.class.getSimpleName(), JobRatingUpdatedEvent.class,
            JobFieldUpdatedEvent.class.getSimpleName(), JobFieldUpdatedEvent.class
    );

    private final MongoIntegrationEventRepository eventRepository;

    private final ObjectMapper objectMapper;

    IntegrationEventPublisherAdapter(MongoIntegrationEventRepository mongoIntegrationEventRepository, ObjectMapper objectMapper) {
        this.eventRepository = mongoIntegrationEventRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public IntegrationEvent publish(IntegrationEvent event) {
        try {
            MongoIntegrationEvent mongoEvent = new MongoIntegrationEvent();
            mongoEvent.setId(event.getId().value());
            mongoEvent.setStatus(EventStatus.PENDING);
            mongoEvent.setOccurredAt(event.getOccurredAt());
            mongoEvent.setType(event.getClass().getSimpleName());
            mongoEvent.setPayload(objectMapper.writeValueAsString(event));
            eventRepository.save(mongoEvent);
            return event;
        }
        catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<IntegrationEvent> findById(IntegrationEventId integrationEventId) {
        return eventRepository.findById(integrationEventId.value()).map(this::readFromPayload);
    }

    public IntegrationEvent readFromPayload(MongoIntegrationEvent mongoIntegrationEvent) {
        Class<? extends IntegrationEvent> clazz = EVENT_TYPE_MAP.get(mongoIntegrationEvent.getType());
        if (clazz == null) {
            throw new IllegalArgumentException("Unknown event type: " + mongoIntegrationEvent.getType());
        }
        try {
            log.info("Reading payload into {}", clazz.getSimpleName());
            return objectMapper.readValue(mongoIntegrationEvent.getPayload(), clazz);
        }
        catch (JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public List<IntegrationEvent> findPending() {
        // to rebuild actual IntegrationEvent we need to match MongoIntegrationEvent.type to a class
        // when an IntegrationEvent is passed to this publisher for handling,
        // we set  the MongoIntegrationEvent.type with event.getClass().getSimpleName()
        // so we just have a EVENT_TYPE_MAP Map to match this name to the actual class
        return eventRepository.findByStatus(EventStatus.PENDING).stream().map(this::readFromPayload).toList();
    }

    @Override
    public List<? extends IntegrationEvent> markAllAs(List<? extends IntegrationEvent> events, EventStatus status) {
        log.info("Marking {} events as {}", events.size(), status);
        eventRepository.saveAll(
                eventRepository.findAllById(
                    events.stream().map(e -> e.getId().value()).toList()
                )
                .stream().map(e -> {e.setStatus(status); return e;})
                .toList()
        );
        return events;
    }
}