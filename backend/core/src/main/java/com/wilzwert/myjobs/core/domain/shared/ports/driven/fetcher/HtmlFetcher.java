package com.wilzwert.myjobs.core.domain.shared.ports.driven.fetcher;


import java.util.Optional;

/**
 * @author Wilhelm Zwertvaegher
 * Date:02/04/2025
 * Time:12:36
 */
public interface HtmlFetcher {
    Optional<String> fetchHtml(String url);

    boolean isCompatible(String url);

}
