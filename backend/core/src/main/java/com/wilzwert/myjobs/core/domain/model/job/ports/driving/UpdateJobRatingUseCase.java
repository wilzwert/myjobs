package com.wilzwert.myjobs.core.domain.model.job.ports.driving;


import com.wilzwert.myjobs.core.domain.model.job.command.UpdateJobRatingCommand;
import com.wilzwert.myjobs.core.domain.model.job.Job;

/**
 * @author Wilhelm Zwertvaegher
 */
public interface UpdateJobRatingUseCase {
    Job updateJobRating(UpdateJobRatingCommand command);
}
