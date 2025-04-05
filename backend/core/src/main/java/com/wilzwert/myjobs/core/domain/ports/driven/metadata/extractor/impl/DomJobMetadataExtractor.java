package com.wilzwert.myjobs.core.domain.ports.driven.metadata.extractor.impl;

import com.wilzwert.myjobs.core.domain.model.JobMetadata;
import com.wilzwert.myjobs.core.domain.ports.driven.metadata.extractor.JobMetadataExtractor;

import java.util.Optional;

/**
 * @author Wilhelm Zwertvaegher
 * Date:02/04/2025
 * Time:13:11
 */

public class DomJobMetadataExtractor implements JobMetadataExtractor {
    @Override
    public Optional<JobMetadata> extractJobMetadata(String html) {
        return Optional.empty();
    }

    @Override
    public boolean isCompatible(String domain) {
        return false;
    }
}
