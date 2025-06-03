package com.wilzwert.myjobs.infrastructure.api.rest.dto;


import com.wilzwert.myjobs.core.domain.model.job.JobStatus;
import com.wilzwert.myjobs.core.domain.model.job.JobStatusFilter;
import lombok.*;

import java.util.Map;
import java.util.Set;

/**
 * @author Wilhelm Zwertvaegher
 */

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserSummaryResponse {
    private int jobsCount;

    private int activeJobsCount;

    private int inactiveJobsCount;

    private int lateJobsCount;

    private Map<JobStatus, Integer> jobStatuses;

    private Set<JobStatusFilter> usableJobStatusFilters;
}
