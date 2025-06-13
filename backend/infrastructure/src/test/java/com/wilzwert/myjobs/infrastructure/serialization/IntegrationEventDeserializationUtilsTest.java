package com.wilzwert.myjobs.infrastructure.serialization;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wilzwert.myjobs.core.domain.shared.event.integration.IntegrationEventId;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IntegrationEventDeserializationUtilsTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void extractId_shouldReturnValidIntegrationEventId() throws Exception {
        String json = """
            {
              "id": { "value": "11111111-1111-1111-1111-111111111111" }
            }
            """;

        JsonNode node = objectMapper.readTree(json);
        IntegrationEventId result = IntegrationEventDeserializationUtils.extractId(node);

        assertThat(result).isEqualTo(new IntegrationEventId(UUID.fromString("11111111-1111-1111-1111-111111111111")));
    }

    @Test
    void extractOccurredAt_shouldReturnValidInstant() throws Exception {
        String json = """
            {
              "occurredAt": "2025-06-08T12:00:00Z"
            }
            """;

        JsonNode node = objectMapper.readTree(json);
        Instant result = IntegrationEventDeserializationUtils.extractOccurredAt(node);

        assertThat(result).isEqualTo(Instant.parse("2025-06-08T12:00:00Z"));
    }

    @Test
    void extractId_shouldThrowExceptionWhenMissing() throws Exception {
        String json = "{}";

        JsonNode node = objectMapper.readTree(json);

        assertThrows(NullPointerException.class, () -> IntegrationEventDeserializationUtils.extractId(node));
    }

    @Test
    void extractOccurredAt_shouldThrowExceptionWhenInvalidFormat() throws Exception {
        String json = """
            {
              "occurredAt": "not-a-date"
            }
            """;

        JsonNode node = objectMapper.readTree(json);

        assertThrows(Exception.class, () -> IntegrationEventDeserializationUtils.extractOccurredAt(node));
    }
}
