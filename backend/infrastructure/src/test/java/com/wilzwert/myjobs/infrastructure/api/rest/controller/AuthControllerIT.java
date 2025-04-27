package com.wilzwert.myjobs.infrastructure.api.rest.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.wilzwert.myjobs.core.domain.model.user.EmailStatus;
import com.wilzwert.myjobs.core.domain.model.user.User;
import com.wilzwert.myjobs.core.domain.ports.driven.UserService;
import com.wilzwert.myjobs.core.domain.shared.validation.ErrorCode;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.RegisterUserRequest;
import com.wilzwert.myjobs.infrastructure.configuration.AbstractBaseIntegrationTest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Wilhelm Zwertvaegher
 * Date:25/04/2025
 * Time:09:18
 */

@AutoConfigureMockMvc
public class AuthControllerIT extends AbstractBaseIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Nested
    class AuthControllerRegisterIT {
        private final static String REGISTER_URL = "/api/auth/register";

        private RegisterUserRequest registerUserRequest;

        @BeforeEach
        public void setup() {
            // setup a default valid signup request
            registerUserRequest = new RegisterUserRequest();
            registerUserRequest.setEmail("test@example.com");
            registerUserRequest.setUsername("username");
            registerUserRequest.setFirstName("firstName");
            registerUserRequest.setLastName("lastName");
            registerUserRequest.setPassword("Abcd1234!");
        }

        @Test
        public void whenRequestBodyEmpty_thenShouldReturnBadRequest() throws Exception {
            mockMvc.perform(post(REGISTER_URL))
                    .andExpect(status().isBadRequest());
        }
        @Test
        public void whenEmailEmpty_thenShouldReturnBadRequestWhenEmailEmpty() throws Exception {
            registerUserRequest.setEmail("");
            mockMvc.perform(post(REGISTER_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(registerUserRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        public void whenEmailInvalid_thenShouldReturnBadRequest() throws Exception {
            registerUserRequest.setEmail("test");
            mockMvc.perform(post(REGISTER_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(registerUserRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errors.email").value(ErrorCode.INVALID_EMAIL.name()));
        }
        /*
        @Test
        public void shouldReturnBadRequestWhenEmailTooLong() throws Exception {
            signupRequest.setEmail("testingaverylongemailaddress@averylongtestdomain.com");
            mockMvc.perform(post(REGISTER_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(signupRequest)))
                    .andExpect(status().isBadRequest());
        }*/

        @Test
        public void whenFirstNameEmpty_thenShouldReturnBadRequest() throws Exception {
            registerUserRequest.setFirstName("");
            mockMvc.perform(post(REGISTER_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(registerUserRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errors.firstName").value(ErrorCode.FIELD_CANNOT_BE_EMPTY.name()));
        }

        @Test
        public void whenLastNameEmpty_thenShouldReturnBadRequest() throws Exception {
            registerUserRequest.setLastName("");
            mockMvc.perform(post(REGISTER_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(registerUserRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errors.lastName").value(ErrorCode.FIELD_CANNOT_BE_EMPTY.name()));
        }

                /*

                .requireNotEmpty("firstName", firstName)
                .requireNotEmpty("lastName", lastName)
                .requireNotEmpty("password", password)*/

        @Test
        public void whenUsernameTooShort_thenShouldReturnBadRequest() throws Exception {
            registerUserRequest.setUsername("T");
            mockMvc.perform(post(REGISTER_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(registerUserRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errors.username").value(ErrorCode.FIELD_TOO_SHORT.name()));
        }

        @Test
        public void whenUsernameTooLong_thenShouldReturnBadRequest() throws Exception {
            registerUserRequest.setUsername("thisisafartoolongusernamethatshouldtriggeravalidationerror");
            mockMvc.perform(post(REGISTER_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(registerUserRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errors.username").value(ErrorCode.FIELD_TOO_LONG.name()));
        }


        /*
        @Test
        public void shouldReturnBadRequestWhenFirstNameTooShort() throws Exception {
            signupRequest.setFirstName("Te");
            mockMvc.perform(post(REGISTER_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(signupRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        public void shouldReturnBadRequestWhenFirstNameTooLong() throws Exception {
            signupRequest.setFirstName("Teststoolongfirstname");
            mockMvc.perform(post(REGISTER_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(signupRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        public void shouldReturnBadRequestWhenLastNameEmpty() throws Exception {
            signupRequest.setLastName("");
            mockMvc.perform(post(REGISTER_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(signupRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        public void shouldReturnBadRequestWhenLastNameTooShort() throws Exception {
            signupRequest.setLastName("Us");
            mockMvc.perform(post(REGISTER_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(signupRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        public void shouldReturnBadRequestWhenLastNameTooLong() throws Exception {
            signupRequest.setLastName("Testingtoolonglastname");
            mockMvc.perform(post(REGISTER_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(signupRequest)))
                    .andExpect(status().isBadRequest());
        }
        */
        @Test
        public void whenPasswordEmpty_thenShouldReturnBadRequest() throws Exception {
            registerUserRequest.setPassword("");
            mockMvc.perform(post(REGISTER_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(registerUserRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errors.password").value(ErrorCode.FIELD_CANNOT_BE_EMPTY.name()));
        }

        @Test
        public void whenPasswordInvalid_thenShouldReturnBadRequest() throws Exception {
            registerUserRequest.setPassword("pass");
            mockMvc.perform(post(REGISTER_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(registerUserRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errors.password").value(ErrorCode.USER_WEAK_PASSWORD.name()));
        }
        /*
        @Test
        public void shouldReturnBadRequestWhenPasswordTooLong() throws Exception {
            signupRequest.setLastName("Testingtoolongpasswordinaresgisterrequest");
            mockMvc.perform(post(REGISTER_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(signupRequest)))
                    .andExpect(status().isBadRequest());
        }
        */
        @Test
        public void whenEmailAlreadyExists_thenShouldReturnBadRequest() throws Exception {
            // we know we already have a User with the email 'existing@example.com' (see resources/test-data/user.json
            registerUserRequest.setEmail("existing@example.com");

            mockMvc.perform(post(REGISTER_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(registerUserRequest)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("message").value(ErrorCode.USER_ALREADY_EXISTS.name()));
        }

        @Test
        public void whenUsernameAlreadyExists_thenShouldReturnBadRequest() throws Exception {
            // we know we already have a User with the email 'existing@example.com' (see resources/test-data/user.json
            registerUserRequest.setUsername("existinguser");

            mockMvc.perform(post(REGISTER_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(registerUserRequest)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("message").value(ErrorCode.USER_ALREADY_EXISTS.name()));
        }

        @Test
        public void shouldRegisterUser() throws Exception {

            Instant beforeCall = Instant.now();
            MvcResult result = mockMvc.perform(post(REGISTER_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(registerUserRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("username").value("username"))
                    .andExpect(jsonPath("firstName").value("firstName"))
                    .andExpect(jsonPath("lastName").value("lastName"))
                    .andExpect(jsonPath("email").value("test@example.com"))
                    .andExpect(jsonPath("emailStatus").value(EmailStatus.PENDING.name()))
                    .andExpect(jsonPath("createdAt").isNotEmpty())
                    .andReturn();

            Instant afterCall = Instant.now();
            String createdAt = objectMapper.readTree(result.getResponse().getContentAsString()).get("createdAt").asText();
            // FIXME this is quite ugly but we have to make sure createdAt is consistent
            Instant instant = Instant.parse(createdAt);
            assertThat(instant)
                    .isAfterOrEqualTo(beforeCall)
                    .isBeforeOrEqualTo(afterCall);


            // delete the created user to allow predictable further tests
            // userService.deleteUser(userService.findByEmail("test@example.com").orElse());
            Optional<User> newUser = userService.findByEmail("test@example.com");
            if(newUser.isEmpty()) {
                fail("Created user should be retrievable.");
            }
            else {
                userService.deleteUser(newUser.get());
            }
        }
    }
}
