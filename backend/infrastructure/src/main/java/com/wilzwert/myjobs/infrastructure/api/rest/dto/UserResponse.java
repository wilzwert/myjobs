package com.wilzwert.myjobs.infrastructure.api.rest.dto;


import com.wilzwert.myjobs.core.domain.model.user.Lang;
import lombok.*;

import java.time.Instant;

/**
 * @author Wilhelm Zwertvaegher
 */

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {

    private String firstName;

    private String lastName;

    private String email;

    private String username;

    private Instant createdAt;

    private String emailStatus;

    private Integer jobFollowUpReminderDays;

    private Instant jobFollowUpReminderSentAt;

    private Lang lang;
}
