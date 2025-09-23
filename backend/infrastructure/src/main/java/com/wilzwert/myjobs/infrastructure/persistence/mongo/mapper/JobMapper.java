package com.wilzwert.myjobs.infrastructure.persistence.mongo.mapper;


import com.wilzwert.myjobs.core.domain.model.job.command.CreateJobCommand;
import com.wilzwert.myjobs.core.domain.model.job.command.UpdateJobFullCommand;
import com.wilzwert.myjobs.core.domain.model.job.command.UpdateJobRatingCommand;
import com.wilzwert.myjobs.core.domain.model.job.EnrichedJob;
import com.wilzwert.myjobs.core.domain.model.job.Job;
import com.wilzwert.myjobs.core.domain.model.job.JobId;
import com.wilzwert.myjobs.core.domain.model.user.UserId;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.job.*;
import com.wilzwert.myjobs.infrastructure.mapper.EnrichedEntityMapper;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.entity.MongoJob;
import org.mapstruct.Mapper;

/**
 * @author Wilhelm Zwertvaegher
 */
@Mapper(componentModel = "spring", uses = {IdMapper.class, ActivityMapper.class, JobRatingMapper.class})
public interface JobMapper extends EnrichedEntityMapper<Job, MongoJob, CreateJobRequest, CreateJobCommand, UpdateJobRequest, UpdateJobFullCommand, JobResponse, EnrichedJob> {

    CreateJobCommand toCommand(CreateJobRequest createJobRequest, UserId userId);

    UpdateJobFullCommand toCommand(UpdateJobRequest updateJobRequest, UserId userId, JobId jobId);

    UpdateJobRatingCommand toCommand(UpdateJobRatingRequest updateJobStatusRequest, UserId userId, JobId jobId);

    @Override
    default JobResponse toEnrichedResponse(EnrichedJob extended) {
        JobResponse jobResponse = toResponse(extended.job());
        jobResponse.setFollowUpLate(extended.isFollowUpLate());
        return jobResponse;
    }
}