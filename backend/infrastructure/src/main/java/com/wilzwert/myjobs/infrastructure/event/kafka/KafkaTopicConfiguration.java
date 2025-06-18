package com.wilzwert.myjobs.infrastructure.event.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfiguration {


    private final String topicPrefix;

    KafkaTopicConfiguration(@Value(value = "${application.kafka.topic-prefix}") String topicPrefix) {
        this.topicPrefix = topicPrefix;
    }
    @Bean
    public NewTopic topicJob() {
        return new NewTopic(topicPrefix+"jobs", 1, (short) 1);
    }

    @Bean
    public NewTopic topicUser() {
        return new NewTopic(topicPrefix+"users", 1, (short) 1);
    }
}