package com.wilzwert.myjobs.infrastructure.event;

import com.wilzwert.myjobs.core.domain.model.job.JobId;
import com.wilzwert.myjobs.core.domain.shared.event.integration.IntegrationEventId;
import com.wilzwert.myjobs.core.domain.model.job.event.integration.JobUpdatedEvent;
import com.wilzwert.myjobs.core.domain.shared.event.integration.IntegrationEvent;
import com.wilzwert.myjobs.infrastructure.configuration.AbstractBaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class KafkaKafkaIntegrationEventProcessorIT extends AbstractBaseIntegrationTest {

    @Autowired
    private IntegrationEventProcessor processor;

    @Test
    void shouldSendToKafkaAndReturnSameEvent() {
        IntegrationEvent input = new JobUpdatedEvent(IntegrationEventId.generate(), Instant.now(), JobId.generate());
        IntegrationEvent output = assertDoesNotThrow(() -> processor.process(input));
        assertThat(output).isEqualTo(input);
    }
}
