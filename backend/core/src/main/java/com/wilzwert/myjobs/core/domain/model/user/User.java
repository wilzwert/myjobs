package com.wilzwert.myjobs.core.domain.model.user;


import com.wilzwert.myjobs.core.domain.exception.*;
import com.wilzwert.myjobs.core.domain.model.*;
import com.wilzwert.myjobs.core.domain.model.activity.Activity;
import com.wilzwert.myjobs.core.domain.model.activity.ActivityType;
import com.wilzwert.myjobs.core.domain.model.job.Job;
import com.wilzwert.myjobs.core.domain.shared.validation.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Wilhelm Zwertvaegher
 * Date:12/03/2025
 * Time:15:32
 * TODO : use defensive copying on collections' getters to ensure immutability
 */
public class User extends DomainEntity<UserId> {

    public static final Integer defaultJobFollowUpReminderDays = 14;
    public static final Lang defaultLang = Lang.EN;
    public static final String defaultRole = "USER";

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

    private final List<Job> jobs;

    public static Builder builder() {
        return new Builder();
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

        private List<Job> jobs;

        public Builder() {}

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
        public Builder jobs(List<Job> jobs) {
            this.jobs = jobs;
            return this;
        }

        public User build() {
            // build User
            return new User(id, email, emailStatus, emailValidationCode, password, username, firstName, lastName, jobFollowUpReminderDays, lang, role, resetPasswordToken, resetPasswordExpiresAt, createdAt, updatedAt, jobs);
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
                .requireMin("jobFollowUpReminderDays", jobFollowUpReminderDays, 3)
                .requireMax("jobFollowUpReminderDays", jobFollowUpReminderDays, 30)
                .requireNotEmpty("role", role)
                .requireNotEmpty("password", password)
                .getErrors();
    }

    private User(UserId id, String email, EmailStatus emailStatus, String emailValidationCode, String password, String username, String firstName, String lastName, Integer jobFollowUpReminderDays, Lang lang, String role, String resetPasswordToken, Instant resetPasswordExpiresAt, Instant createdAt, Instant updatedAt, List<Job> jobs) {
        this.id = id != null ? id : UserId.generate();
        this.email = email;
        this.emailValidationCode = emailValidationCode != null ? emailValidationCode : UUID.randomUUID().toString();
        this.emailStatus = emailStatus != null ? emailStatus : EmailStatus.PENDING;
        this.password = password;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.jobFollowUpReminderDays = jobFollowUpReminderDays != null ? jobFollowUpReminderDays : defaultJobFollowUpReminderDays;
        this.lang = lang != null ? lang : defaultLang;
        this.role = role != null ? role : defaultRole;
        this.resetPasswordToken = resetPasswordToken;
        this.resetPasswordExpiresAt = resetPasswordExpiresAt;
        this.createdAt = createdAt != null ? createdAt : Instant.now();
        this.updatedAt = updatedAt != null ? updatedAt : Instant.now();
        this.jobs = jobs != null ? jobs : new ArrayList<>();

        // validate the User state
        ValidationErrors validationErrors = validate();
        if(validationErrors.hasErrors()) {
            System.out.println(validationErrors);
            throw new ValidationException(validationErrors);
        }
    }

    public static User create(String email, String password, String username, String firstName, String lastName, Integer jobFollowUpReminderDays, Lang lang, String plainPassword) {
        ValidationErrors errors = new ValidationErrors();
        User createdUser = null;
        try {
            createdUser = new User(
                    null,
                    email,
                    null,
                    null,
                    password,
                    username,
                    firstName,
                    lastName,
                    jobFollowUpReminderDays,
                    lang,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
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

        return createdUser;
    }

    public User update(String email, String username, String firstName, String lastName, Integer jobFollowUpReminderDays) {

        // if email changed we have to change its status
        EmailStatus newEmailStatus = getEmailStatus();
        if(!email.equals(getEmail())) {
            newEmailStatus = EmailStatus.PENDING;
        }

        return new User(
                getId(),
                email,
                newEmailStatus,
                getEmailValidationCode(),
                getPassword(),
                username,
                firstName,
                lastName,
                jobFollowUpReminderDays,
                lang,
                getRole(),
                "",
                null,
                getCreatedAt(),
                Instant.now(),
                getJobs()
        );
    }

    public Job addJob(Job job) {
        // check if job to be added already exists by its url
        jobs.stream().filter(j -> j.getUrl().equals(job.getUrl())).findAny().ifPresent(found -> {throw new JobAlreadyExistsException();});

        jobs.add(job);

        // automatically create first activity
        return job.addActivity(Activity.builder().type(ActivityType.CREATION).build());
    }

    public void removeJob(Job job) {
        if(!jobs.contains(job)) {
            throw new JobNotFoundException();
        }
        jobs.remove(job);
    }

    public User withJobs(List<Job> jobs) {
        return new User(
                getId(),
                getEmail(),
                getEmailStatus(),
                getEmailValidationCode(),
                getPassword(),
                getUsername(),
                getFirstName(),
                getLastName(),
                getJobFollowUpReminderDays(),
                getLang(),
                getRole(),
                getResetPasswordToken(),
                getResetPasswordExpiresAt(),
                getCreatedAt(),
                getUpdatedAt(),
                jobs
        );
    }

    public User updatePassword(String plainPassword, String newPassword) {
        ValidationErrors errors = new ValidationErrors();
        User updatedUser = null;
        try {
            updatedUser = new User(
                    getId(),
                    getEmail(),
                    getEmailStatus(),
                    getEmailValidationCode(),
                    newPassword,
                    getUsername(),
                    getFirstName(),
                    getLastName(),
                    getJobFollowUpReminderDays(),
                    getLang(),
                    getRole(),
                    null,
                    null,
                    getCreatedAt(),
                    Instant.now(),
                    getJobs()
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
                getId(),
                getEmail(),
                getEmailStatus(),
                getEmailValidationCode(),
                getPassword(),
                getUsername(),
                getFirstName(),
                getLastName(),
                getJobFollowUpReminderDays(),
                getLang(),
                getRole(),
                // FIXME : maybe we should use a value object with a generator
                UUID.randomUUID().toString(),
                // FIXME : duration should not be hard coded this way
                // it should be handled by domain anyway
                Instant.now().plus(30, ChronoUnit.MINUTES),
                getCreatedAt(),
                getUpdatedAt(),
                getJobs()
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
                getId(),
                getEmail(),
                (getEmailStatus().equals(EmailStatus.PENDING) ? EmailStatus.VALIDATED : getEmailStatus()),
                getEmailValidationCode(),
                getPassword(),
                getUsername(),
                getFirstName(),
                getLastName(),
                getJobFollowUpReminderDays(),
                getLang(),
                getRole(),
                null,
                null,
                getCreatedAt(),
                Instant.now(),
                getJobs()
        );
    }

    public User updateLang(Lang lang) {
        return new User(
                getId(),
                getEmail(),
                getEmailStatus(),
                getEmailValidationCode(),
                getPassword(),
                getUsername(),
                getFirstName(),
                getLastName(),
                getJobFollowUpReminderDays(),
                lang,
                getRole(),
                getResetPasswordToken(),
                getResetPasswordExpiresAt(),
                getCreatedAt(),
                Instant.now(),
                getJobs()
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

    public List<Job> getJobs() {
        return jobs;
    }
}
