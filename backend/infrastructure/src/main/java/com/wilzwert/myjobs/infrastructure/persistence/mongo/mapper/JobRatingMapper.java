package com.wilzwert.myjobs.infrastructure.persistence.mongo.mapper;


import com.wilzwert.myjobs.core.domain.model.job.JobRating;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.JobRatingResponse;
import org.mapstruct.Mapper;;

/**
 * @author Wilhelm Zwertvaegher
 * Date:01/04/2025
 * Time:14:00
 */
@Mapper(componentModel = "spring")
public interface JobRatingMapper {
    JobRatingResponse toResponse(JobRating jobRating);
}
