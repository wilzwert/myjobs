package com.wilzwert.myjobs.core.domain.model.user;


import com.wilzwert.myjobs.core.domain.exception.*;
import com.wilzwert.myjobs.core.domain.model.*;
import com.wilzwert.myjobs.core.domain.model.activity.Activity;
import com.wilzwert.myjobs.core.domain.model.activity.ActivityType;
import com.wilzwert.myjobs.core.domain.model.job.Job;
import com.wilzwert.myjobs.core.domain.shared.validation.ErrorCode;
import com.wilzwert.myjobs.core.domain.shared.validation.ValidationResult;
import com.wilzwert.myjobs.core.domain.shared.validation.Validator;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Wilhelm Zwertvaegher
 * Date:12/03/2025
 * Time:15:32
 * TODO : implement password validation through a value object
 */
public class User extends DomainEntity<UserId> {
    private final UserId id;
    private final String email;
    private final EmailStatus emailStatus;
    private final String emailValidationCode;
    private final String password;
    private final String username;
    private final String firstName;
    private final String lastName;
    private final String role;
    private final String resetPasswordToken;
    private final Instant resetPasswordExpiresAt;
    private final Instant createdAt;
    private final Instant updatedAt;

    private final List<Job> jobs;

    public static Builder builder() {
        return new Builder();
    }

    public static Builder from(User user) {
        return new Builder(user);
    }

    public static class Builder {
        private UserId id;
        private String email;
        private EmailStatus emailStatus;
        private String emailValidationCode;
        private String password;
        private String username;
        private String firstName;
        private String lastName;
        private String role;
        private String resetPasswordToken;
        private Instant resetPasswordExpiresAt;
        private Instant createdAt;
        private Instant updatedAt;

        private List<Job> jobs;

        public Builder() {
            id = UserId.generate();
            emailStatus = EmailStatus.PENDING;
            emailValidationCode = UUID.randomUUID().toString();
            role = "USER";
            createdAt = Instant.now();
            updatedAt = Instant.now();
            jobs = new ArrayList<>();
        }

        public Builder(User user) {
            this.id = user.id;
            this.email = user.email;
            this.emailStatus = user.emailStatus;
            this.emailValidationCode = user.emailValidationCode;
            this.password = user.password;
            this.username = user.username;
            this.firstName = user.firstName;
            this.lastName = user.lastName;
            this.role = user.role;
            this.resetPasswordToken = user.resetPasswordToken;
            this.resetPasswordExpiresAt = user.resetPasswordExpiresAt;
            this.createdAt = user.createdAt;
            this.updatedAt = user.updatedAt;
            this.jobs = user.jobs;
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

        private ValidationResult validate() {
            ValidationResult validationResult = new ValidationResult();

            // let's check mandatory fields
            Validator.requireNotEmpty("email", email, validationResult);
            Validator.requireNotEmpty("username", username, validationResult);
            Validator.requireNotEmpty("lastName", lastName, validationResult);
            Validator.requireNotEmpty("lastName", lastName, validationResult);
            Validator.requireNotEmpty("role", role, validationResult);
            Validator.requireValidEmail("email", email, validationResult);

            // password strength validation is very specific and belongs to the user
            if(!password.matches(".*[A-Z]+.*")
                || !password.matches(".*[a-z]+.*")
                || !password.matches(".*[0-9]+.*")
                || !password.matches(".*\\W.*")) {
                validationResult.addError("password", ErrorCode.USER_WEAK_PASSWORD);
            }

            return validationResult;

        }

        public User build() {
            ValidationResult validationResult = validate();
            if(!validationResult.isValid()) {
                throw new ValidationException(validationResult.getErrors());
            }

            return new User(id, email, emailStatus, emailValidationCode, password, username, firstName, lastName, role, resetPasswordToken, resetPasswordExpiresAt, createdAt, updatedAt, jobs);
        }
    }

    private User(UserId id, String email, EmailStatus emailStatus, String emailValidationCode, String password, String username, String firstName, String lastName, String role, String resetPasswordToken, Instant resetPasswordExpiresAt, Instant createdAt, Instant updatedAt, List<Job> jobs) {
        this.id = id;
        this.email = email;
        this.emailValidationCode = emailValidationCode;
        this.emailStatus = emailStatus;
        this.password = password;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.resetPasswordToken = resetPasswordToken;
        this.resetPasswordExpiresAt = resetPasswordExpiresAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.jobs = jobs;
    }

    public User update(String email, String username, String firstName, String lastName) {

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
                getRole(),
                getResetPasswordToken(),
                getResetPasswordExpiresAt(),
                getCreatedAt(),
                getUpdatedAt(),
                jobs
        );
    }

    public User updatePassword(String newPassword) {
        // a reset password request just overrides all previous ones
        return new User(
                getId(),
                getEmail(),
                getEmailStatus(),
                getEmailValidationCode(),
                newPassword,
                getUsername(),
                getFirstName(),
                getLastName(),
                getRole(),
                null,
                null,
                getCreatedAt(),
                Instant.now(),
                getJobs()
        );
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

    public User createNewPassword(String newPassword) {
        if(resetPasswordExpiresAt.isBefore(Instant.now())) {
            throw new ResetPasswordExpiredException();
        }
        return new User(
                getId(),
                getEmail(),
                getEmailStatus(),
                getEmailValidationCode(),
                newPassword,
                getUsername(),
                getFirstName(),
                getLastName(),
                getRole(),
                null,
                null,
                getCreatedAt(),
                Instant.now(),
                getJobs()
        );
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
                getRole(),
                null,
                null,
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
