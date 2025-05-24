package com.wilzwert.myjobs.core.domain.model.job.ports.driving;


import com.wilzwert.myjobs.core.domain.model.job.JobMetadata;

/**
 * @author Wilhelm Zwertvaegher
 */
public interface ExtractJobMetadataUseCase {

    JobMetadata extract(String url);
}
