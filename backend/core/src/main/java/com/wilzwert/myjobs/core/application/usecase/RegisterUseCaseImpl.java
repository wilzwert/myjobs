package com.wilzwert.myjobs.core.application.usecase;


import com.wilzwert.myjobs.core.domain.command.RegisterUserCommand;
import com.wilzwert.myjobs.core.domain.command.ValidateEmailCommand;
import com.wilzwert.myjobs.core.domain.exception.UserAlreadyExistsException;
import com.wilzwert.myjobs.core.domain.exception.UserNotFoundException;
import com.wilzwert.myjobs.core.domain.model.user.User;
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

        User user = userService.save(User.create(
                registerUserCommand.email(),
                passwordHasher.hashPassword(registerUserCommand.password()),
                registerUserCommand.username(),
                registerUserCommand.firstName(),
                registerUserCommand.lastName(),
                registerUserCommand.jobFollowUpReminderDays(),
                registerUserCommand.lang(),
                registerUserCommand.password()
        ));

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
