package com.wilzwert.myjobs.core.domain.model;

import java.net.URI;
import java.net.URL;
import java.time.Instant;
import java.util.*;

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

    private final String salary;

    private final JobRating rating;

    private final Instant createdAt;

    private final Instant updatedAt;

    private final UserId userId;

    private final List<Activity> activities;

    private final List<Attachment> attachments;

    private final static Map<ActivityType, JobStatus> activityToStatus = Map.of(
        ActivityType.APPLICANT_REFUSAL, JobStatus.APPLICANT_REFUSED,
        ActivityType.COMPANY_REFUSAL, JobStatus.COMPANY_REFUSED,
        ActivityType.RELAUNCH, JobStatus.RELAUNCHED
    );

    public static Job create(String url, String title, String company, String description, String profile, String salary, UserId userId) {
        return new Job(
                JobId.generate(),
                url,
                JobStatus.CREATED,
                title,
                company,
                description,
                profile,
                salary,
                JobRating.of(0),
                Instant.now(),
                Instant.now(),
                userId,
                new ArrayList<>(),
                new ArrayList<>()
        );
    }


    public Job(JobId id, String url, JobStatus status, String title, String company, String description, String profile, String salary, JobRating rating, Instant createdAt, Instant updatedAt, UserId userId, List<Activity> activities, List<Attachment> attachments) {
        if (url != null && !isValidUrl(url)) {
            throw new IllegalArgumentException("Invalid URL format: " + url);
        }

        this.id = id;
        this.url = url;
        this.status = status;
        this.title = title;
        this.company = company;
        this.description = description;
        this.profile = profile;
        this.salary = salary;
        this.rating = rating;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.userId = userId;
        // ensure immutability
        this.activities = List.copyOf(activities);
        // ensure immutability
        this.attachments = List.copyOf(attachments);
    }

    private Job copy(List<Attachment> attachments, List<Activity> activities, JobStatus status, Instant updatedAt) {
        return new Job(
                getId(),
                getUrl(),
                (status != null ? status : getStatus()),
                getTitle(),
                getCompany(),
                getDescription(),
                getProfile(),
                getSalary(),
                getRating(),
                getCreatedAt(),
                (updatedAt != null ? updatedAt : getUpdatedAt()),
                getUserId(),
                (activities != null ? activities : getActivities()),
                (attachments != null ? attachments : getAttachments())
        );
    }
    public Job addActivity(Activity activity) {
        JobStatus newJobStatus = activityToStatus.get(activity.getType());
        var updatedActivities = new ArrayList<>(getActivities());
        updatedActivities.add(activity);
        updatedActivities.sort(Comparator.comparing(Activity::getCreatedAt).reversed());
        return copy(null, updatedActivities, newJobStatus, Instant.now());
    }

    public Job updateJob(String url, String title, String company, String description, String profile, String salary) {
        return new Job(
                getId(),
                url,
                getStatus(),
                title,
                company,
                description,
                profile,
                salary,
                getRating(),
                getCreatedAt(),
                Instant.now(),
                getUserId(),
                getActivities(),
                getAttachments()
        );
    }

    public Job addAttachment(Attachment attachment) {
        var updatedAttachments = new ArrayList<>(getAttachments());
        updatedAttachments.add(attachment);
        updatedAttachments.sort(Comparator.comparing(Attachment::getCreatedAt).reversed());
        return copy(updatedAttachments, null, null, Instant.now());
    }

    public Job removeAttachment(Attachment attachment) {
        var updatedAttachments = new ArrayList<>(getAttachments());
        if(!updatedAttachments.contains(attachment)) {
            throw new IllegalArgumentException("Attachment not in list");
        }

        updatedAttachments.remove(attachment);
        return copy(updatedAttachments, null, null, Instant.now());
    }

    public Job updateStatus(JobStatus newStatus) {
        if(this.status == newStatus) return this;

        Job result;
        ActivityType activityType = activityToStatus.entrySet().stream().filter(entry -> newStatus.equals(entry.getValue())).map(Map.Entry::getKey).findFirst().orElse(null);
        Activity activity = activities.getLast();
        if(activityType != null && !activity.getType().equals(activityType)) {
            // create activity
            Activity newActivity = new Activity(ActivityId.generate(), activityType, getId(), "", Instant.now(), Instant.now());
            result = addActivity(newActivity);
        }
        else {
            result = this;
        }

        return result.copy(null, result.getActivities(), newStatus, Instant.now());
    }

    public Job updateRating(JobRating newJobRating) {
        if(newJobRating.equals(getRating())) return this;

        Job result = new Job(
                getId(),
                getUrl(),
                getStatus(),
                getTitle(),
                getCompany(),
                getDescription(),
                getProfile(),
                getSalary(),
                newJobRating,
                getCreatedAt(),
                Instant.now(),
                getUserId(),
                getActivities(),
                getAttachments()
        );
        Activity newActivity = new Activity(ActivityId.generate(), ActivityType.RATING, getId(), ""+newJobRating.getValue(), Instant.now(), Instant.now());
        return result.addActivity(newActivity);
    }

    private static boolean isValidUrl(String url) {
        try {
            URL u = new URI(url).toURL();
            return true;
        } catch (Exception e) {
            return false;
        }
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

    public String getSalary() { return salary; }

    public JobRating getRating() {
        return rating;
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
    public List<Attachment> getAttachments() {
        return attachments;
    }

}