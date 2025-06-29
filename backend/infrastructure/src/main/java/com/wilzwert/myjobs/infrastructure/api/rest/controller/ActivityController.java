package com.wilzwert.myjobs.infrastructure.api.rest.controller;


import com.wilzwert.myjobs.core.domain.model.activity.command.CreateActivitiesCommand;
import com.wilzwert.myjobs.core.domain.model.job.JobId;
import com.wilzwert.myjobs.core.domain.model.job.ports.driving.AddActivityToJobUseCase;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.*;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.mapper.ActivityMapper;
import com.wilzwert.myjobs.infrastructure.security.service.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * @author Wilhelm Zwertvaegher
 */
@RestController
@Slf4j
@RequestMapping("/api/jobs")
public class ActivityController {

    private final AddActivityToJobUseCase createActivityUseCase;

    private final ActivityMapper activityMapper;

    public ActivityController(AddActivityToJobUseCase createActivityUseCase, ActivityMapper activityMapper) {
        this.createActivityUseCase = createActivityUseCase;
        this.activityMapper = activityMapper;
    }

    @PostMapping("/{id}/activities")
    @ResponseStatus(HttpStatus.CREATED)
    public List<ActivityResponse> addActivity(@PathVariable("id") String id, @RequestBody @Valid List<CreateActivityRequest> createActivitiesRequest, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        CreateActivitiesCommand createActivitiesCommand = new CreateActivitiesCommand(
                createActivitiesRequest.stream().map(activityMapper::toCommand).toList(),
                userDetails.getId(),
                new JobId(UUID.fromString(id))
        );
        return activityMapper.toResponse(createActivityUseCase.addActivitiesToJob(createActivitiesCommand));
    }
}