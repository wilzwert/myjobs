package com.wilzwert.myjobs.core.domain.model.user.ports.driving;


import com.wilzwert.myjobs.core.domain.model.user.command.RegisterUserCommand;
import com.wilzwert.myjobs.core.domain.model.user.User;

/**
 * @author Wilhelm Zwertvaegher
 */
public interface RegisterUseCase {
    User registerUser(RegisterUserCommand registerUserCommand);
}
