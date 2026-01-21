package com.wilzwert.myjobs.infrastructure.serialization.jackson;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import com.wilzwert.myjobs.core.domain.model.job.JobId;
import com.wilzwert.myjobs.core.domain.model.job.JobRating;
import com.wilzwert.myjobs.core.domain.shared.event.integration.IntegrationEventId;
import com.wilzwert.myjobs.core.domain.model.job.event.integration.JobRatingUpdatedEvent;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class JobRatingUpdatedEventDeserializer extends JacksonIntegrationEventDeserializer<JobRatingUpdatedEvent> {

    public JobRatingUpdatedEventDeserializer() {
        super(JobRatingUpdatedEvent.class);
    }

    @Override
    public JobRatingUpdatedEvent deserialize(JsonParser p, DeserializationContext ctxt) throws JacksonException {
        JsonNode node = p.objectReadContext().readTree(p);

        IntegrationEventId id = IntegrationEventDeserializationUtils.extractId(node);
        Instant occurredAt = IntegrationEventDeserializationUtils.extractOccurredAt(node);
        JobRating jobRating = JobRating.of(node.get("jobRating").get("value").asInt());

        JobId jobId = new JobId(UUID.fromString(node.get("jobId").get("value").asString()));

        return new JobRatingUpdatedEvent(id, occurredAt, jobId, jobRating);
    }
}