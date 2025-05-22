package com.wilzwert.myjobs.infrastructure.batch.service;


import com.wilzwert.myjobs.core.domain.model.user.batch.UsersJobsRemindersBatchResult;
import com.wilzwert.myjobs.core.domain.model.user.ports.driving.SendJobsRemindersUseCase;
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

    public void run() {
        try {
            List<UsersJobsRemindersBatchResult> results = sendJobsRemindersUseCase.sendJobsReminders(1);
            int totalSendErrors = results.stream().mapToInt(UsersJobsRemindersBatchResult::getSendErrorsCount).sum();
            int totalSaveErrors = results.stream().mapToInt(UsersJobsRemindersBatchResult::getSaveErrorsCount).sum();
            if (totalSendErrors > 0 || totalSaveErrors > 0) {
                log.warn("SendJobReminders batch run : {} chunks, {} send errors, {} save errors",
                        results.size(), totalSaveErrors, totalSendErrors);
            } else {
                int totalUsersReminded = results.stream().mapToInt(UsersJobsRemindersBatchResult::getUsersCount).sum();
                int totalJobsReminded = results.stream().mapToInt(UsersJobsRemindersBatchResult::getJobsCount).sum();
                log.info("SendJobReminders batch run : {} chunks, {} users, {} jobs",
                        results.size(), totalUsersReminded, totalJobsReminded);
            }
        } catch (Exception e) {
            log.error("SendJobReminders batch threw an exception", e);
        }
    }
}
