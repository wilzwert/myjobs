package com.wilzwert.myjobs.core.domain.shared.pagination;


import com.wilzwert.myjobs.core.domain.shared.exception.PaginationException;
import com.wilzwert.myjobs.core.domain.shared.validation.ErrorCode;

/**
 * @author Wilhelm Zwertvaegher
 */

public class DomainPageable {
    private final int page;
    private final int pageSize;
    private final int offset;

    public DomainPageable(int page, int pageSize) {
        if(page < 0) {
            throw new PaginationException(ErrorCode.PAGINATION_INVALID_PAGE);
        }
        if(pageSize < 1 || pageSize > 100) {
            throw new PaginationException(ErrorCode.PAGINATION_INVALID_PAGE_SIZE);
        }

        this.page = page;
        this.pageSize = pageSize;
        offset = page * pageSize;
        if(offset > 1000) {
            throw new PaginationException(ErrorCode.PAGINATION_OFFSET_TOO_BIG);
        }
    }

    public int getPage() {
        return page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getOffset() {
        return offset;
    }
}