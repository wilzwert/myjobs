package com.wilzwert.myjobs.core.domain.model.job;

import com.wilzwert.myjobs.core.domain.exception.ValidationException;
import com.wilzwert.myjobs.core.domain.model.activity.Activity;
import com.wilzwert.myjobs.core.domain.model.activity.ActivityType;
import com.wilzwert.myjobs.core.domain.model.attachment.Attachment;
import com.wilzwert.myjobs.core.domain.model.DomainEntity;
import com.wilzwert.myjobs.core.domain.model.user.UserId;
import com.wilzwert.myjobs.core.domain.shared.validation.ValidationResult;
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

    private final UserId userId;

    private final List<Activity> activities;

    private final List<Attachment> attachments;

    private final static Map<ActivityType, JobStatus> activityToStatus = Map.of(
        ActivityType.APPLICANT_REFUSAL, JobStatus.APPLICANT_REFUSED,
        ActivityType.COMPANY_REFUSAL, JobStatus.COMPANY_REFUSED,
        ActivityType.RELAUNCH, JobStatus.RELAUNCHED
    );

    public static Builder builder() {
        return new Builder();
    }

    public static Builder from(Job job) {
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

        private UserId userId;

        private List<Activity> activities;

        private List<Attachment> attachments;

        public Builder() {
            id = JobId.generate();
            status = JobStatus.CREATED;
            rating = JobRating.of(0);
            createdAt = Instant.now();
            updatedAt = Instant.now();
            activities = new ArrayList<>();
            attachments = new ArrayList<>();
        }

        public Builder(Job job) {
            id = job.getId();
            url = job.getUrl();
            status = job.getStatus();
            title = job.getTitle();
            company = job.getCompany();
            description = job.getDescription();
            profile = job.getProfile();
            salary = job.getSalary();
            rating = job.getRating();
            createdAt = job.getCreatedAt();
            updatedAt = job.getUpdatedAt();
            userId = job.getUserId();
            activities = job.getActivities();
            attachments = job.getAttachments();
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

        private ValidationResult validate() {
            ValidationResult validationResult = new ValidationResult();

            Validator.requireNotEmpty("title", title, validationResult);
            Validator.requireNotEmpty("description", description, validationResult);
            Validator.requireNotEmpty("url", url, validationResult);
            Validator.requireValidUrl("url", url, validationResult);


            return validationResult;
        }

        public Job build() {
            ValidationResult validationResult = validate();
            if(!validationResult.isValid()) {
                throw new ValidationException(validationResult.getErrors());
            }

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
                    userId,
                    activities,
                    attachments
            );
        }
    }

    private Job(JobId id, String url, JobStatus status, String title, String company, String description, String profile, String salary, JobRating rating, Instant createdAt, Instant updatedAt, UserId userId, List<Activity> activities, List<Attachment> attachments) {
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