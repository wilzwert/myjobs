package com.wilzwert.myjobs.infrastructure.adapter;


import com.wilzwert.myjobs.core.domain.ports.driven.metadata.fetcher.HtmlFetcher;
import com.wilzwert.myjobs.core.domain.ports.driven.metadata.fetcher.HtmlFetcherService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author Wilhelm Zwertvaegher
 * Date:02/04/2025
 * Time:14:53
 */
@Service
public class HtmlFetcherServiceAdapter implements HtmlFetcherService {

    private final List<HtmlFetcher> fetchers;

    public HtmlFetcherServiceAdapter(List<HtmlFetcher> fetchers) {
        this.fetchers = fetchers;
    }

    @Override
    @Cacheable(value = "urlMetadataExists", key = "#url")
    public Optional<String> fetchHtml(String domain, String url) {
        return fetchers.stream()
                .filter((f) -> !f.isIncompatible(domain))
                .map(fetcher -> fetcher.fetchHtml(url))
                .filter(Optional::isPresent)
                .findFirst()
                .map(Optional::get);
    }
}
