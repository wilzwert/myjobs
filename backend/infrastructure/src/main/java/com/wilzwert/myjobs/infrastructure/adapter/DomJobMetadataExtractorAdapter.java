package com.wilzwert.myjobs.infrastructure.adapter;

import com.wilzwert.myjobs.core.domain.ports.driven.metadata.extractor.DomJobMetadataExtractor;
import com.wilzwert.myjobs.core.domain.ports.driven.metadata.extractor.ExtractedMetadata;

/**
 * @author Wilhelm Zwertvaegher
 * Date:02/04/2025
 * Time:13:54
 */

public class DomJobMetadataExtractorAdapter implements DomJobMetadataExtractor {

    @Override
    public ExtractedMetadata getMetadata(String html) {
        return null;
    }
}
