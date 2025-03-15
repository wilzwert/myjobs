package com.wilzwert.myjobs.infrastructure.persistence.mongo.mapper;


import com.wilzwert.myjobs.domain.command.CreateJobCommand;
import com.wilzwert.myjobs.domain.model.Job;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.CreateJobRequest;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.JobResponse;
import com.wilzwert.myjobs.infrastructure.mapper.EntityMapper;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.entity.MongoJob;
import org.mapstruct.Mapper;

import java.util.UUID;

/**
 * @author Wilhelm Zwertvaegher
 * Date:12/03/2025
 * Time:15:48
 */
@Mapper(componentModel = "spring")
public interface JobMapper extends EntityMapper<Job, MongoJob, CreateJobRequest, CreateJobCommand, JobResponse> {
    CreateJobCommand toCommand(CreateJobRequest createJobRequest, UUID userId);
}
