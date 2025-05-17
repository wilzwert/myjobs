package com.wilzwert.myjobs.infrastructure.api.rest.controller;


import com.wilzwert.myjobs.core.domain.model.user.command.RegisterUserCommand;
import com.wilzwert.myjobs.core.domain.model.user.exception.UserAlreadyExistsException;
import com.wilzwert.myjobs.core.domain.model.user.*;
import com.wilzwert.myjobs.core.domain.model.user.ports.driven.UserService;
import com.wilzwert.myjobs.core.domain.model.user.ports.driving.CheckUserAvailabilityUseCase;
import com.wilzwert.myjobs.core.domain.model.user.ports.driving.LoginUseCase;
import com.wilzwert.myjobs.core.domain.model.user.ports.driving.RegisterUseCase;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.AuthResponse;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.LoginRequest;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.RegisterUserRequest;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.UserResponse;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.entity.MongoRefreshToken;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.mapper.UserMapper;
import com.wilzwert.myjobs.infrastructure.security.jwt.JwtAuthenticatedUser;
import com.wilzwert.myjobs.infrastructure.security.model.RefreshToken;
import com.wilzwert.myjobs.infrastructure.security.service.CookieService;
import com.wilzwert.myjobs.infrastructure.security.service.JwtService;
import com.wilzwert.myjobs.infrastructure.security.service.RefreshTokenService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @author Wilhelm Zwertvaegher
 * Date:11/04/2025
 * Time:08:58
 */

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private RegisterUseCase registerUseCase;

    @Mock
    private LoginUseCase loginUseCase;

    @Mock
    private CheckUserAvailabilityUseCase checkUserAvailabilityUseCase;

    @Mock
    private CookieService cookieService;

    @Nested
    class RegisterTest {

        @Test
        public void whenUserAlreadyExists_thenShouldThrowConflictResponseStatusExceptionOnRegister() {
            RegisterUserRequest registerUserRequest = new RegisterUserRequest();
            registerUserRequest.setUsername("test");
            registerUserRequest.setEmail("test@example.com");

            RegisterUserCommand registerUserCommand = new RegisterUserCommand("test@example.com", "password", "username", "firstName", "lastName", null);

            when(userMapper.toCommand(registerUserRequest)).thenReturn(registerUserCommand);
            when(registerUseCase.registerUser(registerUserCommand)).thenThrow(new UserAlreadyExistsException());

            assertThrows(UserAlreadyExistsException.class, () -> authController.register(registerUserRequest));
            verify(userMapper, times(1)).toCommand(registerUserRequest);
            verify(registerUseCase, times(1)).registerUser(registerUserCommand);
            verify(userMapper, times(0)).toResponse(any(User.class));
        }

        @Test
        public void whenRegistrationSucceeded_thenShouldReturnUserResponse() {
            RegisterUserRequest registerUserRequest = new RegisterUserRequest();
            registerUserRequest.setUsername("test");
            registerUserRequest.setEmail("test@example.com");
            RegisterUserCommand registerUserCommand = new RegisterUserCommand("test@example.com", "password", "test", "firstName", "lastName", null);
            User registeredUser = User.builder()
                    .email("test@example.come")
                    .username("test")
                    .password("password")
                    .firstName("firstName")
                    .lastName("lastName")
                    .createdAt(Instant.now())
                    .emailStatus(EmailStatus.PENDING)
                    .build();
            UserResponse expectedUserResponse = UserResponse.builder()
                    .firstName("firstName")
                    .lastName("lastName")
                    .email("test@example.com")
                    .username("test")
                    .lang(Lang.EN)
                    .createdAt(Instant.now())
                    .emailStatus(EmailStatus.PENDING.toString())
                    .build();

            when(userMapper.toCommand(registerUserRequest)).thenReturn(registerUserCommand);
            when(registerUseCase.registerUser(registerUserCommand)).thenReturn(registeredUser);
            when(userMapper.toResponse(registeredUser)).thenReturn(expectedUserResponse);

            UserResponse response = authController.register(registerUserRequest);

            assertThat(response).isEqualTo(expectedUserResponse);

            verify(userMapper, times(1)).toCommand(registerUserRequest);
            verify(registerUseCase, times(1)).registerUser(registerUserCommand);
            verify(userMapper, times(1)).toResponse(registeredUser);
        }
    }

    @Nested
    class LogoutTest {
        @Test
        public void shouldLogout() {
            when(cookieService.revokeAccessTokenCookie()).thenReturn(ResponseCookie.from("access_token", "").build());
            when(cookieService.revokeRefreshTokenCookie()).thenReturn(ResponseCookie.from("refresh_token", "").build());

            ResponseEntity<Void> response = authController.logout();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
            verify(cookieService, times(1)).revokeAccessTokenCookie();
            verify(cookieService, times(1)).revokeRefreshTokenCookie();
        }
    }

    @Nested
    class LoginTest {

        @Test
        public void whenLoginFailed_thenShouldThrowUnauthorizedResponseStatusException() {
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setEmail("test@example.com");
            loginRequest.setPassword("Abcd!1234");

            when(loginUseCase.authenticateUser("test@example.com", "Abcd!1234")).thenThrow(new AuthenticationException("User not found"){
                @Override
                public String getMessage() {
                    return super.getMessage();
                }
            });

            ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> authController.login(loginRequest));
            assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        }

        @Test
        public void whenLoginSucceeded_thenShouldSetCookiesAndReturnAuthResponse() {
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setEmail("test@example.com");
            loginRequest.setPassword("Abcd!1234");

            String accessToken = "access_token";
            String refreshToken = "refresh_token";
            AuthenticatedUser authenticatedUser = new JwtAuthenticatedUser("test@example.com", "username", accessToken, refreshToken, "USER");

            when(loginUseCase.authenticateUser("test@example.com", "Abcd!1234")).thenReturn(authenticatedUser);
            when(cookieService.createAccessTokenCookie("access_token")).thenReturn(ResponseCookie.from("access_token", "access_token").maxAge(600L).build());
            when(cookieService.createRefreshTokenCookie("refresh_token")).thenReturn(ResponseCookie.from("refresh_token", "refresh_token").maxAge(3600L).build());

            ResponseEntity<AuthResponse> authResponse = authController.login(loginRequest);

            assertThat(authResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

            List<String> cookies = authResponse.getHeaders().get(HttpHeaders.SET_COOKIE);
            assertThat(cookies).hasSize(2);
            assertThat(cookies.getFirst()).contains("access_token=access_token");
            assertThat(cookies.get(1)).contains("refresh_token=refresh_token");

            AuthResponse body = authResponse.getBody();
            assertThat(body).isNotNull();
            assertThat(body.getEmail()).isEqualTo("test@example.com");
            assertThat(body.getUsername()).isEqualTo("username");
            assertThat(body.getRole()).isEqualTo("USER");
        }
    }

    @Nested
    class EmailAndUsernameCheckTest {
        @Test
        public void whenEmailTaken_thenShouldReturnUnprocessableEntity() {
            when(checkUserAvailabilityUseCase.isEmailTaken("test@example.com")).thenReturn(true);

            ResponseEntity<?> responseEntity = authController.emailCheck("test@example.com");

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
            verify(checkUserAvailabilityUseCase, times(1)).isEmailTaken("test@example.com");
        }

        @Test
        public void whenUsernameTaken_thenShouldReturnUnprocessableEntity() {
            when(checkUserAvailabilityUseCase.isUsernameTaken("test")).thenReturn(true);

            ResponseEntity<?> responseEntity = authController.usernameCheck("test");

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
            verify(checkUserAvailabilityUseCase, times(1)).isUsernameTaken("test");
        }

        @Test
        public void whenEmailAvailable_thenShouldReturnOk() {
            when(checkUserAvailabilityUseCase.isEmailTaken("test@example.com")).thenReturn(true);

            ResponseEntity<?> responseEntity = authController.emailCheck("test@example.com");

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
            verify(checkUserAvailabilityUseCase, times(1)).isEmailTaken("test@example.com");
        }

        @Test
        public void whenUsernameAvailable_thenShouldReturnOk() {
            when(checkUserAvailabilityUseCase.isUsernameTaken("test")).thenReturn(false);

            ResponseEntity<?> responseEntity = authController.usernameCheck("test");

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            verify(checkUserAvailabilityUseCase, times(1)).isUsernameTaken("test");
        }
    }

    @Nested
    class RefreshTokenTest {

        @Test
        public void whenRefreshTokenEmpty_thenShouldReturnUnauthorizedResponse() {
            ResponseEntity<?> responseEntity = authController.refreshAccessToken(null);
            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        }

        @Test
        public void whenRefreshTokenNotFound_thenShouldThrowUnauthorizedResponseStatusException() {
            when(refreshTokenService.findByToken("refresh_token")).thenReturn(Optional.empty());

            ResponseEntity<?> responseEntity = authController.refreshAccessToken("refresh_token");
            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
            verify(refreshTokenService, times(1)).findByToken("refresh_token");
        }

        @Test
        public void whenRefreshTokenExpired_thenShouldThrowUnauthorizedResponseStatusException() {
            RefreshToken refreshToken = new MongoRefreshToken().setToken("refresh_token");
            when(refreshTokenService.findByToken("refresh_token")).thenReturn(Optional.of(refreshToken));
            when(refreshTokenService.verifyExpiration(refreshToken)).thenReturn(false);

            ResponseEntity<?> responseEntity = authController.refreshAccessToken("refresh_token");
            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
            verify(refreshTokenService, times(1)).findByToken("refresh_token");
            verify(refreshTokenService, times(1)).verifyExpiration(refreshToken);
        }

        @Test
        public void whenUserNotFound_thenShouldThrowUnauthorizedResponseStatusException() {
            UUID userUUID = UUID.randomUUID();
            ArgumentCaptor<UserId> userIdCaptor = ArgumentCaptor.forClass(UserId.class);
            RefreshToken refreshToken = new MongoRefreshToken().setToken("refresh_token").setUserId(userUUID);

            when(refreshTokenService.findByToken("refresh_token")).thenReturn(Optional.of(refreshToken));
            when(refreshTokenService.verifyExpiration(refreshToken)).thenReturn(true);
            when(userService.findById(userIdCaptor.capture())).thenReturn(Optional.empty());

            ResponseEntity<?> responseEntity = authController.refreshAccessToken("refresh_token");
            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
            verify(refreshTokenService, times(1)).findByToken("refresh_token");
            verify(refreshTokenService, times(1)).verifyExpiration(refreshToken);
            verify(userService, times(1)).findById(userIdCaptor.capture());
            assertThat(userIdCaptor.getValue().value()).isEqualTo(userUUID);
        }

        @Test
        public void whenRefreshSuccess_thenShouldSetCookiesAndReturnAuthResponse() {
            UUID userUUID = UUID.randomUUID();
            User user = User.builder()
                    .id(new UserId(userUUID))
                    .email("test@example.com")
                    .username("test")
                    .password("password")
                    .role("USER")
                    .firstName("firstName")
                    .lastName("lastName")
                    .build();
            ArgumentCaptor<UserId> userIdCaptor = ArgumentCaptor.forClass(UserId.class);
            RefreshToken refreshToken = new MongoRefreshToken().setToken("refresh_token").setUserId(userUUID);
            RefreshToken newRefreshToken = new MongoRefreshToken().setToken("new_refresh_token").setUserId(userUUID);

            when(refreshTokenService.findByToken("refresh_token")).thenReturn(Optional.of(refreshToken));
            when(refreshTokenService.verifyExpiration(refreshToken)).thenReturn(true);
            when(userService.findById(userIdCaptor.capture())).thenReturn(Optional.of(user));
            doNothing().when(refreshTokenService).deleteRefreshToken(refreshToken);
            when(refreshTokenService.createRefreshToken(user)).thenReturn(newRefreshToken);
            when(jwtService.generateToken(userUUID.toString())).thenReturn("new_access_token");
            when(cookieService.createAccessTokenCookie("new_access_token")).thenReturn(ResponseCookie.from("access_token", "new_access_token").maxAge(600L).build());
            when(cookieService.createRefreshTokenCookie("new_refresh_token")).thenReturn(ResponseCookie.from("refresh_token", "new_refresh_token").maxAge(3600L).build());

            ResponseEntity<AuthResponse> authResponse = authController.refreshAccessToken("refresh_token");

            assertThat(authResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

            verify(refreshTokenService, times(1)).findByToken("refresh_token");
            verify(refreshTokenService, times(1)).verifyExpiration(refreshToken);
            verify(userService, times(1)).findById(userIdCaptor.capture());
            verify(refreshTokenService, times(1)).deleteRefreshToken(refreshToken);
            verify(refreshTokenService, times(1)).createRefreshToken(user);
            verify(jwtService, times(1)).generateToken(userUUID.toString());
            verify(cookieService, times(1)).createAccessTokenCookie("new_access_token");
            verify(cookieService, times(1)).createRefreshTokenCookie("new_refresh_token");

            List<String> cookies = authResponse.getHeaders().get(HttpHeaders.SET_COOKIE);
            assertThat(cookies).hasSize(2);
            assertThat(cookies.getFirst()).contains("access_token=new_access_token");
            assertThat(cookies.get(1)).contains("refresh_token=new_refresh_token");

            AuthResponse body = authResponse.getBody();
            assertThat(body).isNotNull();
            assertThat(body.getEmail()).isEqualTo("test@example.com");
            assertThat(body.getUsername()).isEqualTo("test");
            assertThat(body.getRole()).isEqualTo("USER");

            assertThat(userIdCaptor.getValue().value()).isEqualTo(userUUID);
        }
    }
}