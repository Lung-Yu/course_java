package com.tygrus.task_list.application.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 匯入結果DTO
 * 
 * 記錄批次匯入任務的執行結果統計
 */
public class ImportResult {
    
    private final int totalCount;
    private final int successCount;
    private final int failureCount;
    private final List<String> errorMessages;
    private final List<TaskDTO> successfulTasks;
    
    private ImportResult(Builder builder) {
        this.totalCount = builder.totalCount;
        this.successCount = builder.successCount;
        this.failureCount = builder.failureCount;
        this.errorMessages = Collections.unmodifiableList(new ArrayList<>(builder.errorMessages));
        this.successfulTasks = Collections.unmodifiableList(new ArrayList<>(builder.successfulTasks));
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private int totalCount;
        private int successCount;
        private int failureCount;
        private List<String> errorMessages = new ArrayList<>();
        private List<TaskDTO> successfulTasks = new ArrayList<>();
        
        public Builder totalCount(int totalCount) {
            this.totalCount = totalCount;
            return this;
        }
        
        public Builder successCount(int successCount) {
            this.successCount = successCount;
            return this;
        }
        
        public Builder failureCount(int failureCount) {
            this.failureCount = failureCount;
            return this;
        }
        
        public Builder errorMessages(List<String> errorMessages) {
            this.errorMessages = new ArrayList<>(errorMessages);
            return this;
        }
        
        public Builder addErrorMessage(String errorMessage) {
            this.errorMessages.add(errorMessage);
            return this;
        }
        
        public Builder successfulTasks(List<TaskDTO> successfulTasks) {
            this.successfulTasks = new ArrayList<>(successfulTasks);
            return this;
        }
        
        public Builder addSuccessfulTask(TaskDTO task) {
            this.successfulTasks.add(task);
            return this;
        }
        
        public ImportResult build() {
            return new ImportResult(this);
        }
    }
    
    // Getters
    public int getTotalCount() {
        return totalCount;
    }
    
    public int getSuccessCount() {
        return successCount;
    }
    
    public int getFailureCount() {
        return failureCount;
    }
    
    public List<String> getErrorMessages() {
        return errorMessages;
    }
    
    public List<TaskDTO> getSuccessfulTasks() {
        return successfulTasks;
    }
    
    public boolean hasErrors() {
        return failureCount > 0;
    }
    
    public double getSuccessRate() {
        return totalCount > 0 ? (double) successCount / totalCount : 0.0;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ImportResult that = (ImportResult) obj;
        return totalCount == that.totalCount &&
               successCount == that.successCount &&
               failureCount == that.failureCount &&
               Objects.equals(errorMessages, that.errorMessages) &&
               Objects.equals(successfulTasks, that.successfulTasks);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(totalCount, successCount, failureCount, errorMessages, successfulTasks);
    }
    
    @Override
    public String toString() {
        return String.format("ImportResult{totalCount=%d, successCount=%d, failureCount=%d, successRate=%.2f%%}", 
            totalCount, successCount, failureCount, getSuccessRate() * 100);
    }
}