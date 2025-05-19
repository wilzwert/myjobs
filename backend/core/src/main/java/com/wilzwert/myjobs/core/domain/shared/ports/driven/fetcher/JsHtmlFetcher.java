package com.wilzwert.myjobs.core.domain.shared.ports.driven.fetcher;


import java.util.List;

/**
 * @author Wilhelm Zwertvaegher
 * Date:02/04/2025
 * Time:12:37
 */

public interface JsHtmlFetcher extends HtmlFetcher {

    // compatible domains as regular expressions
    List<String> COMPATIBLE_DOMAINS = List.of(".*compatible-site.com");

    @Override
    default boolean isCompatible(String domain) {
        return COMPATIBLE_DOMAINS.stream().anyMatch(domain::matches);
    }
}
