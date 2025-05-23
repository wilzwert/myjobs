package com.wilzwert.myjobs.infrastructure.api.rest.controller;


import com.wilzwert.myjobs.core.domain.model.user.command.RegisterUserCommand;
import com.wilzwert.myjobs.core.domain.model.user.User;
import com.wilzwert.myjobs.core.domain.model.user.UserId;
import com.wilzwert.myjobs.core.domain.model.user.ports.driving.CheckUserAvailabilityUseCase;
import com.wilzwert.myjobs.core.domain.model.user.ports.driving.LoginUseCase;
import com.wilzwert.myjobs.core.domain.model.user.ports.driving.RegisterUseCase;
import com.wilzwert.myjobs.core.domain.model.user.ports.driven.UserDataManager;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.*;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.mapper.UserMapper;
import com.wilzwert.myjobs.infrastructure.security.captcha.RequiresCaptcha;
import com.wilzwert.myjobs.infrastructure.security.jwt.JwtAuthenticatedUser;
import com.wilzwert.myjobs.infrastructure.security.model.RefreshToken;
import com.wilzwert.myjobs.infrastructure.security.service.CookieService;
import com.wilzwert.myjobs.infrastructure.security.service.JwtService;
import com.wilzwert.myjobs.infrastructure.security.service.RefreshTokenService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/**
 * @author Wilhelm Zwertvaegher
 */
@RestController
@Slf4j
@RequestMapping("/api/auth")
public class AuthController {

    private final RegisterUseCase registerUseCase;

    private final LoginUseCase loginUseCase;

    private final CheckUserAvailabilityUseCase checkUserAvailabilityUseCase;

    private final UserMapper userMapper;

    private final CookieService cookieService;

    private final RefreshTokenService refreshTokenService;

    private final UserDataManager userDataManager;

    private final JwtService jwtService;

    public AuthController(RegisterUseCase registerUseCase, LoginUseCase loginUseCase, CheckUserAvailabilityUseCase checkUserAvailabilityUseCase, UserMapper userMapper, CookieService cookieService, RefreshTokenService refreshTokenService, UserDataManager userDataManager, JwtService jwtService) {
        this.registerUseCase = registerUseCase;
        this.loginUseCase = loginUseCase;
        this.checkUserAvailabilityUseCase = checkUserAvailabilityUseCase;
        this.userMapper = userMapper;
        this.cookieService = cookieService;
        this.refreshTokenService = refreshTokenService;
        this.userDataManager = userDataManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    @RequiresCaptcha
    public UserResponse register(@RequestBody @Valid final RegisterUserRequest registerUserRequest) {
        RegisterUserCommand registerUserCommand = userMapper.toCommand(registerUserRequest);
        return userMapper.toResponse(registerUseCase.registerUser(registerUserCommand));
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> logout() {
        var responseEntity = ResponseEntity.noContent();
        return responseEntity
                .header(HttpHeaders.SET_COOKIE, cookieService.revokeAccessTokenCookie().toString())
                .header(HttpHeaders.SET_COOKIE, cookieService.revokeRefreshTokenCookie().toString())
                .build();
    }

    @PostMapping("/login")
    @RequiresCaptcha
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid final LoginRequest loginRequest) {
        log.info("User login with email {}", loginRequest.getEmail());
        try {
            log.info("User login - authenticating");
            // casting seems a bit ugly but in infra we actually know we will get a JwtAuthenticatedUser
            JwtAuthenticatedUser authenticatedUser = (JwtAuthenticatedUser) loginUseCase.authenticateUser(loginRequest.getEmail(), loginRequest.getPassword());
            var responseEntity = ResponseEntity.ok();
            return responseEntity
                    .header(HttpHeaders.SET_COOKIE, cookieService.createAccessTokenCookie(authenticatedUser.getJwtToken()).toString())
                    .header(HttpHeaders.SET_COOKIE, cookieService.createRefreshTokenCookie(authenticatedUser.getRefreshToken()).toString())
                    .body(new AuthResponse(authenticatedUser.getEmail(), authenticatedUser.getUsername(), authenticatedUser.getRole()));

        } catch (AuthenticationException e) {
            log.info("Login failed for User with email {}", loginRequest.getEmail());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login failed. " + e.getMessage());
        }
    }

    @GetMapping("/email-check")
    @RequiresCaptcha
    public ResponseEntity<Void> emailCheck(@RequestParam("email") String email) {
        return checkUserAvailabilityUseCase.isEmailTaken(email) ? new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY) : new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/username-check")
    @RequiresCaptcha
    public ResponseEntity<Void> usernameCheck(@RequestParam("username") String username) {
        return checkUserAvailabilityUseCase.isUsernameTaken(username) ? new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY) : new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshAccessToken(@CookieValue(name = "refresh_token", required = false) String refreshToken) {
        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        log.debug("Refresh token {}", refreshToken);
        RefreshToken foundRefreshToken = refreshTokenService.findByToken(refreshToken).orElse(null);
        if (foundRefreshToken == null || !refreshTokenService.verifyExpiration(foundRefreshToken)) {
            log.debug("Found refresh token empty or expired {}", foundRefreshToken);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = userDataManager.findById(new UserId(foundRefreshToken.getUserId())).orElse(null);
        if(user == null) {
            log.debug("Associated user not found for {}", refreshToken);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        log.debug("Delete previous refresh token and return a new one");
        refreshTokenService.deleteRefreshToken(foundRefreshToken);
        var newRefreshToken = refreshTokenService.createRefreshToken(user);
        var newAccessToken = jwtService.generateToken(user.getId().value().toString());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookieService.createAccessTokenCookie(newAccessToken).toString())
                .header(HttpHeaders.SET_COOKIE, cookieService.createRefreshTokenCookie(newRefreshToken.getToken()).toString())
                .body(new AuthResponse(user.getEmail(), user.getUsername(), user.getRole()));
    }
}