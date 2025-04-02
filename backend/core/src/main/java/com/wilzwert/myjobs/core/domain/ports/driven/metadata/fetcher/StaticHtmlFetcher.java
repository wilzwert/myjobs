package com.wilzwert.myjobs.core.domain.ports.driven.metadata.fetcher;


import java.util.List;

/**
 * @author Wilhelm Zwertvaegher
 * Date:02/04/2025
 * Time:12:36
 */

public interface StaticHtmlFetcher extends HtmlFetcher  {

    List<String> NOT_COMPATIBLE_DOMAINS = List.of("indeed.com", "indeed.fr");

    @Override
    default boolean isIncompatible(String domain) {
        return NOT_COMPATIBLE_DOMAINS.contains(domain);
    }
}
