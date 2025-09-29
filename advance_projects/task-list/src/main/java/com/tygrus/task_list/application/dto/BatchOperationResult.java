package com.tygrus.task_list.application.dto;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 批次操作結果DTO
 * 
 * 包含詳細的批次操作統計資訊
 * 展示並行處理的結果彙總和性能指標
 */
public class BatchOperationResult {
    
    private final int totalCount;
    private final int successCount;
    private final int failureCount;
    private final int retryCount;
    private final List<TaskDTO> successfulTasks;
    private final List<BatchOperationError> errors;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final Duration executionTime;
    private final Map<String, Object> performanceMetrics;
    
    private BatchOperationResult(Builder builder) {
        this.totalCount = builder.totalCount;
        this.successCount = builder.successCount;
        this.failureCount = builder.failureCount;
        this.retryCount = builder.retryCount;
        this.successfulTasks = Collections.unmodifiableList(new ArrayList<>(builder.successfulTasks));
        this.errors = Collections.unmodifiableList(new ArrayList<>(builder.errors));
        this.startTime = builder.startTime;
        this.endTime = builder.endTime;
        this.executionTime = Duration.between(startTime, endTime);
        this.performanceMetrics = Collections.unmodifiableMap(new ConcurrentHashMap<>(builder.performanceMetrics));
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private int totalCount;
        private int successCount;
        private int failureCount;
        private int retryCount;
        private List<TaskDTO> successfulTasks = new ArrayList<>();
        private List<BatchOperationError> errors = new ArrayList<>();
        private LocalDateTime startTime = LocalDateTime.now();
        private LocalDateTime endTime = LocalDateTime.now();
        private Map<String, Object> performanceMetrics = new ConcurrentHashMap<>();
        
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
        
        public Builder retryCount(int retryCount) {
            this.retryCount = retryCount;
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
        
        public Builder errors(List<BatchOperationError> errors) {
            this.errors = new ArrayList<>(errors);
            return this;
        }
        
        public Builder addError(BatchOperationError error) {
            this.errors.add(error);
            return this;
        }
        
        public Builder addError(String taskId, String errorMessage, Throwable cause) {
            this.errors.add(new BatchOperationError(taskId, errorMessage, cause));
            return this;
        }
        
        public Builder startTime(LocalDateTime startTime) {
            this.startTime = startTime;
            return this;
        }
        
        public Builder endTime(LocalDateTime endTime) {
            this.endTime = endTime;
            return this;
        }
        
        public Builder performanceMetrics(Map<String, Object> performanceMetrics) {
            this.performanceMetrics = new ConcurrentHashMap<>(performanceMetrics);
            return this;
        }
        
        public Builder addPerformanceMetric(String key, Object value) {
            this.performanceMetrics.put(key, value);
            return this;
        }
        
        public BatchOperationResult build() {
            return new BatchOperationResult(this);
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
    
    public int getRetryCount() {
        return retryCount;
    }
    
    public List<TaskDTO> getSuccessfulTasks() {
        return successfulTasks;
    }
    
    public List<BatchOperationError> getErrors() {
        return errors;
    }
    
    public LocalDateTime getStartTime() {
        return startTime;
    }
    
    public LocalDateTime getEndTime() {
        return endTime;
    }
    
    public Duration getExecutionTime() {
        return executionTime;
    }
    
    public Map<String, Object> getPerformanceMetrics() {
        return performanceMetrics;
    }
    
    /**
     * 計算成功率
     */
    public double getSuccessRate() {
        return totalCount > 0 ? (double) successCount / totalCount * 100.0 : 0.0;
    }
    
    /**
     * 計算失敗率
     */
    public double getFailureRate() {
        return totalCount > 0 ? (double) failureCount / totalCount * 100.0 : 0.0;
    }
    
    /**
     * 計算平均每項任務處理時間（毫秒）
     */
    public double getAverageProcessingTimeMs() {
        return totalCount > 0 ? (double) executionTime.toMillis() / totalCount : 0.0;
    }
    
    /**
     * 計算吞吐量（每秒處理任務數）
     */
    public double getThroughputPerSecond() {
        long totalSeconds = executionTime.getSeconds();
        return totalSeconds > 0 ? (double) totalCount / totalSeconds : 0.0;
    }
    
    /**
     * 是否有錯誤
     */
    public boolean hasErrors() {
        return failureCount > 0;
    }
    
    /**
     * 是否完全成功
     */
    public boolean isCompletelySuccessful() {
        return failureCount == 0 && successCount == totalCount;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        BatchOperationResult that = (BatchOperationResult) obj;
        return totalCount == that.totalCount &&
               successCount == that.successCount &&
               failureCount == that.failureCount &&
               retryCount == that.retryCount &&
               Objects.equals(startTime, that.startTime) &&
               Objects.equals(endTime, that.endTime);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(totalCount, successCount, failureCount, retryCount, startTime, endTime);
    }
    
    @Override
    public String toString() {
        return String.format("BatchOperationResult{total=%d, success=%d, failure=%d, " +
                           "successRate=%.2f%%, executionTime=%dms, throughput=%.2f/sec}", 
            totalCount, successCount, failureCount, getSuccessRate(), 
            executionTime.toMillis(), getThroughputPerSecond());
    }
}