package com.wilzwert.myjobs.infrastructure.serialization.jackson;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import com.wilzwert.myjobs.core.domain.model.user.UserId;
import com.wilzwert.myjobs.core.domain.model.user.event.integration.UserCreatedEvent;
import com.wilzwert.myjobs.core.domain.shared.event.integration.IntegrationEventId;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class UserCreatedEventDeserializer extends JacksonIntegrationEventDeserializer<UserCreatedEvent> {

    public UserCreatedEventDeserializer() {
        super(UserCreatedEvent.class);
    }

    @Override
    public UserCreatedEvent deserialize(JsonParser p, DeserializationContext ctxt) throws JacksonException {
        JsonNode node = p.objectReadContext().readTree(p);

        IntegrationEventId id = IntegrationEventDeserializationUtils.extractId(node);
        Instant occurredAt = IntegrationEventDeserializationUtils.extractOccurredAt(node);

        UserId userId = new UserId(UUID.fromString(node.get("userId").get("value").asText()));

        return new UserCreatedEvent(id, occurredAt, userId);
    }
}