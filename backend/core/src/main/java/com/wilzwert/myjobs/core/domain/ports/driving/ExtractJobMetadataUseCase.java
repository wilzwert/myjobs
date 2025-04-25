package com.wilzwert.myjobs.core.domain.ports.driving;


import com.wilzwert.myjobs.core.domain.model.job.JobMetadata;

/**
 * @author Wilhelm Zwertvaegher
 * Date:12/03/2025
 * Time:15:27
 */
public interface ExtractJobMetadataUseCase {

    JobMetadata extract(String url);
}
