package com.tygrus.task_list.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Task領域實體
 * 
 * 任務管理系統的核心領域物件
 * 實作UC-001: 創建任務的業務規則
 */
public class Task {
    
    private final TaskId id;
    private final String title;
    private final String description;
    private final Priority priority;
    private final LocalDateTime dueDate;
    private final LocalDateTime createdAt;
    private TaskStatus status;
    private LocalDateTime updatedAt;
    
    // Builder pattern for complex construction
    public static class Builder {
        private TaskId id;
        private String title;
        private String description;
        private Priority priority;
        private LocalDateTime dueDate;
        private LocalDateTime createdAt;
        
        public Builder id(TaskId id) {
            this.id = id;
            return this;
        }
        
        public Builder title(String title) {
            this.title = title;
            return this;
        }
        
        public Builder description(String description) {
            this.description = description;
            return this;
        }
        
        public Builder priority(Priority priority) {
            this.priority = priority;
            return this;
        }
        
        public Builder dueDate(LocalDateTime dueDate) {
            this.dueDate = dueDate;
            return this;
        }
        
        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }
        
        public Task build() {
            return new Task(this);
        }
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    private Task(Builder builder) {
        // 驗證必填欄位
        if (builder.id == null) {
            throw new IllegalArgumentException("TaskId cannot be null");
        }
        if (builder.title == null || builder.title.trim().isEmpty()) {
            throw new IllegalArgumentException("Task title cannot be null or empty");
        }
        
        this.id = builder.id;
        this.title = builder.title.trim();
        this.description = builder.description;
        this.priority = builder.priority;
        this.dueDate = builder.dueDate;
        this.createdAt = builder.createdAt != null ? builder.createdAt : LocalDateTime.now();
        this.status = TaskStatus.PENDING; // 預設狀態
        this.updatedAt = this.createdAt;
    }
    
    /**
     * 更新任務狀態
     */
    public void updateStatus(TaskStatus newStatus) {
        if (newStatus == null) {
            throw new IllegalArgumentException("TaskStatus cannot be null");
        }
        
        if (!this.status.canTransitionTo(newStatus)) {
            throw new IllegalStateException(
                String.format("Invalid status transition from %s to %s", 
                    this.status, newStatus)
            );
        }
        
        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters
    public TaskId getId() {
        return id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public Priority getPriority() {
        return priority;
    }
    
    public LocalDateTime getDueDate() {
        return dueDate;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public TaskStatus getStatus() {
        return status;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Task task = (Task) obj;
        return Objects.equals(id, task.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return String.format("Task{id=%s, title='%s', status=%s}", 
            id, title, status);
    }
}