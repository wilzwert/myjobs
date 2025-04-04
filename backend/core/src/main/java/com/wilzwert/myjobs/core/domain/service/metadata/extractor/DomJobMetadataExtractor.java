package com.wilzwert.myjobs.core.domain.service.metadata.extractor;

import java.util.Optional;

/**
 * @author Wilhelm Zwertvaegher
 * Date:02/04/2025
 * Time:13:11
 */

public class DomJobMetadataExtractor implements JobMetadataExtractor {
    @Override
    public Optional<ExtractedMetadata> getMetadata(String html) {
        return Optional.empty();
    }

    @Override
    public boolean isIncompatible(String domain) {
        return false;
    }
}
