package com.tygrus.task_list.application.service.notification;

import com.tygrus.task_list.domain.model.NotificationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 通知服務工廠
 * 負責管理和提供不同類型的通知服務
 */
@Component
public class NotificationServiceFactory {
    
    private final Map<NotificationType, NotificationService> serviceMap;
    
    @Autowired
    public NotificationServiceFactory(
            EmailNotificationService emailService,
            SmsNotificationService smsService,
            PushNotificationService pushService) {
        
        this.serviceMap = new HashMap<>();
        
        // 註冊各種通知服務
        registerService(NotificationType.EMAIL, emailService);
        registerService(NotificationType.SMS, smsService);
        registerService(NotificationType.PUSH, pushService);
    }
    
    /**
     * 註冊通知服務
     * 
     * @param type 通知類型
     * @param service 對應的服務實作
     */
    private void registerService(NotificationType type, NotificationService service) {
        if (service.supportsNotificationType(type)) {
            serviceMap.put(type, service);
        } else {
            throw new IllegalArgumentException(
                String.format("Service %s does not support notification type %s", 
                    service.getServiceName(), type));
        }
    }
    
    /**
     * 根據通知類型獲取對應的服務
     * 
     * @param type 通知類型
     * @return 對應的通知服務，如果不存在則返回空
     */
    public Optional<NotificationService> getService(NotificationType type) {
        return Optional.ofNullable(serviceMap.get(type));
    }
    
    /**
     * 檢查是否支援指定的通知類型
     * 
     * @param type 通知類型
     * @return 如果支援返回true
     */
    public boolean supportsType(NotificationType type) {
        return serviceMap.containsKey(type);
    }
    
    /**
     * 獲取所有支援的通知類型
     * 
     * @return 支援的通知類型列表
     */
    public List<NotificationType> getSupportedTypes() {
        return List.copyOf(serviceMap.keySet());
    }
    
    /**
     * 獲取所有已註冊的服務
     * 
     * @return 服務列表
     */
    public List<NotificationService> getAllServices() {
        return List.copyOf(serviceMap.values());
    }
    
    /**
     * 檢查所有服務的健康狀態
     * 
     * @return 健康狀態映射表
     */
    public Map<NotificationType, Boolean> checkAllServiceHealth() {
        Map<NotificationType, Boolean> healthStatus = new HashMap<>();
        
        for (Map.Entry<NotificationType, NotificationService> entry : serviceMap.entrySet()) {
            try {
                boolean isHealthy = entry.getValue().isHealthy();
                healthStatus.put(entry.getKey(), isHealthy);
            } catch (Exception e) {
                healthStatus.put(entry.getKey(), false);
            }
        }
        
        return healthStatus;
    }
    
    /**
     * 獲取服務統計資訊
     * 
     * @return 服務統計資訊
     */
    public ServiceStatistics getServiceStatistics() {
        int totalServices = serviceMap.size();
        int healthyServices = 0;
        
        for (NotificationService service : serviceMap.values()) {
            try {
                if (service.isHealthy()) {
                    healthyServices++;
                }
            } catch (Exception e) {
                // 健康檢查失敗，視為不健康
            }
        }
        
        return new ServiceStatistics(totalServices, healthyServices, totalServices - healthyServices);
    }
    
    /**
     * 服務統計資訊內部類別
     */
    public static class ServiceStatistics {
        private final int totalServices;
        private final int healthyServices;
        private final int unhealthyServices;
        
        public ServiceStatistics(int totalServices, int healthyServices, int unhealthyServices) {
            this.totalServices = totalServices;
            this.healthyServices = healthyServices;
            this.unhealthyServices = unhealthyServices;
        }
        
        public int getTotalServices() { return totalServices; }
        public int getHealthyServices() { return healthyServices; }
        public int getUnhealthyServices() { return unhealthyServices; }
        
        public double getHealthyPercentage() {
            if (totalServices == 0) return 0.0;
            return (double) healthyServices / totalServices * 100.0;
        }
        
        @Override
        public String toString() {
            return String.format("ServiceStatistics{total=%d, healthy=%d, unhealthy=%d, healthyPercentage=%.1f%%}",
                totalServices, healthyServices, unhealthyServices, getHealthyPercentage());
        }
    }
}