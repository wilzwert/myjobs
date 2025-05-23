package com.wilzwert.myjobs.infrastructure.batch.service;


import com.wilzwert.myjobs.core.domain.model.user.batch.UsersJobsRemindersBulkResult;
import com.wilzwert.myjobs.core.domain.model.user.ports.driving.SendJobsRemindersUseCase;
import com.wilzwert.myjobs.infrastructure.batch.UsersJobsBatchExecutionResult;
import com.wilzwert.myjobs.infrastructure.exception.BatchRunException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Wilhelm Zwertvaegher
 * Date:22/05/2025
 * Time:17:38
 */
@Component
@Slf4j
public class SendJobsRemindersBatchRunner {
    private final SendJobsRemindersUseCase sendJobsRemindersUseCase;

    public SendJobsRemindersBatchRunner(SendJobsRemindersUseCase sendJobsRemindersUseCase) {
        this.sendJobsRemindersUseCase = sendJobsRemindersUseCase;
    }

    public UsersJobsBatchExecutionResult run() {
        try {
            List<UsersJobsRemindersBulkResult> results = sendJobsRemindersUseCase.sendJobsReminders(1);
            int totalSendErrors = results.stream().mapToInt(UsersJobsRemindersBulkResult::getSendErrorsCount).sum();
            int totalSaveErrors = results.stream().mapToInt(UsersJobsRemindersBulkResult::getSaveErrorsCount).sum();
            int totalUsersReminded = results.stream().mapToInt(UsersJobsRemindersBulkResult::getUsersCount).sum();
            int totalJobsReminded = results.stream().mapToInt(UsersJobsRemindersBulkResult::getJobsCount).sum();
            if (totalSendErrors > 0 || totalSaveErrors > 0) {
                log.warn("SendJobReminders batch run : {} chunks, {} send errors, {} save errors",
                        results.size(), totalSaveErrors, totalSendErrors);
            }
            log.info("SendJobReminders batch run : {} chunks, {} users, {} jobs",
                    results.size(), totalUsersReminded, totalJobsReminded);
            return new UsersJobsBatchExecutionResult(results.size(), totalUsersReminded, totalJobsReminded, totalSendErrors, totalSaveErrors);
        } catch (Exception e) {
            log.error("SendJobReminders batch threw an exception", e);
            throw new BatchRunException("SendJobReminders batch failed", e);
        }
    }
}