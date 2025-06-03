package com.wilzwert.myjobs.core.domain.model.user;


import com.wilzwert.myjobs.core.domain.model.*;
import com.wilzwert.myjobs.core.domain.model.activity.Activity;
import com.wilzwert.myjobs.core.domain.model.activity.ActivityType;
import com.wilzwert.myjobs.core.domain.model.job.Job;
import com.wilzwert.myjobs.core.domain.model.job.JobId;
import com.wilzwert.myjobs.core.domain.model.job.exception.JobAlreadyExistsException;
import com.wilzwert.myjobs.core.domain.model.job.exception.JobNotFoundException;
import com.wilzwert.myjobs.core.domain.model.user.exception.ResetPasswordExpiredException;
import com.wilzwert.myjobs.core.domain.model.user.exception.UserNotFoundException;
import com.wilzwert.myjobs.core.domain.shared.exception.ValidationException;
import com.wilzwert.myjobs.core.domain.shared.validation.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Wilhelm Zwertvaegher
 * TODO : use defensive copying on collections' getters to ensure immutability
 */
public class User extends DomainEntity<UserId> {

    public static final Integer DEFAULT_JOB_FOLLOW_UP_REMINDER_DAYS = 14;
    public static final Lang DEFAULT_LANG = Lang.EN;
    public static final String DEFAULT_ROLE = "USER";

    private final UserId id;
    private final String email;
    private final EmailStatus emailStatus;
    private final String emailValidationCode;
    private final String password;
    private final String username;
    private final String firstName;
    private final String lastName;
    /**
     * Number of days after which a reminder should be triggered for jobs considered active
     * that haven't received any user interaction (e.g. no updates, status changes, or comments).
     * This helps ensure that jobs requiring attention are not forgotten.
     * Example: if set to 7, a reminder will be issued 7 days after the last action on the job.
     * Must be between 3 and 30, defaults to 14
     */
    private final Integer jobFollowUpReminderDays;
    private final Lang lang;
    private final String role;
    private final String resetPasswordToken;
    private final Instant resetPasswordExpiresAt;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final Instant jobFollowUpReminderSentAt;
    private final List<Job> jobs;

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Warning : use only when sure user is a complete aggregate !
     * @param user the user we want to get a Builder from
     * @return the Builder
     */
    public static Builder from(User user) {
        return new Builder(user, true);
    }

    /**
     * Warning : this is used to get a Builder allowing an incomplete aggregate
     * Use with great caution, as some methods on the aggregate won't work if it is not complete !
     * @param user the user we want to get a Builder from
     * @return the Builder
     */
    public static Builder fromMinimal(User user) {
        return new Builder(user, false);
    }

    // Only for persistence mapping and tests. Do not use for new User creation!
    public static class Builder {
        private UserId id;
        private String email;
        private EmailStatus emailStatus;
        private String emailValidationCode;
        private String password;
        private String username;
        private String firstName;
        private String lastName;
        private Integer jobFollowUpReminderDays;
        private Lang lang;
        private String role;
        private String resetPasswordToken;
        private Instant resetPasswordExpiresAt;
        private Instant createdAt;
        private Instant updatedAt;
        private Instant jobFollowUpReminderSentAt;

        private List<Job> jobs = null;

        public Builder() {}

        private Builder(User user, boolean full) {
            id = user.getId();
            email = user.getEmail();
            emailStatus = user.getEmailStatus();
            emailValidationCode = user.getEmailValidationCode();
            password = user.getPassword();
            username = user.getUsername();
            firstName = user.getFirstName();
            lastName = user.getLastName();
            jobFollowUpReminderDays = user.getJobFollowUpReminderDays();
            lang = user.getLang();
            role = user.getRole();
            resetPasswordToken = user.getResetPasswordToken();
            resetPasswordExpiresAt = user.getResetPasswordExpiresAt();
            createdAt = user.getCreatedAt();
            updatedAt = user.getUpdatedAt();
            jobFollowUpReminderSentAt = user.getJobFollowUpReminderSentAt();
            jobs = full || user.jobs != null ? user.getJobs() : null;
        }

        public Builder id(UserId userId) {
            this.id = userId;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder emailStatus(EmailStatus emailStatus) {
            this.emailStatus = emailStatus;
            return this;
        }

        public Builder emailValidationCode(String emailValidationCode) {
            this.emailValidationCode = emailValidationCode;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }
        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }
        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder jobFollowUpReminderDays(Integer jobFollowUpReminderDays) {
            this.jobFollowUpReminderDays = jobFollowUpReminderDays;
            return this;
        }

        public Builder lang(Lang lang) {
            this.lang = lang;
            return this;
        }

        public Builder role(String role) {
            this.role = role;
            return this;
        }
        public Builder resetPasswordToken(String resetPasswordToken) {
            this.resetPasswordToken = resetPasswordToken;
            return this;
        }
        public Builder resetPasswordExpiresAt(Instant resetPasswordExpiresAt) {
            this.resetPasswordExpiresAt = resetPasswordExpiresAt;
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

        public Builder jobFollowUpReminderSentAt(Instant jobFollowUpReminderSentAt) {
            this.jobFollowUpReminderSentAt = jobFollowUpReminderSentAt;
            return this;
        }

        public Builder jobs(List<Job> jobs) {
            this.jobs = jobs;
            return this;
        }

        public User build() {
            // build User
            return new User(this);
        }
    }

    private static ValidationError validatePassword(String plainPassword) {
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^\\w\\s]).+$";
        if(plainPassword == null || plainPassword.isEmpty() || !plainPassword.matches(regex)) {
            return new ValidationError("password", ErrorCode.USER_WEAK_PASSWORD);
        }
        return null;
    }

    private ValidationErrors validate() {
        return new Validator()
                .requireValidEmail("email", email)
                .requireMinLength("username", username, 2)
                .requireMaxLength("username", username, 30)
                .requireNotEmpty("firstName", firstName)
                .requireNotEmpty("lastName", lastName)
                .requireMinIfNotNull("jobFollowUpReminderDays", jobFollowUpReminderDays, 3)
                .requireMaxIfNotNull("jobFollowUpReminderDays", jobFollowUpReminderDays, 30)
                .requireNotEmpty("role", role)
                .requireNotEmpty("password", password)
                .getErrors();
    }

    private User(User.Builder builder) {
        this.id = builder.id != null ? builder.id : UserId.generate();
        this.email = builder.email;
        this.emailValidationCode = builder.emailValidationCode != null ? builder.emailValidationCode : UUID.randomUUID().toString();
        this.emailStatus = builder.emailStatus != null ? builder.emailStatus : EmailStatus.PENDING;
        this.password = builder.password;
        this.username = builder.username;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.jobFollowUpReminderDays = builder.jobFollowUpReminderDays != null ? builder.jobFollowUpReminderDays : DEFAULT_JOB_FOLLOW_UP_REMINDER_DAYS;
        this.lang = builder.lang != null ? builder.lang : DEFAULT_LANG;
        this.role = builder.role != null ? builder.role : DEFAULT_ROLE;
        this.resetPasswordToken = builder.resetPasswordToken;
        this.resetPasswordExpiresAt = builder.resetPasswordExpiresAt;
        this.createdAt = builder.createdAt != null ? builder.createdAt : Instant.now();
        this.updatedAt = builder.updatedAt != null ? builder.updatedAt : Instant.now();
        this.jobFollowUpReminderSentAt = builder.jobFollowUpReminderSentAt;
        // jobs may be null in some cases, but it will throw an exception for operations that need them to be loaded
        this.jobs = builder.jobs != null ? List.copyOf(builder.jobs) : null;

        // validate the User state
        ValidationErrors validationErrors = validate();
        if(validationErrors.hasErrors()) {
            throw new ValidationException(validationErrors);
        }
    }

    private void requireFull() {
        requireLoadedProperty(jobs);
    }

    private User copy(List<Job> jobs, Instant updatedAt) {

        return new User(
                from(this)
                        .updatedAt(updatedAt != null ? updatedAt : getUpdatedAt())
                        .jobs(jobs != null ? jobs : getJobs())
        );
    }

    public static User create(User.Builder builder, String plainPassword) {
        ValidationErrors errors = new ValidationErrors();
        User createdUser = null;
        try {
            createdUser = new User(builder);
        }
        catch(ValidationException e) {
            errors.merge(e.getErrors());
        }

        // password strength validation is very specific and belongs to the user
        ValidationError error = validatePassword(plainPassword);
        if(error != null) {
            errors.add(error);
        }

        if(errors.hasErrors()) {
            throw new ValidationException(errors);
        }

        return createdUser;
    }

    public User update(String email, String username, String firstName, String lastName, Integer jobFollowUpReminderDays) {

        // if email changed we have to change its status
        EmailStatus newEmailStatus = getEmailStatus();
        if(!email.equals(getEmail())) {
            newEmailStatus = EmailStatus.PENDING;
        }

        return new User(
                fromMinimal(this)
                        .email(email)
                        .emailStatus(newEmailStatus)
                        .username(username)
                        .firstName(firstName)
                        .lastName(lastName)
                        .jobFollowUpReminderDays(jobFollowUpReminderDays)
                        .updatedAt(Instant.now())
        );
    }

    public User addJob(Job job) {
        requireFull();

        // check if job to be added already exists by its url
        jobs.stream().filter(j -> j.getUrl().equals(job.getUrl())).findAny().ifPresent(found -> {throw new JobAlreadyExistsException();});

        List<Job> newJobs = new ArrayList<>(jobs);

        // automatically create first activity
        newJobs.add(job.addActivity(Activity.builder().type(ActivityType.CREATION).build()));

        return copy(newJobs, Instant.now());
    }

    public Optional<Job> getJobById(JobId jobId) {
        return getJobs().stream().filter(j -> j.getId().equals(jobId)).findFirst();
    }

    public Optional<Job> getJobByUrl(String url) {
        return getJobs().stream().filter(j -> j.getUrl().equals(url)).findFirst();
    }

    public User updateJob(Job job, String url, String title, String company, String description, String profile, String comment, String salary) {
        requireFull();

        if(!jobs.contains(job)) {
            throw new JobNotFoundException();
        }
        if(!url.equals(job.getUrl())) {
            jobs.stream().filter(j -> j.getUrl().equals(url)).findAny().ifPresent(found -> {throw new JobAlreadyExistsException();});
        }

        int index = jobs.indexOf(job);

        List<Job> newJobs = new ArrayList<>(jobs);
        newJobs.set(index,
                Job.from(job)
                    .url(url)
                    .title(title)
                    .company(company)
                    .description(description)
                    .profile(profile)
                    .comment(comment)
                    .salary(salary)
                    .updatedAt(Instant.now())
                    .build()
        );

        System.out.println(newJobs);
        return copy(newJobs, Instant.now());
    }

    public User removeJob(Job job) {
        requireFull();

        if(!jobs.contains(job)) {
            throw new JobNotFoundException();
        }
        List<Job> newJobs = new ArrayList<>(jobs);
        newJobs.remove(job);
        return copy(newJobs, Instant.now());
    }

    /**
     * Used to complete the aggregate with its required properties
     * As of now, the only property needed to be complete, and which may not be loaded, is jobs
     * This may be used when an incomplete User is loaded, and you want to make it complete and coherent
     * and do stuff on it
     * @param jobsList the user's jobs list
     * @return a complete User aggregate
     */
    public User completeWith(List<Job> jobsList) {
        requireLoadedProperty(jobsList);

        return new User(
            fromMinimal(this)
                .jobs(jobsList)
        );
    }

    public User updatePassword(String plainPassword, String newPassword) {
        ValidationErrors errors = new ValidationErrors();
        User updatedUser = null;
        try {
            updatedUser = new User(
                from(this)
                    .password(newPassword)
            );
        }
        catch(ValidationException e) {
            errors.merge(e.getErrors());
        }

        // password strength validation is very specific and belongs to the user
        ValidationError error = validatePassword(plainPassword);
        if(error != null) {
            errors.add(error);
        }

        if(errors.hasErrors()) {
            throw new ValidationException(errors);
        }

        return updatedUser;
    }

    public User resetPassword() {
        // a reset password request just overrides all previous ones
        return new User(
                from(this)
                    // FIXME : maybe we should use a value object with a generator
                    .resetPasswordToken(UUID.randomUUID().toString())
                    // FIXME : duration should not be hard coded this way
                    // it should be handled by domain anyway
                    .resetPasswordExpiresAt(Instant.now().plus(30, ChronoUnit.MINUTES))
        );
    }

    public User createNewPassword(String plainPassword, String newPassword) {
        if(resetPasswordExpiresAt.isBefore(Instant.now())) {
            throw new ResetPasswordExpiredException();
        }

        return updatePassword(plainPassword, newPassword);
    }

    public User validateEmail(String emailValidationCode) {
        if (getEmailValidationCode() == null || !getEmailValidationCode().equals(emailValidationCode)) {
            throw new UserNotFoundException();
        }

        if(getEmailStatus().equals(EmailStatus.VALIDATED)) {
            return this;
        }

        return new User(
            fromMinimal(this)
                .emailStatus(getEmailStatus().equals(EmailStatus.PENDING) ? EmailStatus.VALIDATED : getEmailStatus())
        );
    }

    public User updateLang(Lang lang) {
        return new User(
            fromMinimal(this)
                .lang(lang)
        );
    }

    public User saveJobFollowUpReminderSentAt() {
        return new User(
            fromMinimal(this)
                .jobFollowUpReminderSentAt(Instant.now())
        );
    }

    public UserId getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public EmailStatus getEmailStatus() {
        return emailStatus;
    }

    public String getEmailValidationCode() {
        return emailValidationCode;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Integer getJobFollowUpReminderDays() { return jobFollowUpReminderDays; }

    public Lang getLang() { return lang; }

    public String getRole() {
        return role;
    }

    public String  getResetPasswordToken() {return resetPasswordToken;}

    public Instant getResetPasswordExpiresAt() {return resetPasswordExpiresAt;}

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Instant getJobFollowUpReminderSentAt() {return jobFollowUpReminderSentAt;}

    public List<Job> getJobs() {
        requireLoadedProperty(jobs);
        return jobs;
    }

    @Override
    public String toString() {
        return getId().toString()+ " [email="+getEmail() + "]";
    }

}
