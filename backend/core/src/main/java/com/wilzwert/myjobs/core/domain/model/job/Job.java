package com.wilzwert.myjobs.core.domain.model.job;

import com.wilzwert.myjobs.core.domain.exception.ValidationException;
import com.wilzwert.myjobs.core.domain.model.activity.Activity;
import com.wilzwert.myjobs.core.domain.model.activity.ActivityType;
import com.wilzwert.myjobs.core.domain.model.attachment.Attachment;
import com.wilzwert.myjobs.core.domain.model.DomainEntity;
import com.wilzwert.myjobs.core.domain.model.user.UserId;
import com.wilzwert.myjobs.core.domain.shared.validation.ValidationErrors;
import com.wilzwert.myjobs.core.domain.shared.validation.Validator;

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

    private final Instant statusUpdatedAt;

    private final Instant followUpReminderSentAt;

    private final UserId userId;

    private final List<Activity> activities;

    private final List<Attachment> attachments;

    private static final Map<ActivityType, JobStatus> activityToStatus = Map.of(
        ActivityType.APPLICATION, JobStatus.PENDING,
        ActivityType.APPLICANT_REFUSAL, JobStatus.APPLICANT_REFUSED,
        ActivityType.COMPANY_REFUSAL, JobStatus.COMPANY_REFUSED,
        ActivityType.RELAUNCH, JobStatus.RELAUNCHED
    );

    public static Builder builder() {
        return new Builder();
    }

    private static Builder from(Job job) {
        return new Builder(job);
    }

    public static class Builder {
        private JobId id;

        private String url;

        private JobStatus status;

        private String title;

        private String company;

        private String description;

        private String profile;

        private String salary;

        private JobRating rating;

        private Instant createdAt;

        private Instant updatedAt;

        private Instant statusUpdatedAt;

        private Instant followUpReminderSentAt;

        private UserId userId;

        private List<Activity> activities;

        private List<Attachment> attachments;

        public Builder() {
        }

        public Builder(Job job) {
            this.id = job.getId();
            this.url = job.getUrl();
            this.status = job.getStatus();
            this.title = job.getTitle();
            this.company = job.getCompany();
            this.description = job.getDescription();
            this.profile = job.getProfile();
            this.salary = job.getSalary();
            this.rating = job.getRating();
            this.createdAt = job.getCreatedAt();
            this.updatedAt = job.getUpdatedAt();
            this.statusUpdatedAt = job.getStatusUpdatedAt();
            this.followUpReminderSentAt = job.getFollowUpReminderSentAt();
            this.userId = job.getUserId();
            this.activities = job.getActivities();
            this.attachments = job.getAttachments();
        }

        public Builder id(JobId id) {
            this.id = id;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder status(JobStatus status) {
            this.status = status;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder company(String company) {
            this.company = company;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder profile(String profile) {
            this.profile = profile;
            return this;
        }

        public Builder salary(String salary) {
            this.salary = salary;
            return this;
        }

        public Builder rating(JobRating rating) {
            this.rating = rating;
            return this;
        }

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(Instant updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Builder statusUpdatedAt(Instant updatedAt) {
            this.statusUpdatedAt = updatedAt;
            return this;
        }

        public Builder followUpReminderSentAt(Instant followUpReminderSentAt) {
            this.followUpReminderSentAt = followUpReminderSentAt;
            return this;
        }

        public Builder userId(UserId userId) {
            this.userId = userId;
            return this;
        }

        public Builder activities(List<Activity> activities) {
            this.activities = activities;
            return this;
        }

        public Builder attachments(List<Attachment> attachments) {
            this.attachments = attachments;
            return this;
        }

        public Job build() {
            return new Job(this);
        }
    }

    private ValidationErrors validate() {
        return  new Validator()
                .requireNotEmpty("id", id)
                .requireNotEmpty("userId", userId)
                .requireNotEmpty("title", title)
                .requireNotEmpty("description", description)
                .requireValidUrl("url", url)
                .getErrors();
    }

    private Job(Builder builder) {
        this.id = builder.id != null ? builder.id : JobId.generate();
        this.url = builder.url;
        this.status = builder.status != null ? builder.status : JobStatus.CREATED;
        this.title = builder.title;
        this.company = builder.company;
        this.description = builder.description;
        this.profile = builder.profile;
        this.salary = builder.salary;
        this.rating = builder.rating != null ? builder.rating : JobRating.of(0);
        this.createdAt = builder.createdAt != null ? builder.createdAt : Instant.now();
        this.updatedAt = builder.updatedAt != null ? builder.updatedAt : Instant.now();
        this.statusUpdatedAt = builder.statusUpdatedAt != null ? builder.statusUpdatedAt : Instant.now();
        this.followUpReminderSentAt = builder.followUpReminderSentAt;
        this.userId = builder.userId;

        // ensure immutability
        this.activities = builder.activities != null ? List.copyOf(builder.activities) : new ArrayList<>();
        // ensure immutability
        this.attachments = builder.attachments != null ? List.copyOf(builder.attachments) : new ArrayList<>();

        ValidationErrors validationErrors = validate();
        if(validationErrors.hasErrors()) {
            throw new ValidationException(validationErrors);
        }
    }

    private Job copy(List<Attachment> attachments, List<Activity> activities, JobStatus status, Instant updatedAt) {
        Instant newStatusUpdatedAt = getStatusUpdatedAt();
        if( status != null && !status.equals(getStatus())) {
            // set statusUpdatedAt
            newStatusUpdatedAt = Instant.now();
        }

        return new Job(
            from(this)
                .status(status != null ? status : getStatus())
                .updatedAt(updatedAt != null ? updatedAt : getUpdatedAt())
                .statusUpdatedAt(newStatusUpdatedAt)
                .activities(activities != null ? activities : getActivities())
                .attachments(attachments != null ? attachments : getAttachments())
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
            from(this)
                .url(url)
                .title(title)
                .company(company)
                .description(description)
                .profile(profile)
                .salary(salary)
                .updatedAt(Instant.now())
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
        Activity activity = !activities.isEmpty() ? activities.getLast() : null;
        if(activityType != null && (activity == null || !activity.getType().equals(activityType))) {
            // create activity
            Activity newActivity = Activity.builder().type(activityType).build();
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
            from(this)
                .rating(newJobRating)
                .updatedAt(Instant.now())
        );
        Activity newActivity = Activity.builder().type(ActivityType.RATING).comment(""+newJobRating.getValue()).build();
        return result.addActivity(newActivity);
    }

    public Job saveFollowUpReminderSentAt() {
        return new Job(
            from(this)
                .followUpReminderSentAt(Instant.now())
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

    public Instant getCreatedAt() {return createdAt;}

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Instant getStatusUpdatedAt() {
        return statusUpdatedAt;
    }

    public Instant getFollowUpReminderSentAt() { return followUpReminderSentAt; }

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