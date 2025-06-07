package com.wilzwert.myjobs.infrastructure.event;

import com.wilzwert.myjobs.core.domain.shared.event.integration.IntegrationEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class IntegrationEventWriter implements ItemWriter<IntegrationEvent> {

    private final IntegrationEventDataManager eventDataManager;

    IntegrationEventWriter(IntegrationEventDataManager eventDataManager) {
        this.eventDataManager = eventDataManager;
    }

    @Override
    public void write(Chunk<? extends IntegrationEvent> items) throws Exception {
        log.info("Marking {} integration events as dispatched", items.getItems().size());
        eventDataManager.markAllAsDispatched(items.getItems());
        log.info("Integration events marked as dispatched");
    }
}
