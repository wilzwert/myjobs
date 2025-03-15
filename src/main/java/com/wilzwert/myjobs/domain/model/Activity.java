package com.wilzwert.myjobs.domain.model;


import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.time.Instant;

/**
 * @author Wilhelm Zwertvaegher
 * Date:12/03/2025
 * Time:15:33
 */

@Getter
@EqualsAndHashCode
@Accessors(chain = true)
@AllArgsConstructor
public class Activity {
    private final String id;

    private final ActivityType type;

    private final Instant createdAt;

    private final Instant updatedAt;


}
