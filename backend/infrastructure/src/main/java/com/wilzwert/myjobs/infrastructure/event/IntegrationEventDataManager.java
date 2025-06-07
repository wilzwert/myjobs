package com.wilzwert.myjobs.infrastructure.event;

import com.wilzwert.myjobs.core.domain.shared.event.integration.IntegrationEvent;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.entity.EventStatus;

import java.util.List;

public interface IntegrationEventDataManager {
    List<IntegrationEvent> findPending();

    List<? extends IntegrationEvent> markAllAs(List<? extends IntegrationEvent> events, EventStatus status);

}
