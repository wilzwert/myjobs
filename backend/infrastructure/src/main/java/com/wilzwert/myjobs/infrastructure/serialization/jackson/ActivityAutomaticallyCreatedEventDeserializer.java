package com.wilzwert.myjobs.infrastructure.serialization.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.wilzwert.myjobs.core.domain.model.activity.ActivityId;
import com.wilzwert.myjobs.core.domain.model.activity.ActivityType;
import com.wilzwert.myjobs.core.domain.model.activity.event.integration.ActivityAutomaticallyCreatedEvent;
import com.wilzwert.myjobs.core.domain.model.job.JobId;
import com.wilzwert.myjobs.core.domain.shared.event.integration.IntegrationEventId;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

@Component
public class ActivityAutomaticallyCreatedEventDeserializer extends JacksonIntegrationEventDeserializer<ActivityAutomaticallyCreatedEvent> {

    public ActivityAutomaticallyCreatedEventDeserializer() {
        super(ActivityAutomaticallyCreatedEvent.class);
    }

    @Override
    public ActivityAutomaticallyCreatedEvent deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);

        IntegrationEventId id = IntegrationEventDeserializationUtils.extractId(node);
        Instant occurredAt = IntegrationEventDeserializationUtils.extractOccurredAt(node);

        JobId jobId = new JobId(UUID.fromString(node.get("jobId").get("value").asText()));
        ActivityId activityId = new ActivityId(UUID.fromString(node.get("activityId").get("value").asText()));
        ActivityType activityType = ActivityType.valueOf(node.get("activityType").asText());

        return new ActivityAutomaticallyCreatedEvent(id, occurredAt, jobId, activityId, activityType);
    }
}