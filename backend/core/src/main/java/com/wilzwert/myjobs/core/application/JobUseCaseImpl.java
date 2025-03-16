package com.wilzwert.myjobs.core.application;


import com.wilzwert.myjobs.core.application.command.CreateJobCommand;
import com.wilzwert.myjobs.core.application.command.DeleteJobCommand;
import com.wilzwert.myjobs.core.domain.exception.JobAlreadyExistsException;
import com.wilzwert.myjobs.core.domain.exception.JobNotFoundException;
import com.wilzwert.myjobs.core.domain.exception.UserNotFoundException;
import com.wilzwert.myjobs.core.domain.model.*;
import com.wilzwert.myjobs.core.domain.ports.driven.JobRepository;
import com.wilzwert.myjobs.core.domain.ports.driven.UserRepository;
import com.wilzwert.myjobs.core.domain.ports.driving.CreateJobUseCase;
import com.wilzwert.myjobs.core.domain.ports.driving.DeleteJobUseCase;
import com.wilzwert.myjobs.core.domain.ports.driving.GetUserJobsUseCase;

import java.util.*;

/**
 * @author Wilhelm Zwertvaegher
 * Date:14/03/2025
 * Time:16:55
 */

public class JobUseCaseImpl implements CreateJobUseCase, DeleteJobUseCase, GetUserJobsUseCase {

    private final JobRepository jobRepository;

    private final UserRepository userRepository;

    public JobUseCaseImpl(JobRepository jobRepository, UserRepository userRepository) {
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Job createJob(CreateJobCommand command) {
        Optional<User> user = userRepository.findById(command.userId());
        if(user.isEmpty()) {
            throw new UserNotFoundException();
        }

        if(jobRepository.findByUrlAndUserId(command.url(), user.get().getId()).isPresent()) {
            throw new JobAlreadyExistsException();
        }

        Job job = new Job(
                JobId.generate(),
                command.url(),
                JobStatus.CREATED,
                command.title(),
                command.description(),
                command.profile(),
                null,
                null,
                user.get().getId(),
                new ArrayList<>()
        );

        job = user.get().addJob(job);

        Job result =  jobRepository.save(job);
        userRepository.save(user.get());
        return result;

    }

    @Override
    public void deleteJob(DeleteJobCommand command) {
        Optional<User> foundUser = userRepository.findByIdWithJobs(command.userId());
        if(foundUser.isEmpty()) {
            throw new UserNotFoundException();
        }
        User user = foundUser.get();
        Optional<Job> foundJob = jobRepository.findByIdAndUserId(command.jobId(), user.getId());
        if(foundJob.isEmpty()) {
            throw new JobNotFoundException();
        }

        user.removeJob(foundJob.get());

        jobRepository.delete(foundJob.get());
        userRepository.save(user);
    }

    @Override
    public List<Job> getUserJobs(UserId userId, int page, int size) {
        Optional<User> user = userRepository.findById(userId);
        if(user.isEmpty()) {
            throw new UserNotFoundException();
        }
        return jobRepository.findAllByUserId(user.get().getId(), page, size);
    }
}
