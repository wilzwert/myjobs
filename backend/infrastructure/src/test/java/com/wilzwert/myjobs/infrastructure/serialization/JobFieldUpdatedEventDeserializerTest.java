package com.wilzwert.myjobs.infrastructure.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.wilzwert.myjobs.core.domain.model.job.JobId;
import com.wilzwert.myjobs.core.domain.model.job.command.UpdateJobFieldCommand;
import com.wilzwert.myjobs.core.domain.shared.event.integration.IntegrationEventId;
import com.wilzwert.myjobs.core.domain.model.job.event.integration.JobFieldUpdatedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JobFieldUpdatedEventDeserializerTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(JobFieldUpdatedEvent.class, new JobFieldUpdatedEventDeserializer());
        objectMapper.registerModule(module);
    }

    @Test
    void shouldDeserializeValidJson() throws Exception {
        String json = """
            {
              "id": { "value": "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa" },
              "occurredAt": "2025-06-08T14:00:00Z",
              "jobId": { "value": "bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb" },
              "field": "TITLE"
            }
            """;

        JobFieldUpdatedEvent event = objectMapper.readValue(json, JobFieldUpdatedEvent.class);

        assertThat(event).isNotNull();
        assertThat(event.getId()).isEqualTo(new IntegrationEventId(UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa")));
        assertThat(event.getOccurredAt()).isEqualTo(Instant.parse("2025-06-08T14:00:00Z"));
        assertThat(event.getJobId()).isEqualTo(new JobId(UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb")));
        assertThat(event.getField()).isEqualTo(UpdateJobFieldCommand.Field.TITLE);
    }

    @Test
    void shouldFailDeserializationWhenFieldIsMissing() {
        String json = """
            {
              "id": { "value": "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa" },
              "occurredAt": "2025-06-08T14:00:00Z",
              "jobId": { "value": "bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb" }
            }
            """;

        assertThrows(Exception.class, () -> objectMapper.readValue(json, JobFieldUpdatedEvent.class));
    }
}
