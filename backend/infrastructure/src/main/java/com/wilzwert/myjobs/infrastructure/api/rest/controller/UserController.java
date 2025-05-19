package com.wilzwert.myjobs.infrastructure.api.rest.controller;


import com.wilzwert.myjobs.core.domain.model.user.command.ChangePasswordCommand;
import com.wilzwert.myjobs.core.domain.model.user.command.UpdateUserLangCommand;
import com.wilzwert.myjobs.core.domain.model.user.command.ValidateEmailCommand;
import com.wilzwert.myjobs.core.domain.model.user.ports.driving.*;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.*;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.mapper.UserMapper;
import com.wilzwert.myjobs.infrastructure.security.service.UserDetailsImpl;
import jakarta.validation.Valid;
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
@RequestMapping("/api/user/me")
public class UserController {
    private final ValidateEmailUseCase validateEmailUseCase;

    private final ChangePasswordUseCase changePasswordUseCase;

    private final SendVerificationEmailUseCase sendVerificationEmailUseCase;

    private final GetUserViewUseCase getUserViewUseCase;

    private final UpdateUserUseCase updateUserUseCase;

    private final UpdateUserLangUseCase updateUserLangUseCase;

    private final DeleteAccountUseCase deleteAccountUseCase;

    private final UserMapper userMapper;

    public UserController(ValidateEmailUseCase validateEmailUseCase, ChangePasswordUseCase changePasswordUseCase, SendVerificationEmailUseCase sendVerificationEmailUseCase, GetUserViewUseCase getUserViewUseCase, UpdateUserUseCase updateUserUseCase, UpdateUserLangUseCase updateUserLangUseCase, DeleteAccountUseCase deleteAccountUseCase, UserMapper userMapper) {
        this.validateEmailUseCase = validateEmailUseCase;
        this.changePasswordUseCase = changePasswordUseCase;
        this.sendVerificationEmailUseCase = sendVerificationEmailUseCase;
        this.getUserViewUseCase = getUserViewUseCase;
        this.updateUserUseCase = updateUserUseCase;
        this.updateUserLangUseCase = updateUserLangUseCase;
        this.deleteAccountUseCase = deleteAccountUseCase;
        this.userMapper = userMapper;
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
        return userMapper.toResponseFromView(getUserViewUseCase.getUser(userDetails.getId()));
    }

    @PatchMapping()
    public UserResponse update(@RequestBody @Valid UpdateUserRequest updateUserRequest, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userMapper.toResponse(updateUserUseCase.updateUser(userMapper.toUpdateCommand(updateUserRequest, userDetails.getId())));
    }

    @PutMapping("/lang")
    @ResponseStatus(HttpStatus.OK)
    public void changeLang(@RequestBody @Valid UpdateUserLangRequest updateUserLangRequest, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        updateUserLangUseCase.updateUserLang(new UpdateUserLangCommand(updateUserLangRequest.getLang(), userDetails.getId()));
    }

    @PutMapping("/password")
    @ResponseStatus(HttpStatus.OK)
    public void changePassword(@RequestBody @Valid ChangePasswordRequest changePasswordRequest, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        changePasswordUseCase.changePassword(new ChangePasswordCommand(changePasswordRequest.getPassword(), changePasswordRequest.getOldPassword(), userDetails.getId()));
    }

    @PostMapping("/email/validation")
    @ResponseStatus(HttpStatus.OK)
    public void validateEmail(@RequestBody @Valid ValidateEmailRequest validateEmailRequest) {
        validateEmailUseCase.validateEmail(new ValidateEmailCommand(validateEmailRequest.getCode()));
    }

    @PostMapping("/email/verification")
    @ResponseStatus(HttpStatus.OK)
    public void sendVerificationEmail(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        sendVerificationEmailUseCase.sendVerificationEmail(userDetails.getId());
    }
}