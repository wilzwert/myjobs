package com.wilzwert.myjobs.infrastructure.event.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wilzwert.myjobs.core.domain.model.job.event.integration.*;
import com.wilzwert.myjobs.core.domain.shared.event.integration.IntegrationEvent;
import com.wilzwert.myjobs.infrastructure.event.IntegrationEventProcessor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class KafkaIntegrationEventProcessor implements IntegrationEventProcessor {

    private static final String JOBS_TOPIC = "myjobs-jobs";

    private final static Map<Class<? extends IntegrationEvent>, String> TYPES_TOPICS = Map.of(
            JobCreatedEvent.class, JOBS_TOPIC,
            JobUpdatedEvent.class, JOBS_TOPIC,
            JobStatusUpdatedEvent.class, JOBS_TOPIC,
            JobRatingUpdatedEvent.class, JOBS_TOPIC,
            JobFieldUpdatedEvent.class, JOBS_TOPIC
    );

    private final KafkaTemplate<String, KafkaIntegrationEvent> kafkaTemplate;

    private final ObjectMapper objectMapper;

    KafkaIntegrationEventProcessor(KafkaTemplate<String, KafkaIntegrationEvent> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public IntegrationEvent process(@NonNull IntegrationEvent event) throws Exception {
        log.info("Sending event {}", event.getId().value().toString());
        KafkaIntegrationEvent kafkaEvent = new KafkaIntegrationEvent(event.getClass().getSimpleName(), objectMapper.writeValueAsString(event));
        kafkaTemplate.send(TYPES_TOPICS.get(event.getClass()), event.getId().value().toString(), kafkaEvent).get();
        return event;
    }
}