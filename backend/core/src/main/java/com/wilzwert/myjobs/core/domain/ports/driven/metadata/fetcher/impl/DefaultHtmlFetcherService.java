package com.wilzwert.myjobs.core.domain.ports.driven.metadata.fetcher.impl;


import com.wilzwert.myjobs.core.domain.exception.NoHtmlFetcherException;
import com.wilzwert.myjobs.core.domain.ports.driven.metadata.fetcher.HtmlFetcher;
import com.wilzwert.myjobs.core.domain.ports.driven.metadata.fetcher.HtmlFetcherService;

import java.util.*;

/**
 * @author Wilhelm Zwertvaegher
 * Date:02/04/2025
 * Time:14:53
 */
public class DefaultHtmlFetcherService implements HtmlFetcherService {

    protected List<HtmlFetcher> fetchers;

    private boolean isDefault;

    /**
     * The default fetchers list is empty because HtmlFetchers MUST be implemented in infra
     */
    public DefaultHtmlFetcherService() {
        fetchers = new ArrayList<>();
        isDefault = true;
    }

    /**
     * Allows custom configuration
     * The first call to with resets the default HtmlFetcher list
     * @param fetcher a fetcher to add to the current list
     */
    public DefaultHtmlFetcherService with(HtmlFetcher fetcher) {
        if(isDefault) {
            fetchers = new ArrayList<>();
        }
        fetchers.add(fetcher);
        isDefault = false;
        return this;
    }

    @Override
    public Optional<String> fetchHtml(String domain, String url) {
        if(fetchers.isEmpty()) {
            throw new NoHtmlFetcherException();
        }
        return fetchers.stream()
                .filter((f) -> f.isCompatible(domain))
                .map(fetcher -> fetcher.fetchHtml(url))
                .filter(Optional::isPresent)
                .findFirst()
                .orElse(Optional.empty());
    }
}
