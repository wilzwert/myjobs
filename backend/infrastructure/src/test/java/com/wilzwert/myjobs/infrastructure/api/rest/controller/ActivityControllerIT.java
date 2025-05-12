package com.wilzwert.myjobs.infrastructure.api.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wilzwert.myjobs.core.domain.model.activity.Activity;
import com.wilzwert.myjobs.core.domain.model.activity.ActivityType;
import com.wilzwert.myjobs.core.domain.model.job.Job;
import com.wilzwert.myjobs.core.domain.model.job.JobId;
import com.wilzwert.myjobs.core.domain.ports.driven.JobService;
import com.wilzwert.myjobs.core.domain.shared.validation.ErrorCode;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.*;
import com.wilzwert.myjobs.infrastructure.configuration.AbstractBaseIntegrationTest;
import com.wilzwert.myjobs.infrastructure.security.service.JwtService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// TODO : find a way to do some useful and meaningful IT on the /api/jobs/metadata API endpoint

@AutoConfigureMockMvc
public class ActivityControllerIT extends AbstractBaseIntegrationTest  {
    private static final String JOB_FOR_TEST_ID =  "77777777-7777-7777-7777-123456789012";
    private static final String TEST_URL = "/api/jobs/"+JOB_FOR_TEST_ID+"/activities";

    // id for the User to use for get tests
    private static final String USER_FOR_JOBS_TEST_ID = "abcd1234-1234-1234-1234-123456789012";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private JobService jobService;

    @Autowired
    private ObjectMapper objectMapper;

    Cookie accessTokenCookie;

    @BeforeEach
    public void setup() {
        accessTokenCookie = new Cookie("access_token", jwtService.generateToken(USER_FOR_JOBS_TEST_ID));
    }

    @Test
    public void whenUnauthenticated_thenShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(post(TEST_URL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void whenRequestBodyEmpty_thenShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post(TEST_URL).cookie(accessTokenCookie))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void whenJobIdInvalid_thenShouldReturnBadRequest() throws Exception {
        CreateActivityRequest createActivityRequest = new CreateActivityRequest();
        mockMvc.perform(post("/api/jobs/invalid/activities").cookie(accessTokenCookie).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(createActivityRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void whenJobNotFound_thenShouldReturnBadRequest() throws Exception {
        // we need a fully valid request, because data validation occurs before use case call
        CreateActivityRequest createActivityRequest = new CreateActivityRequest();
        createActivityRequest.setType(ActivityType.IN_PERSON_INTERVIEW);
        createActivityRequest.setComment("Created in person interview activity");
        mockMvc.perform(post("/api/jobs/11111111-1111-1111-1111-111111111111/activities").cookie(accessTokenCookie).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(createActivityRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void whenRequestInvalid_thenShouldReturnBadRequestWithErrors() throws Exception {
        String invalidJson = """
                {
                    "type": "NOT_A_VALID_ACTIVITY_TYPE"
                }
            """;

        MvcResult mvcResult = mockMvc.perform(post(TEST_URL).cookie(accessTokenCookie).contentType(MediaType.APPLICATION_JSON).content(invalidJson))
                .andExpect(status().isBadRequest())
                .andReturn();

        ErrorResponse errorResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ErrorResponse.class);
        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.getStatus()).isEqualTo("400");
        assertThat(errorResponse.getMessage()).isEqualTo(ErrorCode.VALIDATION_FAILED.name());
        assertThat(errorResponse.getErrors()).hasSize(1);
        assertThat(errorResponse.getErrors().get("type")).containsExactly(new ValidationErrorResponse(ErrorCode.INVALID_VALUE.name()));
    }

    @Test
    public void shouldCreateActivity() throws Exception {
        // we need a fully valid request, because data validation occurs before use case call
        CreateActivityRequest createActivityRequest = new CreateActivityRequest();
        createActivityRequest.setType(ActivityType.IN_PERSON_INTERVIEW);
        createActivityRequest.setComment("Created in person interview activity");

        Instant beforeCall = Instant.now();
        MvcResult result = mockMvc.perform(post(TEST_URL).cookie(accessTokenCookie).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(createActivityRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        Instant afterCall = Instant.now();

        ActivityResponse activityResponse = objectMapper.readValue(result.getResponse().getContentAsString(), ActivityResponse.class);
        assertThat(activityResponse).isNotNull();
        assertThat(activityResponse.getId()).isNotNull();
        assertThat(activityResponse.getType()).isEqualTo(ActivityType.IN_PERSON_INTERVIEW);
        assertThat(activityResponse.getComment()).isEqualTo("Created in person interview activity");

        Instant createdAt = activityResponse.getCreatedAt();

        // FIXME this is quite ugly but we have to make sure updatedAt is consistent
        assertThat(createdAt)
                .isAfterOrEqualTo(beforeCall)
                .isBeforeOrEqualTo(afterCall);

        // activity should be retrievable
        Job job = jobService.findById(new JobId(UUID.fromString(JOB_FOR_TEST_ID))).orElse(null);
        assertThat(job).isNotNull();

        Activity activity = job.getActivities().stream().filter(a -> a.getId().value().equals(activityResponse.getId())).findFirst().orElse(null);
        assertThat(activity).isNotNull();
        assertThat(activity.getComment()).isEqualTo("Created in person interview activity");
        assertThat(activity.getType()).isEqualTo(ActivityType.IN_PERSON_INTERVIEW);
    }
}
