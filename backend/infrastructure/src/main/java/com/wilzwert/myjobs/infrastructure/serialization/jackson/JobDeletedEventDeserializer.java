package com.wilzwert.myjobs.infrastructure.serialization.jackson;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import com.wilzwert.myjobs.core.domain.model.job.JobId;
import com.wilzwert.myjobs.core.domain.model.job.event.integration.JobDeletedEvent;
import com.wilzwert.myjobs.core.domain.shared.event.integration.IntegrationEventId;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class JobDeletedEventDeserializer extends JacksonIntegrationEventDeserializer<JobDeletedEvent> {

    public JobDeletedEventDeserializer() {
        super(JobDeletedEvent.class);
    }

    @Override
    public JobDeletedEvent deserialize(JsonParser p, DeserializationContext ctxt) throws JacksonException {
        JsonNode node = p.objectReadContext().readTree(p);

        IntegrationEventId id = IntegrationEventDeserializationUtils.extractId(node);
        Instant occurredAt = IntegrationEventDeserializationUtils.extractOccurredAt(node);

        JobId jobId = new JobId(UUID.fromString(node.get("jobId").get("value").asText()));

        return new JobDeletedEvent(id, occurredAt, jobId);
    }
}