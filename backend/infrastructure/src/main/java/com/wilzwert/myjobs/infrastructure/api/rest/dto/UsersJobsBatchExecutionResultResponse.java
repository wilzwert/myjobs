package com.wilzwert.myjobs.infrastructure.api.rest.dto;


import lombok.Data;

/**
 * @author Wilhelm Zwertvaegher
 */
@Data
public class UsersJobsBatchExecutionResultResponse {
    private final int chunksCount;
    private final int usersCount;
    private final int jobsCount;
    private final int sendErrorsCount;
    private final int saveErrorsCount;
}
