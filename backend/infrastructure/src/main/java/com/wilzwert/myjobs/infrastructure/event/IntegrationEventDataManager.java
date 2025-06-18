package com.wilzwert.myjobs.infrastructure.event;

import com.wilzwert.myjobs.core.domain.shared.event.integration.IntegrationEventId;
import com.wilzwert.myjobs.core.domain.shared.event.integration.IntegrationEvent;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.entity.EventStatus;

import java.util.List;
import java.util.Optional;

public interface IntegrationEventDataManager {

    Optional<IntegrationEvent> findById(IntegrationEventId integrationEventId);

    List<IntegrationEvent> findPending();

    List<? extends IntegrationEvent> markAllAs(List<? extends IntegrationEvent> events, EventStatus status);

}
