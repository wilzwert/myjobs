package com.wilzwert.myjobs.infrastructure.event.kafka;

import com.wilzwert.myjobs.core.domain.shared.event.integration.IntegrationEvent;
import com.wilzwert.myjobs.infrastructure.serialization.IntegrationEventSerializationHandler;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaIntegrationEventProcessor implements com.wilzwert.myjobs.infrastructure.event.IntegrationEventProcessor {

    private final KafkaTemplate<String, KafkaIntegrationEvent> kafkaTemplate;

    private final KafkaIntegrationEventTopicResolver topicResolver;

    private final IntegrationEventSerializationHandler serializationHandler;


    KafkaIntegrationEventProcessor(KafkaTemplate<String, KafkaIntegrationEvent> kafkaTemplate, KafkaIntegrationEventTopicResolver topicResolver, IntegrationEventSerializationHandler serializationHandler) {
        this.kafkaTemplate = kafkaTemplate;
        this.topicResolver = topicResolver;
        this.serializationHandler = serializationHandler;
    }

    @Override
    public IntegrationEvent process(@NonNull IntegrationEvent event) throws Exception {
        log.info("Sending event {}", event.getId().value().toString());
        KafkaIntegrationEvent kafkaEvent = new KafkaIntegrationEvent(event.getClass().getSimpleName(), serializationHandler.serialize(event));
        kafkaTemplate.send(topicResolver.resolve(event), event.getId().value().toString(), kafkaEvent).get();
        return event;
    }
}