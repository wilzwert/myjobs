package com.wilzwert.myjobs.infrastructure.api.rest.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.RegisterUserRequest;
import com.wilzwert.myjobs.infrastructure.configuration.AbstractBaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Wilhelm Zwertvaegher
 * Date:25/04/2025
 * Time:09:18
 */

@AutoConfigureMockMvc
@Tag("Integration")
public class AuthControllerIT extends AbstractBaseIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
            registerUserRequest.setFirstName("Test");
            registerUserRequest.setLastName("User");
            registerUserRequest.setPassword("abcd1234");
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
                    .andExpect(status().isBadRequest());
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
                    .andExpect(status().isBadRequest());
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

        @Test
        public void shouldReturnBadRequestWhenPasswordEmpty() throws Exception {
            signupRequest.setPassword("");
            mockMvc.perform(post(REGISTER_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(signupRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        public void shouldReturnBadRequestWhenPasswordTooShort() throws Exception {
            signupRequest.setPassword("pass");
            mockMvc.perform(post(REGISTER_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(signupRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        public void shouldReturnBadRequestWhenPasswordTooLong() throws Exception {
            signupRequest.setLastName("Testingtoolongpasswordinaresgisterrequest");
            mockMvc.perform(post(REGISTER_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(signupRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        public void shouldReturnBadRequestWhenUserAlreadyExists() throws Exception {
            when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

            mockMvc.perform(post(REGISTER_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(signupRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("message").value("Error: Email is already taken!"));
        }

        @Test
        public void shouldRegisterUser() throws Exception {
            when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

            mockMvc.perform(post(REGISTER_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(signupRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("message").value("User registered successfully!"));
        }*/
    }
}
