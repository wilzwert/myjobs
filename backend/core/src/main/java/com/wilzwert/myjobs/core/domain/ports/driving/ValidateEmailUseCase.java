package com.wilzwert.myjobs.core.domain.ports.driving;

import com.wilzwert.myjobs.core.domain.command.ValidateEmailCommand;
import com.wilzwert.myjobs.core.domain.model.User;

public interface ValidateEmailUseCase {
    User validateEmail(ValidateEmailCommand command);
}
