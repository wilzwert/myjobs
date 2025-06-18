package com.wilzwert.myjobs.infrastructure.serialization.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.wilzwert.myjobs.core.domain.model.job.JobId;
import com.wilzwert.myjobs.core.domain.model.job.JobStatus;
import com.wilzwert.myjobs.core.domain.shared.event.integration.IntegrationEventId;
import com.wilzwert.myjobs.core.domain.model.job.event.integration.JobStatusUpdatedEvent;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

@Component
public class JobStatusUpdatedEventDeserializer extends JacksonIntegrationEventDeserializer<JobStatusUpdatedEvent> {

    public JobStatusUpdatedEventDeserializer() {
        super(JobStatusUpdatedEvent.class);
    }

    @Override
    public JobStatusUpdatedEvent deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);

        IntegrationEventId id = IntegrationEventDeserializationUtils.extractId(node);
        Instant occurredAt = IntegrationEventDeserializationUtils.extractOccurredAt(node);

        JobId jobId = new JobId(UUID.fromString(node.get("jobId").get("value").asText()));
        JobStatus jobStatus = JobStatus.valueOf(node.get("jobStatus").asText());

        return new JobStatusUpdatedEvent(id, occurredAt, jobId, jobStatus);
    }
}
