package com.tygrus.task_list.application.dto;

import com.tygrus.task_list.domain.model.TaskStatus;

/**
 * 更新任務狀態請求DTO
 * 
 * 展示：
 * - 不可變對象設計
 * - Builder模式應用
 * - 輸入驗證封裝
 * - 業務規則表達
 */
public class UpdateTaskStatusRequest {
    
    private final String taskId;
    private final TaskStatus newStatus;
    private final String reason;
    private final String updatedBy; // 可選：操作者ID

    private UpdateTaskStatusRequest(Builder builder) {
        this.taskId = builder.taskId;
        this.newStatus = builder.newStatus;
        this.reason = builder.reason;
        this.updatedBy = builder.updatedBy;
    }

    // Static factory method for simple status update
    public static UpdateTaskStatusRequest of(String taskId, TaskStatus newStatus, String reason) {
        return builder()
            .taskId(taskId)
            .newStatus(newStatus)
            .reason(reason)
            .build();
    }

    // Builder pattern for complex construction
    public static Builder builder() {
        return new Builder();
    }

    // Getters
    public String getTaskId() {
        return taskId;
    }

    public TaskStatus getNewStatus() {
        return newStatus;
    }

    public String getReason() {
        return reason;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    // Validation methods
    public boolean hasUpdatedBy() {
        return updatedBy != null && !updatedBy.trim().isEmpty();
    }

    /**
     * Builder class for creating UpdateTaskStatusRequest instances
     */
    public static class Builder {
        private String taskId;
        private TaskStatus newStatus;
        private String reason;
        private String updatedBy;

        public Builder taskId(String taskId) {
            this.taskId = taskId;
            return this;
        }

        public Builder newStatus(TaskStatus newStatus) {
            this.newStatus = newStatus;
            return this;
        }

        public Builder reason(String reason) {
            this.reason = reason;
            return this;
        }

        public Builder updatedBy(String updatedBy) {
            this.updatedBy = updatedBy;
            return this;
        }

        public UpdateTaskStatusRequest build() {
            validateRequiredFields();
            return new UpdateTaskStatusRequest(this);
        }

        private void validateRequiredFields() {
            if (taskId == null || taskId.trim().isEmpty()) {
                throw new IllegalArgumentException("Task ID cannot be null or empty");
            }
            
            if (newStatus == null) {
                throw new IllegalArgumentException("New status cannot be null");
            }
            
            if (reason == null || reason.trim().isEmpty()) {
                throw new IllegalArgumentException("Reason cannot be null or empty");
            }
        }
    }
}