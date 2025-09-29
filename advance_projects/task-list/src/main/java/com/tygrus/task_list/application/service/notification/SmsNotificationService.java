package com.tygrus.task_list.application.service.notification;

import com.tygrus.task_list.domain.model.Notification;
import com.tygrus.task_list.domain.model.NotificationType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

/**
 * 簡訊通知服務實作
 * 負責發送簡訊通知
 */
@Service
public class SmsNotificationService extends AbstractNotificationService {
    
    private static final String SERVICE_NAME = "SmsNotificationService";
    private static final String SERVICE_VERSION = "1.0.0";
    
    // 手機號碼格式驗證 (支援台灣手機號碼格式)
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^(\\+886|0)?[9][0-9]{8}$"
    );
    
    // SMS內容長度限制
    private static final int MAX_SMS_LENGTH = 160;
    
    // 模擬SMS服務配置
    private boolean serviceEnabled = true;
    private String apiEndpoint = "https://sms-api.example.com";
    private String apiKey = "test-api-key";
    private double costPerMessage = 0.05; // 每則簡訊成本
    
    @Override
    protected boolean doSendNotification(Notification notification) throws Exception {
        // 檢查服務狀態
        if (!serviceEnabled) {
            throw new IllegalStateException("SMS service is disabled");
        }
        
        // 檢查API連接
        if (!isApiEndpointAvailable()) {
            throw new Exception("SMS API endpoint is not available: " + apiEndpoint);
        }
        
        // 模擬發送簡訊的過程
        logger.info("Sending SMS to: {}", notification.getRecipient());
        logger.debug("SMS content: {}", notification.getMessage());
        
        // 計算簡訊長度和數量 (超過160字元會分割成多則)
        String message = notification.getMessage();
        int messageCount = (int) Math.ceil((double) message.length() / MAX_SMS_LENGTH);
        double totalCost = messageCount * costPerMessage;
        
        // 添加SMS特定的元數據
        notification.addMetadata("api_endpoint", apiEndpoint);
        notification.addMetadata("message_count", messageCount);
        notification.addMetadata("total_cost", totalCost);
        notification.addMetadata("character_count", message.length());
        
        // 模擬API調用延遲
        simulateApiCall();
        
        // 模擬發送成功/失敗 (95%成功率)
        boolean success = Math.random() > 0.05;
        
        if (success) {
            logger.info("SMS sent successfully to: {} (messages: {}, cost: ${})", 
                notification.getRecipient(), messageCount, totalCost);
            notification.addMetadata("sent_at", java.time.LocalDateTime.now().toString());
            notification.addMetadata("message_id", generateMessageId());
        } else {
            throw new Exception("SMS API returned error: Invalid phone number or insufficient credits");
        }
        
        return success;
    }
    
    @Override
    protected boolean doValidateNotification(Notification notification) {
        // 驗證手機號碼格式
        String recipient = notification.getRecipient();
        if (!PHONE_PATTERN.matcher(recipient).matches()) {
            logger.warn("Invalid phone number format: {}", recipient);
            return false;
        }
        
        // 驗證簡訊內容不為空
        String message = notification.getMessage();
        if (message.trim().isEmpty()) {
            logger.warn("SMS message cannot be empty");
            return false;
        }
        
        // 警告過長的簡訊 (不阻止發送，但會產生多則簡訊)
        if (message.length() > MAX_SMS_LENGTH) {
            int messageCount = (int) Math.ceil((double) message.length() / MAX_SMS_LENGTH);
            logger.info("SMS message length {} exceeds limit, will be split into {} messages", 
                message.length(), messageCount);
        }
        
        return true;
    }
    
    @Override
    protected boolean doHealthCheck() throws Exception {
        // 檢查SMS服務是否啟用
        if (!serviceEnabled) {
            return false;
        }
        
        // 檢查API端點是否可用
        return isApiEndpointAvailable();
    }
    
    @Override
    public boolean supportsNotificationType(NotificationType type) {
        return type == NotificationType.SMS;
    }
    
    @Override
    public List<NotificationType> getSupportedTypes() {
        return List.of(NotificationType.SMS);
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
            return isApiEndpointAvailable() && testApiCredentials();
        } catch (Exception e) {
            logger.error("Failed to test SMS API connection: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 檢查API端點是否可用
     * 
     * @return 如果可用返回true
     */
    private boolean isApiEndpointAvailable() {
        // 模擬API端點連接檢查
        return serviceEnabled && apiEndpoint != null && !apiEndpoint.isEmpty();
    }
    
    /**
     * 測試API憑證是否有效
     * 
     * @return 如果有效返回true
     */
    private boolean testApiCredentials() {
        // 模擬API憑證驗證
        return apiKey != null && !apiKey.isEmpty() && !apiKey.equals("invalid-key");
    }
    
    /**
     * 模擬API調用延遲
     */
    private void simulateApiCall() {
        try {
            // 模擬API調用延遲 50-200ms
            Thread.sleep(50 + (long)(Math.random() * 150));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("API call simulation interrupted");
        }
    }
    
    /**
     * 生成簡訊訊息ID
     * 
     * @return 訊息ID
     */
    private String generateMessageId() {
        return String.format("SMS-%d-%d", 
            System.currentTimeMillis(),
            Thread.currentThread().getId());
    }
    
    /**
     * 計算簡訊成本
     * 
     * @param message 簡訊內容
     * @return 總成本
     */
    public double calculateCost(String message) {
        int messageCount = (int) Math.ceil((double) message.length() / MAX_SMS_LENGTH);
        return messageCount * costPerMessage;
    }
    
    /**
     * 計算簡訊則數
     * 
     * @param message 簡訊內容
     * @return 簡訊則數
     */
    public int calculateMessageCount(String message) {
        return (int) Math.ceil((double) message.length() / MAX_SMS_LENGTH);
    }
    
    // Configuration methods for testing
    public void setServiceEnabled(boolean enabled) {
        this.serviceEnabled = enabled;
    }
    
    public void setApiEndpoint(String endpoint) {
        this.apiEndpoint = endpoint;
    }
    
    public void setApiKey(String key) {
        this.apiKey = key;
    }
    
    public void setCostPerMessage(double cost) {
        this.costPerMessage = cost;
    }
    
    public boolean isServiceEnabled() {
        return serviceEnabled;
    }
    
    public String getApiEndpoint() {
        return apiEndpoint;
    }
    
    public double getCostPerMessage() {
        return costPerMessage;
    }
}