package com.wilzwert.myjobs.infrastructure.serialization.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.wilzwert.myjobs.core.domain.model.job.JobId;
import com.wilzwert.myjobs.core.domain.shared.event.integration.IntegrationEventId;
import com.wilzwert.myjobs.core.domain.model.job.event.integration.JobUpdatedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JobUpdatedEventDeserializerTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(JobUpdatedEvent.class, new JobUpdatedEventDeserializer());
        objectMapper.registerModule(module);
    }

    @Test
    void shouldDeserializeValidJson() throws Exception {
        // Given
        String json = """
            {
              "id": { "value": "abc12345-1234-5678-9123-abcdefabcdef" },
              "occurredAt": "2025-06-08T12:00:00Z",
              "jobId": { "value": "def67890-5678-1234-5678-fedcbafedcba" }
            }
            """;

        // When
        JobUpdatedEvent event = objectMapper.readValue(json, JobUpdatedEvent.class);

        // Then
        assertThat(event).isNotNull();
        assertThat(event.getId()).isEqualTo(new IntegrationEventId(UUID.fromString("abc12345-1234-5678-9123-abcdefabcdef")));
        assertThat(event.getOccurredAt()).isEqualTo(Instant.parse("2025-06-08T12:00:00Z"));
        assertThat(event.getJobId()).isEqualTo(new JobId(UUID.fromString("def67890-5678-1234-5678-fedcbafedcba")));
    }

    @Test
    void shouldFailDeserializationWhenJobIdIsMissing() {
        // Given
        String json = """
            {
              "id": { "value": "abc12345-1234-5678-9123-abcdefabcdef" },
              "occurredAt": "2025-06-08T12:00:00Z"
            }
            """;

        // Then
        assertThrows(Exception.class, () -> objectMapper.readValue(json, JobUpdatedEvent.class));
    }
}
