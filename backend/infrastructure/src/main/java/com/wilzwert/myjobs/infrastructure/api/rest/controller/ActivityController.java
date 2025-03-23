package com.wilzwert.myjobs.infrastructure.api.rest.controller;


import com.wilzwert.myjobs.core.domain.command.CreateActivityCommand;
import com.wilzwert.myjobs.core.domain.model.JobId;
import com.wilzwert.myjobs.core.domain.ports.driving.*;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.*;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.mapper.ActivityMapper;
import com.wilzwert.myjobs.infrastructure.security.service.UserDetailsImpl;
import lombok.extern.slf4j.Slf4j;
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
public class ActivityController {

    private final AddActivityToJobUseCase createActivityUseCase;

    private final ActivityMapper activityMapper;

    public ActivityController(AddActivityToJobUseCase createActivityUseCase, ActivityMapper activityMapper) {
        this.createActivityUseCase = createActivityUseCase;
        this.activityMapper = activityMapper;
    }

    @PostMapping("/{id}/activities")
    public ActivityResponse addActivity(@PathVariable("id") String id, @RequestBody CreateActivityRequest createActivityRequest, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        System.out.println(createActivityRequest);
        System.out.println(activityMapper.toCommand(createActivityRequest));
        CreateActivityCommand createActivityCommand = activityMapper.toCommand(createActivityRequest, userDetails.getId(), new JobId(UUID.fromString(id)));
        return activityMapper.toResponse(createActivityUseCase.addActivityToJob(createActivityCommand));
    }

}