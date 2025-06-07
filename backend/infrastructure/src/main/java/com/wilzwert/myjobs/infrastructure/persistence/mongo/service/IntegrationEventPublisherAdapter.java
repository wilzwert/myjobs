package com.wilzwert.myjobs.infrastructure.persistence.mongo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wilzwert.myjobs.core.domain.model.job.event.integration.JobCreatedEvent;
import com.wilzwert.myjobs.core.domain.model.job.event.integration.JobFieldUpdatedEvent;
import com.wilzwert.myjobs.core.domain.model.job.event.integration.JobStatusUpdatedEvent;
import com.wilzwert.myjobs.core.domain.model.job.event.integration.JobUpdatedEvent;
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
import java.util.stream.Collectors;

/**
 * @author Wilhelm Zwertvaegher
 * Date:06/06/2025
 * Time:16:18
 */
@Component
@Slf4j
public class IntegrationEventPublisherAdapter implements IntegrationEventPublisher, IntegrationEventDataManager {

    private final static Map<String, Class<? extends IntegrationEvent>> EVENT_TYPE_MAP = Map.of(
        "JobCreatedEvent", JobCreatedEvent.class,
        "JobUpdatedEvent", JobUpdatedEvent.class,
        "JobStatusUpdatedEvent", JobStatusUpdatedEvent.class,
        "JobFieldUpdatedEvent", JobFieldUpdatedEvent.class
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
    public List<IntegrationEvent> findPending() {
        return eventRepository.findByStatus(EventStatus.PENDING).stream().map(e -> {
            Class<? extends IntegrationEvent> clazz = EVENT_TYPE_MAP.get(e.getType());
            if (clazz == null) {
                throw new IllegalArgumentException("Unknown event type: " + e.getType());
            }
            try {
                log.info("Reading payload into {}", clazz.getSimpleName());
                return objectMapper.readValue(e.getPayload(), clazz);
            } catch (JsonProcessingException ex) {
                throw new RuntimeException(ex);
            }
        }).collect(Collectors.toList());
    }

    @Override
    public IntegrationEvent markAsDispatched(IntegrationEvent event) {
        MongoIntegrationEvent mongoIntegrationEvent = eventRepository.findById(event.getId().value()).orElseThrow(RuntimeException::new);
        mongoIntegrationEvent.setStatus(EventStatus.DISPATCHED);
        eventRepository.save(mongoIntegrationEvent);
        return event;
    }

    @Override
    public List<? extends IntegrationEvent> markAllAsDispatched(List<? extends IntegrationEvent> events) {
        eventRepository.saveAll(
                eventRepository.findAllById(
                    events.stream().map(e -> e.getId().value()).collect(Collectors.toList())
                )
                .stream().peek(e -> e.setStatus(EventStatus.DISPATCHED))
                .toList()
        );
        return events;
    }

    @Override
    public IntegrationEvent markAsError(IntegrationEvent event) {
        MongoIntegrationEvent mongoIntegrationEvent = eventRepository.findById(event.getId().value()).orElseThrow(RuntimeException::new);
        mongoIntegrationEvent.setStatus(EventStatus.ERROR);
        eventRepository.save(mongoIntegrationEvent);
        return event;
    }
}