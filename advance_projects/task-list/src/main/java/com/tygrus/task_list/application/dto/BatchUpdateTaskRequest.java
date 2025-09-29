package com.tygrus.task_list.application.dto;

import com.tygrus.task_list.domain.model.TaskStatus;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * 批次更新任務請求DTO
 * 
 * 支援並行處理的批次操作請求物件
 * 展示Builder模式和函數式程式設計概念
 */
public class BatchUpdateTaskRequest {
    
    private final List<String> taskIds;
    private final TaskStatus newStatus;
    private final String updatedBy;
    private final String reason;
    private final int batchSize;
    private final int maxRetries;
    private final Consumer<BatchProgressUpdate> progressCallback;
    
    private BatchUpdateTaskRequest(Builder builder) {
        this.taskIds = List.copyOf(builder.taskIds);
        this.newStatus = builder.newStatus;
        this.updatedBy = builder.updatedBy;
        this.reason = builder.reason;
        this.batchSize = builder.batchSize;
        this.maxRetries = builder.maxRetries;
        this.progressCallback = builder.progressCallback;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private List<String> taskIds;
        private TaskStatus newStatus;
        private String updatedBy;
        private String reason;
        private int batchSize = 10; // 預設批次大小
        private int maxRetries = 3; // 預設重試次數
        private Consumer<BatchProgressUpdate> progressCallback;
        
        public Builder taskIds(List<String> taskIds) {
            this.taskIds = taskIds;
            return this;
        }
        
        public Builder newStatus(TaskStatus newStatus) {
            this.newStatus = newStatus;
            return this;
        }
        
        public Builder updatedBy(String updatedBy) {
            this.updatedBy = updatedBy;
            return this;
        }
        
        public Builder reason(String reason) {
            this.reason = reason;
            return this;
        }
        
        public Builder batchSize(int batchSize) {
            if (batchSize <= 0) {
                throw new IllegalArgumentException("Batch size must be positive");
            }
            this.batchSize = batchSize;
            return this;
        }
        
        public Builder maxRetries(int maxRetries) {
            if (maxRetries < 0) {
                throw new IllegalArgumentException("Max retries cannot be negative");
            }
            this.maxRetries = maxRetries;
            return this;
        }
        
        public Builder progressCallback(Consumer<BatchProgressUpdate> progressCallback) {
            this.progressCallback = progressCallback;
            return this;
        }
        
        public BatchUpdateTaskRequest build() {
            validateRequiredFields();
            return new BatchUpdateTaskRequest(this);
        }
        
        private void validateRequiredFields() {
            if (taskIds == null || taskIds.isEmpty()) {
                throw new IllegalArgumentException("Task IDs cannot be null or empty");
            }
            if (newStatus == null) {
                throw new IllegalArgumentException("New status cannot be null");
            }
            if (updatedBy == null || updatedBy.trim().isEmpty()) {
                throw new IllegalArgumentException("Updated by cannot be null or empty");
            }
        }
    }
    
    // Getters
    public List<String> getTaskIds() {
        return taskIds;
    }
    
    public TaskStatus getNewStatus() {
        return newStatus;
    }
    
    public String getUpdatedBy() {
        return updatedBy;
    }
    
    public String getReason() {
        return reason;
    }
    
    public int getBatchSize() {
        return batchSize;
    }
    
    public int getMaxRetries() {
        return maxRetries;
    }
    
    public Consumer<BatchProgressUpdate> getProgressCallback() {
        return progressCallback;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        BatchUpdateTaskRequest that = (BatchUpdateTaskRequest) obj;
        return batchSize == that.batchSize &&
               maxRetries == that.maxRetries &&
               Objects.equals(taskIds, that.taskIds) &&
               newStatus == that.newStatus &&
               Objects.equals(updatedBy, that.updatedBy) &&
               Objects.equals(reason, that.reason);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(taskIds, newStatus, updatedBy, reason, batchSize, maxRetries);
    }
    
    @Override
    public String toString() {
        return String.format("BatchUpdateTaskRequest{taskIds=%d items, newStatus=%s, updatedBy='%s', batchSize=%d}", 
            taskIds.size(), newStatus, updatedBy, batchSize);
    }
}