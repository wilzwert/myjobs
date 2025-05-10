package com.wilzwert.myjobs.infrastructure.api.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wilzwert.myjobs.core.domain.model.attachment.Attachment;
import com.wilzwert.myjobs.core.domain.model.job.Job;
import com.wilzwert.myjobs.core.domain.model.job.JobId;
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

import java.io.File;
import java.nio.file.Files;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// TODO : find a way to do some useful and meaningful IT on the /api/jobs/jobUd/attachments/file API endpoint

@AutoConfigureMockMvc
public class AttachmentControllerIT extends AbstractBaseIntegrationTest  {
    private final static String JOBS_URL = "/api/jobs";
    private static final String JOB_FOR_TEST_ID =  "77777777-7777-7777-7777-123456789012";
    private static final String JOB_ATTACHMENTS_TEST_URL = "/api/jobs/"+JOB_FOR_TEST_ID+"/attachments";

    // id for the User to use for tests
    private final static String USER_FOR_JOBS_TEST_ID = "abcd1234-1234-1234-1234-123456789012";

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
    class AttachmentControllerCreateIt {

        @Test
        public void whenUnauthenticated_thenShouldReturnUnauthorized() throws Exception {
            mockMvc.perform(post(JOB_ATTACHMENTS_TEST_URL))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        public void whenRequestBodyEmpty_thenShouldReturnBadRequest() throws Exception {
            mockMvc.perform(post(JOB_ATTACHMENTS_TEST_URL).cookie(accessTokenCookie))
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
            mockMvc.perform(post(JOB_ATTACHMENTS_TEST_URL).cookie(accessTokenCookie).contentType(MediaType.APPLICATION_JSON).content(invalidJson))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("message").value(ErrorCode.VALIDATION_FAILED.name()));

        }

        @Test
        public void whenRequestBodyInvalid_thenShouldReturnBadRequest() throws Exception {
            String invalidJson = """
                {
                    "nonexistent": "this field does not exist",
                    "name": "this field does exist"
                }
            """;
            mockMvc.perform(post(JOB_ATTACHMENTS_TEST_URL).cookie(accessTokenCookie).contentType(MediaType.APPLICATION_JSON).content(invalidJson))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("message").value(ErrorCode.VALIDATION_FAILED.name()));

        }

        @Test
        public void whenRequestInvalid_thenShouldReturnBadRequestWithErrors() throws Exception {
            CreateAttachmentRequest createAttachmentRequest = new CreateAttachmentRequest();
            MvcResult mvcResult = mockMvc.perform(post(JOB_ATTACHMENTS_TEST_URL).cookie(accessTokenCookie).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(createAttachmentRequest)))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            ErrorResponse errorResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ErrorResponse.class);
            assertThat(errorResponse).isNotNull();
            assertThat(errorResponse.getStatus()).isEqualTo("400");
            assertThat(errorResponse.getMessage()).isEqualTo(ErrorCode.VALIDATION_FAILED.name());
            assertThat(errorResponse.getErrors()).hasSize(3);

            ValidationErrorResponse expectedError = new ValidationErrorResponse(ErrorCode.FIELD_CANNOT_BE_EMPTY.name());
            assertThat(errorResponse.getErrors().get("name")).containsExactly(expectedError);
            assertThat(errorResponse.getErrors().get("filename")).containsExactly(expectedError);
            assertThat(errorResponse.getErrors().get("content")).containsExactly(expectedError);
        }

        @Test
        public void shouldCreateAttachment() throws Exception {
            CreateAttachmentRequest createAttachmentRequest = new CreateAttachmentRequest();
            File fileToSend = new File("src/test/resources/cv_test.doc");
            byte[] fileContent = Files.readAllBytes(fileToSend.toPath());
            createAttachmentRequest.setContent("base64,"+Base64.getEncoder().encodeToString(fileContent));
            createAttachmentRequest.setFilename("test.doc");
            createAttachmentRequest.setName("CV");

            Instant beforeCall = Instant.now();
            MvcResult result = mockMvc.perform(post(JOB_ATTACHMENTS_TEST_URL).cookie(accessTokenCookie).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(createAttachmentRequest)))
                    .andExpect(status().isCreated())
                    .andReturn();

            Instant afterCall = Instant.now();

            AttachmentResponse attachmentResponse = objectMapper.readValue(result.getResponse().getContentAsString(), AttachmentResponse.class);
            assertThat(attachmentResponse).isNotNull();

            Instant createdAt = attachmentResponse.getCreatedAt();
            // FIXME this is quite ugly but we have to make sure updatedAt is consistent
            assertThat(createdAt)
                    .isAfterOrEqualTo(beforeCall)
                    .isBeforeOrEqualTo(afterCall);

            assertThat(attachmentResponse.getId()).isNotNull();
            assertThat(attachmentResponse.getName()).isEqualTo("CV");
            assertThat(attachmentResponse.getFilename()).isEqualTo("test.doc");
            assertThat(attachmentResponse.getContentType()).isEqualTo("application/msword");

            // attachment should be retrievable
            Job job = jobService.findById(new JobId(UUID.fromString(JOB_FOR_TEST_ID))).orElse(null);

            assertThat(job).isNotNull();
            Attachment attachment = job.getAttachments().getFirst();
            assertThat(attachment).isNotNull();
            assertThat(attachment.getId().value()).isEqualTo(attachmentResponse.getId());
            assertThat(attachment.getName()).isEqualTo("CV");
            assertThat(attachment.getFileId()).isNotEmpty();
            assertThat(attachment.getFilename()).isEqualTo("test.doc");
            assertThat(attachment.getContentType()).isEqualTo("application/msword");

            // TODO
            // file should be downloadable
            // we should also delete the attachment so that our disk or S3 space / quota is not reached
            // we can do that manually in S3, but how can we check and then delete  programmatically ?
        }
    }

    @Nested
    class AttachmentControllerDeleteIt {

        private final static String ATTACHMENT_TEST_ID = "b7777777-7777-7777-7770-123456789012";
        private final static String JOB_ATTACHMENT_TEST_URL = JOB_ATTACHMENTS_TEST_URL+"/b7777777-7777-7777-7770-123456789012";

        @Test
        public void whenUnauthenticated_thenShouldReturnUnauthorized() throws Exception {
            mockMvc.perform(delete(JOB_ATTACHMENT_TEST_URL))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        public void whenJobIdInvalid_thenShouldReturnBadRequest() throws Exception {
            mockMvc.perform(delete(JOBS_URL+"/invalid/attachments/"+ATTACHMENT_TEST_ID).cookie(accessTokenCookie))
                    .andExpect(status().isBadRequest());
        }

        @Test
        public void whenAttachmentIdInvalid_thenShouldReturnBadRequest() throws Exception {
            mockMvc.perform(delete(JOB_ATTACHMENTS_TEST_URL+"/invalid").cookie(accessTokenCookie))
                    .andExpect(status().isBadRequest());
        }

        @Test
        public void whenJobNotFound_thenShouldReturnNotFound() throws Exception {
            mockMvc.perform(delete(JOBS_URL+"/11111111-1111-1111-1111-111111111111/attachments/"+ATTACHMENT_TEST_ID).cookie(accessTokenCookie))
                    .andExpect(status().isNotFound());
        }

        @Test
        public void whenAttachmentNotFound_thenShouldReturnNotFound() throws Exception {
            mockMvc.perform(delete(JOB_ATTACHMENTS_TEST_URL+"/11111111-1111-1111-1111-111111111111").cookie(accessTokenCookie))
                    .andExpect(status().isNotFound());
        }

        @Test
        public void shouldDeleteAttachment() throws Exception {
            mockMvc.perform(delete(JOB_ATTACHMENT_TEST_URL).cookie(accessTokenCookie))
                    .andExpect(status().isNoContent());

            // attachment should not be retrievable
            Job foundJob = jobService.findById(new JobId(UUID.fromString(JOB_FOR_TEST_ID))).orElse(null);
            assertThat(foundJob).isNotNull();
            Attachment attachment = foundJob.getAttachments().stream().filter(a -> a.getId().value().equals(UUID.fromString(ATTACHMENT_TEST_ID))).findFirst().orElse(null);
            assertThat(attachment).isNull();
        }
    }
}
