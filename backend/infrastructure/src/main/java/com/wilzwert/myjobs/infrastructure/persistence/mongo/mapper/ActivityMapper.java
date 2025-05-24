package com.wilzwert.myjobs.infrastructure.persistence.mongo.mapper;


import com.wilzwert.myjobs.core.domain.model.activity.command.CreateActivityCommand;
import com.wilzwert.myjobs.core.domain.model.activity.command.UpdateActivityCommand;
import com.wilzwert.myjobs.core.domain.model.activity.Activity;
import com.wilzwert.myjobs.core.domain.model.job.JobId;
import com.wilzwert.myjobs.core.domain.model.user.UserId;
import com.wilzwert.myjobs.infrastructure.mapper.EntityMapper;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.*;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.entity.MongoActivity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * @author Wilhelm Zwertvaegher
 */

@Mapper(componentModel = "spring", uses = IdMapper.class)
public interface ActivityMapper extends EntityMapper<Activity, MongoActivity, CreateActivityRequest, CreateActivityCommand, UpdateActivityRequest, UpdateActivityCommand, ActivityResponse> {
    @Mapping(source = "createActivityRequest.type", target = "activityType")
    CreateActivityCommand toCommand(CreateActivityRequest createActivityRequest, UserId userId, JobId jobId);
}