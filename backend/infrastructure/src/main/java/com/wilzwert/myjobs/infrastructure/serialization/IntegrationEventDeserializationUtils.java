package com.wilzwert.myjobs.infrastructure.serialization;

import com.fasterxml.jackson.databind.JsonNode;
import com.wilzwert.myjobs.core.domain.model.job.event.integration.IntegrationEventId;

import java.time.Instant;
import java.util.UUID;

public class IntegrationEventDeserializationUtils {

    private IntegrationEventDeserializationUtils() {}

    public static IntegrationEventId extractId(JsonNode node) {
        JsonNode idNode = node.get("id");
        return new IntegrationEventId(UUID.fromString(idNode.get("value").asText()));
    }

    public static Instant extractOccurredAt(JsonNode node) {
        return Instant.parse(node.get("occurredAt").asText());
    }
}