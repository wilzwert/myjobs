package com.wilzwert.myjobs.infrastructure.persistence.mongo.mapper;


import com.wilzwert.myjobs.core.domain.command.CreateJobCommand;
import com.wilzwert.myjobs.core.domain.command.UpdateJobCommand;
import com.wilzwert.myjobs.core.domain.command.UpdateJobRatingCommand;
import com.wilzwert.myjobs.core.domain.command.UpdateJobStatusCommand;
import com.wilzwert.myjobs.core.domain.model.Job;
import com.wilzwert.myjobs.core.domain.model.JobId;
import com.wilzwert.myjobs.core.domain.model.UserId;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.*;
import com.wilzwert.myjobs.infrastructure.mapper.EntityMapper;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.entity.MongoJob;
import org.mapstruct.Mapper;

/**
 * @author Wilhelm Zwertvaegher
 * Date:12/03/2025
 * Time:15:48
 */
@Mapper(componentModel = "spring", uses = {IdMapper.class, ActivityMapper.class, JobRatingMapper.class})
public interface JobMapper extends EntityMapper<Job, MongoJob, CreateJobRequest, CreateJobCommand, UpdateJobRequest, UpdateJobCommand, JobResponse> {

    CreateJobCommand toCommand(CreateJobRequest createJobRequest, UserId userId);

    UpdateJobCommand toCommand(UpdateJobRequest updateJobRequest, UserId userId, JobId jobId);

    UpdateJobStatusCommand toCommand(UpdateJobStatusRequest updateJobStatusRequest, UserId userId, JobId jobId);

    UpdateJobRatingCommand toCommand(UpdateJobRatingRequest updateJobStatusRequest, UserId userId, JobId jobId);
}