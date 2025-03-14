package com.wilzwert.myapps.domain.usecase;


import com.wilzwert.myapps.domain.command.RegisterUserCommand;
import com.wilzwert.myapps.domain.model.User;
import com.wilzwert.myapps.domain.ports.driven.PasswordHasher;
import com.wilzwert.myapps.domain.ports.driven.UserRepository;
import com.wilzwert.myapps.domain.ports.driving.RegisterUseCase;

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
        return userRepository.save(
            User.fromCommand(registerUserCommand)
                .setPassword(passwordHasher.hashPassword(registerUserCommand.password()))
                .setRole("USER")
        );
    }
}
