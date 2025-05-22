package com.wilzwert.myjobs.core.domain.shared.batch;


import java.util.List;

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