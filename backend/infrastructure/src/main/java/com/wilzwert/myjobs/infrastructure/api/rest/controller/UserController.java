package com.wilzwert.myjobs.infrastructure.api.rest.controller;


import com.wilzwert.myjobs.core.domain.command.ChangePasswordCommand;
import com.wilzwert.myjobs.core.domain.command.ValidateEmailCommand;
import com.wilzwert.myjobs.core.domain.exception.UserNotFoundException;
import com.wilzwert.myjobs.core.domain.model.User;
import com.wilzwert.myjobs.core.domain.model.UserId;
import com.wilzwert.myjobs.core.domain.ports.driven.UserService;
import com.wilzwert.myjobs.core.domain.ports.driving.*;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.*;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.mapper.UserMapper;
import com.wilzwert.myjobs.infrastructure.security.service.UserDetailsImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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

    private final SendVerificationEmailUseCase sendVerificationEmailUseCase;

    private final UpdateUserUseCase updateUserUseCase;

    private final DeleteAccountUseCase deleteAccountUseCase;

    private final UserMapper userMapper;

    private final UserService userService;



    public UserController(ValidateEmailUseCase validateEmailUseCase, ChangePasswordUseCase changePasswordUseCase, SendVerificationEmailUseCase sendVerificationEmailUseCase, UpdateUserUseCase updateUserUseCase, DeleteAccountUseCase deleteAccountUseCase, UserMapper userMapper, UserService userService) {
        this.validateEmailUseCase = validateEmailUseCase;
        this.changePasswordUseCase = changePasswordUseCase;
        this.sendVerificationEmailUseCase = sendVerificationEmailUseCase;
        this.updateUserUseCase = updateUserUseCase;
        this.deleteAccountUseCase = deleteAccountUseCase;
        this.userMapper = userMapper;
        this.userService = userService;
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAccount(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        deleteAccountUseCase.deleteAccount(userDetails.getId());
    }

    @GetMapping()
    public UserResponse me(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userService.findById(userDetails.getId()).orElseThrow(UserNotFoundException::new);
        return userMapper.toResponse(user);
    }

    @PatchMapping()
    public UserResponse update(@RequestBody UpdateUserRequest updateUserRequest, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userMapper.toResponse(updateUserUseCase.updateUser(userMapper.toUpdateCommand(updateUserRequest, userDetails.getId())));
    }

    @PutMapping("/password")
    @ResponseStatus(HttpStatus.OK)
    public void changePassword(@RequestBody PasswordRequest passwordRequest, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        changePasswordUseCase.changePassword(new ChangePasswordCommand(passwordRequest.getPassword(), passwordRequest.getOldPassword(), userDetails.getId()));
    }

    @PostMapping("/email/validation")
    @ResponseStatus(HttpStatus.OK)
    public void validateEmail(@RequestBody ValidateEmailRequest validateEmailRequest) {
        validateEmailUseCase.validateEmail(new ValidateEmailCommand(validateEmailRequest.getCode()));
    }

    @PostMapping("/email/verification")
    @ResponseStatus(HttpStatus.OK)
    public void sendVerificationEmail(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        sendVerificationEmailUseCase.sendVerificationEmail(userDetails.getId());
    }
}