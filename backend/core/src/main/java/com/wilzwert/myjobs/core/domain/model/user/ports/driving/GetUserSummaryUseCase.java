package com.wilzwert.myjobs.core.domain.model.user.ports.driving;


import com.wilzwert.myjobs.core.domain.model.user.UserId;
import com.wilzwert.myjobs.core.domain.model.user.UserSummary;

/**
 * @author Wilhelm Zwertvaegher
 */
public interface GetUserSummaryUseCase {
    UserSummary getUserSummary(UserId userId);
}
