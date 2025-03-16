package com.wilzwert.myjobs.core.application;


import com.wilzwert.myjobs.core.application.command.RegisterUserCommand;
import com.wilzwert.myjobs.core.domain.exception.UserAlreadyExistsException;
import com.wilzwert.myjobs.core.domain.model.User;
import com.wilzwert.myjobs.core.domain.model.UserId;
import com.wilzwert.myjobs.core.domain.ports.driven.PasswordHasher;
import com.wilzwert.myjobs.core.domain.ports.driven.UserRepository;
import com.wilzwert.myjobs.core.domain.ports.driving.RegisterUseCase;

import java.util.ArrayList;
/**
 * @author Wilhelm Zwertvaegher
 * Date:13/03/2025
 * Time:09:07
 */

public class RegisterUseCaseImpl implements RegisterUseCase {
    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;

    public RegisterUseCaseImpl(UserRepository userRepository, PasswordHasher passwordHasher) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
    }


    @Override
    public User registerUser(RegisterUserCommand registerUserCommand) {
        if(userRepository.findByEmailOrUsername(registerUserCommand.email(), registerUserCommand.username()).isPresent()) {
            throw new UserAlreadyExistsException();
        }

        return userRepository.save(
                new User(
                        UserId.generate(),
                        registerUserCommand.email(),
                        passwordHasher.hashPassword(registerUserCommand.password()),
                        registerUserCommand.username(),
                        registerUserCommand.firstName(),
                        registerUserCommand.lastName(),
                        "USER",
                        null,
                        null,
                        new ArrayList<>()
                )
        );
    }
}
