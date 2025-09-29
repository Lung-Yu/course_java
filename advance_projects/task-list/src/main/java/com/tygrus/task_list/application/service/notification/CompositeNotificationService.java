package com.tygrus.task_list.application.service.notification;

import com.tygrus.task_list.domain.model.Notification;
import com.tygrus.task_list.domain.model.NotificationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

/**
 * 複合通知服務
 * 支援同時向多個通道發送通知，並提供重試和回退機制
 */
@Service
public class CompositeNotificationService implements NotificationService {
    
    private static final Logger logger = LoggerFactory.getLogger(CompositeNotificationService.class);
    private static final String SERVICE_NAME = "CompositeNotificationService";
    
    private final NotificationServiceFactory serviceFactory;
    
    public CompositeNotificationService(NotificationServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }
    
    /**
     * 向多個通道發送通知
     * 
     * @param notification 原始通知
     * @param recipients 各通道的接收者映射 (通道類型 -> 接收者)
     * @param types 要發送的通知類型列表
     * @return 所有通道的發送結果
     */
    public List<Boolean> sendToMultipleChannels(Notification notification, 
                                               java.util.Map<NotificationType, String> recipients,
                                               NotificationType... types) {
        List<Boolean> results = new ArrayList<>();
        
        for (NotificationType type : types) {
            String recipient = recipients.get(type);
            if (recipient == null) {
                logger.warn("No recipient specified for notification type: {}", type);
                results.add(false);
                continue;
            }
            
            // 為每個通道創建獨立的通知實例
            Notification channelNotification = new Notification(
                recipient, type, notification.getTitle(), notification.getMessage());
            
            // 複製元數據
            notification.getMetadata().forEach(channelNotification::addMetadata);
            
            Optional<NotificationService> service = serviceFactory.getService(type);
            if (service.isPresent()) {
                boolean result = service.get().sendNotification(channelNotification);
                results.add(result);
                logger.debug("Sent notification to {} channel: {}", type, result);
            } else {
                logger.warn("No service available for notification type: {}", type);
                results.add(false);
            }
        }
        
        return results;
    }
    
    /**
     * 異步向多個通道發送通知
     */
    public CompletableFuture<List<Boolean>> sendToMultipleChannelsAsync(
            Notification notification, 
            java.util.Map<NotificationType, String> recipients,
            NotificationType... types) {
        
        List<CompletableFuture<Boolean>> futures = new ArrayList<>();
        
        for (NotificationType type : types) {
            String recipient = recipients.get(type);
            if (recipient == null) {
                futures.add(CompletableFuture.completedFuture(false));
                continue;
            }
            
            Notification channelNotification = new Notification(
                recipient, type, notification.getTitle(), notification.getMessage());
            notification.getMetadata().forEach(channelNotification::addMetadata);
            
            Optional<NotificationService> service = serviceFactory.getService(type);
            if (service.isPresent()) {
                CompletableFuture<Boolean> future = service.get().sendNotificationAsync(channelNotification);
                futures.add(future);
            } else {
                futures.add(CompletableFuture.completedFuture(false));
            }
        }
        
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
            .thenApply(v -> futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList()));
    }
    
    /**
     * 帶有回退機制的通知發送
     * 如果主要通道失敗，會嘗試回退通道
     */
    public boolean sendWithFallback(Notification notification,
                                   java.util.Map<NotificationType, String> recipients,
                                   NotificationType primaryType,
                                   NotificationType... fallbackTypes) {
        
        // 首先嘗試主要通道
        String primaryRecipient = recipients.get(primaryType);
        if (primaryRecipient != null) {
            Notification primaryNotification = new Notification(
                primaryRecipient, primaryType, notification.getTitle(), notification.getMessage());
            notification.getMetadata().forEach(primaryNotification::addMetadata);
            
            Optional<NotificationService> primaryService = serviceFactory.getService(primaryType);
            if (primaryService.isPresent()) {
                boolean success = primaryService.get().sendNotification(primaryNotification);
                if (success) {
                    logger.info("Primary notification sent successfully via {}", primaryType);
                    return true;
                } else {
                    logger.warn("Primary notification failed via {}, trying fallbacks", primaryType);
                }
            }
        }
        
        // 嘗試回退通道
        for (NotificationType fallbackType : fallbackTypes) {
            String fallbackRecipient = recipients.get(fallbackType);
            if (fallbackRecipient == null) {
                continue;
            }
            
            Notification fallbackNotification = new Notification(
                fallbackRecipient, fallbackType, notification.getTitle(), notification.getMessage());
            notification.getMetadata().forEach(fallbackNotification::addMetadata);
            fallbackNotification.addMetadata("fallback_from", primaryType.getCode());
            
            Optional<NotificationService> fallbackService = serviceFactory.getService(fallbackType);
            if (fallbackService.isPresent()) {
                boolean success = fallbackService.get().sendNotification(fallbackNotification);
                if (success) {
                    logger.info("Fallback notification sent successfully via {}", fallbackType);
                    return true;
                } else {
                    logger.warn("Fallback notification failed via {}", fallbackType);
                }
            }
        }
        
        logger.error("All notification channels failed for notification: {}", notification.getId());
        return false;
    }
    
    @Override
    public boolean sendNotification(Notification notification) {
        // 使用單一服務發送
        Optional<NotificationService> service = serviceFactory.getService(notification.getType());
        if (service.isPresent()) {
            return service.get().sendNotification(notification);
        } else {
            logger.warn("No service available for notification type: {}", notification.getType());
            return false;
        }
    }
    
    @Override
    public CompletableFuture<Boolean> sendNotificationAsync(Notification notification) {
        Optional<NotificationService> service = serviceFactory.getService(notification.getType());
        if (service.isPresent()) {
            return service.get().sendNotificationAsync(notification);
        } else {
            return CompletableFuture.completedFuture(false);
        }
    }
    
    @Override
    public List<Boolean> sendNotifications(List<Notification> notifications) {
        return notifications.stream()
            .map(this::sendNotification)
            .collect(Collectors.toList());
    }
    
    @Override
    public CompletableFuture<List<Boolean>> sendNotificationsAsync(List<Notification> notifications) {
        List<CompletableFuture<Boolean>> futures = notifications.stream()
            .map(this::sendNotificationAsync)
            .collect(Collectors.toList());
        
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
            .thenApply(v -> futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList()));
    }
    
    @Override
    public boolean supportsNotificationType(NotificationType type) {
        return serviceFactory.supportsType(type);
    }
    
    @Override
    public List<NotificationType> getSupportedTypes() {
        return serviceFactory.getSupportedTypes();
    }
    
    @Override
    public boolean validateNotification(Notification notification) {
        Optional<NotificationService> service = serviceFactory.getService(notification.getType());
        return service.map(s -> s.validateNotification(notification)).orElse(false);
    }
    
    @Override
    public boolean isHealthy() {
        // 只要有一個服務健康就認為是健康的
        return serviceFactory.getAllServices().stream()
            .anyMatch(NotificationService::isHealthy);
    }
    
    @Override
    public String getServiceName() {
        return SERVICE_NAME;
    }
    
    /**
     * 獲取所有服務的健康狀態報告
     */
    public java.util.Map<NotificationType, Boolean> getHealthReport() {
        return serviceFactory.checkAllServiceHealth();
    }
    
    /**
     * 獲取服務統計資訊
     */
    public NotificationServiceFactory.ServiceStatistics getStatistics() {
        return serviceFactory.getServiceStatistics();
    }
    
    /**
     * 測試所有通道的連接
     */
    public java.util.Map<NotificationType, Boolean> testAllConnections() {
        java.util.Map<NotificationType, Boolean> results = new java.util.HashMap<>();
        
        for (NotificationType type : getSupportedTypes()) {
            Optional<NotificationService> service = serviceFactory.getService(type);
            if (service.isPresent()) {
                try {
                    boolean connectionOk = service.get().testConnection();
                    results.put(type, connectionOk);
                } catch (Exception e) {
                    logger.error("Connection test failed for {}: {}", type, e.getMessage());
                    results.put(type, false);
                }
            } else {
                results.put(type, false);
            }
        }
        
        return results;
    }
}