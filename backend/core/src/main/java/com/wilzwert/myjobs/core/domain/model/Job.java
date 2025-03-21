package com.wilzwert.myjobs.core.domain.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Wilhelm Zwertvaegher
 * Date:12/03/2025
 * Time:15:32
 */
public class Job extends DomainEntity<JobId> {
    private final JobId id;

    private final String url;

    private final JobStatus status;

    private final String title;

    private final String company;

    private final String description;

    private final String profile;

    private final Instant createdAt;

    private final Instant updatedAt;

    private final UserId userId;

    private final List<Activity> activities;

    public Job(JobId id, String url, JobStatus status, String title, String company, String description, String profile, Instant createdAt, Instant updatedAt, UserId userId, List<Activity> activities) {
        this.id = id;
        this.url = url;
        this.status = status;
        this.title = title;
        this.company = company;
        this.description = description;
        this.profile = profile;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.userId = userId;
        // ensure immutability
        this.activities = List.copyOf(activities);
    }

    public Job addActivity(Activity activity) {
        JobStatus newJobStatus;
        switch(activity.getType()) {
            case CREATION -> newJobStatus = JobStatus.CREATED;
            case RELAUNCH -> newJobStatus = JobStatus.RELAUNCHED;
            default -> newJobStatus = JobStatus.PENDING;
        }

        var updatedActivities = new ArrayList<>(getActivities());
        updatedActivities.add(activity);

        return new Job(
                getId(),
                url,
                newJobStatus,
                title,
                company,
                description,
                profile,
                getCreatedAt(),
                Instant.now(),
                getUserId(),
                updatedActivities
        );
    }

    public Job updateJob(String url, String title, String company, String description, String profile) {
        return new Job(
            getId(),
            url,
            getStatus(),
            title,
            company,
            description,
            profile,
            getCreatedAt(),
            Instant.now(),
            getUserId(),
            getActivities()
        );
    }

    public void addAttachment() {
        // TODO
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void editStatus(JobStatus newStatus) {
        // TODO
        if(this.status == newStatus) return;

        // check if last activity matches status
        // if not, automatically create appropriate activity
        // this ensures coherence between different actions
        // e.g. user adds activity COMPANY_REFUSAL -> job  status becomes COMPANY_REFUSED
        // user changes job status to COMPANY_REFUSED -> create activity with type COMPANY_REFUSAL if needed


        throw new UnsupportedOperationException("Not supported yet.");
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

    public String getCompany() {
        return company;
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