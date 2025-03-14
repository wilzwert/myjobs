package com.wilzwert.myapps.infrastructure.persistence.mongo.mapper;


import com.wilzwert.myapps.domain.command.RegisterUserCommand;
import com.wilzwert.myapps.domain.model.User;
import com.wilzwert.myapps.infrastructure.api.rest.dto.RegisterUserRequest;
import com.wilzwert.myapps.infrastructure.mapper.EntityMapper;
import com.wilzwert.myapps.infrastructure.persistence.mongo.entity.MongoUser;
import com.wilzwert.myapps.infrastructure.api.rest.dto.UserResponse;
import org.mapstruct.Mapper;

/**
 * @author Wilhelm Zwertvaegher
 * Date:12/03/2025
 * Time:15:48
 */
@Mapper(componentModel = "spring")
public interface UserMapper extends EntityMapper<User, MongoUser, RegisterUserRequest, RegisterUserCommand, UserResponse> {

}
