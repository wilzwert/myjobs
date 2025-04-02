package com.wilzwert.myjobs.core.domain.service.metadata;


import com.wilzwert.myjobs.core.domain.model.JobMetadata;
import com.wilzwert.myjobs.core.domain.ports.driven.metadata.extractor.JobMetadataExtractor;
import com.wilzwert.myjobs.core.domain.ports.driven.metadata.fetcher.HtmlFetcherService;

import java.util.List;
import java.util.Optional;

/**
 * @author Wilhelm Zwertvaegher
 * Date:02/04/2025
 * Time:12:58
 */

public class JobMetadataExtractorService {
    private final HtmlFetcherService htmlFetcherService;

    private final List<JobMetadataExtractor> extractors;

    public JobMetadataExtractorService(HtmlFetcherService htmlFetcherService, List<JobMetadataExtractor> extractors) {
        this.htmlFetcherService = htmlFetcherService;
        this.extractors = extractors;
    }

    public JobMetadata extractMetadata(String url) {
        String domain = url;
        // extract html
        String html = htmlFetcherService.fetchHtml(domain, url).orElse(null);
        if(html == null) {
            return new JobMetadata.Builder().url(url).build();
        }

        // System.out.println(html);

        JobMetadata result =  extractors.stream()
                .filter((f) -> !f.isIncompatible(domain))
                .map(extractor -> extractor.extract(html))
                .filter(Optional::isPresent)
                .findFirst()
                .map(Optional::get)
                .orElse(new JobMetadata.Builder().url(url).build());

        if(result.url().isEmpty()) {
            result = new JobMetadata.Builder(result).url(url).build();
        }
        System.out.println("got result");
        System.out.println(result);
        return result;
    }
}
