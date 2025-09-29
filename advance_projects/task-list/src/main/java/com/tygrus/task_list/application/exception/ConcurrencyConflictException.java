package com.tygrus.task_list.application.exception;

/**
 * 並發衝突異常
 * 
 * 當批次操作中發生並發衝突時拋出
 * 支援重試和衝突解決策略
 */
public class ConcurrencyConflictException extends RuntimeException {
    
    private final String taskId;
    private final String conflictType;
    private final int attemptNumber;
    
    public ConcurrencyConflictException(String taskId, String conflictType, String message) {
        this(taskId, conflictType, message, 1);
    }
    
    public ConcurrencyConflictException(String taskId, String conflictType, String message, int attemptNumber) {
        super(message);
        this.taskId = taskId;
        this.conflictType = conflictType;
        this.attemptNumber = attemptNumber;
    }
    
    public ConcurrencyConflictException(String taskId, String conflictType, String message, Throwable cause) {
        this(taskId, conflictType, message, cause, 1);
    }
    
    public ConcurrencyConflictException(String taskId, String conflictType, String message, 
                                      Throwable cause, int attemptNumber) {
        super(message, cause);
        this.taskId = taskId;
        this.conflictType = conflictType;
        this.attemptNumber = attemptNumber;
    }
    
    public String getTaskId() {
        return taskId;
    }
    
    public String getConflictType() {
        return conflictType;
    }
    
    public int getAttemptNumber() {
        return attemptNumber;
    }
    
    /**
     * 是否為樂觀鎖衝突
     */
    public boolean isOptimisticLockConflict() {
        return "OPTIMISTIC_LOCK".equals(conflictType);
    }
    
    /**
     * 是否為狀態轉換衝突
     */
    public boolean isStatusTransitionConflict() {
        return "STATUS_TRANSITION".equals(conflictType);
    }
    
    @Override
    public String getMessage() {
        return String.format("[Task: %s, Conflict: %s, Attempt: %d] %s", 
            taskId, conflictType, attemptNumber, super.getMessage());
    }
}