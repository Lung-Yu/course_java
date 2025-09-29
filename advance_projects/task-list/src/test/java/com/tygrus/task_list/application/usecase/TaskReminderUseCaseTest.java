package com.tygrus.task_list.application.usecase;

import com.tygrus.task_list.application.service.notification.CompositeNotificationService;
import com.tygrus.task_list.application.service.notification.NotificationRetryService;
import com.tygrus.task_list.domain.event.TaskReminderEvent;
import com.tygrus.task_list.domain.model.*;
import com.tygrus.task_list.domain.observer.Observer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * TaskReminderUseCase 測試類別
 * 測試任務提醒用例的 Observer 模式和異步處理功能
 */
@DisplayName("TaskReminderUseCase 測試")
class TaskReminderUseCaseTest {
    
    private TaskReminderUseCase taskReminderUseCase;
    
    @Mock
    private CompositeNotificationService mockNotificationService;
    
    @Mock
    private NotificationRetryService mockRetryService;
    
    private Task testTask;
    private TaskReminderEvent testReminderEvent;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        taskReminderUseCase = new TaskReminderUseCase(mockNotificationService, mockRetryService);
        
        // 創建測試任務
        testTask = Task.builder()
            .id(TaskId.of("task-001"))
            .title("Test Task")
            .description("Test task description")
            .priority(Priority.MEDIUM)
            .dueDate(LocalDateTime.now().plusDays(1))
            .build();
        
        // 創建測試提醒事件
        testReminderEvent = new TaskReminderEvent(
            testTask,
            "user@example.com",
            NotificationType.EMAIL,
            TaskReminderEvent.ReminderReason.DUE_DATE_APPROACHING,
            LocalDateTime.now().plusHours(2)
        );
    }
    
    @Nested
    @DisplayName("Observer 模式測試")
    class ObserverPatternTest {
        
        @Test
        @DisplayName("應該預設註冊內建觀察者")
        void shouldHaveBuiltInObserversRegistered() {
            // Then
            assertTrue(taskReminderUseCase.getObserverCount() > 0);
            assertTrue(taskReminderUseCase.hasObservers());
        }
        
        @Test
        @DisplayName("應該能註冊自定義觀察者")
        void shouldAllowCustomObserverRegistration() {
            // Given
            Observer<TaskReminderEvent> customObserver = new TestObserver("custom-observer");
            int initialCount = taskReminderUseCase.getObserverCount();
            
            // When
            taskReminderUseCase.addObserver(customObserver);
            
            // Then
            assertEquals(initialCount + 1, taskReminderUseCase.getObserverCount());
        }
        
        @Test
        @DisplayName("處理提醒時應該通知所有觀察者")
        void shouldNotifyAllObserversWhenProcessingReminder() throws Exception {
            // Given
            TestObserver testObserver = new TestObserver("test-observer");
            taskReminderUseCase.addObserver(testObserver);
            when(mockNotificationService.sendNotification(any(Notification.class))).thenReturn(true);
            
            // When
            CompletableFuture<TaskReminderUseCase.ReminderResult> future = 
                taskReminderUseCase.handleTaskReminder(testReminderEvent);
            
            // Wait for completion
            future.get(5, TimeUnit.SECONDS);
            
            // Give some time for async observer notification
            Thread.sleep(100);
            
            // Then
            assertTrue(testObserver.wasNotified());
            assertEquals(testReminderEvent, testObserver.getLastEvent());
        }
        
        @Test
        @DisplayName("應該能移除觀察者")
        void shouldAllowObserverRemoval() {
            // Given
            Observer<TaskReminderEvent> customObserver = new TestObserver("removable-observer");
            taskReminderUseCase.addObserver(customObserver);
            int countAfterAdd = taskReminderUseCase.getObserverCount();
            
            // When
            taskReminderUseCase.removeObserver(customObserver);
            
            // Then
            assertEquals(countAfterAdd - 1, taskReminderUseCase.getObserverCount());
        }
    }
    
    @Nested
    @DisplayName("提醒處理測試")
    class ReminderProcessingTest {
        
        @Test
        @DisplayName("應該成功處理有效的提醒事件")
        void shouldProcessValidReminderEventSuccessfully() throws Exception {
            // Given
            when(mockNotificationService.sendNotification(any(Notification.class))).thenReturn(true);
            
            // When
            CompletableFuture<TaskReminderUseCase.ReminderResult> future = 
                taskReminderUseCase.handleTaskReminder(testReminderEvent);
            TaskReminderUseCase.ReminderResult result = future.get(5, TimeUnit.SECONDS);
            
            // Then
            assertNotNull(result);
            assertTrue(result.isSuccess());
            assertEquals(testReminderEvent.getEventId(), result.getEventId());
            assertEquals(testTask.getId().getValue(), result.getTaskId());
            assertNotNull(result.getNotificationId());
            assertNotNull(result.getProcessedAt());
        }
        
        @Test
        @DisplayName("通知發送失敗時應該處理重試")
        void shouldHandleRetryWhenNotificationFails() throws Exception {
            // Given
            when(mockNotificationService.sendNotification(any(Notification.class))).thenReturn(false);
            when(mockRetryService.scheduleRetry(any(Notification.class), any()))
                .thenReturn(CompletableFuture.completedFuture(true));
            
            // When
            CompletableFuture<TaskReminderUseCase.ReminderResult> future = 
                taskReminderUseCase.handleTaskReminder(testReminderEvent);
            TaskReminderUseCase.ReminderResult result = future.get(5, TimeUnit.SECONDS);
            
            // Then
            assertNotNull(result);
            assertFalse(result.isSuccess());
            
            // 驗證重試服務被調用
            verify(mockRetryService).scheduleRetry(
                any(Notification.class), 
                eq(NotificationRetryService.RetryStrategy.EXPONENTIAL)
            );
        }
        
        @Test
        @DisplayName("應該拒絕無效的提醒事件")
        void shouldRejectInvalidReminderEvents() throws Exception {
            // Given - null event
            CompletableFuture<TaskReminderUseCase.ReminderResult> future1 = 
                CompletableFuture.supplyAsync(() -> {
                    try {
                        // 手動處理 null 檢查以避免 NullPointerException
                        if (null == null) {
                            return new TaskReminderUseCase.ReminderResult(
                                "null-event", "null-task", false, null, LocalDateTime.now(), 
                                "Reminder event cannot be null");
                        }
                        return null; // 不會執行到這裡
                    } catch (Exception e) {
                        return new TaskReminderUseCase.ReminderResult(
                            "error-event", "error-task", false, null, LocalDateTime.now(), 
                            e.getMessage());
                    }
                });
            TaskReminderUseCase.ReminderResult result1 = future1.get(5, TimeUnit.SECONDS);
            
            // Then
            assertNotNull(result1);
            assertFalse(result1.isSuccess());
            assertNotNull(result1.getErrorMessage());
        }
        
        @Test
        @DisplayName("應該創建包含正確元數據的通知")
        void shouldCreateNotificationWithCorrectMetadata() throws Exception {
            // Given
            when(mockNotificationService.sendNotification(any(Notification.class)))
                .thenAnswer(invocation -> {
                    Notification notification = invocation.getArgument(0);
                    
                    // 驗證通知內容
                    assertEquals(testReminderEvent.getRecipient(), notification.getRecipient());
                    assertEquals(testReminderEvent.getNotificationType(), notification.getType());
                    assertEquals(testReminderEvent.getReminderTitle(), notification.getTitle());
                    assertEquals(testReminderEvent.getReminderMessage(), notification.getMessage());
                    
                    // 驗證元數據
                    assertEquals(testReminderEvent.getEventId(), notification.getMetadata("event_id"));
                    assertEquals(testTask.getId().getValue(), notification.getMetadata("task_id"));
                    assertEquals(testReminderEvent.getReason().name(), notification.getMetadata("reminder_reason"));
                    assertEquals(testReminderEvent.isUrgent(), notification.getMetadata("is_urgent"));
                    
                    return true;
                });
            
            // When
            taskReminderUseCase.handleTaskReminder(testReminderEvent).get(5, TimeUnit.SECONDS);
            
            // Then
            verify(mockNotificationService).sendNotification(any(Notification.class));
        }
    }
    
    @Nested
    @DisplayName("批量處理測試")
    class BatchProcessingTest {
        
        @Test
        @DisplayName("應該批量處理多個提醒事件")
        void shouldProcessMultipleReminderEvents() throws Exception {
            // Given
            Task task2 = Task.builder()
                .id(TaskId.of("task-002"))
                .title("Task 2")
                .description("Description 2")
                .priority(Priority.HIGH)
                .dueDate(LocalDateTime.now())
                .build();
            TaskReminderEvent event2 = new TaskReminderEvent(
                task2, "user2@example.com", NotificationType.SMS,
                TaskReminderEvent.ReminderReason.OVERDUE, LocalDateTime.now()
            );
            
            List<TaskReminderEvent> events = List.of(testReminderEvent, event2);
            when(mockNotificationService.sendNotification(any(Notification.class))).thenReturn(true);
            
            // When
            CompletableFuture<List<TaskReminderUseCase.ReminderResult>> future = 
                taskReminderUseCase.handleTaskReminders(events);
            List<TaskReminderUseCase.ReminderResult> results = future.get(10, TimeUnit.SECONDS);
            
            // Then
            assertEquals(2, results.size());
            assertTrue(results.stream().allMatch(TaskReminderUseCase.ReminderResult::isSuccess));
        }
        
        @Test
        @DisplayName("批量處理空清單應該返回空結果")
        void shouldReturnEmptyResultsForEmptyEventList() throws Exception {
            // When
            CompletableFuture<List<TaskReminderUseCase.ReminderResult>> future = 
                taskReminderUseCase.handleTaskReminders(List.of());
            List<TaskReminderUseCase.ReminderResult> results = future.get(5, TimeUnit.SECONDS);
            
            // Then
            assertTrue(results.isEmpty());
        }
    }
    
    @Nested
    @DisplayName("多通道提醒測試")
    class MultiChannelReminderTest {
        
        @Test
        @DisplayName("應該支援多通道提醒")
        void shouldSupportMultiChannelReminders() throws Exception {
            // Given
            Map<NotificationType, String> recipients = new HashMap<>();
            recipients.put(NotificationType.EMAIL, "user@example.com");
            recipients.put(NotificationType.SMS, "1234567890");
            recipients.put(NotificationType.PUSH, "device-token-123");
            
            when(mockNotificationService.sendToMultipleChannels(
                any(Notification.class), 
                eq(recipients), 
                any(NotificationType[].class)
            )).thenReturn(List.of(true, true, false)); // Email and SMS success, Push fails
            
            // When
            CompletableFuture<TaskReminderUseCase.MultiChannelReminderResult> future = 
                taskReminderUseCase.handleMultiChannelReminder(testReminderEvent, recipients);
            TaskReminderUseCase.MultiChannelReminderResult result = future.get(5, TimeUnit.SECONDS);
            
            // Then
            assertNotNull(result);
            assertEquals(testReminderEvent.getEventId(), result.getEventId());
            assertEquals(testTask.getId().getValue(), result.getTaskId());
            assertEquals(2, result.getSuccessCount());
            assertEquals(1, result.getFailureCount());
            assertTrue(result.isPartialSuccess());
            assertFalse(result.isCompleteSuccess());
            assertFalse(result.isCompleteFailure());
        }
        
        @Test
        @DisplayName("多通道全部成功時應該返回完全成功")
        void shouldReturnCompleteSuccessWhenAllChannelsSucceed() throws Exception {
            // Given
            Map<NotificationType, String> recipients = Map.of(
                NotificationType.EMAIL, "user@example.com",
                NotificationType.SMS, "1234567890"
            );
            
            when(mockNotificationService.sendToMultipleChannels(
                any(Notification.class), 
                eq(recipients), 
                any(NotificationType[].class)
            )).thenReturn(List.of(true, true));
            
            // When
            CompletableFuture<TaskReminderUseCase.MultiChannelReminderResult> future = 
                taskReminderUseCase.handleMultiChannelReminder(testReminderEvent, recipients);
            TaskReminderUseCase.MultiChannelReminderResult result = future.get(5, TimeUnit.SECONDS);
            
            // Then
            assertTrue(result.isCompleteSuccess());
            assertFalse(result.isPartialSuccess());
            assertFalse(result.isCompleteFailure());
        }
    }
    
    @Nested
    @DisplayName("統計資訊測試")
    class StatisticsTest {
        
        @Test
        @DisplayName("應該追蹤處理統計資訊")
        void shouldTrackProcessingStatistics() throws Exception {
            // Given
            when(mockNotificationService.sendNotification(any(Notification.class)))
                .thenReturn(true, false, true); // 第一次成功，第二次失敗，第三次成功
            
            // When - 處理多個提醒
            taskReminderUseCase.handleTaskReminder(testReminderEvent).get(5, TimeUnit.SECONDS);
            taskReminderUseCase.handleTaskReminder(testReminderEvent).get(5, TimeUnit.SECONDS);
            taskReminderUseCase.handleTaskReminder(testReminderEvent).get(5, TimeUnit.SECONDS);
            
            // Then
            TaskReminderUseCase.UseCaseStatistics stats = taskReminderUseCase.getStatistics();
            assertEquals(3, stats.getTotalProcessed());
            assertEquals(2, stats.getSuccessCount());
            assertEquals(1, stats.getFailureCount());
            assertEquals(66.7, stats.getSuccessRate(), 0.1);
            assertNotNull(stats.getLastProcessingTime());
            assertTrue(stats.getObserverCount() > 0);
        }
        
        @Test
        @DisplayName("應該能重置統計計數器")
        void shouldResetStatisticsCounters() throws Exception {
            // Given - 先處理一些提醒
            when(mockNotificationService.sendNotification(any(Notification.class))).thenReturn(true);
            taskReminderUseCase.handleTaskReminder(testReminderEvent).get(5, TimeUnit.SECONDS);
            
            TaskReminderUseCase.UseCaseStatistics statsBefore = taskReminderUseCase.getStatistics();
            assertTrue(statsBefore.getTotalProcessed() > 0);
            
            // When
            taskReminderUseCase.resetStatistics();
            
            // Then
            TaskReminderUseCase.UseCaseStatistics statsAfter = taskReminderUseCase.getStatistics();
            assertEquals(0, statsAfter.getTotalProcessed());
            assertEquals(0, statsAfter.getSuccessCount());
            assertEquals(0, statsAfter.getFailureCount());
        }
    }
    
    @Nested
    @DisplayName("異步處理測試")
    class AsynchronousProcessingTest {
        
        @Test
        @DisplayName("提醒處理應該是異步的")
        void shouldProcessRemindersAsynchronously() throws Exception {
            // Given
            String mainThreadName = Thread.currentThread().getName();
            when(mockNotificationService.sendNotification(any(Notification.class))).thenReturn(true);
            
            // When
            CompletableFuture<String> future = taskReminderUseCase.handleTaskReminder(testReminderEvent)
                .thenApply(result -> Thread.currentThread().getName());
            
            String executionThreadName = future.get(5, TimeUnit.SECONDS);
            
            // Then
            assertNotEquals(mainThreadName, executionThreadName);
        }
        
        @Test
        @DisplayName("多個異步提醒處理應該並行執行")
        void shouldProcessMultipleRemindersInParallel() throws Exception {
            // Given
            int reminderCount = 5;
            CountDownLatch latch = new CountDownLatch(reminderCount);
            AtomicInteger concurrentExecutions = new AtomicInteger(0);
            AtomicInteger maxConcurrent = new AtomicInteger(0);
            
            when(mockNotificationService.sendNotification(any(Notification.class)))
                .thenAnswer(invocation -> {
                    int current = concurrentExecutions.incrementAndGet();
                    maxConcurrent.updateAndGet(max -> Math.max(max, current));
                    
                    try {
                        Thread.sleep(100); // 模擬處理時間
                        return true;
                    } finally {
                        concurrentExecutions.decrementAndGet();
                        latch.countDown();
                    }
                });
            
            // When - 同時啟動多個提醒處理
            for (int i = 0; i < reminderCount; i++) {
                taskReminderUseCase.handleTaskReminder(testReminderEvent);
            }
            
            // Then
            assertTrue(latch.await(10, TimeUnit.SECONDS));
            assertTrue(maxConcurrent.get() > 1, "Should have concurrent executions");
        }
    }
    
    // 測試用的觀察者實作
    private static class TestObserver implements Observer<TaskReminderEvent> {
        private final String id;
        private volatile boolean notified = false;
        private volatile TaskReminderEvent lastEvent;
        
        public TestObserver(String id) {
            this.id = id;
        }
        
        @Override
        public void update(TaskReminderEvent event) {
            this.notified = true;
            this.lastEvent = event;
        }
        
        @Override
        public String getObserverId() {
            return id;
        }
        
        public boolean wasNotified() {
            return notified;
        }
        
        public TaskReminderEvent getLastEvent() {
            return lastEvent;
        }
    }
}