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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * @author Wilhelm Zwertvaegher
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/jobs")
public class JobController {

    private final CreateJobUseCase createJobUseCase;
    private final DeleteJobUseCase deleteJobUseCase;
    private final GetUserJobUseCase getUserJobUseCase;
    private final GetUserJobsUseCase getUserJobsUseCase;
    private final ExtractJobMetadataUseCase extractJobMetadataUseCase;
    private final JobMapper jobMapper;


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

    /*
     * Avec le PagedModel de Spring, tu peux récupérer un Page en appelant le repository et en passant le param Pageable.
     * Tu mappes ta Page dans un PagedModel<JobResponse> pour la response après.
     * Avec Swagger en plus (pour le ParameterObject) ça ressemble à ça le param :
     * @ParameterObject @PageableDefault(size = 100, sort = "productId", direction = Sort.Direction.DESC) final Pageable pageable
     *
     * Tu peux passer un type int au lieu de Integer aussi, par défaut si ya rien c'est 0, Integer par defaut c'est null.
     */
    @GetMapping
    @ResponseStatus(HttpStatus.PARTIAL_CONTENT)
    public RestPage<JobResponse> getUserJobs(
        final Authentication authentication,
        @RequestParam(required = false) final int page,
        @RequestParam(required = false, defaultValue = "10") final int itemsPerPage,
        @RequestParam(required = false) final JobStatus status,
        @RequestParam(required = false) final JobStatusMeta statusMeta,
        @RequestParam(required = false) final String sort) {

        final UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        log.info("Getting user jobs with status[{}], statusMeta[[{}]", status, statusMeta);

        return this.jobMapper.toEnrichedResponse(
            this.getUserJobsUseCase.getUserJobs(userDetails.getId(), page, itemsPerPage, status, statusMeta, sort)
        );
    }
}