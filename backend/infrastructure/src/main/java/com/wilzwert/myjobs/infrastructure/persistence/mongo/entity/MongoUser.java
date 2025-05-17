package com.wilzwert.myjobs.infrastructure.persistence.mongo.entity;

import com.wilzwert.myjobs.core.domain.model.user.Lang;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.UUID;

/**
 * @author Wilhelm Zwertvaegher
 * Date:12/03/2025
 * Time:16:06
 */
@Document(collection = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MongoUser  {
    @Id
    private UUID id;

    @Indexed(unique = true)
    private String email;

    @Field(name = "email_status")
    private String emailStatus;

    @Field(name = "email_validation_code")
    private String emailValidationCode;

    private String password;

    @Indexed(unique = true)
    private String username;

    @Field(name = "first_name")
    private String firstName;
    @Field(name = "last_name")
    private String lastName;

    @Field(name = "job_follow_up_reminder_days")
    private Integer jobFollowUpReminderDays;

    private Lang lang;

    private String role;

    @Field(name = "reset_password_token")
    private String resetPasswordToken;

    @Field(name = "reset_password_expires_at")
    private Instant resetPasswordExpiresAt;

    @Field(name = "created_at")
    @CreatedDate
    private Instant createdAt;

    @Field(name = "updated_at")
    @LastModifiedDate
    private Instant updatedAt;

    @Field(name = "job_follow_up_reminder_sent_at")
    private Instant jobFollowUpReminderSentAt;
}

