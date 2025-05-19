package com.wilzwert.myjobs.core.domain.model.user.batch;


import com.wilzwert.myjobs.core.domain.shared.batch.UsersJobsBatchResult;

import java.util.List;

/**
 * @author Wilhelm Zwertvaegher
 * Date:15/05/2025
 * Time:12:01
 */

public class UsersJobsRemindersBatchResult extends UsersJobsBatchResult {
    private final int sendErrorsCount;

    private final int saveErrorsCount;

    public UsersJobsRemindersBatchResult(int usersCount, int jobsCount, List<String> errors, int sendErrorsCount, int saveErrorsCount) {
        super(usersCount, jobsCount, errors);
        this.sendErrorsCount = sendErrorsCount;
        this.saveErrorsCount = saveErrorsCount;
    }

    public int getSendErrorsCount() {
        return sendErrorsCount;
    }

    public int getSaveErrorsCount() {
        return saveErrorsCount;
    }
}
