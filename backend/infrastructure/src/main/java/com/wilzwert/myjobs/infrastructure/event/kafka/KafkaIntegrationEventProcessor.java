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

    private final Map<Class<? extends IntegrationEvent>, String> typesTopics;

    private final KafkaTemplate<String, KafkaIntegrationEvent> kafkaTemplate;

    private final ObjectMapper objectMapper;

    KafkaIntegrationEventProcessor(KafkaTemplate<String, KafkaIntegrationEvent> kafkaTemplate, KafkaProperties kafkaProperties, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;

        String jobsTopic = kafkaProperties.getTopicPrefix()+"jobs";
        typesTopics = Map.of(
                JobCreatedEvent.class, jobsTopic,
                JobUpdatedEvent.class, jobsTopic,
                JobStatusUpdatedEvent.class, jobsTopic,
                JobRatingUpdatedEvent.class, jobsTopic,
                JobFieldUpdatedEvent.class, jobsTopic
        );
    }

    @Override
    public IntegrationEvent process(@NonNull IntegrationEvent event) throws Exception {
        log.info("Sending event {}", event.getId().value().toString());
        KafkaIntegrationEvent kafkaEvent = new KafkaIntegrationEvent(event.getClass().getSimpleName(), objectMapper.writeValueAsString(event));
        kafkaTemplate.send(typesTopics.get(event.getClass()), event.getId().value().toString(), kafkaEvent).get();
        return event;
    }
}