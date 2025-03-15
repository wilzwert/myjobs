package com.wilzwert.myjobs.domain.model;


import lombok.*;
import lombok.experimental.Accessors;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * @author Wilhelm Zwertvaegher
 * Date:12/03/2025
 * Time:15:32
 */
@Getter
@EqualsAndHashCode
@Accessors(chain = true)
@AllArgsConstructor
public class Job {
    private final UUID id;

    private final String url;

    private JobStatus status;

    private final String title;

    private final String description;

    private final String profile;

    private final Instant createdAt;

    private final Instant updatedAt;

    private final UUID userId;

    private final List<Activity> activities;

    public Activity addActivity(Activity activity) {
        System.out.println(activities);
        activities.add(activity);

        // FIXME
        switch(activity.getType()) {
            case CREATION -> this.status = JobStatus.CREATED;
            default -> this.status = JobStatus.PENDING;
        }

        return activity;
    }
}
