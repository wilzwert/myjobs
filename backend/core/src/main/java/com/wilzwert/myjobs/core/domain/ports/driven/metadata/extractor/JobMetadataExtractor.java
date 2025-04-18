package com.wilzwert.myjobs.core.domain.ports.driven.metadata.extractor;


import com.wilzwert.myjobs.core.domain.model.JobMetadata;

import java.util.Optional;

/**
 * @author Wilhelm Zwertvaegher
 * Date:02/04/2025
 * Time:13:10
 */
public interface JobMetadataExtractor {

    Optional<JobMetadata> extractJobMetadata(String html);

    boolean isCompatible(String domain);
}
