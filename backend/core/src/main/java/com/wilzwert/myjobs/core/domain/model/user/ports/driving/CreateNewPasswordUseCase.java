package com.wilzwert.myjobs.core.domain.model.user.ports.driving;

import com.wilzwert.myjobs.core.domain.model.user.command.CreatePasswordCommand;

public interface CreateNewPasswordUseCase {
    void createNewPassword(CreatePasswordCommand createPasswordCommand);
}
