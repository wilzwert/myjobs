package com.wilzwert.myjobs.infrastructure.event;

import com.wilzwert.myjobs.core.domain.shared.event.integration.IntegrationEvent;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.entity.EventStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@Component
@Slf4j
public class IntegrationEventReader implements ItemReader<IntegrationEvent> {

    private final IntegrationEventDataManager dataManager;

    private Iterator<IntegrationEvent> currentBatchIterator = Collections.emptyIterator();

    public IntegrationEventReader(IntegrationEventDataManager eventDataManager) {
        this.dataManager = eventDataManager;
    }

    @Override
    public IntegrationEvent read()  {
        if (!currentBatchIterator.hasNext()) {
            log.info("Finding pending integration events");
            List<IntegrationEvent> batch = dataManager.findPending();
            if (batch.isEmpty()) {
                return null;  // end batch
            }

            dataManager.markAllAs(batch, EventStatus.IN_PROGRESS);

            currentBatchIterator = batch.iterator();
            log.info("Found {} pending integration events", batch.size());
        }
        return currentBatchIterator.next();
    }
}