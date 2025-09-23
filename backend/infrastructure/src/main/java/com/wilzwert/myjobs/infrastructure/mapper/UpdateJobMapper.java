package com.wilzwert.myjobs.infrastructure.mapper;


import com.wilzwert.myjobs.core.domain.model.job.JobId;
import com.wilzwert.myjobs.core.domain.model.job.command.*;
import com.wilzwert.myjobs.core.domain.model.user.UserId;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.job.*;
import org.mapstruct.Mapper;

/**
 * Maps job update request dtos to domain commands
 * @author Wilhelm Zwertvaegher
 * Date:05/06/2025
 * Time:11:34
 */
@Mapper(componentModel = "spring")
public interface UpdateJobMapper {

    default UpdateJobCommand toCommand(UpdateJobDto request, JobId jobId, UserId userId) {
        return switch (request) {
            case UpdateJobRatingRequest r -> toCommand(r, jobId, userId);
            case UpdateJobRequest j -> toCommand(j, jobId, userId);
            case UpdateJobFieldRequest f -> toCommand(f, jobId, userId);
        };
    }

    default UpdateJobCommand toCommand(UpdateJobFieldRequest request, JobId jobId, UserId userId) {
        UpdateJobFieldCommand.Field field = UpdateJobFieldCommand.Field.fromString(request.getField()).orElseThrow(IllegalArgumentException::new);
        return new UpdateJobFieldCommand(jobId, userId, field, request.getValue());
    }

    UpdateJobFullCommand toCommand(UpdateJobRequest updateJobRequest, JobId jobId, UserId userId);

    UpdateJobRatingCommand toCommand(UpdateJobRatingRequest updateJobRatingRequest, JobId jobId, UserId userId);
}