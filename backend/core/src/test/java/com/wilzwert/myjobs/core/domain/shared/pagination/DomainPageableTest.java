package com.wilzwert.myjobs.core.domain.shared.pagination;

import com.wilzwert.myjobs.core.domain.shared.exception.PaginationException;
import com.wilzwert.myjobs.core.domain.shared.validation.ErrorCode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DomainPageableTest {

    @Test
    void shouldBuildDomainPageable() {
        DomainPageable pageable = new DomainPageable(2, 10);
        assertEquals(2, pageable.getPage());
        assertEquals(10, pageable.getPageSize());
        assertEquals(20, pageable.getOffset());
    }

    @Test
    void whenPageNegative_thenShouldThrowException() {
        var exception = assertThrows(PaginationException.class, () -> { DomainPageable pageable = new DomainPageable(-1, 100); });
        assertEquals(ErrorCode.PAGINATION_INVALID_PAGE, exception.getErrorCode());
    }

    @Test
    void whenPageSizeTooSmall_thenShouldThrowException() {
        var exception = assertThrows(PaginationException.class, () -> { DomainPageable pageable = new DomainPageable(1, 0); });
        assertEquals(ErrorCode.PAGINATION_INVALID_PAGE_SIZE, exception.getErrorCode());
    }

    @Test
    void whenPageSizeTooBig_thenShouldThrowException() {
        var exception = assertThrows(PaginationException.class, () -> { DomainPageable pageable = new DomainPageable(1, 101); });
        assertEquals(ErrorCode.PAGINATION_INVALID_PAGE_SIZE, exception.getErrorCode());
    }

    @Test
    void whenOffsetTooBig_thenShouldThrowException() {
        var exception = assertThrows(PaginationException.class, () -> { DomainPageable pageable = new DomainPageable(1000, 100); });
        assertEquals(ErrorCode.PAGINATION_OFFSET_TOO_BIG, exception.getErrorCode());
    }
}
