package com.wilzwert.myjobs.core.domain.ports.driving;

import com.wilzwert.myjobs.core.domain.command.CreatePasswordCommand;

public interface CreateNewPasswordUseCase {
    void createNewPassword(CreatePasswordCommand createPasswordCommand);
}
