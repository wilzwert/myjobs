package com.wilzwert.myjobs.core.domain.model;


/**
 * @author Wilhelm Zwertvaegher
 * Date:20/03/2025
 * Time:14:34
 */

public class DomainPageable {
    private final int page;
    private final int pageSize;
    private final int offset;

    public DomainPageable(int page, int pageSize) {
        // Assert.field("page", page).min(0);
        // Assert.field("pageSize", pageSize).min(1).max(100);

        this.page = page;
        this.pageSize = pageSize;
        offset = page * pageSize;
        // assertOffset();
    }
/*
    private void assertOffset() {
        if (offset > 10000) {
            throw PaginationException.overTenThousand();
        }
    }*/

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