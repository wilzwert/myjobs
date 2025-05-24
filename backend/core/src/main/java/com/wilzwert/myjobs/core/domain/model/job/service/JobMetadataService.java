package com.wilzwert.myjobs.core.domain.model.job.service;


import com.wilzwert.myjobs.core.domain.shared.exception.MalformedUrlException;
import com.wilzwert.myjobs.core.domain.model.job.JobMetadata;
import com.wilzwert.myjobs.core.domain.model.job.ports.driven.extractor.JobMetadataExtractorService;
import com.wilzwert.myjobs.core.domain.shared.ports.driven.fetcher.HtmlFetcherService;

import java.net.URI;
import java.net.URL;

/**
 * @author Wilhelm Zwertvaegher
 */

public class JobMetadataService {

    private final HtmlFetcherService htmlFetcherService;

    private final JobMetadataExtractorService jobMetadataExtractorService;

    public JobMetadataService(HtmlFetcherService htmlFetcherService, JobMetadataExtractorService jobMetadataExtractorService) {
        this.htmlFetcherService = htmlFetcherService;
        this.jobMetadataExtractorService = jobMetadataExtractorService;
    }

    public String getUrlDomain(String url) throws MalformedUrlException {
        try {
            URL urlObject = new URI(url).toURL();
            if(!urlObject.toString().startsWith("http")) {
                throw new MalformedUrlException();
            }
            return urlObject.getHost();
        } catch (Exception e) {
            throw new MalformedUrlException();
        }
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
