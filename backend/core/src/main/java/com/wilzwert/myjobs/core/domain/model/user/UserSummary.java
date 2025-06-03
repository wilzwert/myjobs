package com.wilzwert.myjobs.core.domain.model.user;


import com.wilzwert.myjobs.core.domain.model.job.JobStatus;

import java.util.Map;

/**
 * @author Wilhelm Zwertvaegher
 * Date:03/06/2025
 * Time:09:43
 */

public final class UserSummary {

    private final int jobsCount;

    private final int activeJobsCount;

    private final int inactiveJobsCount;

    private final Map<JobStatus, Integer> jobStatuses;

    public UserSummary(int jobsCount, int activeJobsCount, int inactiveJobsCount, Map<JobStatus, Integer> jobStatuses) {
        this.jobsCount = jobsCount;
        this.activeJobsCount = activeJobsCount;
        this.inactiveJobsCount = inactiveJobsCount;
        this.jobStatuses = jobStatuses;
    }

    public int getJobsCount() {
        return jobsCount;
    }

    public int getActiveJobsCount() {
        return activeJobsCount;
    }

    public int getInactiveJobsCount() {
        return inactiveJobsCount;
    }

    public Map<JobStatus, Integer> getJobStatuses() {
        return jobStatuses;
    }

    @Override
    public String toString() {
        return "UserSummary [jobsCount=" + jobsCount + ", activeJobsCount=" + activeJobsCount + ", inactiveJobsCount="+inactiveJobsCount + ", jobStatuses=" + jobStatuses + "]";
    }
}