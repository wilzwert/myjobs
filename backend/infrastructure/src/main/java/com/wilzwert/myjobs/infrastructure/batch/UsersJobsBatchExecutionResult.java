package com.wilzwert.myjobs.infrastructure.batch;


import lombok.Data;

/**
 * @author Wilhelm Zwertvaegher
 */
@Data
public class UsersJobsBatchExecutionResult {
    private final int chunksCount;
    private final int usersCount;
    private final int jobsCount;
    private final int sendErrorsCount;
    private final int saveErrorsCount;
}
