package com.wilzwert.myjobs.infrastructure.batch;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;


/**
 * Enable scheduling when possible
 * @author Wilhelm Zwertvaegher
 */
@Configuration
@ConditionalOnProperty(name = "application.batch.enabled", havingValue = "true")
@EnableScheduling
public class SchedulerConfiguration {
}
