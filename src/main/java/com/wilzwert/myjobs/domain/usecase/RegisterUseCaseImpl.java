package com.wilzwert.myjobs.domain.usecase;


import com.wilzwert.myjobs.domain.command.RegisterUserCommand;
import com.wilzwert.myjobs.domain.exception.UserAlreadyExistsException;
import com.wilzwert.myjobs.domain.model.User;
import com.wilzwert.myjobs.domain.ports.driven.PasswordHasher;
import com.wilzwert.myjobs.domain.ports.driven.UserRepository;
import com.wilzwert.myjobs.domain.ports.driving.RegisterUseCase;

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
            User.fromCommand(registerUserCommand)
                .setPassword(passwordHasher.hashPassword(registerUserCommand.password()))
                .setRole("USER")
        );
    }
}
