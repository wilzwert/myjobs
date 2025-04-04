package com.wilzwert.myjobs.core.domain.service.metadata.extractor;


import com.wilzwert.myjobs.core.domain.model.JobMetadata;

import java.util.Optional;

/**
 * @author Wilhelm Zwertvaegher
 * Date:02/04/2025
 * Time:13:10
 */
public interface JobMetadataExtractor {

    default Optional<JobMetadata> extract(String html) {
        return getMetadata(html).map(
                metadata -> new JobMetadata.Builder()
                        .title(metadata.title())
                        .company(metadata.company())
                        .description(metadata.description())
                        .profile(metadata.profile())
                        .url(metadata.url())
                        .salary(metadata.salary())
                        .build()
        );
    }

    Optional<ExtractedMetadata> getMetadata(String html);

    boolean isIncompatible(String domain);
}
