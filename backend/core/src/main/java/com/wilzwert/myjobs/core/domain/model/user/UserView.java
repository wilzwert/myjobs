package com.wilzwert.myjobs.core.domain.model.user;

import java.time.Instant;

/**
 * @author Wilhelm Zwertvaegher
 * Partial view of a User with minimal info for presentation
 * Use for reads only
 */
public class UserView  {

    private final String firstName;

    private final String lastName;

    private final String email;

    private final String username;

    private final Instant createdAt;

    private final EmailStatus emailStatus;

    private final Integer jobFollowUpReminderDays;

    private final Instant jobFollowUpReminderSentAt;

    private final Lang lang;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String firstName;

        private String lastName;

        private String email;

        private String username;

        private Instant createdAt;

        private EmailStatus emailStatus;

        private Integer jobFollowUpReminderDays;

        private Instant jobFollowUpReminderSentAt;

        private Lang lang;

        public Builder() {
            // create empty builder, no default values as they must be provided by the infra
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder emailStatus(EmailStatus emailStatus) {
            this.emailStatus = emailStatus;
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

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder lang(Lang lang) {
            this.lang = lang;
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
        this.email = builder.email;
        this.emailStatus = builder.emailStatus;
        this.username = builder.username;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.jobFollowUpReminderDays = builder.jobFollowUpReminderDays;
        this.lang = builder.lang;
        this.createdAt = builder.createdAt;
        this.jobFollowUpReminderSentAt = builder.jobFollowUpReminderSentAt;

        // FIXME : building the UserView should trigger validation
    }

    public String getEmail() {
        return email;
    }

    public EmailStatus getEmailStatus() {
        return emailStatus;
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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getJobFollowUpReminderSentAt() {return jobFollowUpReminderSentAt;}
}
