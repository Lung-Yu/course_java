package com.tygrus.task_list.domain.model;

/**
 * 通知狀態枚舉
 * 追蹤通知的生命週期狀態
 */
public enum NotificationStatus {
    
    /**
     * 等待發送
     */
    PENDING("pending", "等待發送"),
    
    /**
     * 正在發送
     */
    SENDING("sending", "正在發送"),
    
    /**
     * 發送成功
     */
    SENT("sent", "發送成功"),
    
    /**
     * 發送失敗
     */
    FAILED("failed", "發送失敗"),
    
    /**
     * 重試中
     */
    RETRYING("retrying", "重試中"),
    
    /**
     * 最終失敗 (超過最大重試次數)
     */
    FAILED_FINAL("failed_final", "最終失敗"),
    
    /**
     * 已取消
     */
    CANCELLED("cancelled", "已取消");
    
    private final String code;
    private final String displayName;
    
    NotificationStatus(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * 根據代碼獲取通知狀態
     * 
     * @param code 狀態代碼
     * @return 對應的通知狀態，如果不存在則拋出異常
     */
    public static NotificationStatus fromCode(String code) {
        for (NotificationStatus status : values()) {
            if (status.code.equalsIgnoreCase(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown notification status code: " + code);
    }
    
    /**
     * 檢查是否為最終狀態 (不會再變更)
     * 
     * @return 如果是最終狀態返回true
     */
    public boolean isFinalStatus() {
        return this == SENT || this == FAILED_FINAL || this == CANCELLED;
    }
    
    /**
     * 檢查是否可以重試
     * 
     * @return 如果可以重試返回true
     */
    public boolean canRetry() {
        return this == FAILED || this == RETRYING;
    }
    
    /**
     * 檢查是否為成功狀態
     * 
     * @return 如果是成功狀態返回true
     */
    public boolean isSuccess() {
        return this == SENT;
    }
    
    /**
     * 檢查是否為失敗狀態
     * 
     * @return 如果是失敗狀態返回true
     */
    public boolean isFailure() {
        return this == FAILED || this == FAILED_FINAL;
    }
}