package com.wilzwert.myjobs.infrastructure.api.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wilzwert.myjobs.core.domain.model.user.Lang;
import com.wilzwert.myjobs.core.domain.model.user.User;
import com.wilzwert.myjobs.core.domain.model.user.UserId;
import com.wilzwert.myjobs.core.domain.ports.driven.PasswordHasher;
import com.wilzwert.myjobs.core.domain.ports.driven.UserService;
import com.wilzwert.myjobs.core.domain.shared.validation.ErrorCode;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.ChangePasswordRequest;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.UpdateUserLangRequest;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.UpdateUserRequest;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.UserResponse;
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

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class UserControllerIT extends AbstractBaseIntegrationTest  {
    private final static String USER_URL = "/api/user/me";

    // id for the User to use for get /api/user tests
    private final static String USER_FOR_GET_TEST_ID = "abcd4321-4321-4321-4321-123456789012";
    // id of the User to use for password changes tests
    private final static String USER_FOR_CHANGE_PASSWORD_TEST_ID = "abcd6543-6543-6543-6543-123456789012";
    // id of the User to user for deletion tests
    private final static String USER_FOR_DELETE_TEST_ID = "abcd9876-9876-9876-9876-123456789012";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    Cookie accessTokenCookie;

    @BeforeEach
    public void setup() {
        accessTokenCookie = new Cookie("access_token", jwtService.generateToken(USER_FOR_GET_TEST_ID));
    }

    @Nested
    class UserControllerGetIt {
        @Test
        public void whenUnauthenticated_thenShouldReturnUnauthorized() throws Exception {
            mockMvc.perform(post(USER_URL))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        public void shouldGetUser() throws Exception {
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
            assertEquals("2025-03-29T09:46:09.475Z", userResponse.getCreatedAt());
            assertEquals("PENDING", userResponse.getEmailStatus());
        }
    }

    @Nested
    class UserControllerDeleteIt {

        @Test
        public void whenUnauthenticated_thenShouldReturnUnauthorized() throws Exception {
            mockMvc.perform(delete(USER_URL))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        public void shouldDeleteUser() throws Exception {
            Cookie cookie = new Cookie("access_token", jwtService.generateToken(USER_FOR_DELETE_TEST_ID));
            mockMvc.perform(delete(USER_URL).cookie(cookie))
                    .andExpect(status().isNoContent());

            // since we have UserService at our disposal, we can check that the deleted user cannot be retrieved
            assertThat(userService.findById(new UserId(UUID.fromString(USER_FOR_DELETE_TEST_ID))).isEmpty());
        }
    }

    @Nested
    class UserControllerUpdateIT {
        private final static String UPDATE_URL = USER_URL;

        private UpdateUserRequest updateUserRequest;

        @BeforeEach
        public void setup() {
            // setup a default valid signup request
            updateUserRequest = new UpdateUserRequest();
            updateUserRequest.setEmail("otherexisting-updated@example.com");
            updateUserRequest.setUsername("otherexistinguserupdated");
            updateUserRequest.setFirstName("OtherExistingUpdated");
            updateUserRequest.setLastName("OtherUserUpdated");
        }

        @Test
        public void whenUnauthenticated_thenShouldReturnUnauthorized() throws Exception {
            mockMvc.perform(patch(UPDATE_URL))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        public void whenRequestBodyEmpty_thenShouldReturnBadRequest() throws Exception {
            mockMvc.perform(patch(UPDATE_URL).cookie(accessTokenCookie))
                    .andExpect(status().isBadRequest());
        }
        @Test
        public void whenEmailEmpty_thenShouldReturnBadRequestWhenEmailEmpty() throws Exception {
                updateUserRequest.setEmail("");
            mockMvc.perform(patch(UPDATE_URL).cookie(accessTokenCookie).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateUserRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        public void whenEmailInvalid_thenShouldReturnBadRequest() throws Exception {
            updateUserRequest.setEmail("test");
            mockMvc.perform(patch(UPDATE_URL).cookie(accessTokenCookie).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateUserRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errors.email").value(ErrorCode.INVALID_EMAIL.name()));
        }

        @Test
        public void whenFirstNameEmpty_thenShouldReturnBadRequest() throws Exception {
            updateUserRequest.setFirstName("");
            mockMvc.perform(patch(UPDATE_URL).cookie(accessTokenCookie).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateUserRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errors.firstName").value(ErrorCode.FIELD_CANNOT_BE_EMPTY.name()));
        }

        @Test
        public void whenLastNameEmpty_thenShouldReturnBadRequest() throws Exception {
            updateUserRequest.setLastName("");
            mockMvc.perform(patch(UPDATE_URL).cookie(accessTokenCookie).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateUserRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errors.lastName").value(ErrorCode.FIELD_CANNOT_BE_EMPTY.name()));
        }

        @Test
        public void whenUsernameTooShort_thenShouldReturnBadRequest() throws Exception {
            updateUserRequest.setUsername("T");
            mockMvc.perform(patch(UPDATE_URL).cookie(accessTokenCookie).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateUserRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errors.username").value(ErrorCode.FIELD_TOO_SHORT.name()));
        }

        @Test
        public void whenUsernameTooLong_thenShouldReturnBadRequest() throws Exception {
            updateUserRequest.setUsername("thisisafartoolongusernamethatshouldtriggeravalidationerror");
            mockMvc.perform(patch(UPDATE_URL).cookie(accessTokenCookie).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateUserRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errors.username").value(ErrorCode.FIELD_TOO_LONG.name()));
        }

        @Test
        public void whenEmailAlreadyExists_thenShouldReturnBadRequest() throws Exception {
            // we know we already have a User with the email 'existing@example.com' (see resources/test-data/user.json
            updateUserRequest.setEmail("existing@example.com");

            mockMvc.perform(patch(UPDATE_URL).cookie(accessTokenCookie).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateUserRequest)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("message").value(ErrorCode.USER_ALREADY_EXISTS.name()));
        }

        @Test
        public void whenUsernameAlreadyExists_thenShouldReturnBadRequest() throws Exception {
            // we know we already have a User with the username 'existinguser' (see resources/test-data/user.json
            updateUserRequest.setUsername("existinguser");

            mockMvc.perform(patch(UPDATE_URL).cookie(accessTokenCookie).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateUserRequest)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("message").value(ErrorCode.USER_ALREADY_EXISTS.name()));
        }

        @Test
        public void shouldUpdateUser() throws Exception {
            MvcResult mvcResult = mockMvc.perform(patch(UPDATE_URL).cookie(accessTokenCookie).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateUserRequest)))
                    .andExpect(status().isOk())
                    .andReturn();

            UserResponse userResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), UserResponse.class);
            assertThat(userResponse).isNotNull();
            assertEquals("otherexisting-updated@example.com", userResponse.getEmail());
            assertEquals("otherexistinguserupdated", userResponse.getUsername());
            assertEquals("OtherExistingUpdated", userResponse.getFirstName());
            assertEquals("OtherUserUpdated", userResponse.getLastName());
            assertEquals(Lang.EN, userResponse.getLang());
            assertEquals("2025-03-29T09:46:09.475Z", userResponse.getCreatedAt());
            assertEquals("PENDING", userResponse.getEmailStatus());

            User foundUser = userService.findByEmail("otherexisting-updated@example.com").orElse(null);
            assertThat(foundUser).isNotNull();
        }
    }

    @Nested
    class UserControllerUpdateLangIT {
        private final static String UPDATE_URL = USER_URL+"/lang";

        @Test
        public void whenUnauthenticated_thenShouldReturnUnauthorized() throws Exception {
            mockMvc.perform(put(UPDATE_URL))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        public void whenRequestBodyEmpty_thenShouldReturnBadRequest() throws Exception {
            mockMvc.perform(put(UPDATE_URL).cookie(accessTokenCookie))
                    .andExpect(status().isBadRequest());
        }

        @Test
        public void shouldUpdateUserLang() throws Exception {
            mockMvc.perform(put(UPDATE_URL).cookie(accessTokenCookie).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(new UpdateUserLangRequest(Lang.FR))))
                    .andExpect(status().isOk());

            User foundUser = userService.findById(new UserId(UUID.fromString(USER_FOR_GET_TEST_ID))).orElse(null);
            assertThat(foundUser).isNotNull();
            assertThat(foundUser.getLang()).isEqualTo(Lang.FR);
        }
    }

    @Nested
    class UserControllerChangePasswordIT {

        private static final String CHANGE_PASSWORD_URL = USER_URL + "/password";

        private ChangePasswordRequest changePasswordRequest;

        private Cookie accessTokenCookie;

        @Autowired
        private PasswordHasher passwordHasher;

        @BeforeEach
        public void setUp()  {
            // this is a valid password request
            // it should be changed per case for testing
            changePasswordRequest = new ChangePasswordRequest();
            changePasswordRequest.setPassword("Dcba4321!");
            changePasswordRequest.setOldPassword("Abcd1234!");

            accessTokenCookie = new Cookie("access_token", jwtService.generateToken(USER_FOR_CHANGE_PASSWORD_TEST_ID));
        }

        @Test
        public void whenNoAuth_thenShouldReturnUnauthorized() throws Exception {
            mockMvc.perform(put(CHANGE_PASSWORD_URL))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        public void whenRequestBodyEmpty_thenShouldReturnBadRequest() throws Exception {
            mockMvc.perform(put(CHANGE_PASSWORD_URL).cookie(accessTokenCookie))
                    .andExpect(status().isBadRequest());
        }

        @Test
        public void whenOldPasswordEmpty_thenShouldReturnBadRequest() throws Exception {
            changePasswordRequest.setOldPassword("");
            mockMvc.perform(put(CHANGE_PASSWORD_URL).cookie(accessTokenCookie).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(changePasswordRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("message").value(ErrorCode.VALIDATION_FAILED.name()))
                    .andExpect(jsonPath("errors.oldPassword").value(ErrorCode.FIELD_CANNOT_BE_EMPTY.name()));
        }

        @Test
        public void whenNewPasswordEmpty_thenShouldReturnBadRequest() throws Exception {
            changePasswordRequest.setPassword("");
            mockMvc.perform(put(CHANGE_PASSWORD_URL).cookie(accessTokenCookie).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(changePasswordRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("message").value(ErrorCode.VALIDATION_FAILED.name()))
                    .andExpect(jsonPath("errors.password").value(ErrorCode.FIELD_CANNOT_BE_EMPTY.name()));
        }

        @Test
        public void whenOldPasswordDoesntMatch_thenShouldReturnBadRequest() throws Exception {
            changePasswordRequest.setOldPassword("Pqrs4321!");
            mockMvc.perform(put(CHANGE_PASSWORD_URL).cookie(accessTokenCookie).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(changePasswordRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("message").value(ErrorCode.USER_PASSWORD_MATCH_FAILED.name()));
        }

        @Test
        public void whenNewPasswordWeak_thenShouldReturnBadRequest() throws Exception {
            changePasswordRequest.setPassword("abcd1234!");
            mockMvc.perform(put(CHANGE_PASSWORD_URL).cookie(accessTokenCookie).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(changePasswordRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("message").value(ErrorCode.VALIDATION_FAILED.name()))
                    .andExpect(jsonPath("errors.password").value(ErrorCode.USER_WEAK_PASSWORD.name()));
        }

        @Test
        public void shouldUpdatePassword() throws Exception {
            mockMvc.perform(put(CHANGE_PASSWORD_URL).cookie(accessTokenCookie).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(changePasswordRequest)))
                    .andExpect(status().isOk());

            // check that the password actually changed
            User foundUser = userService.findByEmail("changepassword@example.com").orElse(null);
            assertThat(foundUser).isNotNull();
            assertTrue(passwordHasher.verifyPassword("Dcba4321!", foundUser.getPassword()));
        }
    }
}