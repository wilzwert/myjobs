package com.wilzwert.myjobs.core.domain.shared.ports.driven.fetcher;


import java.util.List;

/**
 * @author Wilhelm Zwertvaegher
 */

public interface StaticHtmlFetcher extends HtmlFetcher  {

    // not compatible domains as regular expressions
    List<String> NOT_COMPATIBLE_DOMAINS = List.of(".*indeed.com", ".*indeed.fr");

    @Override
    default boolean isCompatible(String domain) {
        return NOT_COMPATIBLE_DOMAINS.stream().noneMatch(domain::matches);
    }
}
