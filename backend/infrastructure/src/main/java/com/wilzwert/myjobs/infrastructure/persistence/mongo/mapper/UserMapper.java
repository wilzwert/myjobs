package com.wilzwert.myjobs.infrastructure.persistence.mongo.mapper;

import com.wilzwert.myjobs.core.domain.model.user.UserSummary;
import com.wilzwert.myjobs.core.domain.model.user.command.RegisterUserCommand;
import com.wilzwert.myjobs.core.domain.model.user.command.UpdateUserCommand;
import com.wilzwert.myjobs.core.domain.model.user.User;
import com.wilzwert.myjobs.core.domain.model.user.UserId;
import com.wilzwert.myjobs.core.domain.model.user.UserView;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.RegisterUserRequest;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.UpdateUserRequest;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.UserSummaryResponse;
import com.wilzwert.myjobs.infrastructure.mapper.EntityMapper;
import com.wilzwert.myjobs.infrastructure.mapper.EntityViewMapper;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.entity.MongoUser;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.UserResponse;
import org.mapstruct.Mapper;

/**
 * @author Wilhelm Zwertvaegher
 */
@Mapper(componentModel = "spring", uses = {IdMapper.class, JobMapper.class})
public interface UserMapper extends EntityMapper<User, MongoUser, RegisterUserRequest, RegisterUserCommand, UpdateUserRequest, UpdateUserCommand, UserResponse>, EntityViewMapper<UserView, MongoUser, UserResponse> {
    UpdateUserCommand toUpdateCommand(UpdateUserRequest updateUserRequest, UserId userId);

    UserSummaryResponse toResponse(UserSummary userSummary);
}
