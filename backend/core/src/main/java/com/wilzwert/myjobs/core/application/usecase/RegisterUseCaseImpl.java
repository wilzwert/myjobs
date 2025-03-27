package com.wilzwert.myjobs.core.application.usecase;


import com.wilzwert.myjobs.core.domain.command.RegisterUserCommand;
import com.wilzwert.myjobs.core.domain.model.User;
import com.wilzwert.myjobs.core.domain.ports.driven.UserService;
import com.wilzwert.myjobs.core.domain.ports.driving.CheckUserAvailabilityUseCase;
import com.wilzwert.myjobs.core.domain.ports.driving.RegisterUseCase;
import com.wilzwert.myjobs.core.domain.services.UserDomainService;

/**
 * @author Wilhelm Zwertvaegher
 * Date:13/03/2025
 * Time:09:07
 */

public class RegisterUseCaseImpl implements RegisterUseCase, CheckUserAvailabilityUseCase {
    private final UserDomainService userDomainService;
    private final UserService userService;

    public RegisterUseCaseImpl(UserDomainService userDomainService, UserService userService) {
        this.userDomainService = userDomainService;
        this.userService = userService;
    }

    @Override
    public User registerUser(RegisterUserCommand registerUserCommand) {
        return userService.save(userDomainService.registerUser(registerUserCommand));
    }

    @Override
    public boolean isEmailTaken(String email) {
        return userDomainService.isEmailTaken(email);
    }

    @Override
    public boolean isUsernameTaken(String username) {
        return userDomainService.isUsernameTaken(username);
    }
}
