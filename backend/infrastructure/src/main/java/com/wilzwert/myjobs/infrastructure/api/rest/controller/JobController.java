package com.wilzwert.myjobs.infrastructure.api.rest.controller;


import com.wilzwert.myjobs.core.application.command.CreateJobCommand;
import com.wilzwert.myjobs.core.application.command.DeleteJobCommand;
import com.wilzwert.myjobs.core.application.command.UpdateJobCommand;
import com.wilzwert.myjobs.core.domain.model.JobId;
import com.wilzwert.myjobs.core.domain.ports.driving.CreateJobUseCase;
import com.wilzwert.myjobs.core.domain.ports.driving.DeleteJobUseCase;
import com.wilzwert.myjobs.core.domain.ports.driving.GetUserJobsUseCase;
import com.wilzwert.myjobs.core.domain.ports.driving.UpdateJobUseCase;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.UpdateJobRequest;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.mapper.JobMapper;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.CreateJobRequest;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.JobResponse;
import com.wilzwert.myjobs.infrastructure.security.service.UserDetailsImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    private final UpdateJobUseCase updateJobUseCase;
    private final DeleteJobUseCase deleteJobUseCase;
    private final GetUserJobsUseCase getUserJobsUseCase;
    private final JobMapper jobMapper;

    public JobController(CreateJobUseCase createJobUseCase, UpdateJobUseCase updateJobUseCase, DeleteJobUseCase deleteJobUseCase, GetUserJobsUseCase getUserJobsUseCase, JobMapper jobMapper) {
        this.createJobUseCase = createJobUseCase;
        this.updateJobUseCase = updateJobUseCase;
        this.deleteJobUseCase = deleteJobUseCase;
        this.getUserJobsUseCase = getUserJobsUseCase;
        this.jobMapper = jobMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public JobResponse create(@RequestBody final CreateJobRequest createJobRequest, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        CreateJobCommand createJobCommand = jobMapper.toCommand(createJobRequest, userDetails.getId());
        return jobMapper.toResponse(createJobUseCase.createJob(createJobCommand));
    }
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable("id") String id, @RequestBody final UpdateJobRequest updateJobRequest, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UpdateJobCommand updateJobCommand = jobMapper.toCommand(updateJobRequest, userDetails.getId(), new JobId(UUID.fromString(id)));
        updateJobUseCase.updateJob(updateJobCommand);
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") String id, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        DeleteJobCommand deleteJobCommand = new DeleteJobCommand(new JobId(UUID.fromString(id)), userDetails.getId());
        deleteJobUseCase.deleteJob(deleteJobCommand);
    }

    @GetMapping()
    public List<JobResponse> get(Authentication authentication, @RequestParam(required = false) Integer page) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        if(page == null) {
            page = 0;
        }
        return jobMapper.toResponse(getUserJobsUseCase.getUserJobs(userDetails.getId(), page, 10));
    }
}