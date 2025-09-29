package com.tygrus.task_list.domain.model;

/**
 * 任務狀態枚舉
 */
public enum TaskStatus {
    PENDING("待處理"),
    IN_PROGRESS("進行中"), 
    COMPLETED("已完成"),
    CANCELLED("已取消");
    
    private final String description;
    
    TaskStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * 檢查狀態轉換是否合法
     */
    public boolean canTransitionTo(TaskStatus newStatus) {
        return switch (this) {
            case PENDING -> newStatus == IN_PROGRESS || newStatus == CANCELLED;
            case IN_PROGRESS -> newStatus == COMPLETED || newStatus == CANCELLED;
            case COMPLETED, CANCELLED -> false; // 終態不能轉換
        };
    }
}