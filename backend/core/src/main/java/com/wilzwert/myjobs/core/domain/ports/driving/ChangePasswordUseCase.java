package com.wilzwert.myjobs.core.domain.ports.driving;

import com.wilzwert.myjobs.core.domain.command.ChangePasswordCommand;

public interface ChangePasswordUseCase {
    void changePassword(ChangePasswordCommand changePasswordCommand);
}
