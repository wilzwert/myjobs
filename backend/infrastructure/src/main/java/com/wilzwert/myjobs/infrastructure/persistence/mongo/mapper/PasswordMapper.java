package com.wilzwert.myjobs.infrastructure.persistence.mongo.mapper;

import com.wilzwert.myjobs.core.domain.model.user.Password;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PasswordMapper {
    default Password toDomain(String hashedPassword) {
        return new Password(null, hashedPassword);
    }
}
