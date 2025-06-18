package com.wilzwert.myjobs.infrastructure.batch.service;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import com.wilzwert.myjobs.core.domain.model.user.batch.UsersJobsRemindersBulkResult;
import com.wilzwert.myjobs.core.domain.model.user.ports.driving.SendJobsRemindersUseCase;
import com.wilzwert.myjobs.infrastructure.batch.BatchRunException;
import com.wilzwert.myjobs.infrastructure.utility.MemoryAppender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class SendJobsRemindersBatchRunnerTest {

    private SendJobsRemindersUseCase sendJobsRemindersUseCase;
    private SendJobsRemindersBatchRunner runner;
    private MemoryAppender memoryAppender;


    @BeforeEach
    void setUp() {
        sendJobsRemindersUseCase = mock(SendJobsRemindersUseCase.class);
        runner = new SendJobsRemindersBatchRunner(sendJobsRemindersUseCase);

        Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        memoryAppender = new MemoryAppender();
        memoryAppender.setContext((LoggerContext) org.slf4j.LoggerFactory.getILoggerFactory());
        logger.setLevel(Level.INFO);
        logger.addAppender(memoryAppender);
        memoryAppender.start();
    }

    @Test
    void shouldLogInfoWhenNoErrors() {
        var result = mock(UsersJobsRemindersBulkResult.class);
        when(result.getSendErrorsCount()).thenReturn(0);
        when(result.getSaveErrorsCount()).thenReturn(0);
        when(result.getUsersCount()).thenReturn(3);
        when(result.getJobsCount()).thenReturn(5);

        when(sendJobsRemindersUseCase.sendJobsReminders(1)).thenReturn(List.of(result));

        runner.run();

        verify(sendJobsRemindersUseCase, times(1)).sendJobsReminders(1);
        assertThat(memoryAppender.contains("SendJobReminders batch run : 1 chunks, 3 users, 5 jobs", Level.INFO)).isTrue();
    }

    @Test
    void shouldLogWarningWhenErrors() {
        var result = mock(UsersJobsRemindersBulkResult.class);
        when(result.getSendErrorsCount()).thenReturn(2);
        when(result.getSaveErrorsCount()).thenReturn(1);

        when(sendJobsRemindersUseCase.sendJobsReminders(1)).thenReturn(List.of(result));

        runner.run();

        verify(sendJobsRemindersUseCase, times(1)).sendJobsReminders(1);
        assertThat(memoryAppender.contains("SendJobReminders batch run : 1 chunks, 1 send errors, 2 save errors", Level.WARN)).isTrue();
    }

    @Test
    void shouldLogErrorWhenExceptionThrown() {
        when(sendJobsRemindersUseCase.sendJobsReminders(1)).thenThrow(new RuntimeException("error"));

        assertThrows(BatchRunException.class, runner::run);

        verify(sendJobsRemindersUseCase, times(1)).sendJobsReminders(1);
        assertThat(memoryAppender.contains("SendJobReminders batch threw an exception", Level.ERROR)).isTrue();
    }
}