package com.wilzwert.myjobs.core.domain.model.user.ports.driving;


import com.wilzwert.myjobs.core.domain.model.user.UserId;
import com.wilzwert.myjobs.core.domain.model.user.UserView;

/**
 * @author Wilhelm Zwertvaegher
 */
public interface GetUserViewUseCase {
    UserView getUser(UserId userId);
}
