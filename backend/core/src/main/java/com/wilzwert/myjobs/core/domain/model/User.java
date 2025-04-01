package com.wilzwert.myjobs.core.domain.model;


import com.wilzwert.myjobs.core.domain.exception.JobAlreadyExistsException;
import com.wilzwert.myjobs.core.domain.exception.JobNotFoundException;
import com.wilzwert.myjobs.core.domain.exception.ResetPasswordExpiredException;
import com.wilzwert.myjobs.core.domain.exception.UserNotFoundException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Wilhelm Zwertvaegher
 * Date:12/03/2025
 * Time:15:32
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

    public User(UserId id, String email, EmailStatus emailStatus, String emailValidationCode, String password, String username, String firstName, String lastName, String role, String resetPasswordToken, Instant resetPasswordExpiresAt, Instant createdAt, Instant updatedAt, List<Job> jobs) {
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

    public static User create(String email, String password, String username, String firstName, String lastName) {
        return new User(
                UserId.generate(),
                email,
                EmailStatus.PENDING,
                UUID.randomUUID().toString(),
                password,
                username,
                firstName,
                lastName,
                "USER",
                "",
                null,
                Instant.now(),
                Instant.now(),
                new ArrayList<>()
        );
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
        System.out.println("Adding first activity to job");
        return job.addActivity(new Activity(ActivityId.generate(), ActivityType.CREATION, job.getId(), "", Instant.now(), Instant.now()));
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
        System.out.println(getEmail()+"-"+getEmailValidationCode());
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
