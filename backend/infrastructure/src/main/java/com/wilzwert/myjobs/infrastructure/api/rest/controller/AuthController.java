package com.wilzwert.myjobs.infrastructure.api.rest.controller;


import com.wilzwert.myjobs.core.domain.command.RegisterUserCommand;
import com.wilzwert.myjobs.core.domain.model.AuthenticatedUser;
import com.wilzwert.myjobs.core.domain.model.User;
import com.wilzwert.myjobs.core.domain.model.UserId;
import com.wilzwert.myjobs.core.domain.ports.driven.UserService;
import com.wilzwert.myjobs.core.domain.ports.driving.*;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.*;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.mapper.UserMapper;
import com.wilzwert.myjobs.infrastructure.security.captcha.RequiresCaptcha;
import com.wilzwert.myjobs.infrastructure.security.configuration.CookieProperties;
import com.wilzwert.myjobs.infrastructure.security.configuration.JwtProperties;
import com.wilzwert.myjobs.infrastructure.security.jwt.JwtAuthenticatedUser;
import com.wilzwert.myjobs.infrastructure.security.model.RefreshToken;
import com.wilzwert.myjobs.infrastructure.security.service.JwtService;
import com.wilzwert.myjobs.infrastructure.security.service.RefreshTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

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

    private final RefreshTokenService refreshTokenService;

    private final UserService userService;

    private final JwtService jwtService;

    private final JwtProperties jwtProperties;

    public AuthController(RegisterUseCase registerUseCase, LoginUseCase loginUseCase, CheckUserAvailabilityUseCase checkUserAvailabilityUseCase, UserMapper userMapper, CookieProperties cookieProperties, RefreshTokenService refreshTokenService, UserService userService, JwtService jwtService, JwtProperties jwtProperties) {
        this.registerUseCase = registerUseCase;
        this.loginUseCase = loginUseCase;
        this.checkUserAvailabilityUseCase = checkUserAvailabilityUseCase;
        this.userMapper = userMapper;
        this.cookieProperties = cookieProperties;
        this.refreshTokenService = refreshTokenService;
        this.userService = userService;
        this.jwtService = jwtService;
        this.jwtProperties = jwtProperties;
    }

    @PostMapping("/register")
    @RequiresCaptcha
    public UserResponse register(@RequestBody final RegisterUserRequest registerUserRequest) {
        RegisterUserCommand registerUserCommand = userMapper.toCommand(registerUserRequest);
        return userMapper.toResponse(registerUseCase.registerUser(registerUserCommand));
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> logout() {
        var responseEntity = ResponseEntity.noContent();
        return responseEntity
                .header(HttpHeaders.SET_COOKIE, createCookie("access_token", "", 0).toString())
                .header(HttpHeaders.SET_COOKIE, createCookie("refresh_token", "", 0).toString())
                .build();
    }

    @PostMapping("/login")
    @RequiresCaptcha
    public ResponseEntity<AuthResponse> login(@RequestBody final LoginRequest loginRequest) {
        log.info("User login with email {}", loginRequest.getEmail());
        try {
            log.info("User login - authenticating");
            AuthenticatedUser user = loginUseCase.authenticateUser(loginRequest.getEmail(), loginRequest.getPassword());
            if (user instanceof JwtAuthenticatedUser jwtAuthenticatedUser) {
                var responseEntity = ResponseEntity.ok();

                return responseEntity
                        .header(HttpHeaders.SET_COOKIE, createCookie("access_token", jwtAuthenticatedUser.getJwtToken(), jwtProperties.getExpirationTime()).toString())
                        .header(HttpHeaders.SET_COOKIE, createCookie("refresh_token", jwtAuthenticatedUser.getRefreshToken(), jwtProperties.getRefreshExpirationTime()).toString())
                        .body(new AuthResponse(user.getEmail(), user.getUsername(), user.getRole()));
            }

            return ResponseEntity.ok().body(new AuthResponse(user.getEmail(), user.getUsername(), user.getRole()));
        } catch (AuthenticationException e) {
            log.info("Login failed for User with email {}", loginRequest.getEmail());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login failed. " + e.getMessage());
        }
    }

    @GetMapping("/email-check")
    @RequiresCaptcha
    public ResponseEntity<?> emailCheck(@RequestParam("email") String email) {
        return checkUserAvailabilityUseCase.isEmailTaken(email) ? new ResponseEntity<Void>(HttpStatus.UNPROCESSABLE_ENTITY) : new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/username-check")
    @RequiresCaptcha
    public ResponseEntity<?> usernameCheck(@RequestParam("username") String username) {
        return checkUserAvailabilityUseCase.isUsernameTaken(username) ? new ResponseEntity<Void>(HttpStatus.UNPROCESSABLE_ENTITY) : new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshAccessToken(@CookieValue(name = "refresh_token", required = false) String refreshToken) {
        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        RefreshToken foundRefreshToken = refreshTokenService.findByToken(refreshToken).orElse(null);
        if (foundRefreshToken == null || !refreshTokenService.verifyExpiration(foundRefreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = userService.findById(new UserId(UUID.fromString(foundRefreshToken.getUserId().toString()))).orElse(null);
        if(user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        refreshTokenService.deleteRefreshToken(foundRefreshToken);
        var newRefreshToken = refreshTokenService.createRefreshToken(user);
        var newAccessToken = jwtService.generateToken(user.getEmail());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, createCookie("access_token", newAccessToken, jwtProperties.getExpirationTime()).toString())
                .header(HttpHeaders.SET_COOKIE, createCookie("refresh_token", newRefreshToken.getToken(), jwtProperties.getRefreshExpirationTime()).toString())
                .body(new AuthResponse(user.getEmail(), user.getUsername(), user.getRole()));
    }

    private ResponseCookie createCookie(String name, String value, long maxAge) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(cookieProperties.isSecure())
                .sameSite(cookieProperties.getSameSite())
                .domain(cookieProperties.getDomain())
                .path(cookieProperties.getPath())
                .maxAge(maxAge)
                .build();
    }
}