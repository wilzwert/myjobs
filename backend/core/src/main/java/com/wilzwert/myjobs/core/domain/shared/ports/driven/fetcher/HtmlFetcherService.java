package com.wilzwert.myjobs.core.domain.shared.ports.driven.fetcher;

import java.util.Optional;

/**
 * @author Wilhelm Zwertvaegher
 * Infra MUST implement this interface, using whatever means necessary
 * Having a single entrypoint to actually get the Html allows additional functionnality
 * in infra, sucha as caching, AOP...
 * It is strongly recommended to implement and use HtmlFetcher interfaces to do so,
 * as fetchers contain domain related rules on domains compatibility for example
 * TODO : maybe it would be better to actually enforce the implementation and use of HtmlFetcher interfaces
 *         AS WELL as this service, but for now I have no idea how to do it cleanly
 *        On the other hand, one could also argue that the way it is currently designed allows
 *        for more flexibility on the infra side,
 *        e.g. : domains compatibility rules override (i.e. skip the HtmlFetcher isIncompatible check)
 */

public interface HtmlFetcherService {
    Optional<String> fetchHtml(String domain, String url);
}