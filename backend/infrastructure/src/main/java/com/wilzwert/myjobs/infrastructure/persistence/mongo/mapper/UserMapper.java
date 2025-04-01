package com.wilzwert.myjobs.infrastructure.persistence.mongo.mapper;

import com.wilzwert.myjobs.core.domain.command.RegisterUserCommand;
import com.wilzwert.myjobs.core.domain.command.UpdateJobCommand;
import com.wilzwert.myjobs.core.domain.command.UpdateUserCommand;
import com.wilzwert.myjobs.core.domain.model.JobId;
import com.wilzwert.myjobs.core.domain.model.User;
import com.wilzwert.myjobs.core.domain.model.UserId;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.RegisterUserRequest;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.UpdateJobRequest;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.UpdateUserRequest;
import com.wilzwert.myjobs.infrastructure.mapper.EntityMapper;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.entity.MongoUser;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.UserResponse;
import org.mapstruct.Mapper;

/**
 * @author Wilhelm Zwertvaegher
 * Date:12/03/2025
 * Time:15:48
 */
@Mapper(componentModel = "spring", uses = IdMapper.class)
public interface UserMapper extends EntityMapper<User, MongoUser, RegisterUserRequest, RegisterUserCommand, UpdateUserRequest, UpdateUserCommand, UserResponse> {
    UpdateUserCommand toUpdateCommand(UpdateUserRequest updateUserRequest, UserId userId);
}
