package com.wilzwert.myjobs.core.application.usecase;


import com.wilzwert.myjobs.core.domain.command.RegisterUserCommand;
import com.wilzwert.myjobs.core.domain.exception.UserAlreadyExistsException;
import com.wilzwert.myjobs.core.domain.model.User;
import com.wilzwert.myjobs.core.domain.ports.driven.PasswordHasher;
import com.wilzwert.myjobs.core.domain.ports.driven.UserService;
import com.wilzwert.myjobs.core.domain.ports.driving.CheckUserAvailabilityUseCase;
import com.wilzwert.myjobs.core.domain.ports.driving.RegisterUseCase;

/**
 * @author Wilhelm Zwertvaegher
 * Date:13/03/2025
 * Time:09:07
 */

public class RegisterUseCaseImpl implements RegisterUseCase, CheckUserAvailabilityUseCase {
    private final UserService userService;
    private final PasswordHasher passwordHasher;

    public RegisterUseCaseImpl(UserService userService, PasswordHasher passwordHasher) {
        this.userService = userService;
        this.passwordHasher = passwordHasher;
    }


    @Override
    public User registerUser(RegisterUserCommand registerUserCommand) {
        if(userService.findByEmailOrUsername(registerUserCommand.email(), registerUserCommand.username()).isPresent()) {
            throw new UserAlreadyExistsException();
        }

        return userService.save(
                User.create(
                        registerUserCommand.email(),
                        passwordHasher.hashPassword(registerUserCommand.password()),
                        registerUserCommand.username(),
                        registerUserCommand.firstName(),
                        registerUserCommand.lastName()
                )
        );
    }

    @Override
    public boolean isEmailTaken(String email) {
        return userService.emailExists(email);
    }

    @Override
    public boolean isUsernameTaken(String username) {
        return userService.usernameExists(username);
    }
}
