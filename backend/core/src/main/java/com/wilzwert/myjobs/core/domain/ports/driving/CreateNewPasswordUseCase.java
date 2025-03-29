package com.wilzwert.myjobs.core.domain.ports.driving;

import com.wilzwert.myjobs.core.domain.command.PasswordCommand;

public interface CreateNewPasswordUseCase {
    void createNewPassword(PasswordCommand passwordCommand);
}
