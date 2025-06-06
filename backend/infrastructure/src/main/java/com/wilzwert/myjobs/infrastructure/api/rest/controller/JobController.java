package com.wilzwert.myjobs.infrastructure.api.rest.controller;


import com.wilzwert.myjobs.core.domain.model.job.JobId;
import com.wilzwert.myjobs.core.domain.model.job.JobMetadata;
import com.wilzwert.myjobs.core.domain.model.job.JobStatus;
import com.wilzwert.myjobs.core.domain.model.job.JobStatusMeta;
import com.wilzwert.myjobs.core.domain.model.job.command.*;
import com.wilzwert.myjobs.core.domain.model.job.ports.driving.*;
import com.wilzwert.myjobs.core.domain.model.user.ports.driving.GetUserJobUseCase;
import com.wilzwert.myjobs.core.domain.model.user.ports.driving.GetUserJobsUseCase;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.*;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.job.*;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.mapper.JobMapper;
import com.wilzwert.myjobs.infrastructure.security.service.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * @author Wilhelm Zwertvaegher
 */
@RestController
@Slf4j
@RequestMapping("/api/jobs")
public class JobController {

    private final CreateJobUseCase createJobUseCase;
    private final DeleteJobUseCase deleteJobUseCase;
    private final GetUserJobUseCase getUserJobUseCase;
    private final GetUserJobsUseCase getUserJobsUseCase;
    private final ExtractJobMetadataUseCase extractJobMetadataUseCase;
    private final JobMapper jobMapper;

    public JobController(
            CreateJobUseCase createJobUseCase,
            GetUserJobUseCase getUserJobUseCase,
            DeleteJobUseCase deleteJobUseCase,
            GetUserJobsUseCase getUserJobsUseCase,
            ExtractJobMetadataUseCase extractJobMetadataUseCase,
            JobMapper jobMapper) {
        this.createJobUseCase = createJobUseCase;
        this.getUserJobUseCase = getUserJobUseCase;
        this.deleteJobUseCase = deleteJobUseCase;
        this.getUserJobsUseCase = getUserJobsUseCase;
        this.extractJobMetadataUseCase = extractJobMetadataUseCase;
        this.jobMapper = jobMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public JobResponse create(@RequestBody @Valid final CreateJobRequest createJobRequest, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        CreateJobCommand createJobCommand = jobMapper.toCommand(createJobRequest, userDetails.getId());
        return jobMapper.toResponse(createJobUseCase.createJob(createJobCommand));
    }

    @GetMapping("/metadata")
    public ResponseEntity<JobMetadata> extract(@RequestParam() String url) {
        return ResponseEntity.ok(extractJobMetadataUseCase.extract(url));
    }

    @GetMapping("/{id}")
    public JobResponse get(@PathVariable("id") String id, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return jobMapper.toResponse(getUserJobUseCase.getUserJob(userDetails.getId(), new JobId(UUID.fromString(id))));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") String id, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        DeleteJobCommand deleteJobCommand = new DeleteJobCommand(new JobId(UUID.fromString(id)), userDetails.getId());
        deleteJobUseCase.deleteJob(deleteJobCommand);
    }

    @GetMapping()
    public RestPage<JobResponse> getUserJobs(
            Authentication authentication,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer itemsPerPage,
            @RequestParam(required = false) JobStatus status,
            @RequestParam(required = false) JobStatusMeta statusMeta,
            @RequestParam(required = false) String sort) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        if(page == null) {
            page = 0;
        }
        if(itemsPerPage == null) {
            itemsPerPage = 10;
        }

        log.info("Getting user jobs with status[{}], statusMeta[[{}]", status, statusMeta);

        return jobMapper.toEnrichedResponse(getUserJobsUseCase.getUserJobs(userDetails.getId(), page, itemsPerPage, status, statusMeta, sort));
    }
}