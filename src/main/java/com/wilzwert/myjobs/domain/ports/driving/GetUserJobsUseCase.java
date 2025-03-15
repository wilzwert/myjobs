package com.wilzwert.myjobs.domain.ports.driving;


import com.wilzwert.myjobs.domain.model.Job;

import java.util.List;
import java.util.UUID;

/**
 * @author Wilhelm Zwertvaegher
 * Date:12/03/2025
 * Time:15:25
 */
public interface GetUserJobsUseCase {
    List<Job> getUserJobs(UUID userId, int page, int size);
}
