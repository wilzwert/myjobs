package com.wilzwert.myjobs.core.domain.ports.driven.metadata.extractor;

/**
 * @author Wilhelm Zwertvaegher
 * Date:02/04/2025
 * Time:13:11
 */

public interface DomJobMetadataExtractor extends JobMetadataExtractor {
    @Override
    default boolean isIncompatible(String domain) {
        return false;
    }
}
