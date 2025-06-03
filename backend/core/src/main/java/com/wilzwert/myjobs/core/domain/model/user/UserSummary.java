package com.wilzwert.myjobs.core.domain.model.user;


import com.wilzwert.myjobs.core.domain.model.job.JobStatus;
import com.wilzwert.myjobs.core.domain.model.job.JobStatusFilter;

import java.util.Map;
import java.util.Set;

/**
 * @author Wilhelm Zwertvaegher
 * Date:03/06/2025
 * Time:09:43
 * Represents a User's summary, which consists of metadata about its jobs
 * A related use case gets and returns a user's summary, which may be useful
 * for a client (frontend...) to display only appropriate filters
 */

public final class UserSummary {

    private final int jobsCount;

    private final int activeJobsCount;

    private final int inactiveJobsCount;

    private final int lateJobsCount;

    private final Map<JobStatus, Integer> jobStatuses;

    private final Set<JobStatusFilter> usableJobStatusFilters;

    public UserSummary(int jobsCount, int activeJobsCount, int inactiveJobsCount, int lateJobsCount, Map<JobStatus, Integer> jobStatuses, Set<JobStatusFilter> usableJobStatusFilters) {
        this.jobsCount = jobsCount;
        this.activeJobsCount = activeJobsCount;
        this.inactiveJobsCount = inactiveJobsCount;
        this.lateJobsCount = lateJobsCount;
        this.jobStatuses = jobStatuses;
        this.usableJobStatusFilters = usableJobStatusFilters;
    }

    public int getJobsCount() {
        return jobsCount;
    }

    public int getActiveJobsCount() {
        return activeJobsCount;
    }

    public int getLateJobsCount() {
        return lateJobsCount;
    }

    public int getInactiveJobsCount() {
        return inactiveJobsCount;
    }

    public Map<JobStatus, Integer> getJobStatuses() {
        return jobStatuses;
    }

    public Set<JobStatusFilter> getUsableJobStatusFilters() {
        return usableJobStatusFilters;
    }

    @Override
    public String toString() {
        return "UserSummary [jobsCount=" + jobsCount
                + ", activeJobsCount=" + activeJobsCount
                + ", inactiveJobsCount=" + inactiveJobsCount
                + ", lateJobsCount=" + lateJobsCount
                + ", jobStatuses=" + jobStatuses
                + ", usableJobStatusFilters=" + usableJobStatusFilters
                + "]";
    }
}