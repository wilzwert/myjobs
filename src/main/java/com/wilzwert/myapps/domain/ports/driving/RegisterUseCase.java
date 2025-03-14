package com.wilzwert.myapps.domain.ports.driving;


import com.wilzwert.myapps.domain.command.RegisterUserCommand;
import com.wilzwert.myapps.domain.model.User;

/**
 * @author Wilhelm Zwertvaegher
 * Date:12/03/2025
 * Time:15:26
 */
public interface RegisterUseCase {
    User registerUser(RegisterUserCommand registerUserCommand);
}
