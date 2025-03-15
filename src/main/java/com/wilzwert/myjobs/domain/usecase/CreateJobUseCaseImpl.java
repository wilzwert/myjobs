package com.wilzwert.myjobs.domain.usecase;


import com.wilzwert.myjobs.domain.command.CreateJobCommand;
import com.wilzwert.myjobs.domain.model.Job;
import com.wilzwert.myjobs.domain.model.User;
import com.wilzwert.myjobs.domain.ports.driven.JobRepository;
import com.wilzwert.myjobs.domain.ports.driven.UserRepository;
import com.wilzwert.myjobs.domain.ports.driving.CreateJobUseCase;

import java.util.Optional;

/**
 * @author Wilhelm Zwertvaegher
 * Date:14/03/2025
 * Time:16:55
 */

public class CreateJobUseCaseImpl implements CreateJobUseCase {

    private final JobRepository repository;

    private final UserRepository userRepository;

    public CreateJobUseCaseImpl(JobRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    @Override
    public Job createJob(CreateJobCommand command) {
        Optional<User> user = userRepository.findById(command.userId());
        if(user.isEmpty()) {
            // TODO : improve exception handling
            throw new RuntimeException("User not found");
        }
        return repository.save(Job.fromCommand(command, user.get().getId()));
    }
}
