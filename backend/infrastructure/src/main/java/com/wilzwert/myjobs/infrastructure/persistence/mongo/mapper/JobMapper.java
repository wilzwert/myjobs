package com.wilzwert.myjobs.infrastructure.persistence.mongo.mapper;


import com.wilzwert.myjobs.core.domain.command.CreateJobCommand;
import com.wilzwert.myjobs.core.domain.command.UpdateJobCommand;
import com.wilzwert.myjobs.core.domain.model.Job;
import com.wilzwert.myjobs.core.domain.model.JobId;
import com.wilzwert.myjobs.core.domain.model.UserId;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.CreateJobRequest;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.JobResponse;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.UpdateJobRequest;
import com.wilzwert.myjobs.infrastructure.mapper.EntityMapper;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.entity.MongoJob;
import org.mapstruct.Mapper;

/**
 * @author Wilhelm Zwertvaegher
 * Date:12/03/2025
 * Time:15:48
 */
@Mapper(componentModel = "spring", uses = {IdMapper.class, ActivityMapper.class})
public interface JobMapper extends EntityMapper<Job, MongoJob, CreateJobRequest, CreateJobCommand, UpdateJobRequest, UpdateJobCommand, JobResponse> {

    CreateJobCommand toCommand(CreateJobRequest createJobRequest, UserId userId);

    UpdateJobCommand toCommand(UpdateJobRequest updateJobRequest, UserId userId, JobId jobId);
}