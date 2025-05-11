package com.wilzwert.myjobs.infrastructure.api.rest.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.wilzwert.myjobs.core.domain.model.user.EmailStatus;
import com.wilzwert.myjobs.core.domain.model.user.User;
import com.wilzwert.myjobs.core.domain.ports.driven.UserService;
import com.wilzwert.myjobs.core.domain.shared.validation.ErrorCode;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.AuthResponse;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.LoginRequest;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.RegisterUserRequest;
import com.wilzwert.myjobs.infrastructure.configuration.AbstractBaseIntegrationTest;
import com.wilzwert.myjobs.infrastructure.security.service.JwtService;
import com.wilzwert.myjobs.infrastructure.security.service.RefreshTokenService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
        private static final String REGISTER_URL = "/api/auth/register";

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
            registerUserRequest.setLang("FR");
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
                    .andExpect(jsonPath("errors.email[0].code").value(ErrorCode.INVALID_EMAIL.name()));
        }

        @Test
        public void whenFirstNameEmpty_thenShouldReturnBadRequest() throws Exception {
            registerUserRequest.setFirstName("");
            mockMvc.perform(post(REGISTER_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(registerUserRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errors.firstName[0].code").value(ErrorCode.FIELD_CANNOT_BE_EMPTY.name()));
        }

        @Test
        public void whenLastNameEmpty_thenShouldReturnBadRequest() throws Exception {
            registerUserRequest.setLastName("");
            mockMvc.perform(post(REGISTER_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(registerUserRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errors.lastName[0].code").value(ErrorCode.FIELD_CANNOT_BE_EMPTY.name()));
        }

        @Test
        public void whenUsernameTooShort_thenShouldReturnBadRequest() throws Exception {
            registerUserRequest.setUsername("T");
            mockMvc.perform(post(REGISTER_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(registerUserRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errors.username[0].code").value(ErrorCode.FIELD_TOO_SHORT.name()));
        }

        @Test
        public void whenUsernameTooLong_thenShouldReturnBadRequest() throws Exception {
            registerUserRequest.setUsername("thisisafartoolongusernamethatshouldtriggeravalidationerror");
            mockMvc.perform(post(REGISTER_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(registerUserRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errors.username[0].code").value(ErrorCode.FIELD_TOO_LONG.name()));
        }

        @Test
        public void whenPasswordEmpty_thenShouldReturnBadRequest() throws Exception {
            registerUserRequest.setPassword("");
            mockMvc.perform(post(REGISTER_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(registerUserRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errors.password[0].code").value(ErrorCode.FIELD_CANNOT_BE_EMPTY.name()));
        }

        @Test
        public void whenPasswordInvalid_thenShouldReturnBadRequest() throws Exception {
            registerUserRequest.setPassword("pass");
            mockMvc.perform(post(REGISTER_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(registerUserRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errors.password[0].code").value(ErrorCode.USER_WEAK_PASSWORD.name()));
        }

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
            // we know we already have a User with the username 'existinguser' (see resources/test-data/user.json
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
                    .andExpect(jsonPath("lang").value("FR"))
                    .andExpect(jsonPath("jobFollowUpReminderDays").value(User.DEFAULT_JOB_FOLLOW_UP_REMINDER_DAYS))
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
            Optional<User> newUser = userService.findByEmail("test@example.com");
            if(newUser.isEmpty()) {
                fail("Created user should be retrievable.");
            }
            else {
                userService.deleteUser(newUser.get());
            }
        }
    }

    @Nested
    class AuthControllerLoginIT {
        private static final String LOGIN_URL = "/api/auth/login";

        @Test
        public void whenLoginFailed_thenShouldReturnUnauthorized() throws Exception {
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setEmail("existing@example.com");
            loginRequest.setPassword("abcd1234");

            mockMvc.perform(post(LOGIN_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        public void whenLoginSucceeded_thenShouldSetCookiesAndReturnAuthResponse() throws Exception {
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setEmail("existing@example.com");
            loginRequest.setPassword("Abcd1234!");

            MvcResult mvcResult = mockMvc.perform(post(LOGIN_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isOk())
                    .andReturn();

            AuthResponse authResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), AuthResponse.class);
            assertThat(authResponse).isNotNull();

            assertEquals("existing@example.com", authResponse.getEmail());
            assertEquals("existinguser", authResponse.getUsername());
            assertEquals("USER", authResponse.getRole());
            assertEquals(2, mvcResult.getResponse().getCookies().length);

            Map<String, Cookie> cookies = Stream.of(mvcResult.getResponse().getCookies())
                    .collect(Collectors.toMap(Cookie::getName, c -> c));
            assertThat(cookies.containsKey("access_token"));
            assertThat(cookies.containsKey("refresh_token"));
        }
    }

    @Nested
    class AuthControllerEmailAndUsernameCheckIT {

        @Test
        public void whenNoEmail_thenShouldReturnBadRequest() throws Exception {
            mockMvc.perform(get("/api/auth/email-check"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        public void whenEmailTaken_thenShouldReturnUnprocessableEntity() throws Exception {
            mockMvc.perform(get("/api/auth/email-check").param("email", "existing@example.com"))
                            .andExpect(status().isUnprocessableEntity());
        }

        @Test
        public void whenEmailAvailable_thenShouldReturnOk() throws Exception {
            mockMvc.perform(get("/api/auth/email-check").param("email", "notexisting@example.com"))
                    .andExpect(status().isOk());
        }

        @Test
        public void whenNoUsername_thenShouldReturnBadRequest() throws Exception {
            mockMvc.perform(get("/api/auth/username-check"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        public void whenUsernameTaken_thenShouldReturnUnprocessableEntity() throws Exception {
            mockMvc.perform(get("/api/auth/username-check").param("username", "existinguser"))
                    .andExpect(status().isUnprocessableEntity());
        }

        @Test
        public void whenUsernameAvailable_thenShouldReturnOk() throws Exception {
            mockMvc.perform(get("/api/auth/username-check").param("username", "notexisting"))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    class AuthControllerRefreshTokenIT {
        private static final String REFRESH_TOKEN_URL = "/api/auth/refresh-token";

        @Autowired
        private RefreshTokenService refreshTokenService;


        @Test
        public void whenRefreshTokenEmpty_thenShouldReturnUnauthorized() throws Exception {
            mockMvc.perform(post(REFRESH_TOKEN_URL))
                    .andExpect(status().isUnauthorized());
        }
        @Test
        public void whenRefreshTokenNotFound_thenShouldReturnUnauthorized() throws Exception {
            Cookie cookie = new Cookie("refresh_token", "notExisting");
            mockMvc.perform(
                        post(REFRESH_TOKEN_URL).cookie(cookie)
                    )
                    .andExpect(status().isUnauthorized());
        }

        @Test
        public void whenRefreshTokenExpired_thenShouldReturnUnauthorized() throws Exception {
            Cookie cookie = new Cookie("refresh_token", "expiredRefreshToken");
            mockMvc.perform(
                    post(REFRESH_TOKEN_URL).cookie(cookie)
                )
                .andExpect(status().isUnauthorized());
        }

        @Test
        public void whenUserNotFound_thenShouldReturnUnauthorized() throws Exception {
            Cookie cookie = new Cookie("refresh_token", "unknownUserRefreshToken");
            mockMvc.perform(
                            post(REFRESH_TOKEN_URL).cookie(cookie)
                    )
                    .andExpect(status().isUnauthorized());
        }

        @Test
        public void whenRefreshSuccess_thenShouldSetCookiesAndReturnAuthResponse() throws Exception {
            Cookie cookie = new Cookie("refresh_token", "validRefreshToken");
            MvcResult mvcResult = mockMvc.perform(post(REFRESH_TOKEN_URL).cookie(cookie))
                    .andExpect(status().isOk())
                    .andReturn();

            AuthResponse authResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), AuthResponse.class);
            assertThat(authResponse).isNotNull();

            assertEquals("existing@example.com", authResponse.getEmail());
            assertEquals("existinguser", authResponse.getUsername());
            assertEquals("USER", authResponse.getRole());

            assertEquals(2, mvcResult.getResponse().getCookies().length);
            Map<String, Cookie> cookies = Stream.of(mvcResult.getResponse().getCookies())
                    .collect(Collectors.toMap(Cookie::getName, c -> c));
            assertThat(cookies.containsKey("access_token"));
            assertNotEquals("validRefreshToken", cookies.get("access_token").getValue());
            assertThat(cookies.containsKey("refresh_token"));

            // let's check the initial valid refresh token was deleted
            assertThat(refreshTokenService.findByToken("validRefreshToken").isEmpty());

        }
    }

    @Nested
    class AuthControllerLogoutIT {

        @Autowired
        private JwtService jwtService;

        @Test
        public void whenUnauthenticated_thenShouldReturnUnauthorized() throws Exception {
            Cookie accessTokenCookie = new Cookie("access_token", "accessToken");
            Cookie refreshTokenCookie = new Cookie("refresh_token", "refreshToken");
            mockMvc.perform(post("/api/auth/logout").cookie(accessTokenCookie, refreshTokenCookie))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        public void shouldLogout() throws Exception {
            // generates a token for our "existinguser"
            String token = jwtService.generateToken("abcd1234-1234-1234-1234-123456789012");
            Cookie accessTokenCookie = new Cookie("access_token", token);
            Cookie refreshTokenCookie = new Cookie("refresh_token", "validRefreshTokenUsedForLogoutTest");

            MvcResult mvcResult = mockMvc.perform(
                    post("/api/auth/logout")
                    .cookie(accessTokenCookie, refreshTokenCookie)
                )
                .andExpect(status().isNoContent())
                .andReturn();

            assertEquals(2,mvcResult.getResponse().getCookies().length);
            Map<String, Cookie> cookies = Stream.of(mvcResult.getResponse().getCookies())
                    .collect(Collectors.toMap(Cookie::getName, c -> c));
            assertEquals(0, cookies.get("access_token").getMaxAge());
            assertEquals(0, cookies.get("refresh_token").getMaxAge());
        }
    }
}
