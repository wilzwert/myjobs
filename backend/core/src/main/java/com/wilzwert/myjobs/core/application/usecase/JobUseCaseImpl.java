package com.wilzwert.myjobs.core.application.usecase;


import com.wilzwert.myjobs.core.application.command.CreateActivityCommand;
import com.wilzwert.myjobs.core.application.command.CreateJobCommand;
import com.wilzwert.myjobs.core.application.command.DeleteJobCommand;
import com.wilzwert.myjobs.core.application.command.UpdateJobCommand;
import com.wilzwert.myjobs.core.domain.exception.JobAlreadyExistsException;
import com.wilzwert.myjobs.core.domain.exception.JobNotFoundException;
import com.wilzwert.myjobs.core.domain.exception.UserNotFoundException;
import com.wilzwert.myjobs.core.domain.model.*;
import com.wilzwert.myjobs.core.domain.ports.driven.JobService;
import com.wilzwert.myjobs.core.domain.ports.driven.UserService;
import com.wilzwert.myjobs.core.domain.ports.driving.*;

import java.time.Instant;
import java.util.*;

/**
 * @author Wilhelm Zwertvaegher
 * Date:14/03/2025
 * Time:16:55
 */

public class JobUseCaseImpl implements CreateJobUseCase, GetUserJobUseCase, UpdateJobUseCase, DeleteJobUseCase, GetUserJobsUseCase, AddActivityToJobUseCase {

    private final JobService jobService;

    private final UserService userService;

    public JobUseCaseImpl(JobService jobService, UserService userService) {
        this.jobService = jobService;
        this.userService = userService;
    }

    @Override
    public Job createJob(CreateJobCommand command) {
        Optional<User> user = userService.findById(command.userId());
        if(user.isEmpty()) {
            throw new UserNotFoundException();
        }

        if(jobService.findByUrlAndUserId(command.url(), user.get().getId()).isPresent()) {
            throw new JobAlreadyExistsException();
        }

        Job job = new Job(
                JobId.generate(),
                command.url(),
                JobStatus.CREATED,
                command.title(),
                command.description(),
                command.profile(),
                Instant.now(),
                Instant.now(),
                user.get().getId(),
                new ArrayList<>()
        );
        job = user.get().addJob(job);
        userService.saveUserAndJob(user.get(), job);
        return job;
    }

    @Override
    public void deleteJob(DeleteJobCommand command) {
        Optional<User> foundUser = userService.findByIdWithJobs(command.userId());
        if(foundUser.isEmpty()) {
            throw new UserNotFoundException();
        }

        User user = foundUser.get();

        System.out.println("searching for job "+command.jobId().value()+", user id is "+user.getId().value());
        Optional<Job> foundJob = jobService.findByIdAndUserId(command.jobId(), user.getId());
        if(foundJob.isEmpty()) {
            throw new JobNotFoundException();
        }

        Job job = foundJob.get();

        user.removeJob(job);

        userService.deleteJobAndSaveUser(user, job);
    }

    @Override
    public List<Job> getUserJobs(UserId userId, int page, int size) {
        Optional<User> user = userService.findById(userId);
        if(user.isEmpty()) {
            throw new UserNotFoundException();
        }
        return jobService.findAllByUserId(user.get().getId(), page, size);
    }

    @Override
    public Job updateJob(UpdateJobCommand command) {
        Optional<User> foundUser = userService.findById(command.userId());
        if(foundUser.isEmpty()) {
            throw new UserNotFoundException();
        }
        User user = foundUser.get();

        Optional<Job> foundJob = jobService.findByIdAndUserId(command.jobId(), user.getId());
        if(foundJob.isEmpty()) {
            throw new JobNotFoundException();
        }

        Job job = foundJob.get();

        // if user wants to update the job's url, we have to check if it does not exist yet
        if(!command.url().equals(job.getUrl())) {
            Optional<Job> otherJob = jobService.findByUrlAndUserId(command.url(), user.getId());
            if(otherJob.isPresent() && !otherJob.get().getId().equals(job.getId())) {
                throw new JobAlreadyExistsException();
            }
        }
        job = job.updateJob(command.url(), command.title(), command.description(), command.profile());

        userService.saveUserAndJob(user, job);
        return job;
    }

    @Override
    public Activity addActivityToJob(CreateActivityCommand command) {
        Optional<Job> foundJob = jobService.findById(command.jobId());
        if(foundJob.isEmpty()) {
            throw new JobNotFoundException();
        }
        Job job = foundJob.get();
        Activity activity = new Activity(ActivityId.generate(), command.activityType(), job.getId(), command.comment(), Instant.now(), Instant.now());

        job = job.addActivity(activity);

        // no transaction here as we let infra handle job + activities persistence
        this.jobService.save(job);
        return activity;
    }

    @Override
    public Job getUserJob(UserId userId, JobId jobId) {
        return jobService.findByIdAndUserId(jobId, userId).orElseThrow(JobNotFoundException::new);
    }
}
