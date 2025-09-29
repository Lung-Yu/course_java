package com.tygrus.task_list.domain.model;

/**
 * 通知類型枚舉
 * 定義系統支援的各種通知方式
 */
public enum NotificationType {
    
    /**
     * 電子郵件通知
     */
    EMAIL("email", "電子郵件"),
    
    /**
     * 簡訊通知
     */
    SMS("sms", "簡訊"),
    
    /**
     * 推播通知
     */
    PUSH("push", "推播通知"),
    
    /**
     * 系統內部通知
     */
    IN_APP("in_app", "應用內通知");
    
    private final String code;
    private final String displayName;
    
    NotificationType(String code, String displayName) {
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
     * 根據代碼獲取通知類型
     * 
     * @param code 通知類型代碼
     * @return 對應的通知類型，如果不存在則拋出異常
     */
    public static NotificationType fromCode(String code) {
        for (NotificationType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown notification type code: " + code);
    }
    
    /**
     * 檢查是否為即時通知類型
     * 
     * @return 如果是即時通知返回true
     */
    public boolean isRealTime() {
        return this == PUSH || this == IN_APP;
    }
    
    /**
     * 檢查是否需要外部服務
     * 
     * @return 如果需要外部服務返回true
     */
    public boolean requiresExternalService() {
        return this == EMAIL || this == SMS || this == PUSH;
    }
}