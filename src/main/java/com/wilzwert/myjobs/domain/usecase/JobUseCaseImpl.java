package com.wilzwert.myjobs.domain.usecase;


import com.wilzwert.myjobs.domain.command.CreateJobCommand;
import com.wilzwert.myjobs.domain.command.DeleteJobCommand;
import com.wilzwert.myjobs.domain.exception.JobAlreadyExistsException;
import com.wilzwert.myjobs.domain.exception.JobNotFoundException;
import com.wilzwert.myjobs.domain.exception.UserNotFoundException;
import com.wilzwert.myjobs.domain.model.Job;
import com.wilzwert.myjobs.domain.model.User;
import com.wilzwert.myjobs.domain.ports.driven.JobRepository;
import com.wilzwert.myjobs.domain.ports.driven.UserRepository;
import com.wilzwert.myjobs.domain.ports.driving.CreateJobUseCase;
import com.wilzwert.myjobs.domain.ports.driving.DeleteJobUseCase;
import com.wilzwert.myjobs.domain.ports.driving.GetUserJobsUseCase;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

        Job job = user.get().addJob(Job.fromCommand(command, user.get()));
        userRepository.save(user.get());
        return jobRepository.save(job);
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
    public List<Job> getUserJobs(UUID userId, int page, int size) {
        Optional<User> user = userRepository.findById(userId);
        if(user.isEmpty()) {
            throw new UserNotFoundException();
        }
        return jobRepository.findAllByUserId(user.get().getId(), page, size);
    }
}
