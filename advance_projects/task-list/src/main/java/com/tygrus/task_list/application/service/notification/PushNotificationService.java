package com.tygrus.task_list.application.service.notification;

import com.tygrus.task_list.domain.model.Notification;
import com.tygrus.task_list.domain.model.NotificationType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.UUID;

/**
 * 推播通知服務實作
 * 負責發送推播通知到行動裝置
 */
@Service
public class PushNotificationService extends AbstractNotificationService {
    
    private static final String SERVICE_NAME = "PushNotificationService";
    private static final String SERVICE_VERSION = "1.0.0";
    
    // 支援的平台
    public enum Platform {
        IOS("ios", "Apple Push Notification Service"),
        ANDROID("android", "Firebase Cloud Messaging"),
        WEB("web", "Web Push Protocol");
        
        private final String code;
        private final String description;
        
        Platform(String code, String description) {
            this.code = code;
            this.description = description;
        }
        
        public String getCode() { return code; }
        public String getDescription() { return description; }
    }
    
    // 模擬推播服務配置
    private boolean serviceEnabled = true;
    private String fcmApiKey = "test-fcm-key";
    private String apnsKeyId = "test-apns-key";
    private Set<String> registeredDevices = new HashSet<>();
    
    public PushNotificationService() {
        // 初始化一些模擬的裝置token
        registeredDevices.add("device_token_ios_001");
        registeredDevices.add("device_token_android_001");
        registeredDevices.add("device_token_web_001");
    }
    
    @Override
    protected boolean doSendNotification(Notification notification) throws Exception {
        // 檢查服務狀態
        if (!serviceEnabled) {
            throw new IllegalStateException("Push notification service is disabled");
        }
        
        String deviceToken = notification.getRecipient();
        
        // 檢查裝置是否已註冊
        if (!registeredDevices.contains(deviceToken)) {
            throw new Exception("Device token not registered: " + deviceToken);
        }
        
        // 判斷平台類型
        Platform platform = detectPlatform(deviceToken);
        
        // 模擬發送推播通知的過程
        logger.info("Sending push notification to device: {} (platform: {})", 
            deviceToken, platform.getDescription());
        logger.debug("Push title: {}", notification.getTitle());
        logger.debug("Push message: {}", notification.getMessage());
        
        // 添加推播特定的元數據
        notification.addMetadata("platform", platform.getCode());
        notification.addMetadata("device_token", deviceToken);
        notification.addMetadata("payload_size", calculatePayloadSize(notification));
        
        // 根據平台發送通知
        boolean success = sendToPlatform(platform, notification);
        
        if (success) {
            logger.info("Push notification sent successfully to: {} (platform: {})", 
                deviceToken, platform.getCode());
            notification.addMetadata("sent_at", java.time.LocalDateTime.now().toString());
            notification.addMetadata("message_id", generateMessageId(platform));
        }
        
        return success;
    }
    
    @Override
    protected boolean doValidateNotification(Notification notification) {
        String deviceToken = notification.getRecipient();
        
        // 驗證裝置token格式
        if (deviceToken == null || deviceToken.trim().isEmpty()) {
            logger.warn("Device token cannot be null or empty");
            return false;
        }
        
        // 檢查token長度 (通常應該是64個字元以上)
        if (deviceToken.length() < 10) {
            logger.warn("Device token too short: {}", deviceToken);
            return false;
        }
        
        // 驗證推播標題長度
        if (notification.getTitle().length() > 100) {
            logger.warn("Push notification title too long: {} characters", 
                notification.getTitle().length());
            return false;
        }
        
        // 驗證推播內容長度
        if (notification.getMessage().length() > 500) {
            logger.warn("Push notification message too long: {} characters", 
                notification.getMessage().length());
            return false;
        }
        
        return true;
    }
    
    @Override
    protected boolean doHealthCheck() throws Exception {
        // 檢查推播服務是否啟用
        if (!serviceEnabled) {
            return false;
        }
        
        // 檢查各平台服務狀態
        return isFcmAvailable() && isApnsAvailable();
    }
    
    @Override
    public boolean supportsNotificationType(NotificationType type) {
        return type == NotificationType.PUSH;
    }
    
    @Override
    public List<NotificationType> getSupportedTypes() {
        return List.of(NotificationType.PUSH);
    }
    
    @Override
    public String getServiceName() {
        return SERVICE_NAME;
    }
    
    @Override
    public String getServiceVersion() {
        return SERVICE_VERSION;
    }
    
    @Override
    public boolean testConnection() {
        try {
            return isFcmAvailable() && isApnsAvailable();
        } catch (Exception e) {
            logger.error("Failed to test push notification connections: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 偵測裝置平台
     * 
     * @param deviceToken 裝置token
     * @return 平台類型
     */
    private Platform detectPlatform(String deviceToken) {
        // 簡單的平台偵測邏輯 (實際情況會更複雜)
        if (deviceToken.contains("ios")) {
            return Platform.IOS;
        } else if (deviceToken.contains("android")) {
            return Platform.ANDROID;
        } else {
            return Platform.WEB;
        }
    }
    
    /**
     * 向特定平台發送通知
     * 
     * @param platform 目標平台
     * @param notification 通知物件
     * @return 發送是否成功
     * @throws Exception 發送異常
     */
    private boolean sendToPlatform(Platform platform, Notification notification) throws Exception {
        // 模擬平台特定的發送邏輯
        switch (platform) {
            case IOS -> {
                return sendToApns(notification);
            }
            case ANDROID -> {
                return sendToFcm(notification);
            }
            case WEB -> {
                return sendToWebPush(notification);
            }
            default -> throw new IllegalArgumentException("Unsupported platform: " + platform);
        }
    }
    
    /**
     * 發送到 Apple Push Notification Service
     */
    private boolean sendToApns(Notification notification) throws Exception {
        if (!isApnsAvailable()) {
            throw new Exception("APNS service is not available");
        }
        
        // 模擬APNS調用
        simulatePlatformCall("APNS");
        
        // 98%成功率
        return Math.random() > 0.02;
    }
    
    /**
     * 發送到 Firebase Cloud Messaging
     */
    private boolean sendToFcm(Notification notification) throws Exception {
        if (!isFcmAvailable()) {
            throw new Exception("FCM service is not available");
        }
        
        // 模擬FCM調用
        simulatePlatformCall("FCM");
        
        // 97%成功率
        return Math.random() > 0.03;
    }
    
    /**
     * 發送到 Web Push
     */
    private boolean sendToWebPush(Notification notification) throws Exception {
        // 模擬Web Push調用
        simulatePlatformCall("WebPush");
        
        // 95%成功率
        return Math.random() > 0.05;
    }
    
    /**
     * 檢查FCM服務是否可用
     */
    private boolean isFcmAvailable() {
        return serviceEnabled && fcmApiKey != null && !fcmApiKey.isEmpty();
    }
    
    /**
     * 檢查APNS服務是否可用
     */
    private boolean isApnsAvailable() {
        return serviceEnabled && apnsKeyId != null && !apnsKeyId.isEmpty();
    }
    
    /**
     * 模擬平台調用延遲
     */
    private void simulatePlatformCall(String platform) {
        try {
            // 模擬不同平台的延遲
            long delay = switch (platform) {
                case "APNS" -> 100 + (long)(Math.random() * 200); // 100-300ms
                case "FCM" -> 80 + (long)(Math.random() * 120);   // 80-200ms  
                case "WebPush" -> 50 + (long)(Math.random() * 100); // 50-150ms
                default -> 100;
            };
            
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("{} call simulation interrupted", platform);
        }
    }
    
    /**
     * 計算推播負載大小
     */
    private int calculatePayloadSize(Notification notification) {
        String payload = String.format("{\"title\":\"%s\",\"message\":\"%s\"}", 
            notification.getTitle(), notification.getMessage());
        return payload.getBytes().length;
    }
    
    /**
     * 生成推播訊息ID
     */
    private String generateMessageId(Platform platform) {
        return String.format("%s-%s", 
            platform.getCode().toUpperCase(),
            UUID.randomUUID().toString());
    }
    
    /**
     * 註冊裝置token
     */
    public void registerDevice(String deviceToken) {
        registeredDevices.add(deviceToken);
        logger.info("Device registered: {}", deviceToken);
    }
    
    /**
     * 移除裝置token
     */
    public void unregisterDevice(String deviceToken) {
        registeredDevices.remove(deviceToken);
        logger.info("Device unregistered: {}", deviceToken);
    }
    
    /**
     * 獲取已註冊裝置數量
     */
    public int getRegisteredDeviceCount() {
        return registeredDevices.size();
    }
    
    // Configuration methods for testing
    public void setServiceEnabled(boolean enabled) {
        this.serviceEnabled = enabled;
    }
    
    public void setFcmApiKey(String key) {
        this.fcmApiKey = key;
    }
    
    public void setApnsKeyId(String keyId) {
        this.apnsKeyId = keyId;
    }
    
    public boolean isServiceEnabled() {
        return serviceEnabled;
    }
}