package com.wilzwert.myjobs.core.domain.ports.driving;

import com.wilzwert.myjobs.core.domain.command.ValidateEmailCommand;
import com.wilzwert.myjobs.core.domain.model.User;
import com.wilzwert.myjobs.core.domain.model.UserId;

public interface SendVerificationEmailUseCase {
    void sendVerificationEmail(UserId userId);
}
