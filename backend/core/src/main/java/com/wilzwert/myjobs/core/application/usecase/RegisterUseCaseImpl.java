package com.wilzwert.myjobs.core.application.usecase;


import com.wilzwert.myjobs.core.domain.model.user.command.RegisterUserCommand;
import com.wilzwert.myjobs.core.domain.model.user.command.ValidateEmailCommand;
import com.wilzwert.myjobs.core.domain.model.user.exception.UserAlreadyExistsException;
import com.wilzwert.myjobs.core.domain.model.user.exception.UserNotFoundException;
import com.wilzwert.myjobs.core.domain.model.user.User;
import com.wilzwert.myjobs.core.domain.model.user.ports.driven.AccountCreationMessageProvider;
import com.wilzwert.myjobs.core.domain.model.user.ports.driven.PasswordHasher;
import com.wilzwert.myjobs.core.domain.model.user.ports.driven.UserDataManager;
import com.wilzwert.myjobs.core.domain.model.user.ports.driving.CheckUserAvailabilityUseCase;
import com.wilzwert.myjobs.core.domain.model.user.ports.driving.RegisterUseCase;
import com.wilzwert.myjobs.core.domain.model.user.ports.driving.ValidateEmailUseCase;

import java.util.Collections;
import java.util.Optional;

/**
 * @author Wilhelm Zwertvaegher
 */

public class RegisterUseCaseImpl implements RegisterUseCase, CheckUserAvailabilityUseCase, ValidateEmailUseCase {
    private final UserDataManager userDataManager;
    private final PasswordHasher passwordHasher;
    private final AccountCreationMessageProvider accountCreationMessageProvider;

    public RegisterUseCaseImpl(UserDataManager userDataManager, PasswordHasher passwordHasher, AccountCreationMessageProvider accountCreationMessageProvider) {
        this.userDataManager = userDataManager;
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
        if(userDataManager.findByEmailOrUsername(registerUserCommand.email(), registerUserCommand.username()).isPresent()) {
            throw new UserAlreadyExistsException();
        }

        User user = userDataManager.save(User.create(
                User.builder()
                    .email(registerUserCommand.email())
                    .password(passwordHasher.hashPassword(registerUserCommand.password()))
                    .username(registerUserCommand.username())
                    .firstName(registerUserCommand.firstName())
                    .lastName(registerUserCommand.lastName())
                    .jobFollowUpReminderDays(registerUserCommand.jobFollowUpReminderDays())
                    .lang(registerUserCommand.lang())
                    .jobs(Collections.emptyList()),
                registerUserCommand.password()
        ));

        // send account creation message
        accountCreationMessageProvider.send(user);
        return user;
    }

    @Override
    public boolean isEmailTaken(String email) {
        return userDataManager.emailExists(email);
    }

    @Override
    public boolean isUsernameTaken(String username) {
        return userDataManager.usernameExists(username);
    }

    @Override
    public User validateEmail(ValidateEmailCommand command) {
        Optional<User> userOptional = userDataManager.findMinimalByEmailValidationCode(command.validationCode());
        if(userOptional.isPresent()) {
            User user = userOptional.get();
            user = user.validateEmail(command.validationCode());
            return userDataManager.save(user);
        }
        throw new UserNotFoundException();
    }
}
