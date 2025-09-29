package com.tygrus.task_list.application.exception;

import com.tygrus.task_list.application.dto.BatchOperationError;

import java.util.List;

/**
 * 批次操作異常
 * 
 * 當批次操作部分或全部失敗時拋出
 * 包含詳細的錯誤資訊供錯誤恢復使用
 */
public class BatchOperationException extends RuntimeException {
    
    private final List<BatchOperationError> errors;
    private final int totalTasks;
    private final int failedTasks;
    
    public BatchOperationException(String message, List<BatchOperationError> errors) {
        super(message);
        this.errors = List.copyOf(errors);
        this.totalTasks = errors.size();
        this.failedTasks = errors.size();
    }
    
    public BatchOperationException(String message, List<BatchOperationError> errors, 
                                 int totalTasks, int failedTasks) {
        super(message);
        this.errors = List.copyOf(errors);
        this.totalTasks = totalTasks;
        this.failedTasks = failedTasks;
    }
    
    public BatchOperationException(String message, List<BatchOperationError> errors, Throwable cause) {
        super(message, cause);
        this.errors = List.copyOf(errors);
        this.totalTasks = errors.size();
        this.failedTasks = errors.size();
    }
    
    public List<BatchOperationError> getErrors() {
        return errors;
    }
    
    public int getTotalTasks() {
        return totalTasks;
    }
    
    public int getFailedTasks() {
        return failedTasks;
    }
    
    public int getSuccessfulTasks() {
        return totalTasks - failedTasks;
    }
    
    public double getFailureRate() {
        return totalTasks > 0 ? (double) failedTasks / totalTasks * 100.0 : 0.0;
    }
    
    /**
     * 獲取並發衝突錯誤數量
     */
    public long getConcurrencyErrorCount() {
        return errors.stream()
                    .mapToLong(error -> error.isConcurrencyError() ? 1 : 0)
                    .sum();
    }
    
    /**
     * 獲取業務規則違反錯誤數量
     */
    public long getBusinessRuleViolationCount() {
        return errors.stream()
                    .mapToLong(error -> error.isBusinessRuleViolation() ? 1 : 0)
                    .sum();
    }
    
    @Override
    public String getMessage() {
        return String.format("%s (Failed: %d/%d, Failure Rate: %.2f%%)", 
            super.getMessage(), failedTasks, totalTasks, getFailureRate());
    }
}