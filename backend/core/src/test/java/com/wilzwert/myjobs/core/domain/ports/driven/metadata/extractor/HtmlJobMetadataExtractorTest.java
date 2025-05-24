package com.wilzwert.myjobs.core.domain.ports.driven.metadata.extractor;


import com.wilzwert.myjobs.core.domain.model.job.JobMetadata;
import com.wilzwert.myjobs.core.domain.model.job.ports.driven.extractor.impl.HtmlJobMetadataExtractor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Wilhelm Zwertvaegher
 */

class HtmlJobMetadataExtractorTest {
    private final HtmlJobMetadataExtractor extractor = new HtmlJobMetadataExtractor();

    @Test
    void testEmptyHtmlExtraction() {
        assertTrue(extractor.extractJobMetadata("").isEmpty());
    }

    @Test
    void testIncorrectHtmlExtraction() {
        assertTrue(extractor.extractJobMetadata("this is not HTML").isEmpty());
    }

    @Test
    void testHtmlWithoutTitleWithH1Extraction() {
        JobMetadata expectedMetadata = new JobMetadata.Builder()
                .title("job title")
                .description("this is a description")
                .build();
        String html = "<html><head><meta name=\"description\" content=\"this is a description\"><body><h1>job title</h1><div></body></html>";
        extractor.extractJobMetadata(html).ifPresentOrElse(
                extractedMetadata -> assertEquals(expectedMetadata, extractedMetadata),
                () -> fail("Metadata should not be empty")
        );
    }
    @Test
    void testHtmlWithTitleWithH1Extraction() {
        JobMetadata expectedMetadata = new JobMetadata.Builder()
                .title("job title in head")
                .description("this is a description")
                .build();
        String html = "<html><head><title>job title in head</title><meta name=\"description\" content=\"this is a description\"><body><h1>job title</h1><div></body></html>";
        extractor.extractJobMetadata(html).ifPresentOrElse(
                extractedMetadata -> assertEquals(expectedMetadata, extractedMetadata),
                () -> fail("Metadata should not be empty")
        );
    }
}