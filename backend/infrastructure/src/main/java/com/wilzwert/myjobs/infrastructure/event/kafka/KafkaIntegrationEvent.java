package com.wilzwert.myjobs.infrastructure.event.kafka;

public record KafkaIntegrationEvent(String type, String payload) {
}
