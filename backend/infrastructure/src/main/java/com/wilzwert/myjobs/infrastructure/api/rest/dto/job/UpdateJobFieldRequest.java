package com.wilzwert.myjobs.infrastructure.api.rest.dto.job;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author Wilhelm Zwertvaegher
 * Date:05/06/2025
 * Time:10:19
 * Generic DTO to update a specific field of a job
 * Making the class abstract forces the developer to create specific request dtos for each editable field
 */
@Data
public abstract sealed class UpdateJobFieldRequest implements UpdateJobDto permits
        UpdateJobCommentRequest,
        UpdateJobCompanyRequest,
        UpdateJobDescriptionRequest,
        UpdateJobProfileRequest,
        UpdateJobSalaryRequest,
        UpdateJobTitleRequest,
        UpdateJobUrlRequest {
    @NotBlank(message = "FIELD_CANNOT_BE_EMPTY")
    protected String field;
    protected String value;
}