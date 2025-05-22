package com.wilzwert.myjobs.infrastructure.api.rest.controller;

import com.wilzwert.myjobs.infrastructure.batch.service.SendJobsRemindersBatchRunner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal")
@Slf4j
public class BatchController {

    private String schedulerSecret;
    private final SendJobsRemindersBatchRunner sendJobsRemindersBatchRunner;

    public BatchController(SendJobsRemindersBatchRunner jobsRemindersBatchRunner, @Value("${application.scheduler.secret}") String schedulerSecret) {
        this.sendJobsRemindersBatchRunner = jobsRemindersBatchRunner;
        this.schedulerSecret = schedulerSecret;
    }

    @PostMapping("/jobs-reminders-batch")
    public ResponseEntity<Void> runJobsReminders(@RequestHeader("X-Scheduler-Secret") String headerSecret) {
        if (!schedulerSecret.equals(headerSecret)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        this.sendJobsRemindersBatchRunner.run();

        return ResponseEntity.ok().build();
    }
}
