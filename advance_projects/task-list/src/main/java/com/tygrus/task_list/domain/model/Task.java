package com.tygrus.task_list.domain.model;

import com.tygrus.task_list.domain.event.TaskDeletedEvent;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
    private boolean deleted;
    private LocalDateTime deletedAt;
    private String deletedBy;
    private final List<DomainEvent> domainEvents = new ArrayList<>();
    
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
    
    /**
     * 從持久化層恢復 Task 物件的靜態工廠方法
     * 用於從資料庫讀取時，避免狀態轉換驗證
     */
    public static Task restoreFromPersistence(TaskId id, String title, String description, 
                                             TaskStatus status, LocalDateTime dueDate, 
                                             LocalDateTime createdAt) {
        // 使用 builder 建立基本物件
        Task task = Task.builder()
            .id(id)
            .title(title)
            .description(description)
            .dueDate(dueDate)
            .createdAt(createdAt)
            .build();
        
        // 直接設定狀態，跳過狀態轉換驗證
        task.status = status;
        
        return task;
    }
    
    private Task(Builder builder) {
        // 驗證必填欄位
        validateRequiredFields(builder);
        
        this.id = builder.id;
        this.title = builder.title.trim();
        this.description = builder.description != null ? builder.description.trim() : null;
        this.priority = builder.priority != null ? builder.priority : Priority.MEDIUM; // 預設優先級
        this.dueDate = builder.dueDate;
        this.createdAt = builder.createdAt != null ? builder.createdAt : LocalDateTime.now();
        this.status = TaskStatus.PENDING; // 預設狀態
        this.updatedAt = this.createdAt;
        this.deleted = false; // 預設未刪除
        this.deletedAt = null;
        this.deletedBy = null;
    }
    
    private void validateRequiredFields(Builder builder) {
        if (builder.id == null) {
            throw new IllegalArgumentException("TaskId cannot be null");
        }
        if (builder.title == null || builder.title.trim().isEmpty()) {
            throw new IllegalArgumentException("Task title cannot be null or empty");
        }
        if (builder.title.length() > 255) {
            throw new IllegalArgumentException("Task title cannot exceed 255 characters");
        }
        if (builder.description != null && builder.description.length() > 1000) {
            throw new IllegalArgumentException("Task description cannot exceed 1000 characters");
        }
    }
    
    /**
     * 更新任務狀態
     */
    public void updateStatus(TaskStatus newStatus) {
        if (newStatus == null) {
            throw new IllegalArgumentException("TaskStatus cannot be null");
        }
        
        // 檢查任務是否已被刪除
        if (this.deleted) {
            throw new IllegalStateException("Cannot update status of deleted task");
        }
        
        if (!this.status.canTransitionTo(newStatus)) {
            throw new IllegalStateException(
                String.format("Invalid status transition from %s to %s", 
                    this.status, newStatus)
            );
        }
        
        TaskStatus previousStatus = this.status;
        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();
        
        // 記錄Domain Event
        this.domainEvents.add(new TaskStatusChangedEvent(this.id, previousStatus, newStatus));
    }
    
    /**
     * 軟刪除任務
     * 
     * @param deletedBy 執行刪除的用戶ID
     * @param reason 刪除原因
     */
    public void markAsDeleted(String deletedBy, String reason) {
        if (deletedBy == null || deletedBy.trim().isEmpty()) {
            throw new IllegalArgumentException("Deleted by cannot be null or empty");
        }
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Delete reason cannot be null or empty");
        }
        
        // 檢查任務是否已被刪除
        if (this.deleted) {
            throw new IllegalStateException("Task is already deleted");
        }
        
        this.deleted = true;
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = deletedBy.trim();
        this.updatedAt = LocalDateTime.now();
        
        // 添加刪除事件
        this.domainEvents.add(new TaskDeletedEvent(this.id, deletedBy, reason));
    }
    
    /**
     * 獲取Domain Events (不可變列表)
     */
    public List<DomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }
    
    /**
     * 添加Domain Event
     */
    public void addDomainEvent(DomainEvent event) {
        if (event != null) {
            this.domainEvents.add(event);
        }
    }
    
    /**
     * 清除Domain Events (通常在事件發布後調用)
     */
    public void clearDomainEvents() {
        this.domainEvents.clear();
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
    
    public boolean isDeleted() {
        return deleted;
    }
    
    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }
    
    public String getDeletedBy() {
        return deletedBy;
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