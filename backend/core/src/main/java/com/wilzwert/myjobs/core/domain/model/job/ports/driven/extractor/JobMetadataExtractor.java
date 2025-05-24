package com.wilzwert.myjobs.core.domain.model.job.ports.driven.extractor;


import com.wilzwert.myjobs.core.domain.model.job.JobMetadata;

import java.util.Optional;

/**
 * @author Wilhelm Zwertvaegher
 */
public interface JobMetadataExtractor {

    Optional<JobMetadata> extractJobMetadata(String html);

    boolean isCompatible(String domain);
}
