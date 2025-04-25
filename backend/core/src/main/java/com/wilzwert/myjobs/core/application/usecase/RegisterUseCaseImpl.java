package com.wilzwert.myjobs.core.application.usecase;


import com.wilzwert.myjobs.core.domain.command.RegisterUserCommand;
import com.wilzwert.myjobs.core.domain.command.ValidateEmailCommand;
import com.wilzwert.myjobs.core.domain.exception.UserAlreadyExistsException;
import com.wilzwert.myjobs.core.domain.exception.UserNotFoundException;
import com.wilzwert.myjobs.core.domain.model.user.User;
import com.wilzwert.myjobs.core.domain.model.user.UserId;
import com.wilzwert.myjobs.core.domain.ports.driven.AccountCreationMessageProvider;
import com.wilzwert.myjobs.core.domain.ports.driven.PasswordHasher;
import com.wilzwert.myjobs.core.domain.ports.driven.UserService;
import com.wilzwert.myjobs.core.domain.ports.driving.CheckUserAvailabilityUseCase;
import com.wilzwert.myjobs.core.domain.ports.driving.RegisterUseCase;
import com.wilzwert.myjobs.core.domain.ports.driving.ValidateEmailUseCase;

import java.util.Optional;

/**
 * @author Wilhelm Zwertvaegher
 * Date:13/03/2025
 * Time:09:07
 */

public class RegisterUseCaseImpl implements RegisterUseCase, CheckUserAvailabilityUseCase, ValidateEmailUseCase {
    private final UserService userService;
    private final PasswordHasher passwordHasher;
    private final AccountCreationMessageProvider accountCreationMessageProvider;

    public RegisterUseCaseImpl(UserService userService, PasswordHasher passwordHasher, AccountCreationMessageProvider accountCreationMessageProvider) {
        this.userService = userService;
        this.passwordHasher = passwordHasher;
        this.accountCreationMessageProvider = accountCreationMessageProvider;
    }


    @Override
    public User registerUser(RegisterUserCommand registerUserCommand) {
        // TODO : this should be transactional, i.e. checking availability and register should be atomic
        // to avoid e.g. another register command to succeed
        // while we are processing here a command with the same username/email
        // this will require to provide some kind of transaction management interface
        // and enforce its implementation in infra
        if(userService.findByEmailOrUsername(registerUserCommand.email(), registerUserCommand.username()).isPresent()) {
            throw new UserAlreadyExistsException();
        }

        // 2 steps :
        // - build a temp User with plaintext password to trigger self validation
        // - build the final User from the temp User with the hashed password
        // this could (or maybe should ?) be moved in a factory or a static method in User
        // but for now it will be ok
        User tmpUser = User.builder()
                .id(UserId.generate())
                .email(registerUserCommand.email())
                .password(registerUserCommand.password())
                .username(registerUserCommand.username())
                .firstName(registerUserCommand.firstName())
                .lastName(registerUserCommand.lastName())
                .build();

        // if no exception was thrown
        // then we build and save the final User based on the temp one, only setting the hashed password
        User user = userService.save(
            User.from(tmpUser)
                .password(passwordHasher.hashPassword(registerUserCommand.password()))
                .build()
        );

        // send account creation message
        accountCreationMessageProvider.send(user);
        return user;
    }

    @Override
    public boolean isEmailTaken(String email) {
        return userService.emailExists(email);
    }

    @Override
    public boolean isUsernameTaken(String username) {
        return userService.usernameExists(username);
    }

    @Override
    public User validateEmail(ValidateEmailCommand command) {
        Optional<User> userOptional = userService.findByEmailValidationCode(command.validationCode());
        if(userOptional.isPresent()) {
            User user = userOptional.get();
            user = user.validateEmail(command.validationCode());
            return userService.save(user);
        }
        throw new UserNotFoundException();
    }
}
