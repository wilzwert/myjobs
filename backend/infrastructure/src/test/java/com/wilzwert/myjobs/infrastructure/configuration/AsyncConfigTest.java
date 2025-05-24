package com.wilzwert.myjobs.infrastructure.configuration;


import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import com.wilzwert.myjobs.infrastructure.utility.MemoryAppender;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Wilhelm Zwertvaegher
 */

public class AsyncConfigTest {
    @Test
    void shouldConfigureExecutor() {
        AsyncConfig asyncConfig = new AsyncConfig();
        var executor = asyncConfig.getAsyncExecutor();
        assertNotNull(executor);
        assertInstanceOf(ThreadPoolTaskExecutor.class, executor);
        ThreadPoolTaskExecutor threadPoolExecutor = (ThreadPoolTaskExecutor) executor;
        assertEquals(4, threadPoolExecutor.getCorePoolSize());
        assertEquals(10, threadPoolExecutor.getMaxPoolSize());
        assertEquals(100, threadPoolExecutor.getQueueCapacity());
        assertEquals("AsyncExecutor-", threadPoolExecutor.getThreadNamePrefix());
    }

    @Test
    void shouldProvideAsyncUncaughtExceptionHandler() {
        Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        var memoryAppender = new MemoryAppender();
        memoryAppender.setContext((LoggerContext) org.slf4j.LoggerFactory.getILoggerFactory());
        logger.setLevel(Level.INFO);
        logger.addAppender(memoryAppender);
        memoryAppender.start();

        AsyncConfig asyncConfig = new AsyncConfig();
        var handler = asyncConfig.getAsyncUncaughtExceptionHandler();

        Method method = mock(Method.class);
        when(method.getName()).thenReturn("method");
        assertNotNull(handler);
        assertInstanceOf(AsyncUncaughtExceptionHandler.class, handler);
        assertDoesNotThrow(() -> handler.handleUncaughtException(new Throwable(), method, "string"));
        assertThat(memoryAppender.contains("Exception in async method: method", Level.ERROR)).isTrue();

    }
}
