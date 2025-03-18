package com.wilzwert.myjobs.infrastructure.persistence.mongo.mapper;


import com.wilzwert.myjobs.core.application.command.CreateActivityCommand;
import com.wilzwert.myjobs.core.application.command.UpdateActivityCommand;
import com.wilzwert.myjobs.core.domain.model.Activity;
import com.wilzwert.myjobs.infrastructure.api.mapper.EntityMapper;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.*;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.entity.MongoActivity;
import org.mapstruct.Mapper;

/**
 * @author Wilhelm Zwertvaegher
 * Date:12/03/2025
 * Time:15:48
 */

@Mapper(componentModel = "spring", uses = IdMapper.class)
public interface ActivityMapper extends EntityMapper<Activity, MongoActivity, CreateActivityRequest, CreateActivityCommand, UpdateActivityRequest, UpdateActivityCommand, ActivityResponse> {
}