package com.wilzwert.myapps.infrastructure.api.rest.controller;


import com.wilzwert.myapps.domain.command.RegisterUserCommand;
import com.wilzwert.myapps.domain.model.AuthenticatedUser;
import com.wilzwert.myapps.domain.ports.driving.LoginUseCase;
import com.wilzwert.myapps.domain.ports.driving.RegisterUseCase;
import com.wilzwert.myapps.infrastructure.api.rest.dto.LoginRequest;
import com.wilzwert.myapps.infrastructure.api.rest.dto.RegisterUserRequest;
import com.wilzwert.myapps.infrastructure.persistence.mongo.mapper.UserMapper;
import com.wilzwert.myapps.infrastructure.api.rest.dto.UserResponse;
import com.wilzwert.myapps.infrastructure.security.configuration.CookieProperties;
import com.wilzwert.myapps.infrastructure.security.jwt.JwtAuthenticatedUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;

/**
 * @author Wilhelm Zwertvaegher
 * Date:13/03/2025
 * Time:11:43
 */
@RestController
@Slf4j
@RequestMapping("/api/auth")
public class AuthController {

    private final RegisterUseCase registerUseCase;

    private final LoginUseCase loginUseCase;

    private final UserMapper userMapper;

    public AuthController(RegisterUseCase registerUseCase, LoginUseCase loginUseCase, UserMapper userMapper) {
        this.registerUseCase = registerUseCase;
        this.loginUseCase = loginUseCase;
        this.userMapper = userMapper;
    }

    @PostMapping("/register")
    public UserResponse register(@RequestBody final RegisterUserRequest registerUserRequest) {
        RegisterUserCommand registerUserCommand = userMapper.toCommand(registerUserRequest);
        return userMapper.toResponse(registerUseCase.registerUser(registerUserCommand));
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@RequestBody final LoginRequest loginRequest, CookieProperties cookieProperties) {
        log.info("User login with email {}", loginRequest.getEmail());
        try {
            log.info("User login - authenticating");
            AuthenticatedUser user = loginUseCase.authenticateUser(loginRequest.getEmail(), loginRequest.getPassword());

            // FIXME : needs genericity or strategy
            if (user instanceof JwtAuthenticatedUser jwtAuthenticatedUser) {
                System.out.println("Token " + jwtAuthenticatedUser.getJwtToken() + ", refresh " + jwtAuthenticatedUser.getRefreshToken());

                ResponseCookie accessTokenCookie = ResponseCookie.from("access_token", jwtAuthenticatedUser.getJwtToken())
                        .httpOnly(true)
                        .secure(cookieProperties.isSecure())  // Dynamique
                        .sameSite(cookieProperties.getSameSite())  // Dynamique
                        .domain(cookieProperties.getDomain())  // Dynamique
                        .path(cookieProperties.getPath())
                        .maxAge(Duration.ofHours(1))
                        .build();

                ResponseCookie refreshTokenCookie = ResponseCookie.from("refresh_token", jwtAuthenticatedUser.getRefreshToken())
                        .httpOnly(true)
                        .secure(cookieProperties.isSecure())
                        .sameSite(cookieProperties.getSameSite())
                        .domain(cookieProperties.getDomain())
                        .path(cookieProperties.getPath())
                        .maxAge(Duration.ofDays(7))
                        .build();

                return ResponseEntity.ok()
                        .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                        .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                        .body(new UserResponse(user.getEmail(), user.getUsername(), user.getRole()));

                /*log.info("User login - generating token");
                String token = jwtService.generateToken(user);
                log.info("User with email {} successfully authenticated, sending JWT token", loginRequestDto.getEmail());
                return new JwtResponse(token, "Bearer", refreshTokenService.getOrCreateRefreshToken(user).getToken(), user.getId(), user.getUsername());*/
            }

            return ResponseEntity.ok().body(new UserResponse(user.getEmail(), user.getUsername(), user.getRole()));
        } catch (AuthenticationException e) {
            log.info("Login failed for User with email {}", loginRequest.getEmail());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login failed. " + e.getMessage());
        }
    }

    /*
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken(@RequestBody RefreshTokenRequest request) {
        return refreshTokenProvider.refreshAccessToken(request.getRefreshToken())
                .map(token -> ResponseEntity.ok(new AccessTokenResponse(token)))
    }*/
}