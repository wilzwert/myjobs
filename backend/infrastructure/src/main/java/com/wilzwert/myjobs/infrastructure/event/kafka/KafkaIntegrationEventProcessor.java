package com.wilzwert.myjobs.infrastructure.event.kafka;

import com.wilzwert.myjobs.core.domain.model.job.event.integration.JobCreatedEvent;
import com.wilzwert.myjobs.core.domain.model.job.event.integration.JobFieldUpdatedEvent;
import com.wilzwert.myjobs.core.domain.model.job.event.integration.JobStatusUpdatedEvent;
import com.wilzwert.myjobs.core.domain.model.job.event.integration.JobUpdatedEvent;
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
            JobFieldUpdatedEvent.class, JOBS_TOPIC
    );

    private final KafkaTemplate<String, IntegrationEvent> kafkaTemplate;

    KafkaIntegrationEventProcessor(KafkaTemplate<String, IntegrationEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public IntegrationEvent process(@NonNull IntegrationEvent event) throws Exception {
        // for now, we do synchronous calls
        log.info("Sending event {}", event.getId().value().toString());
        kafkaTemplate.send(TYPES_TOPICS.get(event.getClass()), event.getId().value().toString(), event).get();
        return event;
    }
}