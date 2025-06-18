package com.wilzwert.myjobs.infrastructure.api.rest.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.wilzwert.myjobs.core.domain.model.user.EmailStatus;
import com.wilzwert.myjobs.core.domain.model.user.Lang;
import com.wilzwert.myjobs.core.domain.model.user.User;
import com.wilzwert.myjobs.core.domain.model.user.ports.driven.UserDataManager;
import com.wilzwert.myjobs.core.domain.shared.validation.ErrorCode;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.AuthResponse;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.LoginRequest;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.RegisterUserRequest;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.UserResponse;
import com.wilzwert.myjobs.infrastructure.configuration.AbstractBaseIntegrationTest;
import com.wilzwert.myjobs.infrastructure.security.service.JwtService;
import com.wilzwert.myjobs.infrastructure.security.service.RefreshTokenService;
import com.wilzwert.myjobs.infrastructure.utility.IntegrationEventUtility;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Wilhelm Zwertvaegher
 */

@AutoConfigureMockMvc
public class AuthControllerIT extends AbstractBaseIntegrationTest {

    @Autowired
    private IntegrationEventUtility integrationEventUtility;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserDataManager userDataManager;

    @Nested
    class AuthControllerRegisterIT {
        private static final String REGISTER_URL = "/api/auth/register";

        private RegisterUserRequest registerUserRequest;

        @BeforeEach
        void setup() {
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
        void whenRequestBodyEmpty_thenShouldReturnBadRequest() throws Exception {
            mockMvc.perform(post(REGISTER_URL))
                    .andExpect(status().isBadRequest());
        }
        @Test
        void whenEmailEmpty_thenShouldReturnBadRequestWhenEmailEmpty() throws Exception {
            registerUserRequest.setEmail("");
            mockMvc.perform(post(REGISTER_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(registerUserRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void whenEmailInvalid_thenShouldReturnBadRequest() throws Exception {
            registerUserRequest.setEmail("test");
            mockMvc.perform(post(REGISTER_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(registerUserRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errors.email[0].code").value(ErrorCode.INVALID_EMAIL.name()));
        }

        @Test
        void whenFirstNameEmpty_thenShouldReturnBadRequest() throws Exception {
            registerUserRequest.setFirstName("");
            mockMvc.perform(post(REGISTER_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(registerUserRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errors.firstName[0].code").value(ErrorCode.FIELD_CANNOT_BE_EMPTY.name()));
        }

        @Test
        void whenLastNameEmpty_thenShouldReturnBadRequest() throws Exception {
            registerUserRequest.setLastName("");
            mockMvc.perform(post(REGISTER_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(registerUserRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errors.lastName[0].code").value(ErrorCode.FIELD_CANNOT_BE_EMPTY.name()));
        }

        @Test
        void whenUsernameTooShort_thenShouldReturnUnprocessableEntity() throws Exception {
            registerUserRequest.setUsername("T");
            mockMvc.perform(post(REGISTER_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(registerUserRequest)))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("errors.username[0].code").value(ErrorCode.FIELD_TOO_SHORT.name()));
        }

        @Test
        void whenUsernameTooLong_thenShouldReturnUnprocessableEntity() throws Exception {
            registerUserRequest.setUsername("thisisafartoolongusernamethatshouldtriggeravalidationerror");
            mockMvc.perform(post(REGISTER_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(registerUserRequest)))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("errors.username[0].code").value(ErrorCode.FIELD_TOO_LONG.name()));
        }

        @Test
        void whenPasswordEmpty_thenShouldReturnBadRequest() throws Exception {
            registerUserRequest.setPassword("");
            mockMvc.perform(post(REGISTER_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(registerUserRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errors.password[0].code").value(ErrorCode.FIELD_CANNOT_BE_EMPTY.name()));
        }

        @Test
        void whenPasswordInvalid_thenShouldReturnUnprocessableEntity() throws Exception {
            registerUserRequest.setPassword("pass");
            mockMvc.perform(post(REGISTER_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(registerUserRequest)))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("errors.password[0].code").value(ErrorCode.USER_WEAK_PASSWORD.name()));
        }

        @Test
        void whenEmailAlreadyExists_thenShouldReturnBadRequest() throws Exception {
            // we know we already have a User with the email 'existing@example.com' (see resources/test-data/user.json
            registerUserRequest.setEmail("existing@example.com");

            mockMvc.perform(post(REGISTER_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(registerUserRequest)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("message").value(ErrorCode.USER_ALREADY_EXISTS.name()));
        }

        @Test
        void whenUsernameAlreadyExists_thenShouldReturnBadRequest() throws Exception {
            // we know we already have a User with the username 'existinguser' (see resources/test-data/user.json
            registerUserRequest.setUsername("existinguser");

            mockMvc.perform(post(REGISTER_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(registerUserRequest)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("message").value(ErrorCode.USER_ALREADY_EXISTS.name()));
        }

        @Test
        void shouldRegisterUser() throws Exception {

            Instant beforeCall = Instant.now();
            MvcResult result = mockMvc.perform(post(REGISTER_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(registerUserRequest)))
                    .andExpect(status().isOk())
                    .andReturn();


            UserResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), UserResponse.class);
            assertThat(response).isNotNull();
            assertThat(response.getUsername()).isEqualTo("username");
            assertThat(response.getEmail()).isEqualTo("test@example.com");
            assertThat(response.getFirstName()).isEqualTo("firstName");
            assertThat(response.getLastName()).isEqualTo("lastName");
            assertThat(response.getLang()).isEqualTo(Lang.FR);
            assertThat(response.getJobFollowUpReminderDays()).isEqualTo(User.DEFAULT_JOB_FOLLOW_UP_REMINDER_DAYS);
            assertThat(response.getEmailStatus()).isEqualTo(EmailStatus.PENDING.name());
            assertThat(response.getCreatedAt()).isNotNull();

            Instant afterCall = Instant.now();
            // FIXME this is quite ugly but we have to make sure createdAt is consistent
            Instant instant = Instant.parse(response.getCreatedAt().toString());
            assertThat(instant)
                    .isAfterOrEqualTo(beforeCall)
                    .isBeforeOrEqualTo(afterCall);

            // delete the created user to allow predictable further tests
            Optional<User> newUser = userDataManager.findByEmail("test@example.com");
            if(newUser.isEmpty()) {
                fail("Created user should be retrievable.");
            }
            else {
                // an integration event should have been created
                integrationEventUtility.assertEventCreated("UserCreatedEvent", newUser.get().getId());
                userDataManager.deleteUser(newUser.get());
            }
        }
    }

    @Nested
    class AuthControllerLoginIT {
        private static final String LOGIN_URL = "/api/auth/login";

        @Test
        void whenLoginFailed_thenShouldReturnUnauthorized() throws Exception {
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setEmail("existing@example.com");
            loginRequest.setPassword("abcd1234");

            mockMvc.perform(post(LOGIN_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void whenLoginSucceeded_thenShouldSetCookiesAndReturnAuthResponse() throws Exception {
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setEmail("existing@example.com");
            loginRequest.setPassword("Abcd1234!");

            MvcResult mvcResult = mockMvc.perform(post(LOGIN_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isOk())
                    .andReturn();

            AuthResponse authResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), AuthResponse.class);
            assertThat(authResponse).isNotNull();

            assertThat(authResponse.getEmail()).isEqualTo("existing@example.com");
            assertThat(authResponse.getUsername()).isEqualTo("existinguser");
            assertThat(authResponse.getRole()).isEqualTo("USER");
            assertThat(mvcResult.getResponse().getCookies()).hasSize(2);

            Map<String, Cookie> cookies = Stream.of(mvcResult.getResponse().getCookies())
                    .collect(Collectors.toMap(Cookie::getName, c -> c));
            assertThat(cookies)
                    .containsKey("access_token")
                    .containsKey("refresh_token");
        }
    }

    @Nested
    class AuthControllerEmailAndUsernameCheckIT {

        @Test
        void whenNoEmail_thenShouldReturnBadRequest() throws Exception {
            mockMvc.perform(get("/api/auth/email-check"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void whenEmailTaken_thenShouldReturnUnprocessableEntity() throws Exception {
            mockMvc.perform(get("/api/auth/email-check").param("email", "existing@example.com"))
                            .andExpect(status().isUnprocessableEntity());
        }

        @Test
        void whenEmailAvailable_thenShouldReturnOk() throws Exception {
            mockMvc.perform(get("/api/auth/email-check").param("email", "notexisting@example.com"))
                    .andExpect(status().isOk());
        }

        @Test
        void whenNoUsername_thenShouldReturnBadRequest() throws Exception {
            mockMvc.perform(get("/api/auth/username-check"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void whenUsernameTaken_thenShouldReturnUnprocessableEntity() throws Exception {
            mockMvc.perform(get("/api/auth/username-check").param("username", "existinguser"))
                    .andExpect(status().isUnprocessableEntity());
        }

        @Test
        void whenUsernameAvailable_thenShouldReturnOk() throws Exception {
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
        void whenRefreshTokenEmpty_thenShouldReturnUnauthorized() throws Exception {
            mockMvc.perform(post(REFRESH_TOKEN_URL))
                    .andExpect(status().isUnauthorized());
        }

        @ParameterizedTest
        @ValueSource(strings = {"notExisting", "expiredRefreshToken", "unknownUserRefreshToken"})
        void shouldReturnUnauthorized(String refreshToken) throws Exception {
            Cookie cookie = new Cookie("refresh_token", refreshToken);
            mockMvc.perform(
                        post(REFRESH_TOKEN_URL).cookie(cookie)
                    )
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void whenRefreshSuccess_thenShouldSetCookiesAndReturnAuthResponse() throws Exception {
            Cookie cookie = new Cookie("refresh_token", "validRefreshToken");
            MvcResult mvcResult = mockMvc.perform(post(REFRESH_TOKEN_URL).cookie(cookie))
                    .andExpect(status().isOk())
                    .andReturn();

            AuthResponse authResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), AuthResponse.class);
            assertThat(authResponse).isNotNull();
            assertThat(authResponse.getEmail()).isEqualTo("existing@example.com");
            assertThat(authResponse.getUsername()).isEqualTo("existinguser");
            assertThat(authResponse.getRole()).isEqualTo("USER");
            assertThat(mvcResult.getResponse().getCookies()).hasSize(2);
            Map<String, Cookie> cookies = Stream.of(mvcResult.getResponse().getCookies())
                    .collect(Collectors.toMap(Cookie::getName, c -> c));
            assertThat(cookies).containsKey("access_token");
            assertThat(cookies.get("access_token").getValue()).isNotEqualTo("validRefreshToken");
            assertThat(cookies).containsKey("refresh_token");

            // let's check the initial valid refresh token was deleted
            assertThat(refreshTokenService.findByToken("validRefreshToken")).isEmpty();

        }
    }

    @Nested
    class AuthControllerLogoutIT {

        @Autowired
        private JwtService jwtService;

        @Test
        void whenUnauthenticated_thenShouldReturnUnauthorized() throws Exception {
            Cookie accessTokenCookie = new Cookie("access_token", "accessToken");
            Cookie refreshTokenCookie = new Cookie("refresh_token", "refreshToken");
            mockMvc.perform(post("/api/auth/logout").cookie(accessTokenCookie, refreshTokenCookie))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void shouldLogout() throws Exception {
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

            assertThat(mvcResult.getResponse().getCookies()).hasSize(2);
            Map<String, Cookie> cookies = Stream.of(mvcResult.getResponse().getCookies())
                    .collect(Collectors.toMap(Cookie::getName, c -> c));
            assertThat(cookies.get("access_token").getMaxAge()).isEqualTo(0);
            assertThat(cookies.get("refresh_token").getMaxAge()).isEqualTo(0);
        }
    }
}
