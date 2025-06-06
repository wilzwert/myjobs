package com.wilzwert.myjobs.infrastructure.api.rest.controller;

import com.wilzwert.myjobs.infrastructure.api.rest.dto.UsersJobsBatchExecutionResultResponse;
import com.wilzwert.myjobs.infrastructure.batch.UsersJobsBatchExecutionResult;
import com.wilzwert.myjobs.infrastructure.batch.service.SendJobsRemindersBatchRunner;
import com.wilzwert.myjobs.infrastructure.mapper.UsersJobsBatchResultMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.Instant;

/**
 * @author Wilhelm Zwertvaegher
 * Controller used to trigger batches in a non "batch-capable" environment by HTTP requests
 * It should not be used when batches can be run with @EnableBatchProcessing and/or @Scheduled
 * Requests are authorized via {@link com.wilzwert.myjobs.infrastructure.security.internal.InternalEndpointAuthorization}
 * For now we use a specific header to authenticate requests but in "real life" we should use a more secure way
 */

@RestController
@RequestMapping("/internal")
@Slf4j
public class InternalController {

    private final SendJobsRemindersBatchRunner sendJobsRemindersBatchRunner;

    private final UsersJobsBatchResultMapper usersJobsBatchResultMapper;



    public InternalController(SendJobsRemindersBatchRunner jobsRemindersBatchRunner, UsersJobsBatchResultMapper usersJobsBatchResultMapper) {
        this.sendJobsRemindersBatchRunner = jobsRemindersBatchRunner;
        this.usersJobsBatchResultMapper = usersJobsBatchResultMapper;
    }

    @PostMapping("/jobs-reminders-batch")
    public UsersJobsBatchExecutionResultResponse runJobsReminders() {
        Instant start = Instant.now();
        UsersJobsBatchExecutionResult result = sendJobsRemindersBatchRunner.run();
        Instant end = Instant.now();
        log.info("Job reminders run by http, started at {}, ended at {} (took {} ms}", start, end, Duration.between(start, end).toMillis());
        return usersJobsBatchResultMapper.toResponse(result);
    }
}
