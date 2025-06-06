package com.wilzwert.myjobs.infrastructure.api.rest.controller;

import com.wilzwert.myjobs.infrastructure.api.rest.dto.UsersJobsBatchExecutionResultResponse;
import com.wilzwert.myjobs.infrastructure.batch.UsersJobsBatchExecutionResult;
import com.wilzwert.myjobs.infrastructure.batch.service.SendJobsRemindersBatchRunner;
import com.wilzwert.myjobs.infrastructure.batch.BatchRunException;
import com.wilzwert.myjobs.infrastructure.mapper.UsersJobsBatchResultMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class InternalControllerTest {

    private SendJobsRemindersBatchRunner batchRunner;
    private UsersJobsBatchResultMapper mapper;


    private InternalController underTest;

    @BeforeEach
    void setUp() {
        batchRunner = mock(SendJobsRemindersBatchRunner.class);
        mapper = mock(UsersJobsBatchResultMapper.class);
        underTest = new InternalController(batchRunner, mapper);
    }

    @Test
    void whenRunnerThrowsException_thenShouldThrowBatchRunException() {
        when(batchRunner.run()).thenThrow(BatchRunException.class);

        assertThrows(BatchRunException.class, underTest::runJobsReminders);
    }

    @Test
    void shouldRunBatchAndReturnResponse() {
        UsersJobsBatchExecutionResultResponse response = new UsersJobsBatchExecutionResultResponse(2, 10, 20, 1, 3);
        UsersJobsBatchExecutionResult batchExecutionResult = new UsersJobsBatchExecutionResult(2, 10, 20, 1, 3);

        when(batchRunner.run()).thenReturn(batchExecutionResult);
        when(mapper.toResponse(batchExecutionResult)).thenReturn(response);

        var resultResponse = underTest.runJobsReminders();

        assertThat(resultResponse).isNotNull();
        assertThat(resultResponse).isSameAs(response);
        verify(batchRunner).run();
        verify(mapper).toResponse(batchExecutionResult);

    }
}
