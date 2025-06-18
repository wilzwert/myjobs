package com.wilzwert.myjobs.infrastructure.event;

import com.wilzwert.myjobs.core.domain.shared.event.integration.IntegrationEvent;
import com.wilzwert.myjobs.infrastructure.configuration.AbstractBaseIntegrationTest;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.entity.EventStatus;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.repository.MongoIntegrationEventRepository;
import org.junit.jupiter.api.Test;
import org.springframework.batch.item.Chunk;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class KafkaIntegrationEventWriterIT extends AbstractBaseIntegrationTest {

    @Autowired
    private IntegrationEventDataManager dataManager;

    @Autowired
    private MongoIntegrationEventRepository repository;

    @Autowired
    private IntegrationEventWriter writer;

    @Test
    void shouldUpdateStatusToSent() {
        IntegrationEvent e = dataManager.findPending().getFirst();
        writer.write(Chunk.of(e));

        var mongoEvent = repository.findById(e.getId().value()).orElseThrow();
        assertThat(mongoEvent.getStatus()).isEqualTo(EventStatus.DISPATCHED);
    }
}
