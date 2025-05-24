package com.wilzwert.myjobs.core.domain.model.user.ports.driving;


import com.wilzwert.myjobs.core.domain.model.user.command.UpdateUserCommand;
import com.wilzwert.myjobs.core.domain.model.user.User;

/**
 * @author Wilhelm Zwertvaegher
 */
public interface UpdateUserUseCase {
    User updateUser(UpdateUserCommand command);
}
