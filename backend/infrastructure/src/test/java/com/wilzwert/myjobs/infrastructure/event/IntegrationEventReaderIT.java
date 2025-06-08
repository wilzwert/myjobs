package com.wilzwert.myjobs.infrastructure.event;

import com.wilzwert.myjobs.core.domain.shared.event.integration.IntegrationEvent;
import com.wilzwert.myjobs.infrastructure.configuration.AbstractBaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class IntegrationEventReaderIT extends AbstractBaseIntegrationTest {

    @Autowired
    private IntegrationEventReader reader;

    @Test
    void readShouldReturnPendingEvent() throws Exception {
        IntegrationEvent event = reader.read();
        assertThat(event).isNotNull();
    }
}
