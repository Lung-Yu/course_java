package com.tygrus.task_list.application.dto;

import java.util.Collections;
import java.util.List;

/**
 * 分頁結果包裝類
 * 提供完整的分頁資訊和資料封裝
 * 展示Collections Framework與泛型的結合使用
 *
 * @param <T> 分頁資料的類型
 */
public class PagedResult<T> {
    private final List<T> content;
    private final PageInfo pageInfo;
    private final long totalElements;

    private PagedResult(List<T> content, PageInfo pageInfo, long totalElements) {
        this.content = content != null ? List.copyOf(content) : Collections.emptyList();
        this.pageInfo = pageInfo;
        this.totalElements = Math.max(0, totalElements);
    }

    /**
     * 建立分頁結果
     *
     * @param content 當前頁的資料內容
     * @param page 當前頁號 (0-based)
     * @param pageSize 每頁大小
     * @param totalElements 總資料筆數
     * @param <T> 資料類型
     * @return 分頁結果
     */
    public static <T> PagedResult<T> of(List<T> content, int page, int pageSize, long totalElements) {
        PageInfo pageInfo = new PageInfo(page, pageSize, totalElements);
        return new PagedResult<>(content, pageInfo, totalElements);
    }

    /**
     * 建立空的分頁結果
     *
     * @param page 當前頁號
     * @param pageSize 每頁大小
     * @param <T> 資料類型
     * @return 空的分頁結果
     */
    public static <T> PagedResult<T> empty(int page, int pageSize) {
        return of(Collections.emptyList(), page, pageSize, 0);
    }

    /**
     * 建立單頁結果 (無分頁)
     *
     * @param content 全部資料
     * @param <T> 資料類型
     * @return 單頁結果
     */
    public static <T> PagedResult<T> single(List<T> content) {
        long size = content != null ? content.size() : 0;
        return of(content, 0, (int) size, size);
    }

    // Getters
    public List<T> getContent() {
        return content;
    }

    public PageInfo getPageInfo() {
        return pageInfo;
    }

    public long getTotalElements() {
        return totalElements;
    }

    // Convenience methods
    public int getSize() {
        return content.size();
    }

    public boolean isEmpty() {
        return content.isEmpty();
    }

    public boolean hasContent() {
        return !content.isEmpty();
    }

    public boolean hasNext() {
        return pageInfo.hasNext();
    }

    public boolean hasPrevious() {
        return pageInfo.hasPrevious();
    }

    public int getTotalPages() {
        return pageInfo.getTotalPages();
    }

    public boolean isFirst() {
        return pageInfo.isFirst();
    }

    public boolean isLast() {
        return pageInfo.isLast();
    }

    /**
     * 分頁資訊類
     * 包含分頁的詳細資訊
     */
    public static class PageInfo {
        private final int page;           // 當前頁號 (0-based)
        private final int pageSize;      // 每頁大小
        private final int totalPages;    // 總頁數
        private final long totalElements; // 總資料筆數

        public PageInfo(int page, int pageSize, long totalElements) {
            this.page = Math.max(0, page);
            this.pageSize = Math.max(1, pageSize);
            this.totalElements = Math.max(0, totalElements);
            this.totalPages = calculateTotalPages(this.pageSize, this.totalElements);
        }

        private static int calculateTotalPages(int pageSize, long totalElements) {
            if (totalElements == 0) {
                return 0;
            }
            return (int) Math.ceil((double) totalElements / pageSize);
        }

        // Getters
        public int getPage() {
            return page;
        }

        public int getPageSize() {
            return pageSize;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public long getTotalElements() {
            return totalElements;
        }

        // Utility methods
        public boolean hasNext() {
            return page + 1 < totalPages;
        }

        public boolean hasPrevious() {
            return page > 0;
        }

        public boolean isFirst() {
            return page == 0;
        }

        public boolean isLast() {
            return page == totalPages - 1 || totalPages == 0;
        }

        public int getOffset() {
            return page * pageSize;
        }

        public int getNextPage() {
            return hasNext() ? page + 1 : page;
        }

        public int getPreviousPage() {
            return hasPrevious() ? page - 1 : page;
        }
    }
}