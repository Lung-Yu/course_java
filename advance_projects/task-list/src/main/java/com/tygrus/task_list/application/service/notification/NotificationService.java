package com.tygrus.task_list.application.service.notification;

import com.tygrus.task_list.domain.model.Notification;
import com.tygrus.task_list.domain.model.NotificationStatus;
import com.tygrus.task_list.domain.model.NotificationType;

import java.util.concurrent.CompletableFuture;
import java.util.List;

/**
 * 通知服務介面
 * 定義通知發送的核心方法和狀態管理
 */
public interface NotificationService {
    
    /**
     * 同步發送通知
     * 
     * @param notification 要發送的通知
     * @return 發送結果，true表示成功，false表示失敗
     */
    boolean sendNotification(Notification notification);
    
    /**
     * 異步發送通知
     * 
     * @param notification 要發送的通知
     * @return CompletableFuture包含發送結果
     */
    CompletableFuture<Boolean> sendNotificationAsync(Notification notification);
    
    /**
     * 批量發送通知
     * 
     * @param notifications 要發送的通知列表
     * @return 發送結果列表，與輸入順序對應
     */
    List<Boolean> sendNotifications(List<Notification> notifications);
    
    /**
     * 批量異步發送通知
     * 
     * @param notifications 要發送的通知列表
     * @return CompletableFuture包含發送結果列表
     */
    CompletableFuture<List<Boolean>> sendNotificationsAsync(List<Notification> notifications);
    
    /**
     * 檢查服務是否支援指定的通知類型
     * 
     * @param type 通知類型
     * @return 如果支援返回true，否則返回false
     */
    boolean supportsNotificationType(NotificationType type);
    
    /**
     * 獲取服務支援的通知類型列表
     * 
     * @return 支援的通知類型列表
     */
    List<NotificationType> getSupportedTypes();
    
    /**
     * 驗證通知內容的有效性
     * 
     * @param notification 要驗證的通知
     * @return 驗證結果，如果有效返回true
     */
    boolean validateNotification(Notification notification);
    
    /**
     * 獲取服務的健康狀態
     * 
     * @return 如果服務正常返回true
     */
    boolean isHealthy();
    
    /**
     * 獲取服務名稱
     * 
     * @return 服務名稱
     */
    String getServiceName();
    
    /**
     * 獲取服務版本
     * 
     * @return 服務版本
     */
    default String getServiceVersion() {
        return "1.0.0";
    }
    
    /**
     * 測試連接 (用於外部服務)
     * 
     * @return 如果連接正常返回true
     */
    default boolean testConnection() {
        return isHealthy();
    }
}