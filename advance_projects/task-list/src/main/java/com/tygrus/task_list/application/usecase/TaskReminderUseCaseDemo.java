package com.tygrus.task_list.application.usecase;

import com.tygrus.task_list.application.service.notification.*;
import com.tygrus.task_list.domain.event.TaskReminderEvent;
import com.tygrus.task_list.domain.model.*;
import com.tygrus.task_list.domain.observer.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * UC-009 TaskReminderUseCase 演示程式
 * 
 * 展示 Observer 模式、異步通知處理、多通道通知等功能
 * 這是一個完整的演示，展示了：
 * 1. Observer 設計模式的實作
 * 2. 多種通知服務 (Email, SMS, Push)
 * 3. 異步通知處理
 * 4. 通知重試機制
 * 5. 多通道通知
 */
public class TaskReminderUseCaseDemo {
    
    private static final Logger logger = LoggerFactory.getLogger(TaskReminderUseCaseDemo.class);
    
    public static void main(String[] args) {
        System.out.println("=".repeat(80));
        System.out.println("UC-009 TaskReminderUseCase 演示程式");
        System.out.println("展示 Observer 模式和異步通知處理");
        System.out.println("=".repeat(80));
        
        try {
            TaskReminderUseCaseDemo demo = new TaskReminderUseCaseDemo();
            demo.runDemo();
        } catch (Exception e) {
            logger.error("演示程式執行失敗: {}", e.getMessage(), e);
            System.exit(1);
        }
    }
    
    public void runDemo() throws Exception {
        // 1. 初始化通知服務
        System.out.println("\n1. 初始化通知服務...");
        NotificationServiceFactory serviceFactory = createNotificationServices();
        CompositeNotificationService compositeService = new CompositeNotificationService(serviceFactory);
        NotificationRetryService retryService = new NotificationRetryService(compositeService);
        
        // 2. 創建 TaskReminderUseCase
        System.out.println("2. 創建 TaskReminderUseCase...");
        TaskReminderUseCase useCase = new TaskReminderUseCase(compositeService, retryService);
        
        // 3. 註冊自定義觀察者
        System.out.println("3. 註冊自定義觀察者...");
        useCase.addObserver(new DemoObserver("DemoObserver"));
        System.out.printf("   總觀察者數量: %d%n", useCase.getObserverCount());
        
        // 4. 創建測試任務
        System.out.println("4. 創建測試任務...");
        List<Task> testTasks = createTestTasks();
        testTasks.forEach(task -> 
            System.out.printf("   - %s (優先級: %s, 到期: %s)%n", 
                task.getTitle(), task.getPriority().getDisplayName(), task.getDueDate()));
        
        // 5. 演示單一通知處理
        System.out.println("\n5. 演示單一通知處理...");
        demonstrateSingleNotification(useCase, testTasks.get(0));
        
        // 6. 演示批量通知處理
        System.out.println("\n6. 演示批量通知處理...");
        demonstrateBatchNotification(useCase, testTasks);
        
        // 7. 演示多通道通知
        System.out.println("\n7. 演示多通道通知...");
        demonstrateMultiChannelNotification(useCase, testTasks.get(0));
        
        // 8. 演示重試機制
        System.out.println("\n8. 演示重試機制...");
        demonstrateRetryMechanism(retryService);
        
        // 9. 顯示統計資訊
        System.out.println("\n9. 統計資訊...");
        displayStatistics(useCase, compositeService);
        
        // 10. 清理資源
        System.out.println("\n10. 清理資源...");
        retryService.shutdown();
        
        System.out.println("\n" + "=".repeat(80));
        System.out.println("演示完成！");
        System.out.println("=".repeat(80));
    }
    
    private NotificationServiceFactory createNotificationServices() {
        EmailNotificationService emailService = new EmailNotificationService();
        SmsNotificationService smsService = new SmsNotificationService();
        PushNotificationService pushService = new PushNotificationService();
        
        // 註冊一些測試裝置
        pushService.registerDevice("demo-device-ios");
        pushService.registerDevice("demo-device-android");
        
        return new NotificationServiceFactory(emailService, smsService, pushService);
    }
    
    private List<Task> createTestTasks() {
        List<Task> tasks = new ArrayList<>();
        
        // 即將到期的高優先級任務
        tasks.add(Task.builder()
            .id(TaskId.of("task-urgent"))
            .title("緊急系統維護")
            .description("資料庫伺服器需要緊急維護，請儘快處理")
            .priority(Priority.HIGH)
            .dueDate(LocalDateTime.now().plusHours(2))
            .build());
        
        // 一般優先級任務
        tasks.add(Task.builder()
            .id(TaskId.of("task-normal"))
            .title("撰寫月度報告")
            .description("完成本月度的工作總結報告")
            .priority(Priority.MEDIUM)
            .dueDate(LocalDateTime.now().plusDays(1))
            .build());
        
        // 已逾期的任務
        tasks.add(Task.builder()
            .id(TaskId.of("task-overdue"))
            .title("客戶會議準備")
            .description("準備下週客戶會議的簡報資料")
            .priority(Priority.HIGH)
            .dueDate(LocalDateTime.now().minusHours(6))
            .build());
        
        return tasks;
    }
    
    private void demonstrateSingleNotification(TaskReminderUseCase useCase, Task task) throws Exception {
        TaskReminderEvent event = new TaskReminderEvent(
            task,
            "demo@example.com",
            NotificationType.EMAIL,
            TaskReminderEvent.ReminderReason.DUE_DATE_APPROACHING,
            LocalDateTime.now().plusHours(2)
        );
        
        System.out.printf("   發送提醒: %s%n", event.getReminderTitle());
        
        CompletableFuture<TaskReminderUseCase.ReminderResult> future = useCase.handleTaskReminder(event);
        TaskReminderUseCase.ReminderResult result = future.get(10, TimeUnit.SECONDS);
        
        System.out.printf("   結果: %s (通知ID: %s)%n", 
            result.isSuccess() ? "成功" : "失敗", result.getNotificationId());
    }
    
    private void demonstrateBatchNotification(TaskReminderUseCase useCase, List<Task> tasks) throws Exception {
        List<TaskReminderEvent> events = new ArrayList<>();
        
        for (Task task : tasks) {
            TaskReminderEvent.ReminderReason reason = task.getDueDate().isBefore(LocalDateTime.now()) ?
                TaskReminderEvent.ReminderReason.OVERDUE : TaskReminderEvent.ReminderReason.DUE_DATE_APPROACHING;
            
            TaskReminderEvent event = new TaskReminderEvent(
                task,
                "batch@example.com",
                NotificationType.EMAIL,
                reason,
                task.getDueDate()
            );
            events.add(event);
        }
        
        System.out.printf("   批量處理 %d 個提醒事件...%n", events.size());
        
        CompletableFuture<List<TaskReminderUseCase.ReminderResult>> future = 
            useCase.handleTaskReminders(events);
        List<TaskReminderUseCase.ReminderResult> results = future.get(15, TimeUnit.SECONDS);
        
        long successCount = results.stream().mapToLong(r -> r.isSuccess() ? 1 : 0).sum();
        System.out.printf("   結果: %d 成功, %d 失敗%n", successCount, results.size() - successCount);
    }
    
    private void demonstrateMultiChannelNotification(TaskReminderUseCase useCase, Task task) throws Exception {
        Map<NotificationType, String> recipients = new HashMap<>();
        recipients.put(NotificationType.EMAIL, "multi@example.com");
        recipients.put(NotificationType.SMS, "0912345678");
        recipients.put(NotificationType.PUSH, "demo-device-android");
        
        TaskReminderEvent event = new TaskReminderEvent(
            task,
            "multi@example.com", // 主要接收者
            NotificationType.EMAIL,
            TaskReminderEvent.ReminderReason.STATUS_CHANGE,
            LocalDateTime.now()
        );
        
        System.out.printf("   多通道通知: %s%n", event.getReminderTitle());
        System.out.printf("   通道: Email, SMS, Push%n");
        
        CompletableFuture<TaskReminderUseCase.MultiChannelReminderResult> future =
            useCase.handleMultiChannelReminder(event, recipients);
        TaskReminderUseCase.MultiChannelReminderResult result = future.get(15, TimeUnit.SECONDS);
        
        System.out.printf("   結果: %d 成功, %d 失敗%n", result.getSuccessCount(), result.getFailureCount());
        
        if (result.isPartialSuccess()) {
            System.out.println("   狀態: 部分成功");
        } else if (result.isCompleteSuccess()) {
            System.out.println("   狀態: 完全成功");
        } else {
            System.out.println("   狀態: 完全失敗");
        }
    }
    
    private void demonstrateRetryMechanism(NotificationRetryService retryService) throws Exception {
        // 創建一個可能失敗的通知
        Notification failingNotification = new Notification(
            "retry@example.com",
            NotificationType.EMAIL,
            "重試測試",
            "這是一個測試重試機制的通知"
        );
        
        // 手動標記為失敗以演示重試
        failingNotification.markAsSending();
        failingNotification.markAsFailed("模擬發送失敗");
        
        System.out.printf("   通知 %s 發送失敗，開始重試...%n", failingNotification.getId());
        System.out.printf("   重試次數: %d/%d%n", 
            failingNotification.getRetryCount(), failingNotification.getMaxRetries());
        
        // 安排重試
        CompletableFuture<Boolean> retryFuture = retryService.scheduleRetry(
            failingNotification, 
            NotificationRetryService.RetryStrategy.EXPONENTIAL
        );
        
        System.out.println("   重試已安排，使用指數退避策略");
        System.out.printf("   待處理重試任務數量: %d%n", retryService.getPendingRetryCount());
        
        // 等待一段時間讓重試執行
        Thread.sleep(6000); // 等待6秒
        
        System.out.printf("   重試後狀態: %s%n", failingNotification.getStatus());
    }
    
    private void displayStatistics(TaskReminderUseCase useCase, CompositeNotificationService compositeService) {
        TaskReminderUseCase.UseCaseStatistics useCaseStats = useCase.getStatistics();
        NotificationServiceFactory.ServiceStatistics serviceStats = compositeService.getStatistics();
        Map<NotificationType, Boolean> healthStatus = compositeService.getHealthReport();
        
        System.out.println("   === UseCase 統計 ===");
        System.out.printf("   總處理數: %d%n", useCaseStats.getTotalProcessed());
        System.out.printf("   成功數: %d%n", useCaseStats.getSuccessCount());
        System.out.printf("   失敗數: %d%n", useCaseStats.getFailureCount());
        System.out.printf("   成功率: %.1f%%%n", useCaseStats.getSuccessRate());
        System.out.printf("   觀察者數: %d%n", useCaseStats.getObserverCount());
        
        System.out.println("\n   === 服務統計 ===");
        System.out.printf("   總服務數: %d%n", serviceStats.getTotalServices());
        System.out.printf("   健康服務數: %d%n", serviceStats.getHealthyServices());
        System.out.printf("   健康率: %.1f%%%n", serviceStats.getHealthyPercentage());
        
        System.out.println("\n   === 服務健康狀態 ===");
        healthStatus.forEach((type, healthy) -> 
            System.out.printf("   %s: %s%n", type.getDisplayName(), healthy ? "健康" : "異常"));
    }
    
    /**
     * 演示用的自定義觀察者
     */
    private static class DemoObserver implements Observer<TaskReminderEvent> {
        private final String id;
        private int eventCount = 0;
        
        public DemoObserver(String id) {
            this.id = id;
        }
        
        @Override
        public void update(TaskReminderEvent event) {
            eventCount++;
            System.out.printf("   [%s] 接收到事件 #%d: %s (%s)%n", 
                id, eventCount, event.getTask().getTitle(), event.getReason().getDescription());
        }
        
        @Override
        public String getObserverId() {
            return id;
        }
        
        @Override
        public boolean isInterestedIn(Class<?> eventType) {
            return TaskReminderEvent.class.isAssignableFrom(eventType);
        }
    }
}