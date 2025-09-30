package com.tygrus.task_list.application.dto;

import com.tygrus.task_list.domain.model.Priority;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 創建任務請求DTO
 * 
 * 封裝創建任務所需的資料
 */
public class CreateTaskRequest {
    
    private String title;
    private String description;
    private Priority priority;
    private LocalDateTime dueDate;
    
    // 默認構造函數 (Thymeleaf需要)
    public CreateTaskRequest() {
    }
    
    private CreateTaskRequest(Builder builder) {
        this.title = builder.title;
        this.description = builder.description;
        this.priority = builder.priority;
        this.dueDate = builder.dueDate;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private String title;
        private String description;
        private Priority priority;
        private LocalDateTime dueDate;
        
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
        
        public CreateTaskRequest build() {
            return new CreateTaskRequest(this);
        }
    }
    
    // Getters
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
    
    // Setters (Thymeleaf需要)
    public void setTitle(String title) {
        this.title = title;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public void setPriority(Priority priority) {
        this.priority = priority;
    }
    
    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        CreateTaskRequest that = (CreateTaskRequest) obj;
        return Objects.equals(title, that.title) &&
               Objects.equals(description, that.description) &&
               Objects.equals(priority, that.priority) &&
               Objects.equals(dueDate, that.dueDate);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(title, description, priority, dueDate);
    }
    
    @Override
    public String toString() {
        return String.format("CreateTaskRequest{title='%s', priority=%s}", title, priority);
    }
}