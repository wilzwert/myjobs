package com.wilzwert.myjobs.infrastructure.event.kafka;

import com.wilzwert.myjobs.core.domain.model.job.JobId;
import com.wilzwert.myjobs.core.domain.model.job.event.integration.IntegrationEventId;
import com.wilzwert.myjobs.core.domain.model.job.event.integration.JobUpdatedEvent;
import com.wilzwert.myjobs.core.domain.shared.event.integration.IntegrationEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = {"myjobs-jobs"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class KafkaIntegrationEventProcessorIT {

    @Autowired
    private KafkaIntegrationEventProcessor processor;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafka;

    private KafkaConsumer<String, KafkaIntegrationEvent> consumer;

    @BeforeAll
    void setupConsumer() {
        Map<String, Object> configs = KafkaTestUtils.consumerProps("testGroup", "true", embeddedKafka);
        consumer = new KafkaConsumer<>(
                configs,
                new StringDeserializer(),
                new JsonDeserializer<>(KafkaIntegrationEvent.class, false)
        );
        consumer.subscribe(List.of("myjobs-jobs"));
    }

    @AfterAll
    void tearDown() {
        if (consumer != null) {
            consumer.close();
        }
    }

    @Test
    void shouldSendEventToKafkaTopic() throws Exception {
        // Given
        IntegrationEvent event = new JobUpdatedEvent(
                new IntegrationEventId(UUID.randomUUID()),
                Instant.now(),
                new JobId(UUID.randomUUID())
        );

        // When
        processor.process(event);

        // Then
        ConsumerRecord<String, KafkaIntegrationEvent> record = KafkaTestUtils.getSingleRecord(consumer, "myjobs-jobs", Duration.ofSeconds(5));
        assertThat(record).isNotNull();

        KafkaIntegrationEvent kafkaEvent = record.value();
        assertThat(kafkaEvent).isNotNull();
        assertThat(kafkaEvent.type()).isEqualTo("JobUpdatedEvent");
        assertThat(kafkaEvent.payload()).contains(event.getId().value().toString());
    }
}
