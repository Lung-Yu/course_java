package com.tygrus.task_list.application.dto;

/**
 * 刪除任務請求DTO
 * 
 * 封裝刪除任務操作所需的參數
 * 展示DTO模式和輸入驗證的最佳實踐
 */
public class DeleteTaskRequest {
    
    private final String taskId;
    private final String deletedBy;
    private final String reason;
    
    private DeleteTaskRequest(Builder builder) {
        this.taskId = builder.taskId;
        this.deletedBy = builder.deletedBy;
        this.reason = builder.reason;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private String taskId;
        private String deletedBy;
        private String reason;
        
        public Builder taskId(String taskId) {
            this.taskId = taskId;
            return this;
        }
        
        public Builder deletedBy(String deletedBy) {
            this.deletedBy = deletedBy;
            return this;
        }
        
        public Builder reason(String reason) {
            this.reason = reason;
            return this;
        }
        
        public DeleteTaskRequest build() {
            validate();
            return new DeleteTaskRequest(this);
        }
        
        private void validate() {
            if (taskId == null || taskId.trim().isEmpty()) {
                throw new IllegalArgumentException("Task ID cannot be null or empty");
            }
            if (deletedBy == null || deletedBy.trim().isEmpty()) {
                throw new IllegalArgumentException("Deleted by cannot be null or empty");
            }
            if (reason == null || reason.trim().isEmpty()) {
                throw new IllegalArgumentException("Reason cannot be null or empty");
            }
        }
    }
    
    // Getters
    public String getTaskId() {
        return taskId;
    }
    
    public String getDeletedBy() {
        return deletedBy;
    }
    
    public String getReason() {
        return reason;
    }
    
    @Override
    public String toString() {
        return String.format("DeleteTaskRequest{taskId='%s', deletedBy='%s', reason='%s'}", 
            taskId, deletedBy, reason);
    }
}