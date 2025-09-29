package com.tygrus.task_list.application.dto;

import java.time.LocalDateTime;

/**
 * 批次進度更新DTO
 * 
 * 用於回報批次操作的即時進度
 * 支援進度條和狀態監控
 */
public class BatchProgressUpdate {
    
    private final int totalTasks;
    private final int processedTasks;
    private final int successfulTasks;
    private final int failedTasks;
    private final String currentTaskId;
    private final String currentOperation;
    private final LocalDateTime timestamp;
    
    private BatchProgressUpdate(Builder builder) {
        this.totalTasks = builder.totalTasks;
        this.processedTasks = builder.processedTasks;
        this.successfulTasks = builder.successfulTasks;
        this.failedTasks = builder.failedTasks;
        this.currentTaskId = builder.currentTaskId;
        this.currentOperation = builder.currentOperation;
        this.timestamp = builder.timestamp != null ? builder.timestamp : LocalDateTime.now();
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private int totalTasks;
        private int processedTasks;
        private int successfulTasks;
        private int failedTasks;
        private String currentTaskId;
        private String currentOperation;
        private LocalDateTime timestamp;
        
        public Builder totalTasks(int totalTasks) {
            this.totalTasks = totalTasks;
            return this;
        }
        
        public Builder processedTasks(int processedTasks) {
            this.processedTasks = processedTasks;
            return this;
        }
        
        public Builder successfulTasks(int successfulTasks) {
            this.successfulTasks = successfulTasks;
            return this;
        }
        
        public Builder failedTasks(int failedTasks) {
            this.failedTasks = failedTasks;
            return this;
        }
        
        public Builder currentTaskId(String currentTaskId) {
            this.currentTaskId = currentTaskId;
            return this;
        }
        
        public Builder currentOperation(String currentOperation) {
            this.currentOperation = currentOperation;
            return this;
        }
        
        public Builder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }
        
        public BatchProgressUpdate build() {
            return new BatchProgressUpdate(this);
        }
    }
    
    // Getters
    public int getTotalTasks() {
        return totalTasks;
    }
    
    public int getProcessedTasks() {
        return processedTasks;
    }
    
    public int getSuccessfulTasks() {
        return successfulTasks;
    }
    
    public int getFailedTasks() {
        return failedTasks;
    }
    
    public String getCurrentTaskId() {
        return currentTaskId;
    }
    
    public String getCurrentOperation() {
        return currentOperation;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    /**
     * 計算完成百分比
     */
    public double getProgressPercentage() {
        return totalTasks > 0 ? (double) processedTasks / totalTasks * 100.0 : 0.0;
    }
    
    /**
     * 計算成功率
     */
    public double getSuccessRate() {
        return processedTasks > 0 ? (double) successfulTasks / processedTasks * 100.0 : 0.0;
    }
    
    /**
     * 是否已完成
     */
    public boolean isCompleted() {
        return processedTasks >= totalTasks;
    }
    
    @Override
    public String toString() {
        return String.format("Progress: %d/%d (%.1f%%) - Success: %d, Failed: %d", 
            processedTasks, totalTasks, getProgressPercentage(), successfulTasks, failedTasks);
    }
}