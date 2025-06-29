package com.wilzwert.myjobs.infrastructure.persistence.mongo.service;

import com.wilzwert.myjobs.core.domain.shared.event.integration.IntegrationEvent;
import com.wilzwert.myjobs.core.domain.shared.event.integration.IntegrationEventId;
import com.wilzwert.myjobs.core.domain.shared.ports.driven.event.IntegrationEventPublisher;
import com.wilzwert.myjobs.infrastructure.event.IntegrationEventDataManager;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.entity.EventStatus;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.entity.MongoIntegrationEvent;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.repository.MongoIntegrationEventRepository;
import com.wilzwert.myjobs.infrastructure.serialization.IntegrationEventSerializationHandler;
import com.wilzwert.myjobs.infrastructure.serialization.exception.SerializationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * @author Wilhelm Zwertvaegher
 * Date:06/06/2025
 * Time:16:18
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class IntegrationEventPublisherAdapter implements IntegrationEventPublisher, IntegrationEventDataManager {

    private final MongoIntegrationEventRepository eventRepository;

    private final IntegrationEventSerializationHandler integrationEventSerializationHandler;


    @Override
    public IntegrationEvent publish(final IntegrationEvent event) {
        try {
            final MongoIntegrationEvent mongoEvent = new MongoIntegrationEvent();
            mongoEvent.setId(event.getId().value());
            mongoEvent.setStatus(EventStatus.PENDING);
            mongoEvent.setOccurredAt(event.getOccurredAt());
            mongoEvent.setType(this.integrationEventSerializationHandler.getResolvableType(event));
            mongoEvent.setPayload(this.integrationEventSerializationHandler.serialize(event));
            this.eventRepository.save(mongoEvent);

            return event;
        } catch (SerializationException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<IntegrationEvent> findById(IntegrationEventId integrationEventId) {
        return eventRepository.findById(integrationEventId.value())
            .map(e -> integrationEventSerializationHandler.readFromPayload(e.getType(), e.getPayload()));
    }

    @Override
    public List<IntegrationEvent> findPending() {
        return eventRepository.findByStatus(EventStatus.PENDING).stream()
            .map(e -> integrationEventSerializationHandler.readFromPayload(e.getType(), e.getPayload()))
            .toList();
    }

    @Override
    public List<? extends IntegrationEvent> markAllAs(List<? extends IntegrationEvent> events, EventStatus status) {
        log.info("Marking {} events as {}", events.size(), status);
        eventRepository.saveAll(
            eventRepository.findAllById(
                    events.stream().map(e -> e.getId().value()).toList()
                )
                .stream()
                .map(e -> {
                    e.setStatus(status);
                    return e;
                })
                .toList()
        );
        return events;
    }
}