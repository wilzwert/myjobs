package com.wilzwert.myjobs.infrastructure.api.rest.controller;

import com.wilzwert.myjobs.infrastructure.api.rest.dto.UsersJobsBatchExecutionResultResponse;
import com.wilzwert.myjobs.infrastructure.batch.UsersJobsBatchExecutionResult;
import com.wilzwert.myjobs.infrastructure.batch.BatchRunException;
import com.wilzwert.myjobs.infrastructure.mapper.UsersJobsBatchResultMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.item.ExecutionContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class InternalControllerTest {

    private JobLauncher jobLauncher;
    private Job remindersJob;
    private Job integrationEventsJob;
    private UsersJobsBatchResultMapper mapper;


    private InternalController underTest;

    @BeforeEach
    void setUp() {
        jobLauncher = mock(JobLauncher.class);
        remindersJob = mock(Job.class);
        integrationEventsJob = mock(Job.class);
        mapper = mock(UsersJobsBatchResultMapper.class);
        underTest = new InternalController(jobLauncher, remindersJob, integrationEventsJob, mapper);
    }

    @Test
    void whenRunnerThrowsException_thenShouldThrowBatchRunException() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        when(jobLauncher.run(eq(remindersJob), any(JobParameters.class))).thenThrow(BatchRunException.class);
        assertThrows(BatchRunException.class, underTest::runJobsReminders);
    }

    @Test
    void shouldRunJobsRemindersBatchAndReturnResponse() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        JobExecution jobExecution = mock(JobExecution.class);
        ExecutionContext executionContext = mock(ExecutionContext.class);
        UsersJobsBatchExecutionResult batchExecutionResult = new UsersJobsBatchExecutionResult(2, 10, 20, 1, 3);
        when(executionContext.get(anyString())).thenReturn(batchExecutionResult);
        when(jobExecution.getExecutionContext()).thenReturn(executionContext);
        UsersJobsBatchExecutionResultResponse response = new UsersJobsBatchExecutionResultResponse(2, 10, 20, 1, 3);

        when(jobLauncher.run(eq(remindersJob), any(JobParameters.class))).thenReturn(jobExecution);
        when(mapper.toResponse(batchExecutionResult)).thenReturn(response);

        var resultResponse = underTest.runJobsReminders();

        assertThat(resultResponse).isNotNull().isSameAs(response);
        verify(jobLauncher).run(eq(remindersJob), any(JobParameters.class));
        verify(mapper).toResponse(batchExecutionResult);
    }

    @Test
    void shouldRunIntegrationEventsDispatchBatch() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        JobExecution jobExecution = mock(JobExecution.class);

        when(jobLauncher.run(eq(integrationEventsJob), any(JobParameters.class))).thenReturn(jobExecution);

        assertDoesNotThrow(() -> underTest.runIntegrationEvents());

        verify(jobLauncher).run(eq(integrationEventsJob), any(JobParameters.class));
    }
}