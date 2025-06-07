package com.wilzwert.myjobs.infrastructure.event.kafka;

import com.wilzwert.myjobs.core.domain.shared.event.integration.IntegrationEvent;
import com.wilzwert.myjobs.infrastructure.event.IntegrationEventDispatcher;
import org.springframework.stereotype.Component;

@Component
public class KafkaIntegrationEventDispatcher implements IntegrationEventDispatcher {
    @Override
    public IntegrationEvent dispatch(IntegrationEvent event) {
        return null;
    }
}
