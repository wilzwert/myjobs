package com.wilzwert.myjobs.infrastructure.api.rest.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wilzwert.myjobs.core.domain.model.job.Job;
import com.wilzwert.myjobs.core.domain.model.job.JobId;
import com.wilzwert.myjobs.core.domain.model.job.JobRating;
import com.wilzwert.myjobs.core.domain.model.job.JobStatus;
import com.wilzwert.myjobs.core.domain.ports.driven.JobService;
import com.wilzwert.myjobs.core.domain.shared.validation.ErrorCode;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.*;
import com.wilzwert.myjobs.infrastructure.configuration.AbstractBaseIntegrationTest;
import com.wilzwert.myjobs.infrastructure.security.service.JwtService;
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
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// TODO : find a way to do some useful and meaningful IT on the /api/jobs/metadata API endpoint

@AutoConfigureMockMvc
public class JobControllerIT extends AbstractBaseIntegrationTest  {
    private static final String JOBS_URL = "/api/jobs";

    private static final String JOB_FOR_TEST_ID =  "77777777-7777-7777-7777-123456789012";
    private static final String JOB_FOR_TEST_URL = JOBS_URL+"/"+JOB_FOR_TEST_ID;

    // id for the User to use for get /api/jobs tests
    private static final String USER_FOR_JOBS_TEST_ID = "abcd1234-1234-1234-1234-123456789012";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JobService jobService;

    Cookie accessTokenCookie;

    @BeforeEach
    public void setup() {
        accessTokenCookie = new Cookie("access_token", jwtService.generateToken(USER_FOR_JOBS_TEST_ID));
    }

    @Nested
    class JobControllerGetIt {
        @Test
        public void whenUnauthenticated_thenShouldReturnUnauthorized() throws Exception {
            mockMvc.perform(post(JOBS_URL))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        public void shouldGetJobs() throws Exception {
            MvcResult mvcResult = mockMvc.perform(get(JOBS_URL).cookie(accessTokenCookie))
                    .andExpect(status().isOk())
                    .andReturn();

            RestPage<JobResponse> jobResponseRestPage = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<RestPage<JobResponse>>() {});
            assertThat(jobResponseRestPage).isNotNull();
            assertThat(jobResponseRestPage.getContent()).isNotNull();
            assertThat(jobResponseRestPage.getContent()).hasSize(3);
            assertThat(jobResponseRestPage.getTotalElementsCount()).isEqualTo(3);
            assertThat(jobResponseRestPage.getPageSize()).isEqualTo(10);
            assertThat(jobResponseRestPage.getPagesCount()).isEqualTo(1);
            assertThat(jobResponseRestPage.getCurrentPage()).isEqualTo(0);

            List<String> titles = jobResponseRestPage.getContent().stream().map(JobResponse::getTitle).toList();
            // by default, we expect jobs sorted by creation date desc
            assertThat(titles).containsExactly("My third job", "My second job", "My job");
        }

        @Test
        public void shouldGetJobsWithUserPagination() throws Exception {
            MvcResult mvcResult = mockMvc.perform(
                    get(JOBS_URL).cookie(accessTokenCookie)
                            .param("page", "2") // third page
                            .param("itemsPerPage", "1") // 1 Job per page
                )
                .andExpect(status().isOk())
                .andReturn();

            RestPage<JobResponse> jobResponseRestPage = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<RestPage<JobResponse>>() {});
            assertThat(jobResponseRestPage).isNotNull();
            assertThat(jobResponseRestPage.getContent()).isNotNull();
            assertThat(jobResponseRestPage.getContent().size()).isEqualTo(1);
            assertThat(jobResponseRestPage.getTotalElementsCount()).isEqualTo(3);
            assertThat(jobResponseRestPage.getPageSize()).isEqualTo(1);
            assertThat(jobResponseRestPage.getCurrentPage()).isEqualTo(2);
            assertThat(jobResponseRestPage.getPagesCount()).isEqualTo(3);

            List<String> titles = jobResponseRestPage.getContent().stream().map(JobResponse::getTitle).toList();
            // by default, jobs are sorted by creation date desc
            // as we asked for the last (third) page, we should get the first job
            assertThat(titles).containsExactly("My job");
        }

        @Test
        public void shouldGetJobsSortedByRatingDesc() throws Exception {
            MvcResult mvcResult = mockMvc.perform(
                            get(JOBS_URL).cookie(accessTokenCookie)
                                    .param("sort", "rating,desc")
                    )
                    .andExpect(status().isOk())
                    .andReturn();

            RestPage<JobResponse> jobResponseRestPage = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<RestPage<JobResponse>>() {});
            assertThat(jobResponseRestPage).isNotNull();
            assertThat(jobResponseRestPage.getContent()).isNotNull();
            assertThat(jobResponseRestPage.getContent()).hasSize(3);
            assertThat(jobResponseRestPage.getTotalElementsCount()).isEqualTo(3);
            assertThat(jobResponseRestPage.getPageSize()).isEqualTo(10);
            assertThat(jobResponseRestPage.getPagesCount()).isEqualTo(1);
            assertThat(jobResponseRestPage.getCurrentPage()).isEqualTo(0);

            List<String> titles = jobResponseRestPage.getContent().stream().map(JobResponse::getTitle).toList();
            assertThat(titles).containsExactly("My third job", "My job", "My second job");
        }

        @Test
        public void shouldGetJobsSortedByCreatedAtAsc() throws Exception {
            MvcResult mvcResult = mockMvc.perform(
                            get(JOBS_URL).cookie(accessTokenCookie)
                                    .param("sort", "createdAt,asc")
                    )
                    .andExpect(status().isOk())
                    .andReturn();

            RestPage<JobResponse> jobResponseRestPage = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<RestPage<JobResponse>>() {});
            assertThat(jobResponseRestPage).isNotNull();
            assertThat(jobResponseRestPage.getContent()).isNotNull();
            assertThat(jobResponseRestPage.getContent()).hasSize(3);
            assertThat(jobResponseRestPage.getTotalElementsCount()).isEqualTo(3);
            assertThat(jobResponseRestPage.getPageSize()).isEqualTo(10);
            assertThat(jobResponseRestPage.getPagesCount()).isEqualTo(1);
            assertThat(jobResponseRestPage.getCurrentPage()).isEqualTo(0);

            List<String> titles = jobResponseRestPage.getContent().stream().map(JobResponse::getTitle).toList();
            assertThat(titles).containsExactly("My job", "My second job", "My third job");
        }

        @Test
        public void shouldGetJobsFilteredByStatus() throws Exception {
            MvcResult mvcResult = mockMvc.perform(
                            get(JOBS_URL).cookie(accessTokenCookie)
                                    .param("status", "PENDING")
                    )
                    .andExpect(status().isOk())
                    .andReturn();

            RestPage<JobResponse> jobResponseRestPage = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<RestPage<JobResponse>>() {});
            assertThat(jobResponseRestPage).isNotNull();
            assertThat(jobResponseRestPage.getContent()).isNotNull();
            assertThat(jobResponseRestPage.getContent()).hasSize(1);
            assertThat(jobResponseRestPage.getTotalElementsCount()).isEqualTo(1);
            assertThat(jobResponseRestPage.getPageSize()).isEqualTo(10);
            assertThat(jobResponseRestPage.getPagesCount()).isEqualTo(1);
            assertThat(jobResponseRestPage.getCurrentPage()).isEqualTo(0);

            List<String> titles = jobResponseRestPage.getContent().stream().map(JobResponse::getTitle).toList();
            assertThat(titles).containsExactly("My second job");
        }

        @Test
        public void whenJobIdInvalid_thenShouldReturnBadRequest() throws Exception {
            mockMvc.perform(
                    get(JOBS_URL+"/invalid").cookie(accessTokenCookie)
                )
                .andExpect(status().isBadRequest());
        }

        @Test
        public void whenJobNotFound_thenShouldReturnNotfound() throws Exception {
            mockMvc.perform(
                            get(JOBS_URL+"/11111111-1111-1111-1111-111111111111").cookie(accessTokenCookie)
                    )
                    .andExpect(status().isNotFound());
        }

        @Test
        public void shouldGetJob() throws Exception {
            // get the second job, not the one used for almost all other tests
            // we need to check status, activities size
            MvcResult mvcResult = mockMvc.perform(
                            get(JOBS_URL+"/88888888-8888-8888-8888-123456789012").cookie(accessTokenCookie)
                    )
                    .andExpect(status().isOk())
                    .andReturn();

            JobResponse jobResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), JobResponse.class);
            assertThat(jobResponse).isNotNull();
            assertEquals("My second job", jobResponse.getTitle());
            assertEquals(JobStatus.PENDING, jobResponse.getStatus());
            assertEquals(2, jobResponse.getActivities().size());
        }
    }

    @Nested
    class JobControllerCreateIt {

        @Test
        public void whenUnauthenticated_thenShouldReturnUnauthorized() throws Exception {
            mockMvc.perform(post(JOBS_URL))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        public void whenRequestBodyEmpty_thenShouldReturnBadRequest() throws Exception {
            mockMvc.perform(post(JOBS_URL).cookie(accessTokenCookie))
                    .andExpect(status().isBadRequest());
        }

        @Test
        public void whenRequestBodyInvalidJson_thenShouldReturnBadRequest() throws Exception {
            String invalidJson = """
                {
                    "nonexistent": "this field does not exist",
                    "name": "this field does exist
                }
            """;
            mockMvc.perform(post(JOBS_URL).cookie(accessTokenCookie).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(invalidJson)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("message").value(ErrorCode.VALIDATION_FAILED.name()))
            ;
        }

        @Test
        public void whenRequestBodyInvalid_thenShouldReturnBadRequest() throws Exception {
            String invalidJson = """
                {
                    "nonexistent": "this field does not exist",
                    "name": "this field does exist"
                }
            """;
            mockMvc.perform(post(JOBS_URL).cookie(accessTokenCookie).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(invalidJson)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("message").value(ErrorCode.VALIDATION_FAILED.name()))
                    ;
        }

        @Test
        public void whenJobUrlExists_thenShouldReturnConflict() throws Exception {
            CreateJobRequest createJobRequest = new CreateJobRequest();
            createJobRequest.setUrl("http://www.example.com/my-job");
            createJobRequest.setTitle("My new job");
            createJobRequest.setCompany("My new company");
            createJobRequest.setDescription("My new job description");
            createJobRequest.setProfile("My new job profile");
            createJobRequest.setSalary("My new job salary");

            mockMvc.perform(post(JOBS_URL).cookie(accessTokenCookie).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(createJobRequest)))
                    .andExpect(status().isConflict());
        }

        @Test
        public void whenRequestInvalid_thenShouldReturnBadRequestWithErrors() throws Exception {
            CreateJobRequest createJobRequest = new CreateJobRequest();
            createJobRequest.setUrl("invalid-url");

            MvcResult mvcResult = mockMvc.perform(post(JOBS_URL).cookie(accessTokenCookie).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(createJobRequest)))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            ErrorResponse errorResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ErrorResponse.class);
            assertThat(errorResponse).isNotNull();
            assertThat(errorResponse.getStatus()).isEqualTo("400");
            assertThat(errorResponse.getMessage()).isEqualTo(ErrorCode.VALIDATION_FAILED.name());
            assertThat(errorResponse.getErrors()).hasSize(3);

            String expectedError = ErrorCode.FIELD_CANNOT_BE_EMPTY.name();
            assertThat(errorResponse.getErrors().get("description")).extracting(ValidationErrorResponse::getCode).containsExactly(expectedError);
            assertThat(errorResponse.getErrors().get("title")).extracting(ValidationErrorResponse::getCode).containsExactly(expectedError);
            assertThat(errorResponse.getErrors().get("url")).extracting(ValidationErrorResponse::getCode).containsExactly(ErrorCode.INVALID_URL.name());
        }

        @Test
        public void shouldCreateJob() throws Exception {
            CreateJobRequest createJobRequest = new CreateJobRequest();
            createJobRequest.setUrl("http://www.example.com/new-job");
            createJobRequest.setTitle("My new job");
            createJobRequest.setCompany("My new company");
            createJobRequest.setDescription("My new job description");
            createJobRequest.setProfile("My new job profile");
            createJobRequest.setSalary("My new job salary");

            Instant beforeCall = Instant.now();
            MvcResult result = mockMvc.perform(post(JOBS_URL).cookie(accessTokenCookie).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(createJobRequest)))
                    .andExpect(status().isCreated())
                    .andReturn();

            Instant afterCall = Instant.now();

            JobResponse jobResponse = objectMapper.readValue(result.getResponse().getContentAsString(), JobResponse.class);
            assertThat(jobResponse).isNotNull();

            Instant createdAt = jobResponse.getCreatedAt();
            Instant updatedAt = jobResponse.getUpdatedAt();
            // FIXME this is quite ugly but we have to make sure updatedAt is consistent
            assertThat(createdAt)
                    .isAfterOrEqualTo(beforeCall)
                    .isBeforeOrEqualTo(afterCall);
            assertThat(updatedAt)
                    .isAfterOrEqualTo(beforeCall)
                    .isBeforeOrEqualTo(afterCall);

            assertThat(jobResponse.getTitle()).isEqualTo("My new job");
            assertThat(jobResponse.getCompany()).isEqualTo("My new company");
            assertThat(jobResponse.getDescription()).isEqualTo("My new job description");
            assertThat(jobResponse.getProfile()).isEqualTo("My new job profile");
            assertThat(jobResponse.getSalary()).isEqualTo("My new job salary");

            // let's check the update job is retrievable and consistent
            Job createdJob = jobService.findById(new JobId(jobResponse.getId())).orElse(null);
            assertThat(createdJob).isNotNull();
            assertThat(createdJob.getTitle()).isEqualTo("My new job");
            assertThat(createdJob.getCompany()).isEqualTo("My new company");
            assertThat(createdJob.getDescription()).isEqualTo("My new job description");
            assertThat(createdJob.getProfile()).isEqualTo("My new job profile");
            assertThat(createdJob.getSalary()).isEqualTo("My new job salary");
        }
    }

    @Nested
    class JobControllerUpdateIt {

        @Test
        public void whenUnauthenticated_thenShouldReturnUnauthorized() throws Exception {
            mockMvc.perform(patch(JOB_FOR_TEST_URL))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        public void whenRequestBodyEmpty_thenShouldReturnBadRequest() throws Exception {
            mockMvc.perform(patch(JOB_FOR_TEST_URL).cookie(accessTokenCookie))
                    .andExpect(status().isBadRequest());
        }

        @Test
        public void whenJobIdInvalid_thenShouldReturnBadRequest() throws Exception {
            UpdateJobRequest updateJobRequest = new UpdateJobRequest();
            mockMvc.perform(patch(JOBS_URL+"/invalid").cookie(accessTokenCookie).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateJobRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        public void whenJobNotFound_thenShouldReturnBadRequest() throws Exception {
            // we need a fully valid request, because data validation occurs before use case call
            UpdateJobRequest updateJobRequest = new UpdateJobRequest();
            updateJobRequest.setUrl("http://www.example.com/updated");
            updateJobRequest.setTitle("My job [updated]");
            updateJobRequest.setCompany("My company");
            updateJobRequest.setDescription("My job description [updated]");
            updateJobRequest.setProfile("My job profile [updated]");
            updateJobRequest.setSalary("My job salary [updated]");
            mockMvc.perform(patch(JOBS_URL+"/11111111-1111-1111-1111-111111111111").cookie(accessTokenCookie).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateJobRequest)))
                    .andExpect(status().isNotFound());
        }

        @Test
        public void whenRequestInvalid_thenShouldReturnBadRequestWithErrors() throws Exception {
            UpdateJobRequest updateJobRequest = new UpdateJobRequest();
            updateJobRequest.setUrl("invalid-url");

            MvcResult mvcResult = mockMvc.perform(patch(JOB_FOR_TEST_URL).cookie(accessTokenCookie).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateJobRequest)))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            ErrorResponse errorResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ErrorResponse.class);
            assertThat(errorResponse).isNotNull();
            assertThat(errorResponse.getStatus()).isEqualTo("400");
            assertThat(errorResponse.getMessage()).isEqualTo(ErrorCode.VALIDATION_FAILED.name());
            assertThat(errorResponse.getErrors()).hasSize(3);

            String expectedError = ErrorCode.FIELD_CANNOT_BE_EMPTY.name();
            assertThat(errorResponse.getErrors().get("description")).extracting(ValidationErrorResponse::getCode).containsExactly(expectedError);
            assertThat(errorResponse.getErrors().get("title")).extracting(ValidationErrorResponse::getCode).containsExactly(expectedError);
            assertThat(errorResponse.getErrors().get("url")).extracting(ValidationErrorResponse::getCode).containsExactly(ErrorCode.INVALID_URL.name());
        }

        @Test
        public void shouldUpdateJob() throws Exception {
            UpdateJobRequest updateJobRequest = new UpdateJobRequest();
            updateJobRequest.setUrl("http://www.example.com/updated");
            updateJobRequest.setTitle("My job [updated]");
            updateJobRequest.setCompany("My company");
            updateJobRequest.setDescription("My job description [updated]");
            updateJobRequest.setProfile("My job profile [updated]");
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
            assertThat(jobResponse.getSalary()).isEqualTo("My job salary [updated]");


            // let's check the update job is retrievable and consistent
            Job updatedJob = jobService.findById(new JobId(UUID.fromString(JOB_FOR_TEST_ID))).orElse(null);
            assertThat(updatedJob).isNotNull();
            assertThat(updatedJob.getTitle()).isEqualTo("My job [updated]");
            assertThat(updatedJob.getCompany()).isEqualTo("My company");
            assertThat(updatedJob.getDescription()).isEqualTo("My job description [updated]");
            assertThat(updatedJob.getProfile()).isEqualTo("My job profile [updated]");
            assertThat(updatedJob.getSalary()).isEqualTo("My job salary [updated]");
        }
    }

    @Nested
    class JobControllerUpdateStatusIt {

        private static final String JOB_STATUS_UPDATE_URL = JOB_FOR_TEST_URL+"/status";

        @Test
        public void whenUnauthenticated_thenShouldReturnUnauthorized() throws Exception {
            mockMvc.perform(put(JOB_STATUS_UPDATE_URL))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        public void whenRequestBodyEmpty_thenShouldReturnBadRequest() throws Exception {
            mockMvc.perform(put(JOB_STATUS_UPDATE_URL).cookie(accessTokenCookie))
                    .andExpect(status().isBadRequest());
        }

        @Test
        public void whenJobIdInvalid_thenShouldReturnBadRequest() throws Exception {
            UpdateJobStatusRequest updateJobStatusRequest = new UpdateJobStatusRequest();
            updateJobStatusRequest.setStatus(JobStatus.PENDING);
            mockMvc.perform(put(JOBS_URL+"/invalid/status").cookie(accessTokenCookie).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateJobStatusRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        public void whenJobNotFound_thenShouldReturnNotFound() throws Exception {
            // we need a fully valid request, because data validation occurs before use case call
            UpdateJobStatusRequest updateJobStatusRequest = new UpdateJobStatusRequest();
            updateJobStatusRequest.setStatus(JobStatus.PENDING);
            mockMvc.perform(put(JOBS_URL+"/11111111-1111-1111-1111-111111111111/status").cookie(accessTokenCookie).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateJobStatusRequest)))
                    .andExpect(status().isNotFound());
        }

        @Test
        public void whenRequestInvalid_thenShouldReturnBadRequestWithErrors() throws Exception {
            String invalidJson = """
                {
                    "status": "NOT_A_VALID_JOB_STATUS"
                }
            """;

            MvcResult mvcResult = mockMvc.perform(put(JOB_STATUS_UPDATE_URL).cookie(accessTokenCookie).contentType(MediaType.APPLICATION_JSON).content(invalidJson))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            ErrorResponse errorResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ErrorResponse.class);

            assertThat(errorResponse).isNotNull();
            assertThat(errorResponse.getStatus()).isEqualTo("400");
            assertThat(errorResponse.getMessage()).isEqualTo(ErrorCode.VALIDATION_FAILED.name());
            assertThat(errorResponse.getErrors()).hasSize(1);
            assertThat(errorResponse.getErrors().get("status")).extracting(ValidationErrorResponse::getCode).containsExactly(ErrorCode.INVALID_VALUE.name());
        }

        @Test
        public void shouldUpdateJobStatus() throws Exception {
            // we need a fully valid request, because data validation occurs before use case call
            UpdateJobStatusRequest updateJobStatusRequest = new UpdateJobStatusRequest();
            updateJobStatusRequest.setStatus(JobStatus.RELAUNCHED);

            Instant beforeCall = Instant.now();
            MvcResult result = mockMvc.perform(put(JOB_STATUS_UPDATE_URL).cookie(accessTokenCookie).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateJobStatusRequest)))
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
            Job updatedJob = jobService.findById(new JobId(UUID.fromString(JOB_FOR_TEST_ID))).orElse(null);
            assertThat(updatedJob).isNotNull();
            assertThat(updatedJob.getStatus()).isEqualTo(JobStatus.RELAUNCHED);
        }
    }

    @Nested
    class JobControllerUpdateRatingIT {

        private static final String JOB_RATING_UPDATE_URL = JOB_FOR_TEST_URL+"/rating";

        @Test
        public void whenUnauthenticated_thenShouldReturnUnauthorized() throws Exception {
            mockMvc.perform(put(JOB_RATING_UPDATE_URL))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        public void whenRequestBodyEmpty_thenShouldReturnBadRequest() throws Exception {
            mockMvc.perform(put(JOB_RATING_UPDATE_URL).cookie(accessTokenCookie))
                    .andExpect(status().isBadRequest());
        }

        @Test
        public void whenJobIdInvalid_thenShouldReturnBadRequest() throws Exception {
            // we need a fully valid request, because data validation occurs before use case call
            UpdateJobRatingRequest updateJobRatingRequest = new UpdateJobRatingRequest();
            updateJobRatingRequest.setRating(JobRating.of(5));
            mockMvc.perform(put(JOBS_URL+"/invalid/rating").cookie(accessTokenCookie).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateJobRatingRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        public void whenJobNotFound_thenShouldReturnNotFound() throws Exception {
            // we need a fully valid request, because data validation occurs before use case call
            UpdateJobRatingRequest updateJobRatingRequest = new UpdateJobRatingRequest();
            updateJobRatingRequest.setRating(JobRating.of(5));
            mockMvc.perform(put(JOBS_URL+"/11111111-1111-1111-1111-111111111111/rating").cookie(accessTokenCookie).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateJobRatingRequest)))
                    .andExpect(status().isNotFound());
        }

        @Test
        public void whenRequestInvalid_thenShouldReturnBadRequestWithErrors() throws Exception {
            String invalidJson = """
                {
                    "rating": "NAN"
                }
            """;

            MvcResult mvcResult = mockMvc.perform(put(JOB_RATING_UPDATE_URL).cookie(accessTokenCookie).contentType(MediaType.APPLICATION_JSON).content(invalidJson))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            ErrorResponse errorResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ErrorResponse.class);

            assertThat(errorResponse).isNotNull();
            assertThat(errorResponse.getStatus()).isEqualTo("400");
            assertThat(errorResponse.getMessage()).isEqualTo(ErrorCode.VALIDATION_FAILED.name());
            assertThat(errorResponse.getErrors()).hasSize(1);
            assertThat(errorResponse.getErrors().get("rating")).extracting(ValidationErrorResponse::getCode).containsExactly(ErrorCode.INVALID_VALUE.name());
        }

        @Test
        public void shouldUpdateJobRating() throws Exception {
            // we need a fully valid request, because data validation occurs before use case call
            UpdateJobRatingRequest updateJobRatingRequest = new UpdateJobRatingRequest();
            updateJobRatingRequest.setRating(JobRating.of(5));

            Instant beforeCall = Instant.now();
            MvcResult result = mockMvc.perform(put(JOB_RATING_UPDATE_URL).cookie(accessTokenCookie).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateJobRatingRequest)))
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
            Job updatedJob = jobService.findById(new JobId(UUID.fromString(JOB_FOR_TEST_ID))).orElse(null);

            assertThat(updatedJob).isNotNull();
            assertThat(updatedJob.getRating()).isEqualTo(JobRating.of(5));
        }
    }

    @Nested
    class JobControllerDeleteIt {

        @Test
        public void whenUnauthenticated_thenShouldReturnUnauthorized() throws Exception {
            mockMvc.perform(delete(JOB_FOR_TEST_URL))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        public void whenJobIdInvalid_thenShouldReturnBadRequest() throws Exception {
            mockMvc.perform(delete(JOBS_URL+"/invalid").cookie(accessTokenCookie))
                    .andExpect(status().isBadRequest());
        }

        @Test
        public void whenJobNotFound_thenShouldReturnNotFound() throws Exception {
            mockMvc.perform(delete(JOBS_URL+"/11111111-1111-1111-1111-111111111111").cookie(accessTokenCookie))
                    .andExpect(status().isNotFound());
        }

        @Test
        public void shouldDeleteJob() throws Exception {
            mockMvc.perform(delete(JOB_FOR_TEST_URL).cookie(accessTokenCookie))
                    .andExpect(status().isNoContent());

            // Job was deleted and should not be retrievable
            Job foundJob = jobService.findById(new JobId(UUID.fromString(JOB_FOR_TEST_ID))).orElse(null);
            assertThat(foundJob).isNull();
        }
    }

}
