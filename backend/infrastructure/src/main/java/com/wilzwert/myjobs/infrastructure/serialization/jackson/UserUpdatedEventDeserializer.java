package com.wilzwert.myjobs.infrastructure.serialization.jackson;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import com.wilzwert.myjobs.core.domain.model.user.UserId;
import com.wilzwert.myjobs.core.domain.model.user.event.integration.UserUpdatedEvent;
import com.wilzwert.myjobs.core.domain.shared.event.integration.IntegrationEventId;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class UserUpdatedEventDeserializer extends JacksonIntegrationEventDeserializer<UserUpdatedEvent> {

    public UserUpdatedEventDeserializer() {
        super(UserUpdatedEvent.class);
    }

    @Override
    public UserUpdatedEvent deserialize(JsonParser p, DeserializationContext ctxt) throws JacksonException {
        JsonNode node = p.objectReadContext().readTree(p);

        IntegrationEventId id = IntegrationEventDeserializationUtils.extractId(node);
        Instant occurredAt = IntegrationEventDeserializationUtils.extractOccurredAt(node);

        UserId userId = new UserId(UUID.fromString(node.get("userId").get("value").asString()));

        return new UserUpdatedEvent(id, occurredAt, userId);
    }
}