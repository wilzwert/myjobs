package com.wilzwert.myjobs.infrastructure.adapter.fetcher;

import com.wilzwert.myjobs.core.domain.shared.ports.driven.fetcher.impl.DefaultHtmlFetcherService;
import org.springframework.cache.annotation.Cacheable;

import java.util.Optional;

/**
 * @author Wilhelm Zwertvaegher
 * This adapter has no other than to allow caching of fetched html which is not the domain responsibility
 */
public class CustomHtmlFetcherService extends DefaultHtmlFetcherService {

    public CustomHtmlFetcherService() {
        super();
    }
    @Override
    @Cacheable(value = "urlMetadataExists", key = "#url")
    public Optional<String> fetchHtml(String domain, String url) {
        return super.fetchHtml(domain, url);
    }
}
