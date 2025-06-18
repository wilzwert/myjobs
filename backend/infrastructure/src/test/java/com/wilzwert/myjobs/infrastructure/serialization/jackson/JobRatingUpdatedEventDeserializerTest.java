package com.wilzwert.myjobs.infrastructure.serialization.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.wilzwert.myjobs.core.domain.model.job.JobId;
import com.wilzwert.myjobs.core.domain.model.job.JobRating;
import com.wilzwert.myjobs.core.domain.shared.event.integration.IntegrationEventId;
import com.wilzwert.myjobs.core.domain.model.job.event.integration.JobRatingUpdatedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JobRatingUpdatedEventDeserializerTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(JobRatingUpdatedEvent.class, new JobRatingUpdatedEventDeserializer());
        objectMapper.registerModule(module);
    }

    @Test
    void shouldDeserializeValidJson() throws Exception {
        String json = """
            {
              "id": { "value": "d5e1f6d6-7c99-4b33-9b99-50fa0e1d8300" },
              "occurredAt": "2025-06-08T10:15:30Z",
              "jobId": { "value": "aa0e84c6-5b42-46f6-8b61-0e5343f0e8b9" },
              "jobRating": { "value": 4 }
            }
            """;

        JobRatingUpdatedEvent event = objectMapper.readValue(json, JobRatingUpdatedEvent.class);

        assertThat(event).isNotNull();
        assertThat(event.getId()).isEqualTo(new IntegrationEventId(UUID.fromString("d5e1f6d6-7c99-4b33-9b99-50fa0e1d8300")));
        assertThat(event.getOccurredAt()).isEqualTo(Instant.parse("2025-06-08T10:15:30Z"));
        assertThat(event.getJobId()).isEqualTo(new JobId(UUID.fromString("aa0e84c6-5b42-46f6-8b61-0e5343f0e8b9")));
        assertThat(event.getJobRating()).isEqualTo(JobRating.of(4));
    }

    @Test
    void shouldFailDeserializationWhenJobRatingIsMissing() {
        String json = """
            {
              "id": { "value": "d5e1f6d6-7c99-4b33-9b99-50fa0e1d8300" },
              "occurredAt": "2025-06-08T10:15:30Z",
              "jobId": { "value": "aa0e84c6-5b42-46f6-8b61-0e5343f0e8b9" }
            }
            """;

        assertThrows(Exception.class, () ->
            objectMapper.readValue(json, JobRatingUpdatedEvent.class)
        );
    }
}
