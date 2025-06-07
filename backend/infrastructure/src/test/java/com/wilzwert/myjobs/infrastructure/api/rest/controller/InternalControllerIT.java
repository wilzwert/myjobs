package com.wilzwert.myjobs.infrastructure.api.rest.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.UsersJobsBatchExecutionResultResponse;
import com.wilzwert.myjobs.infrastructure.configuration.AbstractBaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Wilhelm Zwertvaegher
 */
@TestPropertySource(properties = {
        "application.internal.secret=secret"
})
@AutoConfigureMockMvc
public class InternalControllerIT extends AbstractBaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String JOBS_REMINDERS_BATCH_URL = "/internal/jobs-reminders-batch";
    private static final String INTEGRATION_EVENTS_DISPATCH_BATCH_URL = "/internal/integration-events-batch";

    @Test
    void whenSecretIsEmpty_thenShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get(JOBS_REMINDERS_BATCH_URL))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenSecretIsInvalid_thenShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get(JOBS_REMINDERS_BATCH_URL).header("X-Internal-Secret", "invalid"))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenSecretIsValidAndMethodIsNotPost_thenShouldReturnMethod() throws Exception {
        mockMvc.perform(get(JOBS_REMINDERS_BATCH_URL).header("X-Internal-Secret", "secret"))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    void shouldRunJobsRemindersAndReturnResultResponse() throws Exception {

        MvcResult result = mockMvc.perform(post(JOBS_REMINDERS_BATCH_URL).header("X-Internal-Secret", "secret"))
                .andExpect(status().isOk())
                .andReturn();

        UsersJobsBatchExecutionResultResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), UsersJobsBatchExecutionResultResponse.class);
        assertThat(response).isNotNull();

        assertThat(response.getChunksCount()).isEqualTo(1);
        assertThat(response.getUsersCount()).isEqualTo(1);
        assertThat(response.getJobsCount()).isEqualTo(3);
        assertThat(response.getSaveErrorsCount()).isZero();
        assertThat(response.getSendErrorsCount()).isZero();
    }

    @Test
    void shouldRunIntegrationEventsDispatch() throws Exception {
        mockMvc.perform(post(INTEGRATION_EVENTS_DISPATCH_BATCH_URL).header("X-Internal-Secret", "secret"))
                .andExpect(status().isOk());
    }
}