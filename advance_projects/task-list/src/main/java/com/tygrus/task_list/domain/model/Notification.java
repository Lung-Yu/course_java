package com.tygrus.task_list.domain.model;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

/**
 * 通知領域模型
 * 表示系統中的通知實體，包含通知的所有相關資訊
 */
public class Notification {
    
    private final String id;
    private final String recipient;
    private final NotificationType type;
    private final String title;
    private final String message;
    private NotificationStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime sentAt;
    private LocalDateTime lastAttemptAt;
    private int retryCount;
    private int maxRetries;
    private String errorMessage;
    private final Map<String, Object> metadata;
    
    /**
     * 建構子 - 創建新的通知
     * 
     * @param recipient 接收者
     * @param type 通知類型
     * @param title 通知標題
     * @param message 通知內容
     */
    public Notification(String recipient, NotificationType type, String title, String message) {
        this(UUID.randomUUID().toString(), recipient, type, title, message);
    }
    
    /**
     * 建構子 - 使用指定ID創建通知
     * 
     * @param id 通知ID
     * @param recipient 接收者
     * @param type 通知類型
     * @param title 通知標題
     * @param message 通知內容
     */
    public Notification(String id, String recipient, NotificationType type, String title, String message) {
        this.id = Objects.requireNonNull(id, "Notification ID cannot be null");
        this.recipient = Objects.requireNonNull(recipient, "Recipient cannot be null");
        this.type = Objects.requireNonNull(type, "Notification type cannot be null");
        this.title = Objects.requireNonNull(title, "Title cannot be null");
        this.message = Objects.requireNonNull(message, "Message cannot be null");
        this.status = NotificationStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.retryCount = 0;
        this.maxRetries = 3; // 預設最大重試3次
        this.metadata = new HashMap<>();
    }
    
    /**
     * 標記通知為發送中
     */
    public void markAsSending() {
        if (status == NotificationStatus.PENDING || status.canRetry()) {
            this.status = NotificationStatus.SENDING;
            this.lastAttemptAt = LocalDateTime.now();
        } else {
            throw new IllegalStateException("Cannot mark as sending from status: " + status);
        }
    }
    
    /**
     * 標記通知為發送成功
     */
    public void markAsSent() {
        if (status == NotificationStatus.SENDING) {
            this.status = NotificationStatus.SENT;
            this.sentAt = LocalDateTime.now();
            this.errorMessage = null;
        } else {
            throw new IllegalStateException("Cannot mark as sent from status: " + status);
        }
    }
    
    /**
     * 標記通知為發送失敗
     * 
     * @param errorMessage 錯誤訊息
     */
    public void markAsFailed(String errorMessage) {
        if (status == NotificationStatus.SENDING || status == NotificationStatus.RETRYING) {
            this.errorMessage = errorMessage;
            this.lastAttemptAt = LocalDateTime.now();
            
            if (retryCount >= maxRetries) {
                this.status = NotificationStatus.FAILED_FINAL;
            } else {
                this.status = NotificationStatus.FAILED;
            }
        } else {
            throw new IllegalStateException("Cannot mark as failed from status: " + status);
        }
    }
    
    /**
     * 嘗試重試發送
     * 
     * @return 如果可以重試返回true，否則返回false
     */
    public boolean retry() {
        if (status.canRetry() && retryCount < maxRetries) {
            this.retryCount++;
            this.status = NotificationStatus.RETRYING;
            this.lastAttemptAt = LocalDateTime.now();
            return true;
        }
        return false;
    }
    
    /**
     * 取消通知
     */
    public void cancel() {
        if (!status.isFinalStatus()) {
            this.status = NotificationStatus.CANCELLED;
        }
    }
    
    /**
     * 添加元數據
     * 
     * @param key 鍵
     * @param value 值
     */
    public void addMetadata(String key, Object value) {
        this.metadata.put(key, value);
    }
    
    /**
     * 獲取元數據
     * 
     * @param key 鍵
     * @return 值
     */
    public Object getMetadata(String key) {
        return this.metadata.get(key);
    }
    
    /**
     * 設置最大重試次數
     * 
     * @param maxRetries 最大重試次數
     */
    public void setMaxRetries(int maxRetries) {
        if (maxRetries < 0) {
            throw new IllegalArgumentException("Max retries cannot be negative");
        }
        this.maxRetries = maxRetries;
    }
    
    // Getters
    public String getId() { return id; }
    public String getRecipient() { return recipient; }
    public NotificationType getType() { return type; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public NotificationStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getSentAt() { return sentAt; }
    public LocalDateTime getLastAttemptAt() { return lastAttemptAt; }
    public int getRetryCount() { return retryCount; }
    public int getMaxRetries() { return maxRetries; }
    public String getErrorMessage() { return errorMessage; }
    public Map<String, Object> getMetadata() { return new HashMap<>(metadata); }
    
    /**
     * 檢查通知是否已完成 (成功或最終失敗)
     * 
     * @return 如果已完成返回true
     */
    public boolean isCompleted() {
        return status.isFinalStatus();
    }
    
    /**
     * 檢查通知是否需要重試
     * 
     * @return 如果需要重試返回true
     */
    public boolean needsRetry() {
        return status == NotificationStatus.FAILED && retryCount < maxRetries;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Notification that = (Notification) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return String.format("Notification{id='%s', type=%s, recipient='%s', status=%s, retryCount=%d}",
            id, type, recipient, status, retryCount);
    }
}