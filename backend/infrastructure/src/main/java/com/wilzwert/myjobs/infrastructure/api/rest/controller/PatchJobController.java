package com.wilzwert.myjobs.infrastructure.api.rest.controller;


import com.wilzwert.myjobs.core.domain.model.job.JobId;
import com.wilzwert.myjobs.core.domain.model.job.command.*;
import com.wilzwert.myjobs.core.domain.model.job.ports.driving.*;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.job.*;
import com.wilzwert.myjobs.infrastructure.mapper.UpdateJobMapper;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.mapper.JobMapper;
import com.wilzwert.myjobs.infrastructure.security.service.UserDetailsImpl;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * @author Wilhelm Zwertvaegher
 */
@RestController
@Slf4j
@RequestMapping("/api/jobs")
public class PatchJobController {

    private final UpdateJobDtoFactory updateJobDtoFactory;
    private final UpdateJobUseCase updateJobUseCase;
    private final UpdateJobRatingUseCase updateJobRatingUseCase;
    private final JobMapper jobMapper;
    private final UpdateJobMapper updateJobMapper;
    private final Validator validator;

    public PatchJobController(
            UpdateJobDtoFactory updateJobDtoFactory,
            UpdateJobUseCase updateJobUseCase,
            UpdateJobRatingUseCase updateJobRatingUseCase,
            JobMapper jobMapper,
            UpdateJobMapper updateJobMapper,
            Validator validator) {
        this.updateJobDtoFactory = updateJobDtoFactory;
        this.updateJobUseCase = updateJobUseCase;
        this.updateJobRatingUseCase = updateJobRatingUseCase;
        this.jobMapper = jobMapper;
        this.updateJobMapper = updateJobMapper;
        this.validator = validator;
    }

    @PatchMapping("/{id}")
    public JobResponse patch(@PathVariable("id") String id, @RequestBody Map<String, Object> fields, Authentication authentication) {
        // we need the right DTO for the right job
        // the factory will build one based on the request contents
        UpdateJobDto updateJobDto = updateJobDtoFactory.createUpdateJobDto(fields);
        Set<ConstraintViolation<UpdateJobDto>> violations = validator.validate(updateJobDto);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        // DTO is built and validated, now the domain needs a command,
        // so we use the mapper to convert our DTO to the right command
        UpdateJobCommand command = updateJobMapper.toCommand(updateJobDto, new JobId(UUID.fromString(id)), userDetails.getId());
        log.info("got this command {}", command);

        // let's call the right usecase / method
        // TODO : we're breaking open/closed principle here, maybe we should externalize the strategy selection and execution
        // for now this will do because sealed interfaces ensure all cases are covered
        // and it is very explicit, and will remain stable
        return jobMapper.toResponse(switch (command) {
            case UpdateJobFieldCommand c -> updateJobUseCase.updateJobField(c);
            case UpdateJobRatingCommand c -> updateJobRatingUseCase.updateJobRating(c);
            case UpdateJobFullCommand c -> updateJobUseCase.updateJob(c);
        });
    }
}