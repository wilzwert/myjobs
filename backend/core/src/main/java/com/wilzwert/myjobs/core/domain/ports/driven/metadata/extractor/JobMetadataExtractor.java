package com.wilzwert.myjobs.core.domain.ports.driven.metadata.extractor;


import com.wilzwert.myjobs.core.domain.model.JobMetadata;

import java.util.Optional;

/**
 * @author Wilhelm Zwertvaegher
 * Date:02/04/2025
 * Time:13:10
 */
public interface JobMetadataExtractor {

    default Optional<JobMetadata> extract(String html) {
        ExtractedMetadata metadata = getMetadata(html);
        if(metadata != null) {
            return Optional.of(new JobMetadata.Builder()
                    .title(metadata.title())
                    .company(metadata.company())
                    .description(metadata.description())
                    .profile(metadata.profile())
                    .url(metadata.url())
                    .salary(metadata.salary())
                    .build()
            );
        }
        System.out.println("in DomJobMetadataExtractor.extract");
        return Optional.empty();
    }

    ExtractedMetadata getMetadata(String html);

    boolean isIncompatible(String domain);
}
