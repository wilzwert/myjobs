package com.wilzwert.myjobs.infrastructure.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.ContextConfiguration;

import java.util.concurrent.Executor;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(properties = "application.async.enabled=true")
@ContextConfiguration(classes = AsyncConfig.class)
class AsyncConfigTest {

    @Autowired
    Executor executor;

    @Test
    void shouldLoadAsyncExecutor() {
        assertThat(executor).isInstanceOf(ThreadPoolTaskExecutor.class);
    }
}