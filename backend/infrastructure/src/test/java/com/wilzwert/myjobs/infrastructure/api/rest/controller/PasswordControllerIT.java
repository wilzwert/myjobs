package com.wilzwert.myjobs.infrastructure.api.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wilzwert.myjobs.core.domain.model.user.User;
import com.wilzwert.myjobs.core.domain.model.user.ports.driven.PasswordHasher;
import com.wilzwert.myjobs.core.domain.model.user.ports.driven.UserDataManager;
import com.wilzwert.myjobs.core.domain.shared.validation.ErrorCode;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.ChangePasswordRequest;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.NewPasswordRequest;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.ResetPasswordRequest;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class PasswordControllerIT extends AbstractBaseIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserDataManager userDataManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordHasher passwordHasher;

    @Nested
    class PasswordControllerResetPasswordIT {

        private static final String RESET_PASSWORD_URL = "/api/user/password/reset";

        @Test
        void whenEmailInvalid_thenShouldReturnBadRequest() throws Exception {
            ResetPasswordRequest resetPasswordRequest = new ResetPasswordRequest();
            resetPasswordRequest.setEmail("test");

            mockMvc.perform(post(RESET_PASSWORD_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(resetPasswordRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("message").value(ErrorCode.VALIDATION_FAILED.name()))
                    .andExpect(jsonPath("errors.email[0].code").value(ErrorCode.INVALID_EMAIL.name()));
        }

        @Test
        void whenEmailExists_thenShouldUpdateResetPasswordToken() throws Exception {
            ResetPasswordRequest resetPasswordRequest = new ResetPasswordRequest();
            resetPasswordRequest.setEmail("changepassword@example.com");

            mockMvc.perform(post(RESET_PASSWORD_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(resetPasswordRequest)))
                    .andExpect(status().isOk());

            Optional<User> foundUser = userDataManager.findByEmail("changepassword@example.com");
            if(foundUser.isEmpty()) {
                fail("User should be retrievable");
            }
            else {
                assertThat(foundUser.get().getResetPasswordToken()).isNotEmpty();
            }
        }

        @Test
        void whenEmailDoesntExist_thenShouldReturnOk() throws Exception {
            ResetPasswordRequest resetPasswordRequest = new ResetPasswordRequest();
            resetPasswordRequest.setEmail("test@test.com");

            mockMvc.perform(post(RESET_PASSWORD_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(resetPasswordRequest)))
                    .andExpect(status().isOk());
        }
    }


    @Nested
    class PasswordControllerCreatePasswordIT {

        private static final String CREATE_PASSWORD_URL = "/api/user/password";

        private NewPasswordRequest changePasswordRequest;

        @BeforeEach
        void setUp()  {
            // this is a valid password request
            // it should be changed per case for testing
            changePasswordRequest = new NewPasswordRequest();
            changePasswordRequest.setPassword("Dcba4321!");
            changePasswordRequest.setToken("d61c8146-0e7c-4894-b0da-9903d69a7211");
        }

        @Test
        void whenRequestBodyEmpty_thenShouldReturnBadRequest() throws Exception {
            mockMvc.perform(post(CREATE_PASSWORD_URL))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void whenTokenEmpty_thenShouldReturnBadRequest() throws Exception {
            changePasswordRequest.setToken("");
            mockMvc.perform(post(CREATE_PASSWORD_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(changePasswordRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("message").value(ErrorCode.VALIDATION_FAILED.name()))
                    .andExpect(jsonPath("errors.token[0].code").value(ErrorCode.FIELD_CANNOT_BE_EMPTY.name()));
        }

        @Test
        void whenNewPasswordEmpty_thenShouldReturnBadRequest() throws Exception {
            changePasswordRequest.setPassword("");
            mockMvc.perform(post(CREATE_PASSWORD_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(changePasswordRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("message").value(ErrorCode.VALIDATION_FAILED.name()))
                    .andExpect(jsonPath("errors.password[0].code").value(ErrorCode.FIELD_CANNOT_BE_EMPTY.name()));
        }

        @Test
        void whenNewPasswordWeak_thenShouldReturnUnprocessableEntity() throws Exception {
            changePasswordRequest.setPassword("abcd1234!");
            mockMvc.perform(post(CREATE_PASSWORD_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(changePasswordRequest)))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("message").value(ErrorCode.VALIDATION_FAILED.name()))
                    .andExpect(jsonPath("errors.password[0].code").value(ErrorCode.USER_WEAK_PASSWORD.name()));
        }

        @Test
        void whenTokenExpired_thenShouldReturnBadRequest() throws Exception {
            changePasswordRequest.setToken("44a41127-a212-45e5-8170-3c57d2dc0caf");
            mockMvc.perform(post(CREATE_PASSWORD_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(changePasswordRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("message").value(ErrorCode.USER_PASSWORD_RESET_EXPIRED.name()));
        }

        @Test
        void ShouldUpdatePassword() throws Exception {
            mockMvc.perform(post(CREATE_PASSWORD_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(changePasswordRequest)))
                    .andExpect(status().isOk());
        }

    }

    @Nested
    class UserControllerChangePasswordIT {

        private static final String CHANGE_PASSWORD_URL = "/api/user/me/password";

        // id of the User to use for password changes tests
        private static final String USER_FOR_CHANGE_PASSWORD_TEST_ID = "abcd6543-6543-6543-6543-123456789012";

        private ChangePasswordRequest changePasswordRequest;

        private Cookie accessTokenCookie;

        @BeforeEach
        void setUp()  {
            // this is a valid password request
            // it should be changed per case for testing
            changePasswordRequest = new ChangePasswordRequest();
            changePasswordRequest.setPassword("Dcba4321!");
            changePasswordRequest.setOldPassword("Abcd1234!");

            accessTokenCookie = new Cookie("access_token", jwtService.generateToken(USER_FOR_CHANGE_PASSWORD_TEST_ID));
        }

        @Test
        void whenNoAuth_thenShouldReturnUnauthorized() throws Exception {
            mockMvc.perform(put(CHANGE_PASSWORD_URL))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void whenRequestBodyEmpty_thenShouldReturnBadRequest() throws Exception {
            mockMvc.perform(put(CHANGE_PASSWORD_URL).cookie(accessTokenCookie))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void whenOldPasswordEmpty_thenShouldReturnBadRequest() throws Exception {
            changePasswordRequest.setOldPassword("");
            mockMvc.perform(put(CHANGE_PASSWORD_URL).cookie(accessTokenCookie).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(changePasswordRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("message").value(ErrorCode.VALIDATION_FAILED.name()))
                    .andExpect(jsonPath("errors.oldPassword[0].code").value(ErrorCode.FIELD_CANNOT_BE_EMPTY.name()));
        }

        @Test
        void whenNewPasswordEmpty_thenShouldReturnBadRequest() throws Exception {
            changePasswordRequest.setPassword("");
            mockMvc.perform(put(CHANGE_PASSWORD_URL).cookie(accessTokenCookie).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(changePasswordRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("message").value(ErrorCode.VALIDATION_FAILED.name()))
                    .andExpect(jsonPath("errors.password[0].code").value(ErrorCode.FIELD_CANNOT_BE_EMPTY.name()));
        }

        @Test
        void whenOldPasswordDoesntMatch_thenShouldReturnBadRequest() throws Exception {
            changePasswordRequest.setOldPassword("Pqrs4321!");
            mockMvc.perform(put(CHANGE_PASSWORD_URL).cookie(accessTokenCookie).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(changePasswordRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("message").value(ErrorCode.USER_PASSWORD_MATCH_FAILED.name()));
        }

        @Test
        void whenNewPasswordWeak_thenShouldReturnUnprocessableEntity() throws Exception {
            changePasswordRequest.setPassword("abcd1234!");
            mockMvc.perform(put(CHANGE_PASSWORD_URL).cookie(accessTokenCookie).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(changePasswordRequest)))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("message").value(ErrorCode.VALIDATION_FAILED.name()))
                    .andExpect(jsonPath("errors.password[0].code").value(ErrorCode.USER_WEAK_PASSWORD.name()));
        }

        @Test
        void shouldUpdatePassword() throws Exception {
            mockMvc.perform(put(CHANGE_PASSWORD_URL).cookie(accessTokenCookie).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(changePasswordRequest)))
                    .andExpect(status().isOk());

            // check that the password actually changed
            User foundUser = userDataManager.findByEmail("changepassword@example.com").orElse(null);
            assertThat(foundUser).isNotNull();
            assertTrue(passwordHasher.verifyPassword("Dcba4321!", foundUser.getPassword()));
        }
    }
}
