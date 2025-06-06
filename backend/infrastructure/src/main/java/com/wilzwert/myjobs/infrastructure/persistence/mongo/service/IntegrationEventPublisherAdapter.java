package com.wilzwert.myjobs.infrastructure.persistence.mongo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wilzwert.myjobs.core.domain.shared.event.integration.IntegrationEvent;
import com.wilzwert.myjobs.core.domain.shared.ports.driven.event.IntegrationEventPublisher;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.entity.EventStatus;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.entity.MongoIntegrationEvent;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.repository.MongoIntegrationEventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * @author Wilhelm Zwertvaegher
 * Date:06/06/2025
 * Time:16:18
 */
@Component
@Slf4j
public class IntegrationEventPublisherAdapter implements IntegrationEventPublisher {

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
            mongoEvent.setId(UUID.randomUUID());
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
}