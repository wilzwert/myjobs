package com.wilzwert.myjobs.infrastructure.api.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wilzwert.myjobs.core.domain.model.job.Job;
import com.wilzwert.myjobs.core.domain.model.job.JobId;
import com.wilzwert.myjobs.core.domain.model.job.JobRating;
import com.wilzwert.myjobs.core.domain.model.job.JobStatus;
import com.wilzwert.myjobs.core.domain.model.job.ports.driven.JobDataManager;
import com.wilzwert.myjobs.core.domain.shared.validation.ErrorCode;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.ErrorResponse;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.ValidationErrorResponse;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.job.*;
import com.wilzwert.myjobs.infrastructure.configuration.AbstractBaseIntegrationTest;
import com.wilzwert.myjobs.infrastructure.security.service.JwtService;
import com.wilzwert.myjobs.infrastructure.utility.IntegrationEventUtility;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// TODO : find a way to do some useful and meaningful IT on the /api/jobs/metadata API endpoint

@AutoConfigureMockMvc
class PatchJobControllerIT extends AbstractBaseIntegrationTest  {
    private static final String JOBS_URL = "/api/jobs";

    private static final String JOB_FOR_TEST_ID =  "77777777-7777-7777-7777-123456789012";
    private static final String JOB_FOR_TEST_URL = JOBS_URL+"/"+JOB_FOR_TEST_ID;

    // id for the User to use for get /api/jobs tests
    private static final String USER_FOR_JOBS_TEST_ID = "abcd1234-1234-1234-1234-123456789012";

    @Autowired
    private IntegrationEventUtility integrationEventUtility;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JobDataManager jobDataManager;

    Cookie accessTokenCookie;

    @BeforeEach
    void setup() {
        accessTokenCookie = new Cookie("access_token", jwtService.generateToken(USER_FOR_JOBS_TEST_ID));
    }

    @Test
    void whenUnauthenticated_thenShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(patch(JOB_FOR_TEST_URL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenUnsupportedMethod_thenShouldReturnUnsupportedMethod() throws Exception {
        mockMvc.perform(put(JOB_FOR_TEST_URL).cookie(accessTokenCookie))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    void whenRequestBodyEmpty_thenShouldReturnBadRequest() throws Exception {
        mockMvc.perform(patch(JOB_FOR_TEST_URL).cookie(accessTokenCookie))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenJobIdInvalid_thenShouldReturnUnprocessableEntity() throws Exception {
        UpdateJobRequest updateJobRequest = new UpdateJobRequest();
        mockMvc.perform(patch(JOBS_URL+"/invalid").cookie(accessTokenCookie).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateJobRequest)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void whenUnknownRequestFormat_thenShouldReturnBadRequest() throws Exception {
        String json = """
                {
                    "unknown": "some value"
                """;
        mockMvc.perform(patch(JOB_FOR_TEST_URL).cookie(accessTokenCookie).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenJobNotFound_thenShouldReturnBadRequest() throws Exception {
        // we need a fully valid request, because data validation occurs before use case call
        UpdateJobRequest updateJobRequest = new UpdateJobRequest();
        updateJobRequest.setUrl("http://www.example.com/updated");
        updateJobRequest.setTitle("My job [updated]");
        updateJobRequest.setCompany("My company");
        updateJobRequest.setDescription("My job description [updated]");
        updateJobRequest.setProfile("My job profile [updated]");
        updateJobRequest.setComment("My comment [updated]");
        updateJobRequest.setSalary("My job salary [updated]");
        mockMvc.perform(patch(JOBS_URL+"/11111111-1111-1111-1111-111111111111").cookie(accessTokenCookie).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateJobRequest)))
                .andExpect(status().isNotFound());
    }

    @Nested
    class JobControllerUpdateIt {
        @Test
        void whenRequestInvalid_thenShouldReturnUnprocessableEntityWithErrors() throws Exception {
            UpdateJobRequest updateJobRequest = new UpdateJobRequest();
            updateJobRequest.setUrl("invalid-url");

            MvcResult mvcResult = mockMvc.perform(patch(JOB_FOR_TEST_URL).cookie(accessTokenCookie).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateJobRequest)))
                    .andExpect(status().isUnprocessableEntity())
                    .andReturn();

            ErrorResponse errorResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ErrorResponse.class);
            assertThat(errorResponse).isNotNull();
            assertThat(errorResponse.getStatus()).isEqualTo("422");
            assertThat(errorResponse.getMessage()).isEqualTo(ErrorCode.VALIDATION_FAILED.name());
            assertThat(errorResponse.getErrors()).hasSize(3);

            String expectedError = ErrorCode.FIELD_CANNOT_BE_EMPTY.name();
            assertThat(errorResponse.getErrors().get("description")).extracting(ValidationErrorResponse::getCode).containsExactly(expectedError);
            assertThat(errorResponse.getErrors().get("title")).extracting(ValidationErrorResponse::getCode).containsExactly(expectedError);
            assertThat(errorResponse.getErrors().get("url")).extracting(ValidationErrorResponse::getCode).containsExactly(ErrorCode.INVALID_URL.name());
        }

        @Test
        void shouldUpdateJob() throws Exception {
            UpdateJobRequest updateJobRequest = new UpdateJobRequest();
            updateJobRequest.setUrl("http://www.example.com/updated");
            updateJobRequest.setTitle("My job [updated]");
            updateJobRequest.setCompany("My company");
            updateJobRequest.setDescription("My job description [updated]");
            updateJobRequest.setProfile("My job profile [updated]");
            updateJobRequest.setComment("My comment [updated]");
            updateJobRequest.setSalary("My job salary [updated]");

            Instant beforeCall = Instant.now();
            MvcResult result = mockMvc.perform(patch(JOB_FOR_TEST_URL).cookie(accessTokenCookie).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateJobRequest)))
                    .andExpect(status().isOk())
                    .andReturn();

            Instant afterCall = Instant.now();

            JobResponse jobResponse = objectMapper.readValue(result.getResponse().getContentAsString(), JobResponse.class);
            assertThat(jobResponse).isNotNull();

            Instant updatedAt = jobResponse.getUpdatedAt();

            // FIXME this is quite ugly but we have to make sure updatedAt is consistent
            assertThat(updatedAt)
                    .isAfterOrEqualTo(beforeCall)
                    .isBeforeOrEqualTo(afterCall);
            assertThat(jobResponse.getTitle()).isEqualTo("My job [updated]");
            assertThat(jobResponse.getCompany()).isEqualTo("My company");
            assertThat(jobResponse.getDescription()).isEqualTo("My job description [updated]");
            assertThat(jobResponse.getProfile()).isEqualTo("My job profile [updated]");
            assertThat(jobResponse.getComment()).isEqualTo("My comment [updated]");
            assertThat(jobResponse.getSalary()).isEqualTo("My job salary [updated]");


            // let's check the update job is retrievable and consistent
            Job updatedJob = jobDataManager.findById(new JobId(UUID.fromString(JOB_FOR_TEST_ID))).orElse(null);
            assertThat(updatedJob).isNotNull();
            assertThat(updatedJob.getTitle()).isEqualTo("My job [updated]");
            assertThat(updatedJob.getCompany()).isEqualTo("My company");
            assertThat(updatedJob.getDescription()).isEqualTo("My job description [updated]");
            assertThat(updatedJob.getProfile()).isEqualTo("My job profile [updated]");
            assertThat(updatedJob.getComment()).isEqualTo("My comment [updated]");
            assertThat(updatedJob.getSalary()).isEqualTo("My job salary [updated]");

            integrationEventUtility.assertEventCreated("JobUpdatedEvent", JOB_FOR_TEST_ID);
        }
    }

    @Nested
    class JobControllerUpdateFieldIt {
        @Test
        void whenRequestInvalid_thenShouldReturnBadRequestWithErrors() throws Exception {
            String json = """
                {"url": "invalid-url"}
            """;
            MvcResult mvcResult = mockMvc.perform(patch(JOB_FOR_TEST_URL).cookie(accessTokenCookie).contentType(MediaType.APPLICATION_JSON).content(json))
                    .andExpect(status().isUnprocessableEntity())
                    .andReturn();

            ErrorResponse errorResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ErrorResponse.class);
            assertThat(errorResponse).isNotNull();
            assertThat(errorResponse.getStatus()).isEqualTo("422");
            assertThat(errorResponse.getMessage()).isEqualTo(ErrorCode.VALIDATION_FAILED.name());
            assertThat(errorResponse.getErrors()).hasSize(1);

            assertThat(errorResponse.getErrors().get("url")).extracting(ValidationErrorResponse::getCode).containsExactly(ErrorCode.INVALID_URL.name());
        }

        @Test
        void shouldUpdateJob() throws Exception {
            String json = """
                {"title": "My job [updated]"}
            """;

            Instant beforeCall = Instant.now();
            MvcResult result = mockMvc.perform(patch(JOB_FOR_TEST_URL).cookie(accessTokenCookie).contentType(MediaType.APPLICATION_JSON).content(json))
                    .andExpect(status().isOk())
                    .andReturn();

            Instant afterCall = Instant.now();

            JobResponse jobResponse = objectMapper.readValue(result.getResponse().getContentAsString(), JobResponse.class);
            assertThat(jobResponse).isNotNull();

            Instant updatedAt = jobResponse.getUpdatedAt();

            // FIXME this is quite ugly but we have to make sure updatedAt is consistent
            assertThat(updatedAt)
                    .isAfterOrEqualTo(beforeCall)
                    .isBeforeOrEqualTo(afterCall);
            assertThat(jobResponse.getTitle()).isEqualTo("My job [updated]");

            // let's check the update job is retrievable and consistent : only the title should have been updated
            Job updatedJob = jobDataManager.findById(new JobId(UUID.fromString(JOB_FOR_TEST_ID))).orElse(null);
            assertThat(updatedJob).isNotNull();
            assertThat(updatedJob.getTitle()).isEqualTo("My job [updated]");
            assertThat(updatedJob.getCompany()).isEqualTo("Some company");

            integrationEventUtility.assertEventCreated("JobFieldUpdatedEvent", JOB_FOR_TEST_ID, beforeCall);
        }
    }

    @Nested
    class JobControllerUpdateStatusIt {
        @Test
        void whenRequestInvalid_thenShouldReturnBadRequestWithErrors() throws Exception {
            String invalidJson = """
                {
                    "status": "NOT_A_VALID_JOB_STATUS"
                }
            """;

            MvcResult mvcResult = mockMvc.perform(patch(JOB_FOR_TEST_URL).cookie(accessTokenCookie).contentType(MediaType.APPLICATION_JSON).content(invalidJson))
                    .andExpect(status().isUnprocessableEntity())
                    .andReturn();

            ErrorResponse errorResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ErrorResponse.class);

            assertThat(errorResponse).isNotNull();
            assertThat(errorResponse.getStatus()).isEqualTo("422");
            assertThat(errorResponse.getMessage()).isEqualTo(ErrorCode.VALIDATION_FAILED.name());
            assertThat(errorResponse.getErrors()).hasSize(1);
            assertThat(errorResponse.getErrors().get("status")).extracting(ValidationErrorResponse::getCode).containsExactly(ErrorCode.INVALID_VALUE.name());
        }

        @Test
        void shouldUpdateJobStatus() throws Exception {
            // we need a fully valid request, because data validation occurs before use case call
            UpdateJobStatusRequest updateJobStatusRequest = new UpdateJobStatusRequest();
            updateJobStatusRequest.setStatus(JobStatus.RELAUNCHED);

            Instant beforeCall = Instant.now();
            MvcResult result = mockMvc.perform(patch(JOB_FOR_TEST_URL).cookie(accessTokenCookie).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateJobStatusRequest)))
                    .andExpect(status().isOk())
                    .andReturn();

            Instant afterCall = Instant.now();

            JobResponse jobResponse = objectMapper.readValue(result.getResponse().getContentAsString(), JobResponse.class);
            assertThat(jobResponse).isNotNull();

            Instant updatedAt = jobResponse.getUpdatedAt();

            // FIXME this is quite ugly but we have to make sure updatedAt is consistent
            assertThat(updatedAt)
                    .isAfterOrEqualTo(beforeCall)
                    .isBeforeOrEqualTo(afterCall);
            assertThat(jobResponse.getStatus()).isEqualTo(JobStatus.RELAUNCHED);

            // let's check the update job is retrievable and consistent
            Job updatedJob = jobDataManager.findById(new JobId(UUID.fromString(JOB_FOR_TEST_ID))).orElse(null);
            assertThat(updatedJob).isNotNull();
            assertThat(updatedJob.getStatus()).isEqualTo(JobStatus.RELAUNCHED);

            integrationEventUtility.assertEventCreated("JobStatusUpdatedEvent", JOB_FOR_TEST_ID, beforeCall);
        }
    }

    @Nested
    class JobControllerUpdateRatingIT {

        @Test
        void whenRequestInvalid_thenShouldReturnBadRequestWithErrors() throws Exception {
            String invalidJson = """
                {
                    "rating": "NAN"
                }
            """;

            MvcResult mvcResult = mockMvc.perform(patch(JOB_FOR_TEST_URL).cookie(accessTokenCookie).contentType(MediaType.APPLICATION_JSON).content(invalidJson))
                    .andExpect(status().isUnprocessableEntity())
                    .andReturn();

            ErrorResponse errorResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ErrorResponse.class);

            assertThat(errorResponse).isNotNull();
            assertThat(errorResponse.getStatus()).isEqualTo("422");
            assertThat(errorResponse.getMessage()).isEqualTo(ErrorCode.VALIDATION_FAILED.name());
            assertThat(errorResponse.getErrors()).hasSize(1);
            assertThat(errorResponse.getErrors().get("rating")).extracting(ValidationErrorResponse::getCode).containsExactly(ErrorCode.INVALID_VALUE.name());
        }

        @Test
        void shouldUpdateJobRating() throws Exception {
            // we need a fully valid request, because data validation occurs before use case call
            UpdateJobRatingRequest updateJobRatingRequest = new UpdateJobRatingRequest();
            updateJobRatingRequest.setRating(JobRating.of(5));

            Instant beforeCall = Instant.now();
            MvcResult result = mockMvc.perform(patch(JOB_FOR_TEST_URL).cookie(accessTokenCookie).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateJobRatingRequest)))
                    .andExpect(status().isOk())
                    .andReturn();

            Instant afterCall = Instant.now();

            JobResponse jobResponse = objectMapper.readValue(result.getResponse().getContentAsString(), JobResponse.class);
            assertThat(jobResponse).isNotNull();

            Instant updatedAt = jobResponse.getUpdatedAt();

            // FIXME this is quite ugly but we have to make sure updatedAt is consistent
            assertThat(updatedAt)
                    .isAfterOrEqualTo(beforeCall)
                    .isBeforeOrEqualTo(afterCall);
            assertThat(jobResponse.getRating()).isEqualTo(new JobRatingResponse(5));

            // let's check the update job is retrievable and consistent
            Job updatedJob = jobDataManager.findById(new JobId(UUID.fromString(JOB_FOR_TEST_ID))).orElse(null);

            assertThat(updatedJob).isNotNull();
            assertThat(updatedJob.getRating()).isEqualTo(JobRating.of(5));

            integrationEventUtility.assertEventCreated("JobRatingUpdatedEvent", JOB_FOR_TEST_ID, beforeCall);
        }
    }
}
