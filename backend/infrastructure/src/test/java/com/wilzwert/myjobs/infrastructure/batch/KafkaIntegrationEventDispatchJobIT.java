package com.wilzwert.myjobs.infrastructure.batch;

import com.wilzwert.myjobs.infrastructure.configuration.AbstractBaseIntegrationTest;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.entity.EventStatus;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.entity.MongoIntegrationEvent;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.repository.MongoIntegrationEventRepository;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.test.JobOperatorTestUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class KafkaIntegrationEventDispatchJobIT extends AbstractBaseIntegrationTest {
    @Autowired
    private JobOperatorTestUtils jobOperatorTestUtils;

    @Autowired
    private MongoIntegrationEventRepository repository;

    @Autowired
    private Job integrationEventDispatchJob;

    @Test
    void testIntegrationEventDispatchJob() throws Exception {

        jobOperatorTestUtils.setJob(integrationEventDispatchJob);
        JobExecution jobExecution = jobOperatorTestUtils.startJob();

        assertThat(jobExecution.getExitStatus().getExitCode()).isEqualTo("COMPLETED");

        List<MongoIntegrationEvent> remaining = repository.findByStatus(EventStatus.PENDING);
        assertThat(remaining).isEmpty();
    }
}
