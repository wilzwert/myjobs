package com.wilzwert.myjobs.infrastructure.batch.service;

import com.wilzwert.myjobs.infrastructure.batch.UsersJobsBatchExecutionResult;
import com.wilzwert.myjobs.infrastructure.configuration.AbstractBaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class SendJobsRemindersBatchRunnerIT extends AbstractBaseIntegrationTest {

    @Autowired
    private SendJobsRemindersBatchRunner batchRunner;

    @Test
    void shouldReturnUsersJobsBatchExecutionResult() {
        var result = batchRunner.run();
        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(UsersJobsBatchExecutionResult.class);
        assertThat(result.getChunksCount()).isEqualTo(1);
        assertThat(result.getUsersCount()).isEqualTo(1);
        assertThat(result.getJobsCount()).isEqualTo(3);
        assertThat(result.getSendErrorsCount()).isEqualTo(0);
        assertThat(result.getSaveErrorsCount()).isEqualTo(0);
    }
}