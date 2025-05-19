package com.wilzwert.myjobs.core.domain.ports.driven.metadata.fetcher;


import com.wilzwert.myjobs.core.domain.shared.ports.driven.fetcher.JsHtmlFetcher;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Wilhelm Zwertvaegher
 * Date:08/04/2025
 * Time:17:09
 */

public class JsHtmlFetcherTest {
    @Test
    void isCompatible_shouldReturnFalseForBlacklistedDomains() {
        JsHtmlFetcher fetcher = url -> Optional.empty();

        assertFalse(fetcher.isCompatible("indeed.com"));
        assertTrue(fetcher.isCompatible("compatible-site.com"));
    }
}
