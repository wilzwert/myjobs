package com.wilzwert.myjobs.core.domain.ports.driven.metadata.fetcher;


import com.wilzwert.myjobs.core.domain.shared.ports.driven.fetcher.StaticHtmlFetcher;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Wilhelm Zwertvaegher
 */

class StaticHtmlFetcherTest {
    @Test
    void isCompatible_shouldReturnFalseForBlacklistedDomains() {
        StaticHtmlFetcher fetcher = url -> Optional.empty();

        assertFalse(fetcher.isCompatible("indeed.com"));
        assertFalse(fetcher.isCompatible("indeed.fr"));
        assertTrue(fetcher.isCompatible("example.com"));
    }
}
