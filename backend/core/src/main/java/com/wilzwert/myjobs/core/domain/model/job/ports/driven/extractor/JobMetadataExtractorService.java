package com.wilzwert.myjobs.core.domain.model.job.ports.driven.extractor;


import com.wilzwert.myjobs.core.domain.model.job.JobMetadata;

import java.util.Optional;

/**
 * @author Wilhelm Zwertvaegher
 */
public interface JobMetadataExtractorService {

    Optional<JobMetadata> extractJobMetadata(String domain, String html);
}
