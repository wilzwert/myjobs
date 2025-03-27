package com.wilzwert.myjobs.core.domain.services;


import com.wilzwert.myjobs.core.domain.command.RegisterUserCommand;
import com.wilzwert.myjobs.core.domain.exception.LoginException;
import com.wilzwert.myjobs.core.domain.exception.UserAlreadyExistsException;
import com.wilzwert.myjobs.core.domain.model.User;
import com.wilzwert.myjobs.core.domain.ports.driven.PasswordHasher;
import com.wilzwert.myjobs.core.domain.ports.driven.UserService;

/**
 * @author Wilhelm Zwertvaegher
 * Date:13/03/2025
 * Time:09:07
 */

public class UserDomainService {
    private final UserService userService;
    private final PasswordHasher passwordHasher;

    public UserDomainService(UserService userService, PasswordHasher passwordHasher) {
        this.userService = userService;
        this.passwordHasher = passwordHasher;
    }

    public User registerUser(RegisterUserCommand registerUserCommand) {
        if(userService.findByEmailOrUsername(registerUserCommand.email(), registerUserCommand.username()).isPresent()) {
            throw new UserAlreadyExistsException();
        }

        return
            User.create(
                    registerUserCommand.email(),
                    passwordHasher.hashPassword(registerUserCommand.password()),
                    registerUserCommand.username(),
                    registerUserCommand.firstName(),
                    registerUserCommand.lastName()
            );
    }

    public User authenticateUser(String email, String password) {
        User user = userService.findByEmail(email)
                .orElseThrow(LoginException::new);

        if(!passwordHasher.verifyPassword(password, user.getPassword())) {
            throw new LoginException();
        }

        return user;
    }

    public boolean isEmailTaken(String email) {
        return userService.emailExists(email);
    }

    public boolean isUsernameTaken(String username) {
        return userService.usernameExists(username);
    }
}
