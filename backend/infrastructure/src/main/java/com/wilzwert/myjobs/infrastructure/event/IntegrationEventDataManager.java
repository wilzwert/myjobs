package com.wilzwert.myjobs.infrastructure.event;

import com.wilzwert.myjobs.core.domain.shared.event.integration.IntegrationEvent;

import java.util.List;

public interface IntegrationEventDataManager {
    List<IntegrationEvent> findPending();

    IntegrationEvent markAsDispatched(IntegrationEvent event);

    List<? extends IntegrationEvent> markAllAsDispatched(List<? extends IntegrationEvent> events);

    IntegrationEvent markAsError(IntegrationEvent event);
}
