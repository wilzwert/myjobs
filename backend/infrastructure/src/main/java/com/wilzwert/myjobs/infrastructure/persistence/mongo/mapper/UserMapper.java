package com.wilzwert.myjobs.infrastructure.persistence.mongo.mapper;

import com.wilzwert.myjobs.core.application.command.RegisterUserCommand;
import com.wilzwert.myjobs.core.application.command.UpdateUserCommand;
import com.wilzwert.myjobs.core.domain.model.User;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.RegisterUserRequest;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.UpdateUserRequest;
import com.wilzwert.myjobs.infrastructure.api.mapper.EntityMapper;
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
}
