package com.wilzwert.myjobs.domain.usecase;


import com.wilzwert.myjobs.domain.exception.LoginException;
import com.wilzwert.myjobs.domain.model.AuthenticatedUser;
import com.wilzwert.myjobs.domain.model.User;
import com.wilzwert.myjobs.domain.ports.driven.Authenticator;
import com.wilzwert.myjobs.domain.ports.driven.PasswordHasher;
import com.wilzwert.myjobs.domain.ports.driven.UserRepository;
import com.wilzwert.myjobs.domain.ports.driving.LoginUseCase;

/**
 * @author Wilhelm Zwertvaegher
 * Date:13/03/2025
 * Time:19:10
 */

public class LoginUseCaseImpl implements LoginUseCase {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final Authenticator authenticator;

    public LoginUseCaseImpl(UserRepository userRepository, PasswordHasher passwordHasher, Authenticator authenticator) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.authenticator = authenticator;
    }


    @Override
    public AuthenticatedUser authenticateUser(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(LoginException::new);

        if(!passwordHasher.verifyPassword(password, user.getPassword())) {
            throw new LoginException();
        }

        return authenticator.authenticate(user);
    }
}
