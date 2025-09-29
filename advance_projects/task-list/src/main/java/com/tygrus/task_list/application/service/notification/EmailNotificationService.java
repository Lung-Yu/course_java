package com.tygrus.task_list.application.service.notification;

import com.tygrus.task_list.domain.model.Notification;
import com.tygrus.task_list.domain.model.NotificationType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

/**
 * 電子郵件通知服務實作
 * 負責發送電子郵件通知
 */
@Service
public class EmailNotificationService extends AbstractNotificationService {
    
    private static final String SERVICE_NAME = "EmailNotificationService";
    private static final String SERVICE_VERSION = "1.0.0";
    
    // Email格式驗證的正則表達式
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$"
    );
    
    // 模擬郵件服務的配置
    private boolean serviceEnabled = true;
    private String smtpServer = "smtp.example.com";
    private int smtpPort = 587;
    private boolean useSSL = true;
    
    @Override
    protected boolean doSendNotification(Notification notification) throws Exception {
        // 模擬檢查服務狀態
        if (!serviceEnabled) {
            throw new IllegalStateException("Email service is disabled");
        }
        
        // 模擬檢查SMTP連接
        if (!isSmtpServerAvailable()) {
            throw new Exception("SMTP server is not available: " + smtpServer + ":" + smtpPort);
        }
        
        // 模擬發送電子郵件的過程
        logger.info("Sending email to: {}", notification.getRecipient());
        logger.debug("Email subject: {}", notification.getTitle());
        logger.debug("Email content: {}", notification.getMessage());
        
        // 添加郵件特定的元數據
        notification.addMetadata("smtp_server", smtpServer);
        notification.addMetadata("smtp_port", smtpPort);
        notification.addMetadata("use_ssl", useSSL);
        notification.addMetadata("content_type", "text/html");
        
        // 模擬發送延遲
        simulateSendingDelay();
        
        // 模擬發送成功/失敗 (90%成功率)
        boolean success = Math.random() > 0.1;
        
        if (success) {
            logger.info("Email sent successfully to: {}", notification.getRecipient());
            notification.addMetadata("sent_at", java.time.LocalDateTime.now().toString());
            notification.addMetadata("message_id", generateMessageId());
        } else {
            throw new Exception("SMTP server returned error: 550 Mailbox unavailable");
        }
        
        return success;
    }
    
    @Override
    protected boolean doValidateNotification(Notification notification) {
        // 驗證收件人是否為有效的電子郵件地址
        String recipient = notification.getRecipient();
        if (!EMAIL_PATTERN.matcher(recipient).matches()) {
            logger.warn("Invalid email address: {}", recipient);
            return false;
        }
        
        // 驗證郵件標題長度
        if (notification.getTitle().length() > 200) {
            logger.warn("Email subject too long: {} characters", notification.getTitle().length());
            return false;
        }
        
        // 驗證郵件內容長度
        if (notification.getMessage().length() > 10000) {
            logger.warn("Email content too long: {} characters", notification.getMessage().length());
            return false;
        }
        
        return true;
    }
    
    @Override
    protected boolean doHealthCheck() throws Exception {
        // 檢查郵件服務是否啟用
        if (!serviceEnabled) {
            return false;
        }
        
        // 檢查SMTP服務器連接
        return isSmtpServerAvailable();
    }
    
    @Override
    public boolean supportsNotificationType(NotificationType type) {
        return type == NotificationType.EMAIL;
    }
    
    @Override
    public List<NotificationType> getSupportedTypes() {
        return List.of(NotificationType.EMAIL);
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
            return isSmtpServerAvailable();
        } catch (Exception e) {
            logger.error("Failed to test SMTP connection: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 檢查SMTP服務器是否可用
     * 
     * @return 如果可用返回true
     */
    private boolean isSmtpServerAvailable() {
        // 模擬SMTP服務器連接檢查
        // 在實際實作中，這裡會嘗試連接到SMTP服務器
        return serviceEnabled && smtpServer != null && !smtpServer.isEmpty();
    }
    
    /**
     * 模擬發送延遲
     */
    private void simulateSendingDelay() {
        try {
            // 模擬網路延遲 100-500ms
            Thread.sleep(100 + (long)(Math.random() * 400));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Sending simulation interrupted");
        }
    }
    
    /**
     * 生成郵件訊息ID
     * 
     * @return 訊息ID
     */
    private String generateMessageId() {
        return String.format("<%d.%d@%s>", 
            System.currentTimeMillis(),
            Thread.currentThread().getId(),
            smtpServer);
    }
    
    // Configuration methods for testing
    public void setServiceEnabled(boolean enabled) {
        this.serviceEnabled = enabled;
    }
    
    public void setSmtpServer(String server) {
        this.smtpServer = server;
    }
    
    public void setSmtpPort(int port) {
        this.smtpPort = port;
    }
    
    public boolean isServiceEnabled() {
        return serviceEnabled;
    }
    
    public String getSmtpServer() {
        return smtpServer;
    }
    
    public int getSmtpPort() {
        return smtpPort;
    }
}