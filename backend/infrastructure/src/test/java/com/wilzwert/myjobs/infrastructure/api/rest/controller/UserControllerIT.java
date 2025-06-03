package com.wilzwert.myjobs.infrastructure.api.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wilzwert.myjobs.core.domain.model.job.JobStatus;
import com.wilzwert.myjobs.core.domain.model.job.JobStatusMeta;
import com.wilzwert.myjobs.core.domain.model.user.Lang;
import com.wilzwert.myjobs.core.domain.model.user.User;
import com.wilzwert.myjobs.core.domain.model.user.UserId;
import com.wilzwert.myjobs.core.domain.model.user.ports.driven.UserDataManager;
import com.wilzwert.myjobs.core.domain.shared.validation.ErrorCode;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.UpdateUserLangRequest;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.UpdateUserRequest;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.UserResponse;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.UserSummaryResponse;
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

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class UserControllerIT extends AbstractBaseIntegrationTest  {
    private static final String USER_URL = "/api/user/me";

    // id for the User to use for get /api/user tests
    private static final String USER_FOR_GET_TEST_ID = "abcd4321-4321-4321-4321-123456789012";

    // id of the User to user for deletion tests
    private static final String USER_FOR_DELETE_TEST_ID = "abcd9876-9876-9876-9876-123456789012";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserDataManager userDataManager;

    Cookie accessTokenCookie;

    @BeforeEach
    void setup() {
        accessTokenCookie = new Cookie("access_token", jwtService.generateToken(USER_FOR_GET_TEST_ID));
    }

    @Nested
    class UserControllerGetIt {
        @Test
        void whenUnauthenticated_thenShouldReturnUnauthorized() throws Exception {
            mockMvc.perform(post(USER_URL))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void shouldGetUser() throws Exception {
            MvcResult mvcResult = mockMvc.perform(get(USER_URL).cookie(accessTokenCookie))
                    .andExpect(status().isOk())
                    .andReturn();
            UserResponse userResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), UserResponse.class);
            assertThat(userResponse).isNotNull();
            assertEquals("otherexisting@example.com", userResponse.getEmail());
            assertEquals("otherexistinguser", userResponse.getUsername());
            assertEquals("OtherExisting", userResponse.getFirstName());
            assertEquals("OtherUser", userResponse.getLastName());
            assertEquals(Lang.EN, userResponse.getLang());
            assertEquals("2025-03-29T09:46:09.475Z", userResponse.getCreatedAt().toString());
            assertEquals("PENDING", userResponse.getEmailStatus());
        }
    }

    @Nested
    class UserControllerDeleteIt {

        @Test
        void whenUnauthenticated_thenShouldReturnUnauthorized() throws Exception {
            mockMvc.perform(delete(USER_URL))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void shouldDeleteUser() throws Exception {
            Cookie cookie = new Cookie("access_token", jwtService.generateToken(USER_FOR_DELETE_TEST_ID));
            mockMvc.perform(delete(USER_URL).cookie(cookie))
                    .andExpect(status().isNoContent());

            // since we have UserDataManager at our disposal, we can check that the deleted user cannot be retrieved
            assertThat(userDataManager.findById(new UserId(UUID.fromString(USER_FOR_DELETE_TEST_ID)))).isEmpty();
        }
    }

    @Nested
    class UserControllerUpdateIT {
        private static final String UPDATE_URL = USER_URL;

        private UpdateUserRequest updateUserRequest;

        @BeforeEach
        void setup() {
            // setup a default valid signup request
            updateUserRequest = new UpdateUserRequest();
            updateUserRequest.setEmail("otherexisting-updated@example.com");
            updateUserRequest.setUsername("otherexistinguserupdated");
            updateUserRequest.setFirstName("OtherExistingUpdated");
            updateUserRequest.setLastName("OtherUserUpdated");
            updateUserRequest.setJobFollowUpReminderDays(12);
        }

        @Test
        void whenUnauthenticated_thenShouldReturnUnauthorized() throws Exception {
            mockMvc.perform(patch(UPDATE_URL))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void whenRequestBodyEmpty_thenShouldReturnBadRequest() throws Exception {
            mockMvc.perform(patch(UPDATE_URL).cookie(accessTokenCookie))
                    .andExpect(status().isBadRequest());
        }
        @Test
        void whenEmailEmpty_thenShouldReturnBadRequestWhenEmailEmpty() throws Exception {
                updateUserRequest.setEmail("");
            mockMvc.perform(patch(UPDATE_URL).cookie(accessTokenCookie).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateUserRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void whenEmailInvalid_thenShouldReturnBadRequest() throws Exception {
            updateUserRequest.setEmail("test");
            mockMvc.perform(patch(UPDATE_URL).cookie(accessTokenCookie).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateUserRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errors.email[0].code").value(ErrorCode.INVALID_EMAIL.name()));
        }

        @Test
        void whenFirstNameEmpty_thenShouldReturnBadRequest() throws Exception {
            updateUserRequest.setFirstName("");
            mockMvc.perform(patch(UPDATE_URL).cookie(accessTokenCookie).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateUserRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errors.firstName[0].code").value(ErrorCode.FIELD_CANNOT_BE_EMPTY.name()));
        }

        @Test
        void whenLastNameEmpty_thenShouldReturnBadRequest() throws Exception {
            updateUserRequest.setLastName("");
            mockMvc.perform(patch(UPDATE_URL).cookie(accessTokenCookie).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateUserRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errors.lastName[0].code").value(ErrorCode.FIELD_CANNOT_BE_EMPTY.name()));
        }

        @Test
        void whenUsernameTooShort_thenShouldReturnBadRequest() throws Exception {
            updateUserRequest.setUsername("T");
            mockMvc.perform(patch(UPDATE_URL).cookie(accessTokenCookie).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateUserRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errors.username[0].code").value(ErrorCode.FIELD_TOO_SHORT.name()));
        }

        @Test
        void whenUsernameTooLong_thenShouldReturnBadRequest() throws Exception {
            updateUserRequest.setUsername("thisisafartoolongusernamethatshouldtriggeravalidationerror");
            mockMvc.perform(patch(UPDATE_URL).cookie(accessTokenCookie).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateUserRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errors.username[0].code").value(ErrorCode.FIELD_TOO_LONG.name()));
        }

        @Test
        void whenEmailAlreadyExists_thenShouldReturnBadRequest() throws Exception {
            // we know we already have a User with the email 'existing@example.com' (see resources/test-data/user.json
            updateUserRequest.setEmail("existing@example.com");

            mockMvc.perform(patch(UPDATE_URL).cookie(accessTokenCookie).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateUserRequest)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("message").value(ErrorCode.USER_ALREADY_EXISTS.name()));
        }

        @Test
        void whenUsernameAlreadyExists_thenShouldReturnBadRequest() throws Exception {
            // we know we already have a User with the username 'existinguser' (see resources/test-data/user.json
            updateUserRequest.setUsername("existinguser");

            mockMvc.perform(patch(UPDATE_URL).cookie(accessTokenCookie).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateUserRequest)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("message").value(ErrorCode.USER_ALREADY_EXISTS.name()));
        }

        @Test
        void shouldUpdateUser() throws Exception {
            MvcResult mvcResult = mockMvc.perform(patch(UPDATE_URL).cookie(accessTokenCookie).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateUserRequest)))
                    .andExpect(status().isOk())
                    .andReturn();

            UserResponse userResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), UserResponse.class);
            assertThat(userResponse).isNotNull();
            assertEquals("otherexisting-updated@example.com", userResponse.getEmail());
            assertEquals("otherexistinguserupdated", userResponse.getUsername());
            assertEquals("OtherExistingUpdated", userResponse.getFirstName());
            assertEquals("OtherUserUpdated", userResponse.getLastName());
            assertEquals(12, userResponse.getJobFollowUpReminderDays());
            assertEquals(Lang.EN, userResponse.getLang());
            assertEquals("2025-03-29T09:46:09.475Z", userResponse.getCreatedAt().toString());
            assertEquals("PENDING", userResponse.getEmailStatus());

            User foundUser = userDataManager.findByEmail("otherexisting-updated@example.com").orElse(null);
            assertThat(foundUser).isNotNull();
        }
    }

    @Nested
    class UserControllerUpdateLangIT {
        private static final String UPDATE_URL = USER_URL+"/lang";

        @Test
        void whenUnauthenticated_thenShouldReturnUnauthorized() throws Exception {
            mockMvc.perform(put(UPDATE_URL))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void whenRequestBodyEmpty_thenShouldReturnBadRequest() throws Exception {
            mockMvc.perform(put(UPDATE_URL).cookie(accessTokenCookie))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void shouldUpdateUserLang() throws Exception {
            mockMvc.perform(put(UPDATE_URL).cookie(accessTokenCookie).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(new UpdateUserLangRequest(Lang.FR))))
                    .andExpect(status().isOk());

            User foundUser = userDataManager.findById(new UserId(UUID.fromString(USER_FOR_GET_TEST_ID))).orElse(null);
            assertThat(foundUser).isNotNull();
            assertThat(foundUser.getLang()).isEqualTo(Lang.FR);
        }
    }

    @Nested
    class UserControllerGetUserSummaryIT {
        private static final String SUMMARY_URL = USER_URL+"/summary";

        @BeforeEach
        void setUp() {
            // for summary tests, lets take our first user, who actually has jobs
            // (see jobs.json and users.json in test-data)
            accessTokenCookie = new Cookie("access_token", jwtService.generateToken("abcd1234-1234-1234-1234-123456789012"));
        }

        @Test
        void whenUnauthenticated_thenShouldReturnUnauthorized() throws Exception {
            mockMvc.perform(get(SUMMARY_URL))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void whenUnsupportedMethod_thenVerificationShouldReturnUnsupported() throws Exception {
            mockMvc.perform(post(SUMMARY_URL).cookie(accessTokenCookie))
                    .andExpect(status().isMethodNotAllowed());
        }

        @Test
        void shouldGetUserSummary() throws Exception {
            MvcResult result = mockMvc.perform(get(SUMMARY_URL).cookie(accessTokenCookie))
                    .andExpect(status().isOk())
                    .andReturn();

            var summary = objectMapper.readValue(result.getResponse().getContentAsString(), UserSummaryResponse.class);

            assertThat(summary).isNotNull();
            assertThat(summary.getActiveJobsCount()).isEqualTo(3);
            assertThat(summary.getInactiveJobsCount()).isEqualTo(1);
            assertThat(summary.getJobsCount()).isEqualTo(4);
            assertThat(summary.getLateJobsCount()).isEqualTo(3);
            assertThat(summary.getJobStatuses()).containsExactlyInAnyOrderEntriesOf(Map.of(JobStatus.PENDING, 1, JobStatus.CREATED, 2, JobStatus.COMPANY_REFUSED, 1));
            assertThat(summary.getUsableJobStatusMetas()).containsAll(List.of(JobStatusMeta.ACTIVE, JobStatusMeta.INACTIVE, JobStatusMeta.LATE));
        }
    }
}