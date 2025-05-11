package com.wilzwert.myjobs.core.domain.service.job;


import com.wilzwert.myjobs.core.domain.exception.MalformedUrlException;
import com.wilzwert.myjobs.core.domain.model.job.JobMetadata;
import com.wilzwert.myjobs.core.domain.ports.driven.metadata.extractor.JobMetadataExtractorService;
import com.wilzwert.myjobs.core.domain.ports.driven.metadata.fetcher.HtmlFetcherService;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Wilhelm Zwertvaegher
 * Date:02/04/2025
 * Time:12:58
 */

public class JobMetadataService {
    Pattern DOMAIN_PATTERN = Pattern.compile(
            "(?<scheme>https?://)(?<subdomain>\\S*?)(?<domainword>[^.\\s]+)(?<tld>\\.[a-z]{2,6}|\\.[a-z]{2}\\.[a-z]{2,6})(?=/|$)",
            Pattern.CASE_INSENSITIVE
    );

    private final HtmlFetcherService htmlFetcherService;

    private final JobMetadataExtractorService jobMetadataExtractorService;

    public JobMetadataService(HtmlFetcherService htmlFetcherService, JobMetadataExtractorService jobMetadataExtractorService) {
        this.htmlFetcherService = htmlFetcherService;
        this.jobMetadataExtractorService = jobMetadataExtractorService;
    }

    public String getUrlDomain(String url) throws MalformedUrlException {
        Matcher matcher = DOMAIN_PATTERN.matcher(url);
        if (matcher.find()) {
            return matcher.group(3)+matcher.group(4);
        }
        throw new MalformedUrlException();
    }

    public JobMetadata extractMetadata(String url) throws MalformedUrlException {
        String domain = getUrlDomain(url);

        // extract html
        String html = htmlFetcherService.fetchHtml(domain, url).orElse(null);
        if(html == null) {
            return new JobMetadata.Builder().url(url).build();
        }

        // extract metadata
        JobMetadata result =  jobMetadataExtractorService.extractJobMetadata(domain, html)
                .orElse(new JobMetadata.Builder().url(url).build());

        // set url only if not already set
        if(result.url() == null) {
            result = new JobMetadata.Builder(result).url(url).build();
        }

        return result;
    }
}
