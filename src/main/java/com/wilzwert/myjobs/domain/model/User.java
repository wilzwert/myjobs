package com.wilzwert.myjobs.domain.model;


import com.wilzwert.myjobs.domain.command.RegisterUserCommand;
import com.wilzwert.myjobs.domain.exception.JobNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * @author Wilhelm Zwertvaegher
 * Date:12/03/2025
 * Time:15:32
 */

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private UUID id;
    private String email;
    private String password;
    private String username;
    private String firstName;
    private String lastName;
    private String role;
    private Instant createdAt;
    private Instant updatedAt;

    private List<Job> jobs;

    public Job addJob(Job job) {
        jobs.add(job);
        return job;
    }

    public void removeJob(Job job) {
        if(!jobs.contains(job)) {
            throw new JobNotFoundException();
        }
    }

    public static User fromCommand(RegisterUserCommand command) {
        return new User(
            UUID.randomUUID(),
            command.email(),
            command.password(),
            command.username(),
            command.firstName(),
            command.lastName(),
            "",
            null,
            null,
            Collections.emptyList()
        );
    }
}
