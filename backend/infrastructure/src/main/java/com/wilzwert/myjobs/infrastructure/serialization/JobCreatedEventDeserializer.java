package com.wilzwert.myjobs.infrastructure.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.wilzwert.myjobs.core.domain.model.job.JobId;
import com.wilzwert.myjobs.core.domain.model.job.event.integration.IntegrationEventId;
import com.wilzwert.myjobs.core.domain.model.job.event.integration.JobCreatedEvent;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

public class JobCreatedEventDeserializer extends StdDeserializer<JobCreatedEvent> {

    public JobCreatedEventDeserializer() {
        super(JobCreatedEvent.class);
    }

    @Override
    public JobCreatedEvent deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);

        IntegrationEventId id = IntegrationEventDeserializationUtils.extractId(node);
        Instant occurredAt = IntegrationEventDeserializationUtils.extractOccurredAt(node);

        JobId jobId = new JobId(UUID.fromString(node.get("jobId").get("value").asText()));

        return new JobCreatedEvent(id, occurredAt, jobId);
    }
}
