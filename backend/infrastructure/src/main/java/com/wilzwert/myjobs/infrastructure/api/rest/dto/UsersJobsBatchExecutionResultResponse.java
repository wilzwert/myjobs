package com.wilzwert.myjobs.infrastructure.api.rest.dto;


import lombok.Data;

/**
 * @author Wilhelm Zwertvaegher
 * Date:23/05/2025
 * Time:10:26
 */
@Data
public class UsersJobsBatchExecutionResultResponse {
    private final int chunksCount;
    private final int usersCount;
    private final int jobsCount;
    private final int sendErrorsCount;
    private final int saveErrorsCount;
}
