package com.wilzwert.myjobs.core.domain.model.user.ports.driving;


import com.wilzwert.myjobs.core.domain.model.user.command.RegisterUserCommand;
import com.wilzwert.myjobs.core.domain.model.user.User;

/**
 * @author Wilhelm Zwertvaegher
 * Date:12/03/2025
 * Time:15:26
 */
public interface RegisterUseCase {
    User registerUser(RegisterUserCommand registerUserCommand);
}
