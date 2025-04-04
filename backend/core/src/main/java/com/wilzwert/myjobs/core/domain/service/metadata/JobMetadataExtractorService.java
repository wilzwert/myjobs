package com.wilzwert.myjobs.core.domain.service.metadata;


import com.wilzwert.myjobs.core.domain.exception.MalformedUrlException;
import com.wilzwert.myjobs.core.domain.model.JobMetadata;
import com.wilzwert.myjobs.core.domain.ports.driven.metadata.extractor.JobMetadataExtractor;
import com.wilzwert.myjobs.core.domain.ports.driven.metadata.fetcher.HtmlFetcherService;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Wilhelm Zwertvaegher
 * Date:02/04/2025
 * Time:12:58
 */

public class JobMetadataExtractorService {
    Pattern DOMAIN_PATTERN = Pattern.compile(
            // "https?://([a-z0-9.-]+.)?([^/]+)/",
            // "^https?://([-a-zA-Z0-9.]*)?\\.?[a-z0-9]+\\.[a-z]+",
            "(?<scheme>https?://)(?<subdomain>\\S*?)(?<domainword>[^.\\s]+)(?<tld>\\.[a-z]+|\\.[a-z]{2,3}\\.[a-z]{2,3})(?=/|$)",
            Pattern.CASE_INSENSITIVE
    );

    private final HtmlFetcherService htmlFetcherService;

    private final List<JobMetadataExtractor> extractors;

    public JobMetadataExtractorService(HtmlFetcherService htmlFetcherService, List<JobMetadataExtractor> extractors) {
        this.htmlFetcherService = htmlFetcherService;
        this.extractors = extractors;
    }

    public String getDomainFromUrl(String url) throws MalformedUrlException {
        Matcher matcher = DOMAIN_PATTERN.matcher(url);
        if (matcher.find()) {
            return matcher.group(3)+matcher.group(4);
        }
        throw new MalformedUrlException();
    }

    public JobMetadata extractMetadata(String url) throws MalformedUrlException {
        String domain = getDomainFromUrl(url);

        // extract html
        String html = htmlFetcherService.fetchHtml(domain, url).orElse(null);
        if(html == null) {
            return new JobMetadata.Builder().url(url).build();
        }

        JobMetadata result =  extractors.stream()
                .filter((f) -> !f.isIncompatible(domain))
                .map(extractor -> extractor.extractJobMetadata(html))
                .filter(Optional::isPresent)
                .findFirst()
                .map(Optional::get)
                .orElse(new JobMetadata.Builder().url(url).build());

        // if url has been extracted, no need to overwrite it
        if(result.url() == null) {
            result = new JobMetadata.Builder(result).url(url).build();
        }

        return result;
    }
}
