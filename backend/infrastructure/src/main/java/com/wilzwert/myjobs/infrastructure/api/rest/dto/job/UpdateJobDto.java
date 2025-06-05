package com.wilzwert.myjobs.infrastructure.api.rest.dto.job;


/**
 * @author Wilhelm Zwertvaegher
 * Date:05/06/2025
 * Time:10:19
 */
public sealed  interface UpdateJobDto permits UpdateJobFieldRequest, UpdateJobRatingRequest, UpdateJobRequest, UpdateJobStatusRequest {
}
