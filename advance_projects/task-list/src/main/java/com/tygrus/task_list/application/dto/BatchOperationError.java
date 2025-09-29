package com.tygrus.task_list.application.dto;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 批次操作錯誤DTO
 * 
 * 記錄批次操作中單個任務的錯誤資訊
 * 支援錯誤分類和根本原因分析
 */
public class BatchOperationError {
    
    private final String taskId;
    private final String errorMessage;
    private final String errorType;
    private final Throwable cause;
    private final LocalDateTime timestamp;
    private final int retryAttempts;
    
    public BatchOperationError(String taskId, String errorMessage, Throwable cause) {
        this(taskId, errorMessage, cause, 0);
    }
    
    public BatchOperationError(String taskId, String errorMessage, Throwable cause, int retryAttempts) {
        this.taskId = taskId;
        this.errorMessage = errorMessage;
        this.errorType = cause != null ? cause.getClass().getSimpleName() : "Unknown";
        this.cause = cause;
        this.timestamp = LocalDateTime.now();
        
        // 如果是 ConcurrencyConflictException，使用其 attemptNumber
        if (cause instanceof com.tygrus.task_list.application.exception.ConcurrencyConflictException) {
            com.tygrus.task_list.application.exception.ConcurrencyConflictException cce = 
                (com.tygrus.task_list.application.exception.ConcurrencyConflictException) cause;
            this.retryAttempts = cce.getAttemptNumber() - 1; // attemptNumber 是從1開始，retryAttempts 是重試次數
        } else {
            this.retryAttempts = retryAttempts;
        }
    }
    
    // Getters
    public String getTaskId() {
        return taskId;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public String getErrorType() {
        return errorType;
    }
    
    public Throwable getCause() {
        return cause;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public int getRetryAttempts() {
        return retryAttempts;
    }
    
    /**
     * 是否為重試後仍失敗
     */
    public boolean isFailedAfterRetry() {
        return retryAttempts > 0;
    }
    
    /**
     * 是否為並發衝突錯誤
     */
    public boolean isConcurrencyError() {
        return "OptimisticLockException".equals(errorType) || 
               "ConcurrencyConflictException".equals(errorType);
    }
    
    /**
     * 是否為業務規則違反錯誤
     */
    public boolean isBusinessRuleViolation() {
        return "IllegalStatusTransitionException".equals(errorType) || 
               "DomainException".equals(errorType);
    }
    
    /**
     * 獲取錯誤的根本原因訊息
     */
    public String getRootCauseMessage() {
        Throwable rootCause = cause;
        while (rootCause != null && rootCause.getCause() != null) {
            rootCause = rootCause.getCause();
        }
        return rootCause != null ? rootCause.getMessage() : errorMessage;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        BatchOperationError that = (BatchOperationError) obj;
        return retryAttempts == that.retryAttempts &&
               Objects.equals(taskId, that.taskId) &&
               Objects.equals(errorMessage, that.errorMessage) &&
               Objects.equals(errorType, that.errorType) &&
               Objects.equals(timestamp, that.timestamp);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(taskId, errorMessage, errorType, timestamp, retryAttempts);
    }
    
    @Override
    public String toString() {
        return String.format("BatchOperationError{taskId='%s', errorType='%s', message='%s', retries=%d}", 
            taskId, errorType, errorMessage, retryAttempts);
    }
}