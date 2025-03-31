package com.wilzwert.myjobs.infrastructure.api.rest.controller;


import com.wilzwert.myjobs.core.domain.command.ChangePasswordCommand;
import com.wilzwert.myjobs.core.domain.command.ValidateEmailCommand;
import com.wilzwert.myjobs.core.domain.exception.UserNotFoundException;
import com.wilzwert.myjobs.core.domain.model.User;
import com.wilzwert.myjobs.core.domain.ports.driven.UserService;
import com.wilzwert.myjobs.core.domain.ports.driving.*;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.*;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.mapper.UserMapper;
import com.wilzwert.myjobs.infrastructure.security.service.UserDetailsImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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

    private final ChangePasswordUseCase changePasswordUseCase;

    private final UserMapper userMapper;

    private final UserService userService;



    public UserController(ValidateEmailUseCase validateEmailUseCase, ChangePasswordUseCase changePasswordUseCase, UserMapper userMapper, UserService userService) {
        this.validateEmailUseCase = validateEmailUseCase;
        this.changePasswordUseCase = changePasswordUseCase;
        this.userMapper = userMapper;
        this.userService = userService;
    }

    @GetMapping("/me")
    public UserResponse me(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userService.findById(userDetails.getId()).orElseThrow(UserNotFoundException::new);
        return userMapper.toResponse(user);
    }

    @PutMapping("/password")
    public ResponseEntity<?> changePassword(@RequestBody PasswordRequest passwordRequest, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        changePasswordUseCase.changePassword(new ChangePasswordCommand(passwordRequest.getPassword(), passwordRequest.getOldPassword(), userDetails.getId()));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/email/validation")
    public ResponseEntity<?> validateEmail(@RequestBody ValidateEmailRequest validateEmailRequest) {
        validateEmailUseCase.validateEmail(new ValidateEmailCommand(validateEmailRequest.getCode()));
        return new ResponseEntity<>(HttpStatus.OK);
    }
}