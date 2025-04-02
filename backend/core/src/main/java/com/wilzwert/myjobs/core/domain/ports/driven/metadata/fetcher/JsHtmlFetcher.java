package com.wilzwert.myjobs.core.domain.ports.driven.metadata.fetcher;


import java.util.List;

/**
 * @author Wilhelm Zwertvaegher
 * Date:02/04/2025
 * Time:12:37
 */

public interface JsHtmlFetcher extends HtmlFetcher {

    List<String> COMPATIBLE_DOMAINS = List.of("compatible-site.com");

    @Override
    default boolean isIncompatible(String domain) {
        return !COMPATIBLE_DOMAINS.contains(domain);
    }
}
