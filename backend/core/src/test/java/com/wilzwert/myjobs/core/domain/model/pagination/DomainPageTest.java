package com.wilzwert.myjobs.core.domain.model.pagination;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Wilhelm Zwertvaegher
 * Date:26/04/2025
 * Time:14:48
 */
class DomainPageTest {

    @Test
    void shouldBuildSinglePageProperly() {
        List<String> content = List.of("A", "B", "C");

        DomainPage<String> page = DomainPage.singlePage(content);

        assertEquals(content, page.getContent());
        assertEquals(0, page.getCurrentPage());
        assertEquals(3, page.getPageSize());
        assertEquals(3, page.getTotalElementsCount());
        assertEquals(1, page.getPageCount());
        assertFalse(page.isNotLast());
    }

    @Test
    void shouldBuildPageWithCustomParameters() {
        List<String> content = List.of("A", "B", "C", "D");

        DomainPage<String> page = DomainPage.builder(content)
                .currentPage(1)
                .pageSize(2)
                .totalElementsCount(4)
                .build();

        assertEquals(content, page.getContent());
        assertEquals(1, page.getCurrentPage());
        assertEquals(2, page.getPageSize());
        assertEquals(4, page.getTotalElementsCount());
        assertEquals(2, page.getPageCount());
        assertFalse(page.isNotLast()); // page 1 sur 2, indexé à partir de 0
    }

    @Test
    void shouldHandleNullContent() {
        DomainPage<String> page = DomainPage.<String>builder(null)
                .currentPage(0)
                .pageSize(5)
                .totalElementsCount(0)
                .build();

        assertNotNull(page.getContent());
        assertTrue(page.getContent().isEmpty());
        assertEquals(5, page.getPageSize());
        assertEquals(0, page.getTotalElementsCount());
        assertEquals(1, page.getPageCount());
        assertFalse(page.isNotLast());
    }

    @Test
    void shouldComputePagesCount() {
        DomainPage<String> page = DomainPage.<String>builder(null)
                .currentPage(0)
                .pageSize(5)
                .totalElementsCount(52)
                .build();

        assertNotNull(page.getContent());
        assertEquals(11, page.getPageCount());
        assertTrue(page.isNotLast());
    }

    @Test
    void whenPageSizeNotSet_thenShouldThrowException() {
        List<String> content = List.of("A", "B");

        assertThrows(IllegalStateException.class, () -> DomainPage.builder(content)
                .currentPage(0)
                .totalElementsCount(10)
                .build()
        );
    }

    @Test
    void whenTotalElementsCountNotSet_thenShouldThrowException() {
        List<String> content = List.of("A", "B");

        assertThrows(IllegalStateException.class, () -> DomainPage.builder(content)
                .currentPage(0)
                .pageSize(10)
                .build()
        );
    }
    @Test
    void whenCurrentPageNotSet_thenShouldThrowException() {
        List<String> content = List.of("A", "B");

        assertThrows(IllegalStateException.class, () -> DomainPage.builder(content)
                .totalElementsCount(20)
                .pageSize(10)
                .build()
        );
    }


    @Test
    void shouldRespectUnmodifiableContent() {
        List<String> content = new ArrayList<>(List.of("A", "B"));

        DomainPage<String> page = DomainPage.singlePage(content);

        assertThrows(UnsupportedOperationException.class, () -> page.getContent().add("C"));
    }

    @ParameterizedTest
    @CsvSource({
            "0, 2, 5, true",    // page 0 of 3 (5/2 = 2.5 → 3 pages)
            "1, 2, 5, true",    // page 1 of 3
            "2, 2, 5, false",   // page 2 of 3 → last
            "0, 3, 6, true",    // page 0 of  2
            "1, 3, 6, false",   // page 1 of 2 → last
            "0, 10, 5, false"   // page 0, but only one page
    })
    public void shouldCorrectlyDetectIfNotLast(int currentPage, int pageSize, int totalElementsCount, boolean expectedIsNotLast) {
        List<String> content = List.of("A", "B");

        DomainPage<String> page = DomainPage.builder(content)
                .currentPage(currentPage)
                .pageSize(pageSize)
                .totalElementsCount(totalElementsCount)
                .build();

        assertEquals(expectedIsNotLast, page.isNotLast());
    }

    @Test
    void shouldDetectLastPageCorrectly() {
        List<String> content = List.of("A");

        DomainPage<String> page = DomainPage.builder(content)
                .currentPage(1)
                .pageSize(1)
                .totalElementsCount(2)
                .build();

        assertFalse(page.isNotLast());
    }
}

