package com.wilzwert.myjobs.core.domain.shared.batch;


import com.wilzwert.myjobs.core.domain.model.user.User;
import com.wilzwert.myjobs.core.domain.shared.bulk.BulkServiceSaveResult;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Wilhelm Zwertvaegher
 * Date:15/05/2025
 * Time:12:01
 */
public class UsersJobsBatchResult {
    private final int usersCount;
    private final int jobsCount;
    private final List<String> errors;

    public UsersJobsBatchResult(int usersCount, int jobsCount, List<String> errors) {
        this.usersCount = usersCount;
        this.jobsCount = jobsCount;
        this.errors = errors;
    }

    public int getUsersCount() {
        return usersCount;
    }

    public int getJobsCount() {
        return jobsCount;
    }

    public List<String> getErrors() {
        return errors;
    }
}