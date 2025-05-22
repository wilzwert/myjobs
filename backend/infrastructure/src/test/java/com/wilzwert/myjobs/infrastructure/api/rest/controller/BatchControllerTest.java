package com.wilzwert.myjobs.infrastructure.api.rest.controller;

import com.wilzwert.myjobs.infrastructure.batch.service.SendJobsRemindersBatchRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class BatchControllerTest {

    private SendJobsRemindersBatchRunner batchRunner;
    private BatchController controller;

    @BeforeEach
    void setUp() {
        batchRunner = mock(SendJobsRemindersBatchRunner.class);
        controller = new BatchController(batchRunner, "expected-secret");
    }

    @Test
    void whenSecretIsInvalid_then_shouldReturnForbidden() {
        ResponseEntity<Void> response = controller.runJobsReminders("invalid-secret");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        verifyNoInteractions(batchRunner);
    }

    @Test
    void whenSecretIsValid_then_shouldRunBatchAndReturnOk() {
        ResponseEntity<Void> response = controller.runJobsReminders("expected-secret");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(batchRunner).run();
    }
}
