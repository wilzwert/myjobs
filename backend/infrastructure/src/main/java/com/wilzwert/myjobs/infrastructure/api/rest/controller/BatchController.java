package com.wilzwert.myjobs.infrastructure.api.rest.controller;

import com.wilzwert.myjobs.infrastructure.api.rest.dto.UsersJobsBatchExecutionResultResponse;
import com.wilzwert.myjobs.infrastructure.batch.service.SendJobsRemindersBatchRunner;
import com.wilzwert.myjobs.infrastructure.mapper.UsersJobsBatchResultMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Wilhelm Zwertvaegher
 * Controller used to trigger batches in a non "batch-capable" environment by HTTP requests
 * It hould not be used when batches can be run with @EnableBatchProcessing and/or @Scheduled
 */

@RestController
@RequestMapping("/internal")
@Slf4j
public class BatchController {

    private final SendJobsRemindersBatchRunner sendJobsRemindersBatchRunner;

    private final UsersJobsBatchResultMapper usersJobsBatchResultMapper;



    public BatchController(SendJobsRemindersBatchRunner jobsRemindersBatchRunner, UsersJobsBatchResultMapper usersJobsBatchResultMapper) {
        this.sendJobsRemindersBatchRunner = jobsRemindersBatchRunner;
        this.usersJobsBatchResultMapper = usersJobsBatchResultMapper;
    }

    @PostMapping("/jobs-reminders-batch")
    public UsersJobsBatchExecutionResultResponse runJobsReminders() {
        return usersJobsBatchResultMapper.toResponse(sendJobsRemindersBatchRunner.run());
    }
}
