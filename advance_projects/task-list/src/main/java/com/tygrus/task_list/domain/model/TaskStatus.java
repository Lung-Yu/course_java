package com.tygrus.task_list.domain.model;

/**
 * 任務狀態枚舉
 */
public enum TaskStatus {
    TODO("待處理"),
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
    
    // 為了向後兼容性添加 getDisplayName 方法
    public String getDisplayName() {
        return description;
    }
    
    /**
     * 檢查狀態轉換是否合法
     * 
     * 看板系統允許較靈活的狀態轉換，支持前後移動任務
     */
    public boolean canTransitionTo(TaskStatus newStatus) {
        // 允許轉換到相同狀態（用於重新確認或記錄操作）
        if (this == newStatus) {
            return true;
        }
        
        return switch (this) {
            // PENDING/TODO 可以前進到 IN_PROGRESS 或取消
            case TODO, PENDING -> newStatus == IN_PROGRESS || newStatus == CANCELLED;
            
            // IN_PROGRESS 可以：
            // - 前進到 COMPLETED
            // - 退回到 PENDING（重新規劃）
            // - 取消
            case IN_PROGRESS -> newStatus == COMPLETED || newStatus == PENDING || newStatus == CANCELLED;
            
            // COMPLETED 可以重新打開（退回到 IN_PROGRESS 或 PENDING）
            case COMPLETED -> newStatus == IN_PROGRESS || newStatus == PENDING;
            
            // CANCELLED 終態不能轉換
            case CANCELLED -> false;
        };
    }
}