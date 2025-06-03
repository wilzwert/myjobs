package com.wilzwert.myjobs.infrastructure.api.rest.dto;


import com.wilzwert.myjobs.core.domain.model.job.JobStatus;
import lombok.*;

import java.util.Map;

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

    private Map<JobStatus, Integer> jobStatuses;
}
