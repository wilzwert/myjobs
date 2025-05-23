package com.wilzwert.myjobs.core.domain.shared.bulk;


import java.util.List;

/**
 * @author Wilhelm Zwertvaegher
 * Date:15/05/2025
 * Time:12:01
 */
public class UsersJobsBulkResult {
    private final int usersCount;
    private final int jobsCount;
    private final List<String> errors;

    public UsersJobsBulkResult(int usersCount, int jobsCount, List<String> errors) {
        if(usersCount < 1) {
            throw new IllegalArgumentException("usersCount must be greater than 0");
        }
        if(jobsCount < 1) {
            throw new IllegalArgumentException("jobsCount must be greater than 0");
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