package com.wilzwert.myjobs.core.domain.model.job.ports.driven.extractor.impl;

import com.wilzwert.myjobs.core.domain.model.job.exception.NoJobMetadataExtractorException;
import com.wilzwert.myjobs.core.domain.model.job.JobMetadata;
import com.wilzwert.myjobs.core.domain.model.job.ports.driven.extractor.JobMetadataExtractor;
import com.wilzwert.myjobs.core.domain.model.job.ports.driven.extractor.JobMetadataExtractorService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Wilhelm Zwertvaegher
 */

public class DefaultJobMetadataExtractorService implements JobMetadataExtractorService {
    private List<JobMetadataExtractor> extractors;

    private boolean isDefault;

    /**
     * Default configuration : JsonLd and Dom concrete default extractors
     */
    public DefaultJobMetadataExtractorService() {
        extractors = new ArrayList<>();
        isDefault = true;
    }

    /**
     * Allows custom configuration
     * The first call to with resets the default JobMetadataExtractor list
     * @param extractor an extractor to add to the current list
     */
    public DefaultJobMetadataExtractorService with(JobMetadataExtractor extractor) {
        if(isDefault) {
            extractors = new ArrayList<>();
        }
        extractors.add(extractor);
        isDefault = false;
        return this;
    }

    @Override
    public Optional<JobMetadata> extractJobMetadata(String domain, String html) {
        if(extractors.isEmpty()) {
            throw new NoJobMetadataExtractorException();
        }
        return extractors.stream()
                .filter(f -> f.isCompatible(domain))
                .map(extractor -> extractor.extractJobMetadata(html))
                .filter(Optional::isPresent)
                .findFirst().orElse(Optional.empty());
    }
}
