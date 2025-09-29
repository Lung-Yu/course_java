package com.tygrus.task_list.application.service.notification;

import com.tygrus.task_list.domain.model.Notification;
import com.tygrus.task_list.domain.model.NotificationStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 通知重試服務
 * 處理失敗通知的重試邏輯，包含指數退避策略
 */
@Service
public class NotificationRetryService {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationRetryService.class);
    
    private final CompositeNotificationService notificationService;
    private final ScheduledExecutorService retryExecutor;
    private final Map<String, RetryTask> pendingRetries;
    
    // 重試配置
    private final int baseDelaySeconds = 5;      // 基礎延遲時間
    private final int maxDelaySeconds = 300;     // 最大延遲時間 (5分鐘)
    private final double backoffMultiplier = 2.0; // 指數退避倍數
    
    @Autowired
    public NotificationRetryService(CompositeNotificationService notificationService) {
        this.notificationService = notificationService;
        this.retryExecutor = Executors.newScheduledThreadPool(5);
        this.pendingRetries = new ConcurrentHashMap<>();
    }
    
    /**
     * 重試策略枚舉
     */
    public enum RetryStrategy {
        IMMEDIATE("立即重試"),
        LINEAR("線性退避"),
        EXPONENTIAL("指數退避"),
        FIXED_INTERVAL("固定間隔");
        
        private final String description;
        
        RetryStrategy(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    /**
     * 重試任務內部類別
     */
    private static class RetryTask {
        private final Notification notification;
        private final RetryStrategy strategy;
        private final LocalDateTime scheduledTime;
        private volatile boolean cancelled = false;
        
        public RetryTask(Notification notification, RetryStrategy strategy, LocalDateTime scheduledTime) {
            this.notification = notification;
            this.strategy = strategy;
            this.scheduledTime = scheduledTime;
        }
        
        public Notification getNotification() { return notification; }
        public RetryStrategy getStrategy() { return strategy; }
        public LocalDateTime getScheduledTime() { return scheduledTime; }
        public boolean isCancelled() { return cancelled; }
        public void cancel() { this.cancelled = true; }
    }
    
    /**
     * 安排重試任務
     * 
     * @param notification 需要重試的通知
     * @param strategy 重試策略
     * @return 重試任務的Future
     */
    public CompletableFuture<Boolean> scheduleRetry(Notification notification, RetryStrategy strategy) {
        if (notification == null || !notification.needsRetry()) {
            return CompletableFuture.completedFuture(false);
        }
        
        // 檢查是否已經有待處理的重試任務
        String notificationId = notification.getId();
        if (pendingRetries.containsKey(notificationId)) {
            logger.warn("Retry already scheduled for notification: {}", notificationId);
            return CompletableFuture.completedFuture(false);
        }
        
        // 計算重試延遲
        long delaySeconds = calculateRetryDelay(notification, strategy);
        LocalDateTime scheduledTime = LocalDateTime.now().plusSeconds(delaySeconds);
        
        // 創建重試任務
        RetryTask retryTask = new RetryTask(notification, strategy, scheduledTime);
        pendingRetries.put(notificationId, retryTask);
        
        logger.info("Scheduled retry for notification {} in {} seconds using {} strategy", 
            notificationId, delaySeconds, strategy);
        
        // 安排執行重試
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        retryExecutor.schedule(() -> {
            executeRetry(retryTask, future);
        }, delaySeconds, TimeUnit.SECONDS);
        
        return future;
    }
    
    /**
     * 執行重試邏輯
     */
    private void executeRetry(RetryTask retryTask, CompletableFuture<Boolean> future) {
        Notification notification = retryTask.getNotification();
        String notificationId = notification.getId();
        
        try {
            // 移除待處理的重試任務
            pendingRetries.remove(notificationId);
            
            // 檢查任務是否被取消
            if (retryTask.isCancelled()) {
                logger.info("Retry task cancelled for notification: {}", notificationId);
                future.complete(false);
                return;
            }
            
            // 檢查通知是否仍需要重試
            if (!notification.needsRetry()) {
                logger.info("Notification {} no longer needs retry", notificationId);
                future.complete(false);
                return;
            }
            
            // 嘗試重試
            boolean retrySuccess = notification.retry();
            if (!retrySuccess) {
                logger.warn("Cannot retry notification {} - max retries exceeded", notificationId);
                future.complete(false);
                return;
            }
            
            logger.info("Executing retry #{} for notification: {}", 
                notification.getRetryCount(), notificationId);
            
            // 發送通知
            boolean sendSuccess = notificationService.sendNotification(notification);
            
            if (sendSuccess) {
                logger.info("Retry successful for notification: {}", notificationId);
                future.complete(true);
            } else {
                logger.warn("Retry failed for notification: {}", notificationId);
                
                // 如果還可以重試，安排下一次重試
                if (notification.needsRetry()) {
                    scheduleRetry(notification, retryTask.getStrategy())
                        .whenComplete((result, throwable) -> {
                            if (throwable != null) {
                                future.completeExceptionally(throwable);
                            } else {
                                future.complete(result);
                            }
                        });
                } else {
                    future.complete(false);
                }
            }
            
        } catch (Exception e) {
            logger.error("Error during retry execution for notification {}: {}", 
                notificationId, e.getMessage(), e);
            
            // 標記為失敗並完成Future
            notification.markAsFailed("Retry execution error: " + e.getMessage());
            future.completeExceptionally(e);
        }
    }
    
    /**
     * 計算重試延遲時間
     */
    private long calculateRetryDelay(Notification notification, RetryStrategy strategy) {
        int retryCount = notification.getRetryCount();
        
        return switch (strategy) {
            case IMMEDIATE -> 0;
            case LINEAR -> Math.min(baseDelaySeconds * (retryCount + 1), maxDelaySeconds);
            case EXPONENTIAL -> Math.min(
                (long) (baseDelaySeconds * Math.pow(backoffMultiplier, retryCount)), 
                maxDelaySeconds);
            case FIXED_INTERVAL -> baseDelaySeconds;
        };
    }
    
    /**
     * 批量安排重試
     */
    public List<CompletableFuture<Boolean>> scheduleRetries(
            List<Notification> notifications, RetryStrategy strategy) {
        
        List<CompletableFuture<Boolean>> futures = new ArrayList<>();
        
        for (Notification notification : notifications) {
            CompletableFuture<Boolean> future = scheduleRetry(notification, strategy);
            futures.add(future);
        }
        
        return futures;
    }
    
    /**
     * 取消通知的重試任務
     */
    public boolean cancelRetry(String notificationId) {
        RetryTask task = pendingRetries.get(notificationId);
        if (task != null) {
            task.cancel();
            pendingRetries.remove(notificationId);
            logger.info("Cancelled retry for notification: {}", notificationId);
            return true;
        }
        return false;
    }
    
    /**
     * 取消所有重試任務
     */
    public void cancelAllRetries() {
        int cancelledCount = pendingRetries.size();
        
        for (RetryTask task : pendingRetries.values()) {
            task.cancel();
        }
        
        pendingRetries.clear();
        logger.info("Cancelled {} pending retry tasks", cancelledCount);
    }
    
    /**
     * 獲取待處理的重試任務數量
     */
    public int getPendingRetryCount() {
        return pendingRetries.size();
    }
    
    /**
     * 獲取特定通知的重試狀態
     */
    public RetryStatus getRetryStatus(String notificationId) {
        RetryTask task = pendingRetries.get(notificationId);
        if (task != null) {
            return new RetryStatus(
                notificationId,
                task.getStrategy(),
                task.getScheduledTime(),
                task.isCancelled()
            );
        }
        return null;
    }
    
    /**
     * 獲取所有重試狀態
     */
    public List<RetryStatus> getAllRetryStatuses() {
        return pendingRetries.entrySet().stream()
            .map(entry -> new RetryStatus(
                entry.getKey(),
                entry.getValue().getStrategy(),
                entry.getValue().getScheduledTime(),
                entry.getValue().isCancelled()
            ))
            .toList();
    }
    
    /**
     * 重試狀態資訊類別
     */
    public static class RetryStatus {
        private final String notificationId;
        private final RetryStrategy strategy;
        private final LocalDateTime scheduledTime;
        private final boolean cancelled;
        
        public RetryStatus(String notificationId, RetryStrategy strategy, 
                          LocalDateTime scheduledTime, boolean cancelled) {
            this.notificationId = notificationId;
            this.strategy = strategy;
            this.scheduledTime = scheduledTime;
            this.cancelled = cancelled;
        }
        
        public String getNotificationId() { return notificationId; }
        public RetryStrategy getStrategy() { return strategy; }
        public LocalDateTime getScheduledTime() { return scheduledTime; }
        public boolean isCancelled() { return cancelled; }
        
        public Duration getTimeUntilExecution() {
            return Duration.between(LocalDateTime.now(), scheduledTime);
        }
        
        @Override
        public String toString() {
            return String.format("RetryStatus{id='%s', strategy=%s, scheduledTime=%s, cancelled=%s}",
                notificationId, strategy, scheduledTime, cancelled);
        }
    }
    
    /**
     * 關閉重試服務
     */
    public void shutdown() {
        cancelAllRetries();
        retryExecutor.shutdown();
        
        try {
            if (!retryExecutor.awaitTermination(60, TimeUnit.SECONDS)) {
                retryExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            retryExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        
        logger.info("Notification retry service shutdown completed");
    }
}