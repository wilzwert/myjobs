package com.wilzwert.myjobs.core.domain.model.user.ports.driving;

import com.wilzwert.myjobs.core.domain.model.user.batch.UsersJobsRemindersBatchResult;

import java.util.List;

/**
 * @author Wilhelm Zwertvaegher
 * Date:12/05/2025
 * Time:10:19
 */
public interface SendJobsRemindersUseCase {
    List<UsersJobsRemindersBatchResult> sendJobsReminders(int batchSize);
}
