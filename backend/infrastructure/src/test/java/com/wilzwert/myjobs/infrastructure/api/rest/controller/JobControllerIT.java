package com.wilzwert.myjobs.infrastructure.api.rest.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wilzwert.myjobs.core.domain.model.job.Job;
import com.wilzwert.myjobs.core.domain.model.job.JobId;
import com.wilzwert.myjobs.core.domain.model.job.JobStatus;
import com.wilzwert.myjobs.core.domain.model.job.ports.driven.JobDataManager;
import com.wilzwert.myjobs.core.domain.shared.validation.ErrorCode;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.*;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.job.*;
import com.wilzwert.myjobs.infrastructure.configuration.AbstractBaseIntegrationTest;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.entity.EventStatus;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.entity.MongoIntegrationEvent;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.repository.MongoIntegrationEventRepository;
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
    private JobDataManager jobDataManager;

    @Autowired
    private MongoIntegrationEventRepository integrationEventRepository;

    Cookie accessTokenCookie;

    @BeforeEach
    void setup() {
        accessTokenCookie = new Cookie("access_token", jwtService.generateToken(USER_FOR_JOBS_TEST_ID));
    }

    @Nested
    class JobControllerGetIt {
        @Test
        void whenUnauthenticated_thenShouldReturnUnauthorized() throws Exception {
            mockMvc.perform(post(JOBS_URL))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void shouldGetJobs() throws Exception {
            MvcResult mvcResult = mockMvc.perform(get(JOBS_URL).cookie(accessTokenCookie))
                    .andExpect(status().isOk())
                    .andReturn();

            RestPage<JobResponse> jobResponseRestPage = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {});
            assertThat(jobResponseRestPage).isNotNull();
            assertThat(jobResponseRestPage.getContent()).isNotNull();
            assertThat(jobResponseRestPage.getContent()).hasSize(4);
            assertThat(jobResponseRestPage.getTotalElementsCount()).isEqualTo(4);
            assertThat(jobResponseRestPage.getPageSize()).isEqualTo(10);
            assertThat(jobResponseRestPage.getPagesCount()).isEqualTo(1);
            assertThat(jobResponseRestPage.getCurrentPage()).isZero();

            List<String> titles = jobResponseRestPage.getContent().stream().map(JobResponse::getTitle).toList();
            // by default, we expect jobs sorted by creation date desc
            assertThat(titles).containsExactly("My refused job", "My third job", "My second job", "My job");
        }

        @Test
        void shouldGetJobsWithUserPagination() throws Exception {
            MvcResult mvcResult = mockMvc.perform(
                    get(JOBS_URL).cookie(accessTokenCookie)
                            .param("page", "3") // fourth page
                            .param("itemsPerPage", "1") // 1 Job per page
                )
                .andExpect(status().isOk())
                .andReturn();

            RestPage<JobResponse> jobResponseRestPage = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<RestPage<JobResponse>>() {});
            assertThat(jobResponseRestPage).isNotNull();
            assertThat(jobResponseRestPage.getContent()).isNotNull();
            assertThat(jobResponseRestPage.getContent()).hasSize(1);
            assertThat(jobResponseRestPage.getTotalElementsCount()).isEqualTo(4);
            assertThat(jobResponseRestPage.getPageSize()).isEqualTo(1);
            assertThat(jobResponseRestPage.getCurrentPage()).isEqualTo(3);
            assertThat(jobResponseRestPage.getPagesCount()).isEqualTo(4);

            List<String> titles = jobResponseRestPage.getContent().stream().map(JobResponse::getTitle).toList();
            // by default, jobs are sorted by creation date desc
            // as we asked for the last (third) page, we should get the first job
            assertThat(titles).containsExactly("My job");
        }

        @Test
        void shouldGetJobsSortedByRatingDesc() throws Exception {
            MvcResult mvcResult = mockMvc.perform(
                            get(JOBS_URL).cookie(accessTokenCookie)
                                    .param("sort", "rating,desc")
                    )
                    .andExpect(status().isOk())
                    .andReturn();

            RestPage<JobResponse> jobResponseRestPage = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<RestPage<JobResponse>>() {});
            assertThat(jobResponseRestPage).isNotNull();
            assertThat(jobResponseRestPage.getContent()).isNotNull();
            assertThat(jobResponseRestPage.getContent()).hasSize(4);
            assertThat(jobResponseRestPage.getTotalElementsCount()).isEqualTo(4);
            assertThat(jobResponseRestPage.getPageSize()).isEqualTo(10);
            assertThat(jobResponseRestPage.getPagesCount()).isEqualTo(1);
            assertThat(jobResponseRestPage.getCurrentPage()).isZero();

            List<String> titles = jobResponseRestPage.getContent().stream().map(JobResponse::getTitle).toList();
            assertThat(titles).containsExactly("My third job", "My job", "My refused job", "My second job");
        }

        @Test
        void shouldGetJobsSortedByCreatedAtAsc() throws Exception {
            MvcResult mvcResult = mockMvc.perform(
                            get(JOBS_URL).cookie(accessTokenCookie)
                                    .param("sort", "createdAt,asc")
                    )
                    .andExpect(status().isOk())
                    .andReturn();

            RestPage<JobResponse> jobResponseRestPage = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<RestPage<JobResponse>>() {});
            assertThat(jobResponseRestPage).isNotNull();
            assertThat(jobResponseRestPage.getContent()).isNotNull();
            assertThat(jobResponseRestPage.getContent()).hasSize(4);
            assertThat(jobResponseRestPage.getTotalElementsCount()).isEqualTo(4);
            assertThat(jobResponseRestPage.getPageSize()).isEqualTo(10);
            assertThat(jobResponseRestPage.getPagesCount()).isEqualTo(1);
            assertThat(jobResponseRestPage.getCurrentPage()).isZero();

            List<String> titles = jobResponseRestPage.getContent().stream().map(JobResponse::getTitle).toList();
            assertThat(titles).containsExactly("My job", "My second job", "My third job", "My refused job");
        }

        @Test
        void shouldGetJobsFilteredByStatus() throws Exception {
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
            assertThat(jobResponseRestPage.getCurrentPage()).isZero();

            List<String> titles = jobResponseRestPage.getContent().stream().map(JobResponse::getTitle).toList();
            assertThat(titles).containsExactly("My second job");
        }

        @Test
        void shouldGetJobsFilteredByStatusFilter() throws Exception {
            MvcResult mvcResult = mockMvc.perform(
                            get(JOBS_URL).cookie(accessTokenCookie)
                                    .param("statusMeta", "ACTIVE")
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
            assertThat(jobResponseRestPage.getCurrentPage()).isZero();

            List<String> titles = jobResponseRestPage.getContent().stream().map(JobResponse::getTitle).toList();
            assertThat(titles).containsExactly("My third job", "My second job", "My job");
        }

        @Test
        void whenJobIdInvalid_thenShouldReturnBadRequest() throws Exception {
            mockMvc.perform(
                    get(JOBS_URL+"/invalid").cookie(accessTokenCookie)
                )
                .andExpect(status().isBadRequest());
        }

        @Test
        void whenJobNotFound_thenShouldReturnNotfound() throws Exception {
            mockMvc.perform(
                            get(JOBS_URL+"/11111111-1111-1111-1111-111111111111").cookie(accessTokenCookie)
                    )
                    .andExpect(status().isNotFound());
        }

        @Test
        void shouldGetJob() throws Exception {
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
        void whenUnauthenticated_thenShouldReturnUnauthorized() throws Exception {
            mockMvc.perform(post(JOBS_URL))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void whenRequestBodyEmpty_thenShouldReturnBadRequest() throws Exception {
            mockMvc.perform(post(JOBS_URL).cookie(accessTokenCookie))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void whenRequestBodyInvalidJson_thenShouldReturnBadRequest() throws Exception {
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
        void whenRequestBodyInvalid_thenShouldReturnBadRequest() throws Exception {
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
        void whenJobUrlExists_thenShouldReturnConflict() throws Exception {
            CreateJobRequest createJobRequest = new CreateJobRequest();
            createJobRequest.setUrl("http://www.example.com/my-job");
            createJobRequest.setTitle("My new job");
            createJobRequest.setCompany("My new company");
            createJobRequest.setDescription("My new job description");
            createJobRequest.setProfile("My new job profile");
            createJobRequest.setComment("My new job comment");
            createJobRequest.setSalary("My new job salary");

            mockMvc.perform(post(JOBS_URL).cookie(accessTokenCookie).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(createJobRequest)))
                    .andExpect(status().isConflict());
        }

        @Test
        void whenRequestInvalid_thenShouldReturnBadRequestWithErrors() throws Exception {
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
        void shouldCreateJob() throws Exception {
            CreateJobRequest createJobRequest = new CreateJobRequest();
            createJobRequest.setUrl("http://www.example.com/new-job");
            createJobRequest.setTitle("My new job");
            createJobRequest.setCompany("My new company");
            createJobRequest.setDescription("My new job description");
            createJobRequest.setProfile("My new job profile");
            createJobRequest.setComment("My new job comment");
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
            assertThat(jobResponse.getComment()).isEqualTo("My new job comment");
            assertThat(jobResponse.getSalary()).isEqualTo("My new job salary");

            // let's check the update job is retrievable and consistent
            Job createdJob = jobDataManager.findById(new JobId(jobResponse.getId())).orElse(null);
            assertThat(createdJob).isNotNull();
            assertThat(createdJob.getTitle()).isEqualTo("My new job");
            assertThat(createdJob.getCompany()).isEqualTo("My new company");
            assertThat(createdJob.getDescription()).isEqualTo("My new job description");
            assertThat(createdJob.getProfile()).isEqualTo("My new job profile");
            assertThat(createdJob.getComment()).isEqualTo("My new job comment");
            assertThat(createdJob.getSalary()).isEqualTo("My new job salary");

            // let's check an event has been created
            // Assert
            List<MongoIntegrationEvent> events = integrationEventRepository.findByType("JobCreatedEvent");
            assertThat(events).hasSize(1);

            MongoIntegrationEvent event = events.getFirst();
            System.out.println(event);
            assertThat(event.getStatus()).isEqualTo(EventStatus.PENDING);
            assertThat(event.getPayload()).contains(createdJob.getId().toString());
        }
    }

    @Nested
    class JobControllerDeleteIt {

        @Test
        void whenUnauthenticated_thenShouldReturnUnauthorized() throws Exception {
            mockMvc.perform(delete(JOB_FOR_TEST_URL))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void whenJobIdInvalid_thenShouldReturnBadRequest() throws Exception {
            mockMvc.perform(delete(JOBS_URL+"/invalid").cookie(accessTokenCookie))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void whenJobNotFound_thenShouldReturnNotFound() throws Exception {
            mockMvc.perform(delete(JOBS_URL+"/11111111-1111-1111-1111-111111111111").cookie(accessTokenCookie))
                    .andExpect(status().isNotFound());
        }

        @Test
        void shouldDeleteJob() throws Exception {
            mockMvc.perform(delete(JOB_FOR_TEST_URL).cookie(accessTokenCookie))
                    .andExpect(status().isNoContent());

            // Job was deleted and should not be retrievable
            Job foundJob = jobDataManager.findById(new JobId(UUID.fromString(JOB_FOR_TEST_ID))).orElse(null);
            assertThat(foundJob).isNull();
        }
    }

}
