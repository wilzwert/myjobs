package com.wilzwert.myjobs.core.domain.model.user.ports.driving;

import com.wilzwert.myjobs.core.domain.model.user.command.UpdateUserLangCommand;
import com.wilzwert.myjobs.core.domain.model.user.User;

/**
 * @author Wilhelm Zwertvaegher
 */
public interface UpdateUserLangUseCase {
    User updateUserLang(UpdateUserLangCommand command);
}
