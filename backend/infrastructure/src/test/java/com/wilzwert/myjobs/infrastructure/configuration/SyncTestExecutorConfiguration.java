package com.wilzwert.myjobs.infrastructure.configuration;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

/**
 * @author Wilhelm Zwertvaegher
 */
@TestConfiguration
public class SyncTestExecutorConfiguration {
    /**
     * In order to ease testing of async event handlers we overwrite Springs default TaskExecutor
     * with an in-thread version. Each invocation takes place in the calling thread.
     *
     * @return an instance of SyncTaskExecutor
     */
    @Bean(name = "taskExecutor")
    @Primary
    TaskExecutor taskExecutor() {
        return new SyncTaskExecutor();
    }
}