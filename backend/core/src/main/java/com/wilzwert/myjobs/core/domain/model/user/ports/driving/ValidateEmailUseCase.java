package com.wilzwert.myjobs.core.domain.model.user.ports.driving;

import com.wilzwert.myjobs.core.domain.model.user.command.ValidateEmailCommand;
import com.wilzwert.myjobs.core.domain.model.user.User;

public interface ValidateEmailUseCase {
    User validateEmail(ValidateEmailCommand command);
}
