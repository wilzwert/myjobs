package com.wilzwert.myjobs.infrastructure.api.rest.controller;


import com.wilzwert.myjobs.core.application.command.RegisterUserCommand;
import com.wilzwert.myjobs.core.domain.model.AuthenticatedUser;
import com.wilzwert.myjobs.core.domain.ports.driving.CheckUserAvailabilityUseCase;
import com.wilzwert.myjobs.core.domain.ports.driving.LoginUseCase;
import com.wilzwert.myjobs.core.domain.ports.driving.RegisterUseCase;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.LoginRequest;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.RegisterUserRequest;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.mapper.UserMapper;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.UserResponse;
import com.wilzwert.myjobs.infrastructure.security.captcha.RequiresCaptcha;
import com.wilzwert.myjobs.infrastructure.security.configuration.CookieProperties;
import com.wilzwert.myjobs.infrastructure.security.jwt.JwtAuthenticatedUser;
import com.wilzwert.myjobs.infrastructure.security.service.UserDetailsImpl;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;

/**
 * @author Wilhelm Zwertvaegher
 * Date:13/03/2025
 * Time:11:43
 * TODO : add rate limiting on public endpoints
 */
@RestController
@Slf4j
@RequestMapping("/api/auth")
public class AuthController {

    private final RegisterUseCase registerUseCase;

    private final LoginUseCase loginUseCase;

    private final CheckUserAvailabilityUseCase checkUserAvailabilityUseCase;

    private final UserMapper userMapper;

    private final CookieProperties cookieProperties;

    public AuthController(RegisterUseCase registerUseCase, LoginUseCase loginUseCase, CheckUserAvailabilityUseCase checkUserAvailabilityUseCase, UserMapper userMapper, CookieProperties cookieProperties) {
        this.registerUseCase = registerUseCase;
        this.loginUseCase = loginUseCase;
        this.checkUserAvailabilityUseCase = checkUserAvailabilityUseCase;
        this.userMapper = userMapper;
        this.cookieProperties = cookieProperties;
    }

    @PostMapping("/register")
    @RequiresCaptcha
    public UserResponse register(@RequestBody final RegisterUserRequest registerUserRequest) {
        RegisterUserCommand registerUserCommand = userMapper.toCommand(registerUserRequest);
        return userMapper.toResponse(registerUseCase.registerUser(registerUserCommand));
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        ResponseCookie accessTokenCookie = ResponseCookie.from("access_token", null)
                .httpOnly(true)
                .secure(cookieProperties.isSecure())
                .sameSite(cookieProperties.getSameSite())
                .domain(cookieProperties.getDomain())
                .path(cookieProperties.getPath())
                .maxAge(0)
                .build();

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refresh_token", null)
                .httpOnly(true)
                .secure(cookieProperties.isSecure())
                .sameSite(cookieProperties.getSameSite())
                .domain(cookieProperties.getDomain())
                .path(cookieProperties.getPath())
                .maxAge(0)
                .build();

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .build();

    }

    @PostMapping("/login")
    @RequiresCaptcha
    public ResponseEntity<UserResponse> login(@RequestBody final LoginRequest loginRequest) {
        log.info("User login with email {}", loginRequest.getEmail());
        try {
            log.info("User login - authenticating");
            AuthenticatedUser user = loginUseCase.authenticateUser(loginRequest.getEmail(), loginRequest.getPassword());

            // FIXME : needs genericity or strategy
            if (user instanceof JwtAuthenticatedUser jwtAuthenticatedUser) {
                ResponseCookie accessTokenCookie = ResponseCookie.from("access_token", jwtAuthenticatedUser.getJwtToken())
                        .httpOnly(true)
                        .secure(cookieProperties.isSecure())  // Dynamique
                        .sameSite(cookieProperties.getSameSite())  // Dynamique
                        .domain(cookieProperties.getDomain())  // Dynamique
                        .path(cookieProperties.getPath())
                        .maxAge(Duration.ofDays(7))
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
            }

            return ResponseEntity.ok().body(new UserResponse(user.getEmail(), user.getUsername(), user.getRole()));
        } catch (AuthenticationException e) {
            log.info("Login failed for User with email {}", loginRequest.getEmail());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login failed. " + e.getMessage());
        }
    }

    @GetMapping("/me")
    public UserResponse me(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return new UserResponse(userDetails.getEmail(), userDetails.getUsername(), userDetails.getRole());
    }

    @GetMapping("/email-check")
    @RequiresCaptcha
    public ResponseEntity<Void> emailCheck(@RequestParam("email") String email) {
        return checkUserAvailabilityUseCase.isEmailTaken(email) ? new ResponseEntity<Void>(HttpStatus.UNPROCESSABLE_ENTITY) : new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/username-check")
    @RequiresCaptcha
    public ResponseEntity<Void> usernameCheck(@RequestParam("username") String username) {
        return checkUserAvailabilityUseCase.isUsernameTaken(username) ? new ResponseEntity<Void>(HttpStatus.UNPROCESSABLE_ENTITY) : new ResponseEntity<>(HttpStatus.OK);
    }

    /*
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken(@RequestBody RefreshTokenRequest request) {
        return refreshTokenProvider.refreshAccessToken(request.getRefreshToken())
                .map(token -> ResponseEntity.ok(new AccessTokenResponse(token)))
    }*/
}