package com.tygrus.task_list.application.dto;

import com.tygrus.task_list.domain.model.Priority;
import com.tygrus.task_list.domain.model.Task;
import com.tygrus.task_list.domain.model.TaskStatus;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 任務資料傳輸物件
 * 
 * 用於在Application層和外部層之間傳遞任務資料
 */
public class TaskDTO {
    
    private final String id;
    private final String title;
    private final String description;
    private final Priority priority;
    private final TaskStatus status;
    private final LocalDateTime dueDate;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    
    private TaskDTO(Builder builder) {
        this.id = builder.id;
        this.title = builder.title;
        this.description = builder.description;
        this.priority = builder.priority;
        this.status = builder.status;
        this.dueDate = builder.dueDate;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
    }
    
    /**
     * 從Task實體轉換為DTO
     */
    public static TaskDTO fromTask(Task task) {
        return TaskDTO.builder()
            .id(task.getId().getValue())
            .title(task.getTitle())
            .description(task.getDescription())
            .priority(task.getPriority())
            .status(task.getStatus())
            .dueDate(task.getDueDate())
            .createdAt(task.getCreatedAt())
            .updatedAt(task.getUpdatedAt())
            .build();
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private String id;
        private String title;
        private String description;
        private Priority priority;
        private TaskStatus status;
        private LocalDateTime dueDate;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        
        public Builder id(String id) {
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
        
        public Builder status(TaskStatus status) {
            this.status = status;
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
        
        public Builder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }
        
        public TaskDTO build() {
            return new TaskDTO(this);
        }
    }
    
    // Getters
    public String getId() {
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
    
    public TaskStatus getStatus() {
        return status;
    }
    
    public LocalDateTime getDueDate() {
        return dueDate;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        TaskDTO taskDTO = (TaskDTO) obj;
        return Objects.equals(id, taskDTO.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return String.format("TaskDTO{id='%s', title='%s', status=%s}", id, title, status);
    }
}