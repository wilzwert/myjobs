package com.wilzwert.myjobs.core.domain.model.user.ports.driving;

import com.wilzwert.myjobs.core.domain.model.user.batch.UsersJobsRemindersBulkResult;

import java.util.List;

/**
 * @author Wilhelm Zwertvaegher
 */
public interface SendJobsRemindersUseCase {
    List<UsersJobsRemindersBulkResult> sendJobsReminders(int batchSize);
}
