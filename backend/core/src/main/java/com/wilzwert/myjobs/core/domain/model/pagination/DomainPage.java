package com.wilzwert.myjobs.core.domain.model.pagination;


import java.util.Collections;
import java.util.List;

/**
 * @author Wilhelm Zwertvaegher
 * Date:20/03/2025
 * Time:14:32
 */

public class DomainPage<T> {
    private static final int MINIMAL_PAGE_COUNT = 1;

    private final List<T> content;
    private final int currentPage;
    private final int pageSize;
    private final long totalElementsCount;
    private final int pageCount;

    private DomainPage(DomainPageBuilder<T> builder) {
        if(builder.pageSize == -1) {
            throw new IllegalStateException("Page size is not set");
        }
        if(builder.totalElementsCount == -1) {
            throw new IllegalStateException("Total elements count is not set");
        }
        if(builder.currentPage == -1) {
            throw new IllegalStateException("Current page is not set");
        }
        content = buildContent(builder.content);
        currentPage = builder.currentPage;
        pageSize = builder.pageSize;
        totalElementsCount = builder.totalElementsCount;
        pageCount = buildPageCount();
    }

    private List<T> buildContent(List<T> content) {
        if (content == null) {
            return List.of();
        }

        return Collections.unmodifiableList(content);
    }

    public int buildPageCount() {
        if (totalElementsCount > 0) {
            return (int) Math.ceil(totalElementsCount / (float) pageSize);
        }

        return MINIMAL_PAGE_COUNT;
    }

    public static <T> DomainPageBuilder<T> builder(DomainPage<?> page, List<T> content) {
        return new DomainPageBuilder<>(content)
                .currentPage(page.currentPage)
                .pageSize(page.pageSize)
                .totalElementsCount(page.totalElementsCount)
                ;
    }

    public static <T> DomainPageBuilder<T> builder(List<T> content) {
        return new DomainPageBuilder<>(content);
    }

    public static <T> DomainPage<T> singlePage(List<T> content) {
        return builder(content)
                .currentPage(0)
                .totalElementsCount(content.size())
                .pageSize(content.size())
                .build();
    }

    public List<T> getContent() {
        return content;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public long getTotalElementsCount() {
        return totalElementsCount;
    }

    public int getPageCount() {
        return pageCount;
    }

    public boolean isNotLast() {
        return currentPage + 1 != getPageCount();
    }

    public static class DomainPageBuilder<T> {
        private final List<T> content;
        private int currentPage = -1;
        private int pageSize = -1;
        private long totalElementsCount = -1;

        private DomainPageBuilder(List<T> content) {
            this.content = content;
        }

        public DomainPageBuilder<T> pageSize(int pageSize) {
            this.pageSize = pageSize;

            return this;
        }

        public DomainPageBuilder<T> currentPage(int currentPage) {
            this.currentPage = currentPage;

            return this;
        }

        public DomainPageBuilder<T> totalElementsCount(long totalElementsCount) {
            this.totalElementsCount = totalElementsCount;

            return this;
        }

        public DomainPage<T> build() {
           return new DomainPage<>(this);
        }
    }
}