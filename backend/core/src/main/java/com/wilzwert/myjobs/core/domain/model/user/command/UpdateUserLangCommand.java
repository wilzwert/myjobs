package com.wilzwert.myjobs.core.domain.model.user.command;


import com.wilzwert.myjobs.core.domain.model.user.Lang;
import com.wilzwert.myjobs.core.domain.model.user.UserId;

/**
 * @author Wilhelm Zwertvaegher
 */

public record UpdateUserLangCommand(Lang lang, UserId userId) {
}
