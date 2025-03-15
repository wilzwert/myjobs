package com.wilzwert.myjobs.infrastructure.api.rest.controller;


import com.wilzwert.myjobs.domain.command.CreateJobCommand;
import com.wilzwert.myjobs.domain.command.DeleteJobCommand;
import com.wilzwert.myjobs.domain.ports.driving.CreateJobUseCase;
import com.wilzwert.myjobs.domain.ports.driving.DeleteJobUseCase;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.*;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.mapper.JobMapper;
import com.wilzwert.myjobs.infrastructure.security.service.UserDetailsImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * @author Wilhelm Zwertvaegher
 * Date:13/03/2025
 * Time:11:43
 */
@RestController
@Slf4j
@RequestMapping("/api/jobs")
public class JobController {

    private final CreateJobUseCase createJobUseCase;
    private final DeleteJobUseCase deleteJobUseCase;

    private final JobMapper jobMapper;

    public JobController(CreateJobUseCase createJobUseCase, DeleteJobUseCase deleteJobUseCase, JobMapper jobMapper) {
        this.createJobUseCase = createJobUseCase;
        this.deleteJobUseCase = deleteJobUseCase;
        this.jobMapper = jobMapper;
    }

    @PostMapping
    public JobResponse create(@RequestBody final CreateJobRequest createJobRequest, Authentication authentication) {
        System.out.println("create Job");
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        CreateJobCommand createJobCommand = jobMapper.toCommand(createJobRequest, userDetails.getId());
        System.out.println("User "+createJobCommand.userId());
        return jobMapper.toResponse(createJobUseCase.createJob(createJobCommand));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") String id, Authentication authentication) {
        System.out.println("delete Job");
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        DeleteJobCommand deleteJobCommand = new DeleteJobCommand(UUID.fromString(id), userDetails.getId());
        deleteJobUseCase.deleteJob(deleteJobCommand);
    }
}