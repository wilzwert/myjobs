package com.wilzwert.myjobs.infrastructure.api.rest.controller;


import com.wilzwert.myjobs.core.domain.model.user.command.UpdateUserLangCommand;
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
 * TODO : add rate limiting on public endpoints
 */
@RestController
@Slf4j
@RequestMapping("/api/user/me")
public class UserController {

    private final GetUserViewUseCase getUserViewUseCase;

    private final UpdateUserUseCase updateUserUseCase;

    private final UpdateUserLangUseCase updateUserLangUseCase;

    private final DeleteAccountUseCase deleteAccountUseCase;

    private final GetUserSummaryUseCase getUserSummaryUseCase;

    private final UserMapper userMapper;

    public UserController(GetUserViewUseCase getUserViewUseCase, UpdateUserUseCase updateUserUseCase, UpdateUserLangUseCase updateUserLangUseCase, DeleteAccountUseCase deleteAccountUseCase, GetUserSummaryUseCase getUserSummaryUseCase, UserMapper userMapper) {
        this.getUserViewUseCase = getUserViewUseCase;
        this.updateUserUseCase = updateUserUseCase;
        this.updateUserLangUseCase = updateUserLangUseCase;
        this.deleteAccountUseCase = deleteAccountUseCase;
        this.getUserSummaryUseCase = getUserSummaryUseCase;
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

    @GetMapping("/summary")
    @ResponseStatus(HttpStatus.OK)
    public UserSummaryResponse getSummary(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userMapper.toResponse(getUserSummaryUseCase.getUserSummary(userDetails.getId()));
    }
}