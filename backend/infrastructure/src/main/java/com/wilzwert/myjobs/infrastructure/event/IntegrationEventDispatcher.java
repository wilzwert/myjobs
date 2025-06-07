package com.wilzwert.myjobs.infrastructure.event;

import com.wilzwert.myjobs.core.domain.shared.event.integration.IntegrationEvent;

public interface IntegrationEventDispatcher {
    IntegrationEvent dispatch(IntegrationEvent event);
}
