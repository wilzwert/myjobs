package com.wilzwert.myjobs.domain.model;


import com.wilzwert.myjobs.domain.exception.JobNotFoundException;
import lombok.*;
import lombok.experimental.Accessors;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * @author Wilhelm Zwertvaegher
 * Date:12/03/2025
 * Time:15:32
 */

@Getter
@EqualsAndHashCode
@Accessors(chain = true)
@AllArgsConstructor
public class User {
    private final UUID id;
    private final String email;
    private final String password;
    private final String username;
    private final String firstName;
    private final String lastName;
    private final String role;
    private final Instant createdAt;
    private final Instant updatedAt;

    private final List<Job> jobs;

    public Job addJob(Job job) {
        jobs.add(job);

        // automatically create first activity
        job.addActivity(new Activity("", ActivityType.CREATION, null, null));

        return job;
    }

    public void removeJob(Job job) {
        if(!jobs.contains(job)) {
            throw new JobNotFoundException();
        }

        jobs.remove(job);
    }

    public User withJobs(List<Job> jobs) {
        return new User(
                this.id,
                this.email,
                this.password,
                this.username,
                this.firstName,
                this.lastName,
                this.role,
                this.createdAt,
                this.updatedAt,
                jobs
        );
    }
}
