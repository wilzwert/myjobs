package com.wilzwert.myjobs.infrastructure.event.kafka;

import com.wilzwert.myjobs.core.domain.model.job.JobId;
import com.wilzwert.myjobs.core.domain.shared.event.integration.IntegrationEvent;
import com.wilzwert.myjobs.core.domain.shared.event.integration.IntegrationEventId;
import com.wilzwert.myjobs.core.domain.model.job.event.integration.JobUpdatedEvent;
import com.wilzwert.myjobs.infrastructure.configuration.AbstractBaseIntegrationTest;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class KafkaIntegrationEventProcessorIT extends AbstractBaseIntegrationTest {

    @Value("${application.kafka.topic-prefix}")
    private String kafkaTopicPrefix;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.properties.sasl.jaas.config}")
    private String jaasConfig;

    @Value("${spring.kafka.properties.security.protocol}")
    private String securityProtocol;

    @Value("${spring.kafka.properties.ssl.endpoint.identification.algorithm}")
    private String identificationAlgorithm;

    @Value("${spring.kafka.properties.sasl.mechanism}")
    private String saslMechanism;

    @Autowired
    private KafkaIntegrationEventProcessor processor;

    private KafkaConsumer<String, KafkaIntegrationEvent> consumer;

    @BeforeAll
    void setupConsumer() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, securityProtocol);
        props.put(SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, identificationAlgorithm);
        props.put(SaslConfigs.SASL_MECHANISM, saslMechanism);
        props.put(SaslConfigs.SASL_JAAS_CONFIG, jaasConfig);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "myjobs_integration");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonDeserializer.class);

        consumer = new KafkaConsumer<>(
                props,
                new StringDeserializer(),
                new JacksonJsonDeserializer<>(KafkaIntegrationEvent.class)
        );
        consumer.subscribe(List.of(kafkaTopicPrefix+"jobs"));
    }

    @AfterAll
    void tearDown() {
        if (consumer != null) {
            consumer.close();
        }
    }

    @Test
    void shouldSendEventToKafkaTopic() throws Exception {
        IntegrationEvent event = new JobUpdatedEvent(
                new IntegrationEventId(UUID.randomUUID()),
                Instant.now(),
                new JobId(UUID.randomUUID())
        );

        processor.process(event);

        Iterable<ConsumerRecord<String, KafkaIntegrationEvent>> topicRecords = KafkaTestUtils.getRecords(consumer, Duration.ofSeconds(5)).records(kafkaTopicPrefix+"jobs");
        Stream<ConsumerRecord<String, KafkaIntegrationEvent>> recordStream =
                StreamSupport.stream(topicRecords.spliterator(), false);

        ConsumerRecord<String, KafkaIntegrationEvent>  kafkaRecord = recordStream
                .filter(r ->
                    r.value().payload().contains(event.getId().value().toString())
                    && r.value().type().equals("JobUpdatedEvent")
                )
                .findFirst()
                .orElseThrow(() -> new AssertionError("Expected message not found"));

        assertThat(kafkaRecord).isNotNull();
        KafkaIntegrationEvent kafkaEvent = kafkaRecord.value();
        assertThat(kafkaEvent).isNotNull();
        assertThat(kafkaRecord.value().type()).isEqualTo("JobUpdatedEvent");
        assertThat(kafkaEvent.payload()).contains(event.getId().value().toString());
    }
}