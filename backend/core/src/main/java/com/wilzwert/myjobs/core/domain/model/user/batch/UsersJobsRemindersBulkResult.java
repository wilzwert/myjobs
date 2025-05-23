package com.wilzwert.myjobs.core.domain.model.user.batch;


import com.wilzwert.myjobs.core.domain.shared.bulk.UsersJobsBulkResult;

import java.util.List;

/**
 * @author Wilhelm Zwertvaegher
 * Date:15/05/2025
 * Time:12:01
 * A result of a "chunk" of jobs reminders batch
 */

public class UsersJobsRemindersBulkResult extends UsersJobsBulkResult {
    private final int sendErrorsCount;

    private final int saveErrorsCount;

    public UsersJobsRemindersBulkResult(int usersCount, int jobsCount, List<String> errors, int sendErrorsCount, int saveErrorsCount) {
        super(usersCount, jobsCount, errors);
        if(sendErrorsCount < 0) {
            throw new IllegalArgumentException("sendErrorsCount < 0");
        }
        if(saveErrorsCount < 0) {
            throw new IllegalArgumentException("saveErrorsCount < 0");
        }
        this.sendErrorsCount = sendErrorsCount;
        this.saveErrorsCount = saveErrorsCount;
    }

    public int getSendErrorsCount() {
        return sendErrorsCount;
    }

    public int getSaveErrorsCount() {
        return saveErrorsCount;
    }

    @Override
    public String toString() {
        return "UsersJobsRemindersBulkResult [sendErrorsCount=" + sendErrorsCount+ ", saveErrorsCount=" + saveErrorsCount + ", usersCount=" + getUsersCount() + ", jobsCount=" + getJobsCount() + "]";
    }
}
