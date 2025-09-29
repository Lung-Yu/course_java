package com.tygrus.task_list.application.usecase;

import com.tygrus.task_list.domain.event.TaskReminderEvent;
import com.tygrus.task_list.domain.model.Notification;
import com.tygrus.task_list.domain.model.NotificationType;
import com.tygrus.task_list.domain.observer.Observer;
import com.tygrus.task_list.domain.observer.ObservableSupport;
import com.tygrus.task_list.application.service.notification.CompositeNotificationService;
import com.tygrus.task_list.application.service.notification.NotificationRetryService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.List;
import java.util.Map;

/**
 * UC-009 任務提醒用例實作
 * 展示 Observer 設計模式和異步通知處理
 * 
 * 核心功能：
 * 1. Observer 模式實作通知機制
 * 2. 多種通知方式整合 (Email, SMS, Push)
 * 3. 異步通知處理避免阻塞
 * 4. 通知狀態追蹤和重試機制
 * 5. 定時任務檢查整合
 */
@Service
public class TaskReminderUseCase extends ObservableSupport<TaskReminderEvent> {
    
    private static final Logger logger = LoggerFactory.getLogger(TaskReminderUseCase.class);
    
    private final CompositeNotificationService notificationService;
    private final NotificationRetryService retryService;
    
    // 使用案例統計
    private int totalRemindersProcessed = 0;
    private int successfulNotifications = 0;
    private int failedNotifications = 0;
    private LocalDateTime lastProcessingTime;
    
    public TaskReminderUseCase(CompositeNotificationService notificationService,
                              NotificationRetryService retryService) {
        super(); // 使用預設的 ForkJoinPool
        this.notificationService = notificationService;
        this.retryService = retryService;
        
        // 註冊內建的通知觀察者
        this.addObserver(new NotificationObserver());
        this.addObserver(new StatisticsObserver());
        
        logger.info("TaskReminderUseCase initialized with {} observers", getObserverCount());
    }
    
    /**
     * 處理任務提醒事件
     * 這是用例的主要入口點
     * 
     * @param reminderEvent 任務提醒事件
     * @return 處理結果的 CompletableFuture
     */
    public CompletableFuture<ReminderResult> handleTaskReminder(TaskReminderEvent reminderEvent) {
        logger.info("Processing task reminder event: {}", reminderEvent.getEventId());
        
        return CompletableFuture.supplyAsync(() -> {
            String currentThread = Thread.currentThread().getName();
            logger.debug("Processing reminder in thread: {}", currentThread);
            
            try {
                lastProcessingTime = LocalDateTime.now();
                totalRemindersProcessed++;
                
                logger.debug("Validating reminder event: {}", reminderEvent.getEventId());
                
                // 驗證提醒事件
                validateReminderEvent(reminderEvent);
                
                logger.debug("Notifying observers for event: {}", reminderEvent.getEventId());
                
                // 通知所有觀察者 (異步)
                notifyObserversAsync(reminderEvent);
                
                logger.debug("Creating notification from event: {}", reminderEvent.getEventId());
                
                // 創建通知物件
                Notification notification = createNotificationFromEvent(reminderEvent);
                
                logger.debug("Sending notification: {} for event: {}", 
                    notification.getId(), reminderEvent.getEventId());
                
                // 發送通知
                boolean notificationSent = sendNotificationWithRetry(notification);
                
                // 創建結果
                ReminderResult result = new ReminderResult(
                    reminderEvent.getEventId(),
                    reminderEvent.getTask().getId().getValue(),
                    notificationSent,
                    notification.getId(),
                    LocalDateTime.now()
                );
                
                if (notificationSent) {
                    successfulNotifications++;
                    logger.info("Task reminder processed successfully: {} in thread: {}", 
                        reminderEvent.getEventId(), currentThread);
                } else {
                    failedNotifications++;
                    logger.warn("Task reminder processing failed: {} in thread: {}", 
                        reminderEvent.getEventId(), currentThread);
                }
                
                return result;
                
            } catch (Exception e) {
                failedNotifications++;
                logger.error("Error processing task reminder {} in thread {}: {}", 
                    reminderEvent.getEventId(), currentThread, e.getMessage(), e);
                
                return new ReminderResult(
                    reminderEvent.getEventId(),
                    reminderEvent.getTask().getId().getValue(),
                    false,
                    null,
                    LocalDateTime.now(),
                    e.getMessage()
                );
            }
        }, ForkJoinPool.commonPool()); // 使用 ForkJoinPool 確保異步執行
    }
    
    /**
     * 批量處理任務提醒事件
     * 
     * @param reminderEvents 提醒事件列表
     * @return 處理結果列表的 CompletableFuture
     */
    public CompletableFuture<List<ReminderResult>> handleTaskReminders(List<TaskReminderEvent> reminderEvents) {
        logger.info("Processing {} task reminder events", reminderEvents.size());
        
        List<CompletableFuture<ReminderResult>> futures = reminderEvents.stream()
            .map(this::handleTaskReminder)
            .toList();
        
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
            .thenApply(v -> futures.stream()
                .map(CompletableFuture::join)
                .toList());
    }
    
    /**
     * 使用多通道發送提醒 (Email + SMS + Push)
     * 
     * @param reminderEvent 提醒事件
     * @param recipients 各通道的接收者映射
     * @return 處理結果
     */
    public CompletableFuture<MultiChannelReminderResult> handleMultiChannelReminder(
            TaskReminderEvent reminderEvent, 
            Map<NotificationType, String> recipients) {
        
        logger.info("Processing multi-channel reminder for event: {}", reminderEvent.getEventId());
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                // 通知觀察者
                notifyObserversAsync(reminderEvent);
                
                // 創建通知物件
                Notification baseNotification = createNotificationFromEvent(reminderEvent);
                
                // 發送到多個通道
                List<Boolean> results = notificationService.sendToMultipleChannels(
                    baseNotification, recipients, 
                    NotificationType.EMAIL, NotificationType.SMS, NotificationType.PUSH);
                
                // 統計結果
                long successCount = results.stream().mapToLong(success -> success ? 1 : 0).sum();
                
                MultiChannelReminderResult result = new MultiChannelReminderResult(
                    reminderEvent.getEventId(),
                    reminderEvent.getTask().getId().getValue(),
                    results,
                    successCount,
                    results.size() - successCount,
                    LocalDateTime.now()
                );
                
                logger.info("Multi-channel reminder processed: {} success, {} failed", 
                    successCount, results.size() - successCount);
                
                return result;
                
            } catch (Exception e) {
                logger.error("Error processing multi-channel reminder {}: {}", 
                    reminderEvent.getEventId(), e.getMessage(), e);
                
                return new MultiChannelReminderResult(
                    reminderEvent.getEventId(),
                    reminderEvent.getTask().getId().getValue(),
                    List.of(),
                    0,
                    recipients.size(),
                    LocalDateTime.now(),
                    e.getMessage()
                );
            }
        });
    }
    
    /**
     * 驗證提醒事件
     */
    private void validateReminderEvent(TaskReminderEvent reminderEvent) {
        logger.debug("Validating reminder event: {}", reminderEvent != null ? reminderEvent.getEventId() : "null");
        
        if (reminderEvent == null) {
            logger.error("Reminder event validation failed: event is null");
            throw new IllegalArgumentException("Reminder event cannot be null");
        }
        
        if (reminderEvent.getTask() == null) {
            logger.error("Reminder event validation failed: task is null for event {}", reminderEvent.getEventId());
            throw new IllegalArgumentException("Task in reminder event cannot be null");
        }
        
        if (reminderEvent.getRecipient() == null || reminderEvent.getRecipient().trim().isEmpty()) {
            logger.error("Reminder event validation failed: recipient is null or empty for event {}", reminderEvent.getEventId());
            throw new IllegalArgumentException("Recipient in reminder event cannot be null or empty");
        }
        
        if (reminderEvent.getNotificationType() == null) {
            logger.error("Reminder event validation failed: notification type is null for event {}", reminderEvent.getEventId());
            throw new IllegalArgumentException("Notification type in reminder event cannot be null");
        }
        
        logger.debug("Reminder event validation passed for event: {}", reminderEvent.getEventId());
    }
    
    /**
     * 從提醒事件創建通知物件
     */
    private Notification createNotificationFromEvent(TaskReminderEvent reminderEvent) {
        Notification notification = new Notification(
            reminderEvent.getRecipient(),
            reminderEvent.getNotificationType(),
            reminderEvent.getReminderTitle(),
            reminderEvent.getReminderMessage()
        );
        
        // 添加事件相關的元數據
        notification.addMetadata("event_id", reminderEvent.getEventId());
        notification.addMetadata("task_id", reminderEvent.getTask().getId().getValue());
        notification.addMetadata("reminder_reason", reminderEvent.getReason().name());
        notification.addMetadata("scheduled_time", reminderEvent.getScheduledTime().toString());
        notification.addMetadata("is_urgent", reminderEvent.isUrgent());
        notification.addMetadata("task_priority", reminderEvent.getTask().getPriority().name());
        notification.addMetadata("task_status", reminderEvent.getTask().getStatus().name());
        
        // 根據緊急程度設置重試次數
        if (reminderEvent.isUrgent()) {
            notification.setMaxRetries(5); // 緊急通知多重試幾次
        } else {
            notification.setMaxRetries(3); // 一般通知預設重試次數
        }
        
        return notification;
    }
    
    /**
     * 發送通知並處理重試
     */
    private boolean sendNotificationWithRetry(Notification notification) {
        logger.debug("Attempting to send notification: {}", notification.getId());
        
        // 在發送前標記為 SENDING 狀態
        notification.markAsSending();
        
        boolean success = notificationService.sendNotification(notification);
        
        if (!success) {
            logger.warn("Notification failed to send: {}, scheduling retry", notification.getId());
            
            // 設置通知狀態為失敗
            notification.markAsFailed("Notification service returned false");
            
            // 檢查是否需要重試
            if (notification.needsRetry()) {
                logger.info("Scheduling retry for notification: {}, retry count: {}", 
                    notification.getId(), notification.getRetryCount());
                
                // 安排重試，使用指數退避策略
                retryService.scheduleRetry(notification, NotificationRetryService.RetryStrategy.EXPONENTIAL);
            } else {
                logger.error("Max retries exceeded for notification: {}", notification.getId());
            }
        } else {
            logger.info("Notification sent successfully: {}", notification.getId());
            // 標記為成功發送
            notification.markAsSent();
        }
        
        return success;
    }
    
    /**
     * 獲取使用案例統計資訊
     */
    public UseCaseStatistics getStatistics() {
        return new UseCaseStatistics(
            totalRemindersProcessed,
            successfulNotifications,
            failedNotifications,
            lastProcessingTime,
            getObserverCount()
        );
    }
    
    /**
     * 重置統計計數器
     */
    public void resetStatistics() {
        totalRemindersProcessed = 0;
        successfulNotifications = 0;
        failedNotifications = 0;
        logger.info("TaskReminderUseCase statistics reset");
    }
    
    // ==================== 內建觀察者實作 ====================
    
    /**
     * 通知觀察者 - 負責實際發送通知
     */
    private class NotificationObserver implements Observer<TaskReminderEvent> {
        
        @Override
        public void update(TaskReminderEvent event) {
            logger.debug("NotificationObserver received event: {}", event.getEventId());
            // 這裡可以添加額外的通知邏輯，比如記錄到資料庫
        }
        
        @Override
        public String getObserverId() {
            return "NotificationObserver";
        }
        
        @Override
        public boolean isInterestedIn(Class<?> eventType) {
            return TaskReminderEvent.class.isAssignableFrom(eventType);
        }
    }
    
    /**
     * 統計觀察者 - 負責收集統計資訊
     */
    private class StatisticsObserver implements Observer<TaskReminderEvent> {
        
        @Override
        public void update(TaskReminderEvent event) {
            logger.debug("StatisticsObserver received event: {}", event.getEventId());
            // 這裡可以添加統計邏輯，比如更新監控指標
        }
        
        @Override
        public String getObserverId() {
            return "StatisticsObserver";
        }
        
        @Override
        public boolean isInterestedIn(Class<?> eventType) {
            return TaskReminderEvent.class.isAssignableFrom(eventType);
        }
    }
    
    // ==================== 結果類別定義 ====================
    
    /**
     * 提醒處理結果
     */
    public static class ReminderResult {
        private final String eventId;
        private final String taskId;
        private final boolean success;
        private final String notificationId;
        private final LocalDateTime processedAt;
        private final String errorMessage;
        
        public ReminderResult(String eventId, String taskId, boolean success, 
                            String notificationId, LocalDateTime processedAt) {
            this(eventId, taskId, success, notificationId, processedAt, null);
        }
        
        public ReminderResult(String eventId, String taskId, boolean success, 
                            String notificationId, LocalDateTime processedAt, String errorMessage) {
            this.eventId = eventId;
            this.taskId = taskId;
            this.success = success;
            this.notificationId = notificationId;
            this.processedAt = processedAt;
            this.errorMessage = errorMessage;
        }
        
        // Getters
        public String getEventId() { return eventId; }
        public String getTaskId() { return taskId; }
        public boolean isSuccess() { return success; }
        public String getNotificationId() { return notificationId; }
        public LocalDateTime getProcessedAt() { return processedAt; }
        public String getErrorMessage() { return errorMessage; }
        
        @Override
        public String toString() {
            return String.format("ReminderResult{eventId='%s', taskId='%s', success=%s, processedAt=%s}",
                eventId, taskId, success, processedAt);
        }
    }
    
    /**
     * 多通道提醒處理結果
     */
    public static class MultiChannelReminderResult {
        private final String eventId;
        private final String taskId;
        private final List<Boolean> channelResults;
        private final long successCount;
        private final long failureCount;
        private final LocalDateTime processedAt;
        private final String errorMessage;
        
        public MultiChannelReminderResult(String eventId, String taskId, List<Boolean> channelResults,
                                        long successCount, long failureCount, LocalDateTime processedAt) {
            this(eventId, taskId, channelResults, successCount, failureCount, processedAt, null);
        }
        
        public MultiChannelReminderResult(String eventId, String taskId, List<Boolean> channelResults,
                                        long successCount, long failureCount, LocalDateTime processedAt,
                                        String errorMessage) {
            this.eventId = eventId;
            this.taskId = taskId;
            this.channelResults = List.copyOf(channelResults);
            this.successCount = successCount;
            this.failureCount = failureCount;
            this.processedAt = processedAt;
            this.errorMessage = errorMessage;
        }
        
        // Getters
        public String getEventId() { return eventId; }
        public String getTaskId() { return taskId; }
        public List<Boolean> getChannelResults() { return channelResults; }
        public long getSuccessCount() { return successCount; }
        public long getFailureCount() { return failureCount; }
        public LocalDateTime getProcessedAt() { return processedAt; }
        public String getErrorMessage() { return errorMessage; }
        public boolean isPartialSuccess() { return successCount > 0 && failureCount > 0; }
        public boolean isCompleteSuccess() { return successCount > 0 && failureCount == 0; }
        public boolean isCompleteFailure() { return successCount == 0 && failureCount > 0; }
        
        @Override
        public String toString() {
            return String.format("MultiChannelReminderResult{eventId='%s', taskId='%s', success=%d, failure=%d}",
                eventId, taskId, successCount, failureCount);
        }
    }
    
    /**
     * 使用案例統計資訊
     */
    public static class UseCaseStatistics {
        private final int totalProcessed;
        private final int successCount;
        private final int failureCount;
        private final LocalDateTime lastProcessingTime;
        private final int observerCount;
        
        public UseCaseStatistics(int totalProcessed, int successCount, int failureCount,
                               LocalDateTime lastProcessingTime, int observerCount) {
            this.totalProcessed = totalProcessed;
            this.successCount = successCount;
            this.failureCount = failureCount;
            this.lastProcessingTime = lastProcessingTime;
            this.observerCount = observerCount;
        }
        
        // Getters
        public int getTotalProcessed() { return totalProcessed; }
        public int getSuccessCount() { return successCount; }
        public int getFailureCount() { return failureCount; }
        public LocalDateTime getLastProcessingTime() { return lastProcessingTime; }
        public int getObserverCount() { return observerCount; }
        
        public double getSuccessRate() {
            if (totalProcessed == 0) return 0.0;
            return (double) successCount / totalProcessed * 100.0;
        }
        
        @Override
        public String toString() {
            return String.format("UseCaseStatistics{total=%d, success=%d, failure=%d, successRate=%.1f%%, observers=%d}",
                totalProcessed, successCount, failureCount, getSuccessRate(), observerCount);
        }
    }
}