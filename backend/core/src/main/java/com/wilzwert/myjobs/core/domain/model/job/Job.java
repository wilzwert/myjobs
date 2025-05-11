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

        private UserId userId;

        private List<Activity> activities;

        private List<Attachment> attachments;

        public Builder() {
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
            return new Job(
                    id,
                    url,
                    status,
                    title,
                    company,
                    description,
                    profile,
                    salary,
                    rating,
                    createdAt,
                    updatedAt,
                    statusUpdatedAt,
                    userId,
                    activities,
                    attachments
            );
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

    private Job(JobId id, String url, JobStatus status, String title, String company, String description, String profile, String salary, JobRating rating, Instant createdAt, Instant updatedAt, Instant statusUpdatedAt, UserId userId, List<Activity> activities, List<Attachment> attachments) {
        this.id = id != null ? id : JobId.generate();
        this.url = url;
        this.status = status != null ? status : JobStatus.CREATED;
        this.title = title;
        this.company = company;
        this.description = description;
        this.profile = profile;
        this.salary = salary;
        this.rating = rating != null ? rating : JobRating.of(0);
        this.createdAt = createdAt != null ? createdAt : Instant.now();
        this.updatedAt = updatedAt != null ? updatedAt : Instant.now();
        this.statusUpdatedAt = statusUpdatedAt != null ? statusUpdatedAt : Instant.now();
        this.userId = userId;

        // ensure immutability
        this.activities = activities != null ? List.copyOf(activities) : new ArrayList<>();
        // ensure immutability
        this.attachments = attachments != null ? List.copyOf(attachments) : new ArrayList<>();

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
            newStatusUpdatedAt,
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
                getStatusUpdatedAt(),
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
                getStatusUpdatedAt(),
                getUserId(),
                getActivities(),
                getAttachments()
        );
        Activity newActivity = Activity.builder().type(ActivityType.RATING).comment(""+newJobRating.getValue()).build();
        return result.addActivity(newActivity);
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

    public Instant getStatusUpdatedAt() {
        return statusUpdatedAt;
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