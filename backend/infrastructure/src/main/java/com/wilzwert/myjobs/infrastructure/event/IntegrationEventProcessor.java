package com.wilzwert.myjobs.infrastructure.event;

import com.wilzwert.myjobs.core.domain.shared.event.integration.IntegrationEvent;
import lombok.NonNull;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public interface IntegrationEventProcessor extends ItemProcessor<IntegrationEvent, IntegrationEvent> {

    @Override
    IntegrationEvent process(@NonNull IntegrationEvent event) throws Exception;
}