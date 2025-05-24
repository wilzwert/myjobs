package com.wilzwert.myjobs.core.domain.shared.bulk;


import java.util.List;

/**
 * @author Wilhelm Zwertvaegher
 */
public class UsersJobsBulkResult {
    private final int usersCount;
    private final int jobsCount;
    private final List<String> errors;

    public UsersJobsBulkResult(int usersCount, int jobsCount, List<String> errors) {
        if(usersCount < 0) {
            throw new IllegalArgumentException("users count must be greater than or equal to 0");
        }
        if(jobsCount < 0) {
            throw new IllegalArgumentException("jobs count must be greater than or equal to 0");
        }
        if(errors == null) {
            throw new IllegalArgumentException("errors must not be null");
        }
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