package com.wilzwert.myjobs.infrastructure.configuration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.concurrent.Executor;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = AsyncConfig.class)
class AsyncConfigTest {

    @Autowired
    Executor executor;

    @Test
    void shouldLoadAsyncExecutor() {
        assertThat(executor).isInstanceOf(ThreadPoolTaskExecutor.class);
    }
}