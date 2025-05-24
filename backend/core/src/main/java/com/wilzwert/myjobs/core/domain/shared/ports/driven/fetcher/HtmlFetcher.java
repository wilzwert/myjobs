package com.wilzwert.myjobs.core.domain.shared.ports.driven.fetcher;


import java.util.Optional;

/**
 * @author Wilhelm Zwertvaegher
 */
public interface HtmlFetcher {
    Optional<String> fetchHtml(String url);

    boolean isCompatible(String url);

}
