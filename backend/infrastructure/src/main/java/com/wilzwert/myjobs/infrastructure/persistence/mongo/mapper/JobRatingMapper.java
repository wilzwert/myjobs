package com.wilzwert.myjobs.infrastructure.persistence.mongo.mapper;


import com.wilzwert.myjobs.core.domain.model.job.JobRating;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.job.JobRatingResponse;
import org.mapstruct.Mapper;

/**
 * @author Wilhelm Zwertvaegher
 */
@Mapper(componentModel = "spring")
public interface JobRatingMapper {
    JobRatingResponse toResponse(JobRating jobRating);
}
