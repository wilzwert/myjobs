package com.wilzwert.myjobs.core.domain.model.job;

import java.util.List;

/**
 * @author Wilhelm Zwertvaegher
 */
public enum JobStatus {
    CREATED,
    PENDING,
    RELAUNCHED,
    APPLICANT_REFUSED,
    COMPANY_REFUSED,
    ACCEPTED;

    private static final List<JobStatus> active = List.of(CREATED, PENDING, RELAUNCHED);

    private static final List<JobStatus> inactive = List.of(APPLICANT_REFUSED, COMPANY_REFUSED, ACCEPTED);

    public static List<JobStatus> activeStatuses() {
        return active;
    }

    public static List<JobStatus> inactiveStatuses() {
        return inactive;
    }


}
