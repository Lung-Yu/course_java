package com.tygrus.task_list.application.dto;

import com.tygrus.task_list.domain.model.TaskStatus;
import com.tygrus.task_list.domain.model.Priority;

import java.util.Collections;
import java.util.List;

/**
 * QueryTaskListUseCase的請求DTO
 * 支援複雜的過濾、排序和分頁查詢
 * 展示Collections Framework在複雜查詢場景中的應用
 */
public class TaskQueryRequest {
    private final List<TaskStatus> statusFilter;
    private final List<Priority> priorityFilter;
    private final String titleContains;
    private final String descriptionContains;
    private final TaskSortField sortField;
    private final SortDirection sortDirection;
    private final int page;
    private final int pageSize;

    // Private constructor for builder pattern
    private TaskQueryRequest(Builder builder) {
        this.statusFilter = builder.statusFilter != null ? 
            List.copyOf(builder.statusFilter) : Collections.emptyList();
        this.priorityFilter = builder.priorityFilter != null ? 
            List.copyOf(builder.priorityFilter) : Collections.emptyList();
        this.titleContains = builder.titleContains;
        this.descriptionContains = builder.descriptionContains;
        this.sortField = builder.sortField != null ? builder.sortField : TaskSortField.CREATED_AT;
        this.sortDirection = builder.sortDirection != null ? builder.sortDirection : SortDirection.DESC;
        this.page = Math.max(0, builder.page); // 確保page >= 0
        this.pageSize = Math.max(1, builder.pageSize); // 確保pageSize >= 1
    }

    // Static factory method for simple queries
    public static TaskQueryRequest allTasks() {
        return builder().build();
    }

    // Static factory method with pagination
    public static TaskQueryRequest allTasks(int page, int pageSize) {
        return builder()
            .page(page)
            .pageSize(pageSize)
            .build();
    }

    // Builder pattern for complex queries
    public static Builder builder() {
        return new Builder();
    }

    // Getters
    public List<TaskStatus> getStatusFilter() {
        return statusFilter;
    }

    public List<Priority> getPriorityFilter() {
        return priorityFilter;
    }

    public String getTitleContains() {
        return titleContains;
    }

    public String getDescriptionContains() {
        return descriptionContains;
    }

    public TaskSortField getSortField() {
        return sortField;
    }

    public SortDirection getSortDirection() {
        return sortDirection;
    }

    public int getPage() {
        return page;
    }

    public int getPageSize() {
        return pageSize;
    }

    // Utility methods
    public boolean hasStatusFilter() {
        return !statusFilter.isEmpty();
    }

    public boolean hasPriorityFilter() {
        return !priorityFilter.isEmpty();
    }

    public boolean hasTitleFilter() {
        return titleContains != null && !titleContains.trim().isEmpty();
    }

    public boolean hasDescriptionFilter() {
        return descriptionContains != null && !descriptionContains.trim().isEmpty();
    }

    public int getOffset() {
        return page * pageSize;
    }

    /**
     * Builder class for creating TaskQueryRequest instances
     */
    public static class Builder {
        private List<TaskStatus> statusFilter;
        private List<Priority> priorityFilter;
        private String titleContains;
        private String descriptionContains;
        private TaskSortField sortField;
        private SortDirection sortDirection;
        private int page = 0;
        private int pageSize = 10;

        public Builder statusFilter(List<TaskStatus> statusFilter) {
            this.statusFilter = statusFilter;
            return this;
        }

        public Builder statusFilter(TaskStatus... statuses) {
            this.statusFilter = List.of(statuses);
            return this;
        }

        public Builder priorityFilter(List<Priority> priorityFilter) {
            this.priorityFilter = priorityFilter;
            return this;
        }

        public Builder priorityFilter(Priority... priorities) {
            this.priorityFilter = List.of(priorities);
            return this;
        }

        public Builder titleContains(String titleContains) {
            this.titleContains = titleContains;
            return this;
        }

        public Builder descriptionContains(String descriptionContains) {
            this.descriptionContains = descriptionContains;
            return this;
        }

        public Builder sortBy(TaskSortField sortField, SortDirection sortDirection) {
            this.sortField = sortField;
            this.sortDirection = sortDirection;
            return this;
        }

        public Builder sortBy(TaskSortField sortField) {
            this.sortField = sortField;
            this.sortDirection = SortDirection.ASC;
            return this;
        }

        public Builder page(int page) {
            this.page = page;
            return this;
        }

        public Builder pageSize(int pageSize) {
            this.pageSize = pageSize;
            return this;
        }

        public TaskQueryRequest build() {
            return new TaskQueryRequest(this);
        }
    }

    /**
     * 任務排序欄位枚舉
     * 定義可以排序的欄位
     */
    public enum TaskSortField {
        TITLE,
        STATUS,
        PRIORITY,
        CREATED_AT,
        UPDATED_AT
    }

    /**
     * 排序方向枚舉
     */
    public enum SortDirection {
        ASC,
        DESC
    }
}