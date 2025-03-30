package com.wilzwert.myjobs.infrastructure.api.rest.controller;


import com.wilzwert.myjobs.core.domain.command.RegisterUserCommand;
import com.wilzwert.myjobs.core.domain.command.ValidateEmailCommand;
import com.wilzwert.myjobs.core.domain.model.AuthenticatedUser;
import com.wilzwert.myjobs.core.domain.model.User;
import com.wilzwert.myjobs.core.domain.model.UserId;
import com.wilzwert.myjobs.core.domain.ports.driven.UserService;
import com.wilzwert.myjobs.core.domain.ports.driving.CheckUserAvailabilityUseCase;
import com.wilzwert.myjobs.core.domain.ports.driving.LoginUseCase;
import com.wilzwert.myjobs.core.domain.ports.driving.RegisterUseCase;
import com.wilzwert.myjobs.core.domain.ports.driving.ValidateEmailUseCase;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.LoginRequest;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.RegisterUserRequest;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.UserResponse;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.ValidateEmailRequest;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.mapper.UserMapper;
import com.wilzwert.myjobs.infrastructure.security.captcha.RequiresCaptcha;
import com.wilzwert.myjobs.infrastructure.security.configuration.CookieProperties;
import com.wilzwert.myjobs.infrastructure.security.configuration.JwtProperties;
import com.wilzwert.myjobs.infrastructure.security.jwt.JwtAuthenticatedUser;
import com.wilzwert.myjobs.infrastructure.security.model.RefreshToken;
import com.wilzwert.myjobs.infrastructure.security.service.JwtService;
import com.wilzwert.myjobs.infrastructure.security.service.RefreshTokenService;
import com.wilzwert.myjobs.infrastructure.security.service.UserDetailsImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
@RequestMapping("/api/user")
public class UserController {
    private final ValidateEmailUseCase validateEmailUseCase;

    public UserController(ValidateEmailUseCase validateEmailUseCase, UserMapper userMapper) {
        this.validateEmailUseCase = validateEmailUseCase;
    }

    @GetMapping("/me")
    public UserResponse me(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return new UserResponse(userDetails.getEmail(), userDetails.getUsername(), userDetails.getRole());
    }

    @PostMapping("/email/validation")
    public ResponseEntity<?> validateEmail(@RequestBody ValidateEmailRequest validateEmailRequest) {
        System.out.println(validateEmailRequest.getCode());
        validateEmailUseCase.validateEmail(new ValidateEmailCommand(validateEmailRequest.getCode()));
        return new ResponseEntity<>(HttpStatus.OK);
    }
}