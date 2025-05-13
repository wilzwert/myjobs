package com.wilzwert.myjobs.core.domain.model.user;

import java.time.Instant;

/**
 * @author Wilhelm Zwertvaegher
 * Date:12/03/2025
 * Time:15:32
 * Partial view of a User (without jobs in our case)
 * Used for reads only to avoid loading all data when not necessary
 */
public class UserView  {

    private final UserId id;
    private final String email;
    private final EmailStatus emailStatus;
    private final String emailValidationCode;
    private final String password;
    private final String username;
    private final String firstName;
    private final String lastName;
    private final Integer jobFollowUpReminderDays;
    private final Lang lang;
    private final String role;
    private final String resetPasswordToken;
    private final Instant resetPasswordExpiresAt;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final Instant jobFollowUpReminderSentAt;

    public static Builder builder() {
        return new Builder();
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
        private Integer jobFollowUpReminderDays;
        private Lang lang;
        private String role;
        private String resetPasswordToken;
        private Instant resetPasswordExpiresAt;
        private Instant createdAt;
        private Instant updatedAt;
        private Instant jobFollowUpReminderSentAt;

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

        public Builder jobFollowUpReminderSentAt(Instant jobFollowUpReminderSentAt) {
            this.jobFollowUpReminderSentAt = jobFollowUpReminderSentAt;
            return this;
        }

        public UserView build() {
            // build User
            return new UserView(this);
        }
    }

    private UserView(UserView.Builder builder) {
        this.id = builder.id;
        this.email = builder.email;
        this.emailValidationCode = builder.emailValidationCode;
        this.emailStatus = builder.emailStatus;
        this.password = builder.password;
        this.username = builder.username;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.jobFollowUpReminderDays = builder.jobFollowUpReminderDays;
        this.lang = builder.lang;
        this.role = builder.role;
        this.resetPasswordToken = builder.resetPasswordToken;
        this.resetPasswordExpiresAt = builder.resetPasswordExpiresAt;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
        this.jobFollowUpReminderSentAt = builder.jobFollowUpReminderSentAt;
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
}
