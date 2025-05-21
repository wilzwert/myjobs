package com.wilzwert.myjobs.core.domain.model.pagination;

import com.wilzwert.myjobs.core.domain.shared.exception.PaginationException;
import com.wilzwert.myjobs.core.domain.shared.pagination.DomainPageable;
import com.wilzwert.myjobs.core.domain.shared.validation.ErrorCode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DomainPageableTest {
    @Test
    public void whenPageNegative_thenShouldThrowException() {
        var exception = assertThrows(PaginationException.class, () -> { DomainPageable pageable = new DomainPageable(-1, 100); });
        assertEquals(ErrorCode.PAGINATION_INVALID_PAGE, exception.getErrorCode());
    }

    @Test
    public void whenPageSizeTooSmall_thenShouldThrowException() {
        var exception = assertThrows(PaginationException.class, () -> { DomainPageable pageable = new DomainPageable(1, 0); });
        assertEquals(ErrorCode.PAGINATION_INVALID_PAGE_SIZE, exception.getErrorCode());
    }

    @Test
    public void whenPageSizeTooBig_thenShouldThrowException() {
        var exception = assertThrows(PaginationException.class, () -> { DomainPageable pageable = new DomainPageable(1, 101); });
        assertEquals(ErrorCode.PAGINATION_INVALID_PAGE_SIZE, exception.getErrorCode());
    }

    @Test
    public void whenOffsetTooBig_thenShouldThrowException() {
        var exception = assertThrows(PaginationException.class, () -> { DomainPageable pageable = new DomainPageable(1000, 100); });
        assertEquals(ErrorCode.PAGINATION_OFFSET_TOO_BIG, exception.getErrorCode());
    }
}
