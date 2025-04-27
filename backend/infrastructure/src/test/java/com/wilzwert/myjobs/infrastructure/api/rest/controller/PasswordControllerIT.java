package com.wilzwert.myjobs.infrastructure.api.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wilzwert.myjobs.core.domain.model.user.User;
import com.wilzwert.myjobs.core.domain.ports.driven.UserService;
import com.wilzwert.myjobs.core.domain.shared.validation.ErrorCode;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.NewPasswordRequest;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.ResetPasswordRequest;
import com.wilzwert.myjobs.infrastructure.configuration.AbstractBaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.Fail.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class PasswordControllerIT extends AbstractBaseIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Nested
    class PasswordControllerResetPasswordIT {

        private static final String RESET_PASSWORD_URL = "/api/user/password/reset";

        @Test
        public void whenEmailInvalid_thenShouldReturnBadRequest() throws Exception {
            ResetPasswordRequest resetPasswordRequest = new ResetPasswordRequest();
            resetPasswordRequest.setEmail("test");

            mockMvc.perform(post(RESET_PASSWORD_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(resetPasswordRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("message").value("Validation error"))
                    .andExpect(jsonPath("errors.email").value(ErrorCode.INVALID_EMAIL.name()));
        }

        @Test
        public void whenEmailExists_thenShouldUpdateResetPasswordToken() throws Exception {
            ResetPasswordRequest resetPasswordRequest = new ResetPasswordRequest();
            resetPasswordRequest.setEmail("changepassword@example.com");

            mockMvc.perform(post(RESET_PASSWORD_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(resetPasswordRequest)))
                    .andExpect(status().isOk());

            Optional<User> foundUser = userService.findByEmail("changepassword@example.com");
            if(foundUser.isEmpty()) {
                fail("User should be retrievable");
            }
            else {
                assertThat(!foundUser.get().getResetPasswordToken().isEmpty());
            }
        }

        @Test
        public void whenEmailDoesntExist_thenShouldReturnOk() throws Exception {
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
        public void setUp()  {
            // this is a valid password request
            // it should be changed per case for testing
            changePasswordRequest = new NewPasswordRequest();
            changePasswordRequest.setPassword("Dcba4321!");
            changePasswordRequest.setToken("d61c8146-0e7c-4894-b0da-9903d69a7211");
        }

        @Test
        public void whenRequestBodyEmpty_thenShouldReturnBadRequest() throws Exception {
            mockMvc.perform(post(CREATE_PASSWORD_URL))
                    .andExpect(status().isBadRequest());
        }

        @Test
        public void whenTokenEmpty_thenShouldReturnBadRequest() throws Exception {
            changePasswordRequest.setToken("");
            mockMvc.perform(post(CREATE_PASSWORD_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(changePasswordRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("message").value("Validation error"))
                    .andExpect(jsonPath("errors.token").value(ErrorCode.FIELD_CANNOT_BE_EMPTY.name()));
        }

        @Test
        public void whenNewPasswordEmpty_thenShouldReturnBadRequest() throws Exception {
            changePasswordRequest.setPassword("");
            mockMvc.perform(post(CREATE_PASSWORD_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(changePasswordRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("message").value("Validation error"))
                    .andExpect(jsonPath("errors.password").value(ErrorCode.FIELD_CANNOT_BE_EMPTY.name()));
        }

        @Test
        public void whenNewPasswordWeak_thenShouldReturnBadRequest() throws Exception {
            changePasswordRequest.setPassword("abcd1234!");
            mockMvc.perform(post(CREATE_PASSWORD_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(changePasswordRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("message").value("Validation error"))
                    .andExpect(jsonPath("errors.password").value(ErrorCode.USER_WEAK_PASSWORD.name()));
        }

        @Test
        public void whenTokenExpired_thenShouldReturnBadRequest() throws Exception {
            changePasswordRequest.setToken("44a41127-a212-45e5-8170-3c57d2dc0caf");
            mockMvc.perform(post(CREATE_PASSWORD_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(changePasswordRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("message").value(ErrorCode.USER_PASSWORD_RESET_EXPIRED.name()));
        }

        @Test
        public void ShouldUpdatePassword() throws Exception {
            mockMvc.perform(post(CREATE_PASSWORD_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(changePasswordRequest)))
                    .andExpect(status().isOk());
        }

    }
}
