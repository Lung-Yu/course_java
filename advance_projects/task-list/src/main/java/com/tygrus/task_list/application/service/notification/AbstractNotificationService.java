package com.tygrus.task_list.application.service.notification;

import com.tygrus.task_list.domain.model.Notification;
import com.tygrus.task_list.domain.model.NotificationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import java.util.List;
import java.util.ArrayList;

/**
 * 通知服務抽象基礎類別
 * 提供通用的通知發送邏輯和異步處理能力
 */
public abstract class AbstractNotificationService implements NotificationService {
    
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected final Executor asyncExecutor;
    
    /**
     * 建構子 - 使用預設執行器
     */
    protected AbstractNotificationService() {
        this(ForkJoinPool.commonPool());
    }
    
    /**
     * 建構子 - 使用自定義執行器
     * 
     * @param executor 異步執行器
     */
    protected AbstractNotificationService(Executor executor) {
        this.asyncExecutor = executor;
    }
    
    @Override
    public boolean sendNotification(Notification notification) {
        if (!validateNotification(notification)) {
            logger.warn("Invalid notification: {}", notification);
            return false;
        }
        
        if (!supportsNotificationType(notification.getType())) {
            logger.warn("Unsupported notification type: {} for service: {}", 
                notification.getType(), getServiceName());
            return false;
        }
        
        try {
            // 標記為發送中
            notification.markAsSending();
            logger.debug("Sending notification: {}", notification.getId());
            
            // 調用具體實作的發送邏輯
            boolean result = doSendNotification(notification);
            
            if (result) {
                notification.markAsSent();
                logger.info("Notification sent successfully: {}", notification.getId());
            } else {
                notification.markAsFailed("Send operation returned false");
                logger.warn("Failed to send notification: {}", notification.getId());
            }
            
            return result;
            
        } catch (Exception e) {
            String errorMessage = "Exception occurred while sending notification: " + e.getMessage();
            notification.markAsFailed(errorMessage);
            logger.error("Error sending notification {}: {}", notification.getId(), e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public CompletableFuture<Boolean> sendNotificationAsync(Notification notification) {
        return CompletableFuture.supplyAsync(() -> sendNotification(notification), asyncExecutor);
    }
    
    @Override
    public List<Boolean> sendNotifications(List<Notification> notifications) {
        if (notifications == null || notifications.isEmpty()) {
            return new ArrayList<>();
        }
        
        return notifications.stream()
            .map(this::sendNotification)
            .collect(Collectors.toList());
    }
    
    @Override
    public CompletableFuture<List<Boolean>> sendNotificationsAsync(List<Notification> notifications) {
        if (notifications == null || notifications.isEmpty()) {
            return CompletableFuture.completedFuture(new ArrayList<>());
        }
        
        // 為每個通知創建異步任務
        List<CompletableFuture<Boolean>> futures = notifications.stream()
            .map(this::sendNotificationAsync)
            .collect(Collectors.toList());
        
        // 等待所有任務完成並收集結果
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
            .thenApply(v -> futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList()));
    }
    
    @Override
    public boolean validateNotification(Notification notification) {
        if (notification == null) {
            logger.warn("Notification is null");
            return false;
        }
        
        if (notification.getRecipient() == null || notification.getRecipient().trim().isEmpty()) {
            logger.warn("Notification recipient is null or empty");
            return false;
        }
        
        if (notification.getTitle() == null || notification.getTitle().trim().isEmpty()) {
            logger.warn("Notification title is null or empty");
            return false;
        }
        
        if (notification.getMessage() == null || notification.getMessage().trim().isEmpty()) {
            logger.warn("Notification message is null or empty");
            return false;
        }
        
        if (notification.getType() == null) {
            logger.warn("Notification type is null");
            return false;
        }
        
        // 調用具體實作的額外驗證邏輯
        return doValidateNotification(notification);
    }
    
    @Override
    public boolean isHealthy() {
        try {
            return doHealthCheck();
        } catch (Exception e) {
            logger.error("Health check failed for service {}: {}", getServiceName(), e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * 具體的通知發送實作
     * 子類別必須實作此方法來定義實際的發送邏輯
     * 
     * @param notification 要發送的通知
     * @return 發送是否成功
     * @throws Exception 發送過程中的任何異常
     */
    protected abstract boolean doSendNotification(Notification notification) throws Exception;
    
    /**
     * 額外的通知驗證邏輯
     * 子類別可以覆寫此方法來添加特定的驗證規則
     * 
     * @param notification 要驗證的通知
     * @return 驗證是否通過
     */
    protected boolean doValidateNotification(Notification notification) {
        return true; // 預設通過驗證
    }
    
    /**
     * 健康檢查實作
     * 子類別可以覆寫此方法來實作特定的健康檢查邏輯
     * 
     * @return 服務是否健康
     * @throws Exception 健康檢查過程中的任何異常
     */
    protected boolean doHealthCheck() throws Exception {
        return true; // 預設為健康
    }
    
    /**
     * 格式化錯誤訊息
     * 
     * @param operation 操作名稱
     * @param notification 通知物件
     * @param error 錯誤訊息或異常
     * @return 格式化的錯誤訊息
     */
    protected String formatErrorMessage(String operation, Notification notification, Object error) {
        return String.format("Failed to %s notification %s (%s): %s",
            operation, notification.getId(), notification.getType(), error.toString());
    }
}