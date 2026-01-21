package com.wilzwert.myjobs.infrastructure.serialization.jackson;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import com.wilzwert.myjobs.core.domain.model.activity.ActivityId;
import com.wilzwert.myjobs.core.domain.model.activity.ActivityType;
import com.wilzwert.myjobs.core.domain.model.activity.event.integration.ActivityCreatedEvent;
import com.wilzwert.myjobs.core.domain.model.job.JobId;
import com.wilzwert.myjobs.core.domain.shared.event.integration.IntegrationEventId;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class ActivityCreatedEventDeserializer extends JacksonIntegrationEventDeserializer<ActivityCreatedEvent> {

    public ActivityCreatedEventDeserializer() {
        super(ActivityCreatedEvent.class);
    }

    @Override
    public ActivityCreatedEvent deserialize(JsonParser p, DeserializationContext ctxt) throws JacksonException {
        JsonNode node = p.objectReadContext().readTree(p);

        IntegrationEventId id = IntegrationEventDeserializationUtils.extractId(node);
        Instant occurredAt = IntegrationEventDeserializationUtils.extractOccurredAt(node);

        JobId jobId = new JobId(UUID.fromString(node.get("jobId").get("value").asString()));
        ActivityId activityId = new ActivityId(UUID.fromString(node.get("activityId").get("value").asString()));
        ActivityType activityType = ActivityType.valueOf(node.get("activityType").asString());

        return new ActivityCreatedEvent(id, occurredAt, jobId, activityId, activityType);
    }
}