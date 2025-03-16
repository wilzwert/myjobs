package com.wilzwert.myjobs.core.domain.model;

import java.time.Instant;
import java.util.List;

/**
 * @author Wilhelm Zwertvaegher
 * Date:12/03/2025
 * Time:15:32
 */
public class Job {
    private final JobId id;

    private final String url;

    private JobStatus status;

    private final String title;

    private final String description;

    private final String profile;

    private final Instant createdAt;

    private final Instant updatedAt;

    private final UserId userId;

    private final List<Activity> activities;

    public Job(JobId id, String url, JobStatus status, String title, String description, String profile, Instant createdAt, Instant updatedAt, UserId userId, List<Activity> activities) {
        this.id = id;
        this.url = url;
        this.status = status;
        this.title = title;
        this.description = description;
        this.profile = profile;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.userId = userId;
        this.activities = activities;
    }

    public Activity addActivity(Activity activity) {
        activities.add(activity);

        // FIXME
        switch(activity.getType()) {
            case CREATION -> this.status = JobStatus.CREATED;
            default -> this.status = JobStatus.PENDING;
        }

        return activity;
    }

    public Job updateJob(JobStatus status, String url, String title, String description, String profile) {
        return new Job(
            getId(),
            url,
            status,
            title,
            description,
            profile,
            getCreatedAt(),
            Instant.now(),
            getUserId(),
            getActivities()
        );
    }

    public JobId getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public JobStatus getStatus() {
        return status;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getProfile() {
        return profile;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public UserId getUserId() {
        return userId;
    }

    public List<Activity> getActivities() {
        return activities;
    }
}
