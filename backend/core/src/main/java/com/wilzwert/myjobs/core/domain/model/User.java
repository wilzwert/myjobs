package com.wilzwert.myjobs.core.domain.model;


import com.wilzwert.myjobs.core.domain.exception.JobNotFoundException;
import java.time.Instant;
import java.util.List;

/**
 * @author Wilhelm Zwertvaegher
 * Date:12/03/2025
 * Time:15:32
 */
public class User {
    private final UserId id;
    private final String email;
    private final String password;
    private final String username;
    private final String firstName;
    private final String lastName;
    private final String role;
    private final Instant createdAt;
    private final Instant updatedAt;

    private final List<Job> jobs;

    public User(UserId id, String email, String password, String username, String firstName, String lastName, String role, Instant createdAt, Instant updatedAt, List<Job> jobs) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.jobs = jobs;
    }

    public Job addJob(Job job) {
        jobs.add(job);

        // automatically create first activity
        job.addActivity(new Activity(ActivityId.generate(), ActivityType.CREATION, null, null));

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

    public UserId getId() {
        return id;
    }

    public String getEmail() {
        return email;
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
