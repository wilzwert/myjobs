package com.wilzwert.myjobs.infrastructure.api.rest.controller;

import com.wilzwert.myjobs.infrastructure.api.rest.dto.UsersJobsBatchExecutionResultResponse;
import com.wilzwert.myjobs.infrastructure.batch.BatchRunException;
import com.wilzwert.myjobs.infrastructure.batch.UsersJobsBatchExecutionResult;
import com.wilzwert.myjobs.infrastructure.mapper.UsersJobsBatchResultMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * @author Wilhelm Zwertvaegher
 * Controller used to trigger batches in a non "batch-capable" environment by HTTP requests
 * It should not be used when batches can be batched or run with @Scheduled
 * Requests are authorized via {@link com.wilzwert.myjobs.infrastructure.security.internal.InternalEndpointAuthorization}
 * For now we use a specific header to authenticate requests but in "real life" we should use a more secure way
 */

@RestController
@RequestMapping("/internal")
@Slf4j
public class InternalController {


    private final JobOperator jobOperator;

    private final Job jobReminderJob;

    private final Job integrationEventDispatchJob;

    private final UsersJobsBatchResultMapper usersJobsBatchResultMapper;



    public InternalController(
            JobOperator jobOperator,
            Job jobReminderJob,
            Job integrationEventDispatchJob,
            UsersJobsBatchResultMapper usersJobsBatchResultMapper) {
        this.jobOperator = jobOperator;
        this.jobReminderJob = jobReminderJob;
        this.integrationEventDispatchJob = integrationEventDispatchJob;
        this.usersJobsBatchResultMapper = usersJobsBatchResultMapper;
    }

    @PostMapping("/jobs-reminders-batch")
    public UsersJobsBatchExecutionResultResponse runJobsReminders() {
        JobParameters params = new JobParametersBuilder()
                .addString("run.id", UUID.randomUUID().toString(), true)
                .toJobParameters();
        try {
            JobExecution execution = jobOperator.start(jobReminderJob, params);
            UsersJobsBatchExecutionResult result = (UsersJobsBatchExecutionResult) execution
                    .getExecutionContext()
                    .get("usersJobsBatchExecutionResult");
            log.info("Job reminders run by http, started at {}, ended at {}, exited with {}", execution.getStartTime(), execution.getEndTime(), execution.getExitStatus());
            return usersJobsBatchResultMapper.toResponse(result);
        }
        catch (Exception e) {
            log.error("Job reminders run by http threw an exception {}", e.getMessage());
            throw new BatchRunException("Job reminders threw an exception", e);
        }
    }

    @PostMapping("/integration-events-batch")
    public void runIntegrationEvents() {
        JobParameters params = new JobParametersBuilder()
                .addString("run.id", UUID.randomUUID().toString(), true)
                .toJobParameters();
        try {
            JobExecution execution = jobOperator.start(integrationEventDispatchJob, params);
            log.info("Job integration events run by http, started at {}, ended at {}, exited with {}", execution.getStartTime(), execution.getEndTime(), execution.getExitStatus());
        }
        catch (Exception e) {
            log.error("Job integration events run by http threw an exception {}", e.getMessage());
            throw new BatchRunException("Job integration events threw an exception", e);
        }
    }
}
