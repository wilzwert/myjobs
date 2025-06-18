package com.wilzwert.myjobs.infrastructure.event.kafka;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Provides server related configuration properties
 * @author Wilhelm Zwertvaegher
 *
 */
@ConfigurationProperties(prefix = "application.kafka")
@Getter
@Setter
public class KafkaProperties {
    private String topicPrefix;
}