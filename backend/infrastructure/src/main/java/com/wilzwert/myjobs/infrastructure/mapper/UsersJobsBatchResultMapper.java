package com.wilzwert.myjobs.infrastructure.mapper;

import com.wilzwert.myjobs.infrastructure.api.rest.dto.UsersJobsBatchExecutionResultResponse;
import com.wilzwert.myjobs.infrastructure.batch.UsersJobsBatchExecutionResult;
import org.mapstruct.Mapper;

/**
 * @author Wilhelm Zwertvaegher
 * Date:05/23/2025
 *
 */
@Mapper(componentModel = "spring")
public interface UsersJobsBatchResultMapper {
    UsersJobsBatchExecutionResultResponse toResponse(UsersJobsBatchExecutionResult usersJobsBatchResult);
}
