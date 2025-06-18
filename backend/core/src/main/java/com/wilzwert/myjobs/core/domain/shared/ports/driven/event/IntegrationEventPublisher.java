package com.wilzwert.myjobs.core.domain.shared.ports.driven.event;

import com.wilzwert.myjobs.core.domain.shared.event.integration.IntegrationEvent;

/**
 * @author Wilhelm Zwertvaegher
 * Date:06/06/2025
 * Time:16:04
 */
public interface IntegrationEventPublisher {
    IntegrationEvent publish(IntegrationEvent event);
}
