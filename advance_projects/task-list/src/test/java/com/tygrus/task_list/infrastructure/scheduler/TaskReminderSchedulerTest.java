package com.tygrus.task_list.infrastructure.scheduler;

import com.tygrus.task_list.application.usecase.TaskReminderUseCase;
import com.tygrus.task_list.domain.event.TaskReminderEvent;
import com.tygrus.task_list.domain.model.*;
import com.tygrus.task_list.infrastructure.repository.TaskRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * TaskReminderScheduler 測試類別
 * 測試定時任務調度功能
 */
@DisplayName("TaskReminderScheduler 測試")
class TaskReminderSchedulerTest {
    
    private TaskReminderScheduler scheduler;
    
    @Mock
    private TaskRepository mockTaskRepository;
    
    @Mock
    private TaskReminderUseCase mockTaskReminderUseCase;
    
    private Task testTask;
    private List<Task> testTasks;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        scheduler = new TaskReminderScheduler(mockTaskRepository, mockTaskReminderUseCase);
        
        // 創建測試任務
        testTask = Task.builder()
            .id(TaskId.of("task-001"))
            .title("Test Task")
            .description("Test description")
            .priority(Priority.MEDIUM)
            .dueDate(LocalDateTime.now().plusHours(12)) // 12小時後到期
            .build();
        
        testTasks = List.of(testTask);
    }
    
    @Nested
    @DisplayName("調度器基本功能測試")
    class BasicFunctionalityTest {
        
        @Test
        @DisplayName("調度器預設應該是啟用的")
        void shouldBeEnabledByDefault() {
            // Then
            assertTrue(scheduler.isSchedulerEnabled());
        }
        
        @Test
        @DisplayName("應該能禁用調度器")
        void shouldAllowDisablingScheduler() {
            // When
            scheduler.disableScheduler();
            
            // Then
            assertFalse(scheduler.isSchedulerEnabled());
        }
        
        @Test
        @DisplayName("應該能重新啟用調度器")
        void shouldAllowReenablingScheduler() {
            // Given
            scheduler.disableScheduler();
            
            // When
            scheduler.enableScheduler();
            
            // Then
            assertTrue(scheduler.isSchedulerEnabled());
        }
        
        @Test
        @DisplayName("應該提供統計資訊")
        void shouldProvideStatistics() {
            // When
            TaskReminderScheduler.SchedulerStatistics stats = scheduler.getStatistics();
            
            // Then
            assertNotNull(stats);
            assertEquals(scheduler.isSchedulerEnabled(), stats.isEnabled());
            assertEquals(0, stats.getProcessedTasks()); // 初始狀態
            assertEquals(0, stats.getSentNotifications());
        }
        
        @Test
        @DisplayName("應該能重置統計資訊")
        void shouldResetStatistics() {
            // When
            scheduler.resetStatistics();
            
            // Then
            TaskReminderScheduler.SchedulerStatistics stats = scheduler.getStatistics();
            assertEquals(0, stats.getProcessedTasks());
            assertEquals(0, stats.getSentNotifications());
        }
    }
    
    @Nested
    @DisplayName("即將到期任務檢查測試")
    class UpcomingDueDateCheckTest {
        
        @Test
        @DisplayName("應該檢查即將到期的任務")
        void shouldCheckUpcomingDueTasks() throws Exception {
            // Given
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime tomorrow = now.plusHours(24);
            
            when(mockTaskRepository.findTasksWithDueDateBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(testTasks);
            when(mockTaskReminderUseCase.handleTaskReminder(any(TaskReminderEvent.class)))
                .thenReturn(CompletableFuture.completedFuture(
                    new TaskReminderUseCase.ReminderResult("event-1", "task-001", true, "notif-1", now)));
            
            // When
            scheduler.checkUpcomingDueDates();
            
            // Wait for async execution
            Thread.sleep(100);
            
            // Then
            verify(mockTaskRepository).findTasksWithDueDateBetween(
                argThat(time -> time.isAfter(now.minusMinutes(1)) && time.isBefore(now.plusMinutes(1))),
                argThat(time -> time.isAfter(tomorrow.minusMinutes(1)) && time.isBefore(tomorrow.plusMinutes(1)))
            );
            
            // 驗證提醒事件被處理
            ArgumentCaptor<TaskReminderEvent> eventCaptor = ArgumentCaptor.forClass(TaskReminderEvent.class);
            verify(mockTaskReminderUseCase, timeout(1000)).handleTaskReminder(eventCaptor.capture());
            
            TaskReminderEvent capturedEvent = eventCaptor.getValue();
            assertEquals(testTask, capturedEvent.getTask());
            assertEquals(TaskReminderEvent.ReminderReason.DUE_DATE_APPROACHING, capturedEvent.getReason());
        }
        
        @Test
        @DisplayName("禁用時不應該檢查即將到期的任務")
        void shouldNotCheckWhenDisabled() {
            // Given
            scheduler.disableScheduler();
            
            // When
            scheduler.checkUpcomingDueDates();
            
            // Then
            verify(mockTaskRepository, never()).findTasksWithDueDateBetween(any(), any());
            verify(mockTaskReminderUseCase, never()).handleTaskReminder(any());
        }
        
        @Test
        @DisplayName("應該忽略已完成的任務")
        void shouldIgnoreCompletedTasks() {
            // Given
            Task completedTask = Task.builder()
                .id(TaskId.of("completed-task"))
                .title("Completed Task")
                .description("Description")
                .priority(Priority.LOW)
                .dueDate(LocalDateTime.now().plusHours(1))
                .build();
            // 先轉到 IN_PROGRESS 再轉到 COMPLETED
            completedTask.updateStatus(TaskStatus.IN_PROGRESS);
            completedTask.updateStatus(TaskStatus.COMPLETED);
            
            when(mockTaskRepository.findTasksWithDueDateBetween(any(), any()))
                .thenReturn(List.of(completedTask));
            
            // When
            scheduler.checkUpcomingDueDates();
            
            // Then
            verify(mockTaskReminderUseCase, never()).handleTaskReminder(any());
        }
        
        @Test
        @DisplayName("應該根據優先級和時間選擇通知類型")
        void shouldSelectNotificationTypeBasedOnPriorityAndTime() throws Exception {
            // Given
            Task highPriorityTask = Task.builder()
                .id(TaskId.of("high-priority"))
                .title("High Priority Task")
                .description("Description")
                .priority(Priority.HIGH)
                .dueDate(LocalDateTime.now().plusHours(1)) // 1小時後到期
                .build();
            
            when(mockTaskRepository.findTasksWithDueDateBetween(any(), any()))
                .thenReturn(List.of(highPriorityTask));
            when(mockTaskReminderUseCase.handleTaskReminder(any()))
                .thenReturn(CompletableFuture.completedFuture(
                    new TaskReminderUseCase.ReminderResult("event-1", "high-priority", true, "notif-1", LocalDateTime.now())));
            
            // When
            scheduler.checkUpcomingDueDates();
            
            // Then
            ArgumentCaptor<TaskReminderEvent> eventCaptor = ArgumentCaptor.forClass(TaskReminderEvent.class);
            verify(mockTaskReminderUseCase, timeout(1000)).handleTaskReminder(eventCaptor.capture());
            
            TaskReminderEvent event = eventCaptor.getValue();
            // 高優先級且接近到期應該使用SMS
            assertTrue(event.getNotificationType() == NotificationType.SMS || 
                      event.getNotificationType() == NotificationType.PUSH);
        }
    }
    
    @Nested
    @DisplayName("逾期任務檢查測試")
    class OverdueTaskCheckTest {
        
        @Test
        @DisplayName("應該檢查逾期任務")
        void shouldCheckOverdueTasks() throws Exception {
            // Given
            Task overdueTask = Task.builder()
                .id(TaskId.of("overdue-task"))
                .title("Overdue Task")
                .description("Description")
                .priority(Priority.HIGH)
                .dueDate(LocalDateTime.now().minusHours(2)) // 2小時前就到期了
                .build();
            
            when(mockTaskRepository.findOverdueTasks(any(LocalDateTime.class)))
                .thenReturn(List.of(overdueTask));
            when(mockTaskReminderUseCase.handleTaskReminder(any()))
                .thenReturn(CompletableFuture.completedFuture(
                    new TaskReminderUseCase.ReminderResult("event-1", "overdue-task", true, "notif-1", LocalDateTime.now())));
            
            // When
            scheduler.checkOverdueTasks();
            
            // Then
            verify(mockTaskRepository).findOverdueTasks(any(LocalDateTime.class));
            
            ArgumentCaptor<TaskReminderEvent> eventCaptor = ArgumentCaptor.forClass(TaskReminderEvent.class);
            verify(mockTaskReminderUseCase, timeout(1000)).handleTaskReminder(eventCaptor.capture());
            
            TaskReminderEvent event = eventCaptor.getValue();
            assertEquals(overdueTask, event.getTask());
            assertEquals(TaskReminderEvent.ReminderReason.OVERDUE, event.getReason());
        }
        
        @Test
        @DisplayName("高優先級逾期任務應該使用SMS通知")
        void shouldUseSmsForHighPriorityOverdueTasks() throws Exception {
            // Given
            Task highPriorityOverdueTask = Task.builder()
                .id(TaskId.of("high-overdue"))
                .title("High Priority Overdue")
                .description("Description")
                .priority(Priority.HIGH)
                .dueDate(LocalDateTime.now().minusHours(1))
                .build();
            
            when(mockTaskRepository.findOverdueTasks(any()))
                .thenReturn(List.of(highPriorityOverdueTask));
            when(mockTaskReminderUseCase.handleTaskReminder(any()))
                .thenReturn(CompletableFuture.completedFuture(
                    new TaskReminderUseCase.ReminderResult("event-1", "high-overdue", true, "notif-1", LocalDateTime.now())));
            
            // When
            scheduler.checkOverdueTasks();
            
            // Then
            ArgumentCaptor<TaskReminderEvent> eventCaptor = ArgumentCaptor.forClass(TaskReminderEvent.class);
            verify(mockTaskReminderUseCase, timeout(1000)).handleTaskReminder(eventCaptor.capture());
            
            TaskReminderEvent event = eventCaptor.getValue();
            assertEquals(NotificationType.SMS, event.getNotificationType());
        }
        
        @Test
        @DisplayName("一般優先級逾期任務應該使用Email通知")
        void shouldUseEmailForNormalPriorityOverdueTasks() throws Exception {
            // Given
            Task normalOverdueTask = Task.builder()
                .id(TaskId.of("normal-overdue"))
                .title("Normal Overdue")
                .description("Description")
                .priority(Priority.MEDIUM)
                .dueDate(LocalDateTime.now().minusHours(1))
                .build();
            
            when(mockTaskRepository.findOverdueTasks(any()))
                .thenReturn(List.of(normalOverdueTask));
            when(mockTaskReminderUseCase.handleTaskReminder(any()))
                .thenReturn(CompletableFuture.completedFuture(
                    new TaskReminderUseCase.ReminderResult("event-1", "normal-overdue", true, "notif-1", LocalDateTime.now())));
            
            // When
            scheduler.checkOverdueTasks();
            
            // Then
            ArgumentCaptor<TaskReminderEvent> eventCaptor = ArgumentCaptor.forClass(TaskReminderEvent.class);
            verify(mockTaskReminderUseCase, timeout(1000)).handleTaskReminder(eventCaptor.capture());
            
            TaskReminderEvent event = eventCaptor.getValue();
            assertEquals(NotificationType.EMAIL, event.getNotificationType());
        }
    }
    
    @Nested
    @DisplayName("每日摘要測試")
    class DailySummaryTest {
        
        @Test
        @DisplayName("應該發送每日摘要")
        void shouldSendDailySummary() throws Exception {
            // Given
            when(mockTaskRepository.findByStatus(TaskStatus.TODO, TaskStatus.IN_PROGRESS))
                .thenReturn(testTasks);
            when(mockTaskReminderUseCase.handleTaskReminder(any()))
                .thenReturn(CompletableFuture.completedFuture(
                    new TaskReminderUseCase.ReminderResult("daily-1", "task-001", true, "notif-1", LocalDateTime.now())));
            
            // When
            scheduler.sendDailySummary();
            
            // Then
            verify(mockTaskRepository).findByStatus(TaskStatus.TODO, TaskStatus.IN_PROGRESS);
            
            ArgumentCaptor<TaskReminderEvent> eventCaptor = ArgumentCaptor.forClass(TaskReminderEvent.class);
            verify(mockTaskReminderUseCase, timeout(1000)).handleTaskReminder(eventCaptor.capture());
            
            TaskReminderEvent event = eventCaptor.getValue();
            assertEquals(TaskReminderEvent.ReminderReason.DAILY_SUMMARY, event.getReason());
            assertEquals(NotificationType.EMAIL, event.getNotificationType());
        }
        
        @Test
        @DisplayName("禁用時不應該發送每日摘要")
        void shouldNotSendDailySummaryWhenDisabled() {
            // Given
            scheduler.disableScheduler();
            
            // When
            scheduler.sendDailySummary();
            
            // Then
            verify(mockTaskRepository, never()).findByStatus(any());
            verify(mockTaskReminderUseCase, never()).handleTaskReminder(any());
        }
    }
    
    @Nested
    @DisplayName("每週摘要測試")
    class WeeklySummaryTest {
        
        @Test
        @DisplayName("應該發送每週摘要")
        void shouldSendWeeklySummary() throws Exception {
            // Given
            when(mockTaskRepository.findAll()).thenReturn(testTasks);
            when(mockTaskReminderUseCase.handleTaskReminder(any()))
                .thenReturn(CompletableFuture.completedFuture(
                    new TaskReminderUseCase.ReminderResult("weekly-1", "task-001", true, "notif-1", LocalDateTime.now())));
            
            // When
            scheduler.sendWeeklySummary();
            
            // Then
            verify(mockTaskRepository).findAll();
            
            ArgumentCaptor<TaskReminderEvent> eventCaptor = ArgumentCaptor.forClass(TaskReminderEvent.class);
            verify(mockTaskReminderUseCase, timeout(1000)).handleTaskReminder(eventCaptor.capture());
            
            TaskReminderEvent event = eventCaptor.getValue();
            assertEquals(TaskReminderEvent.ReminderReason.WEEKLY_SUMMARY, event.getReason());
            assertEquals(NotificationType.EMAIL, event.getNotificationType());
        }
    }
    
    @Nested
    @DisplayName("錯誤處理測試")
    class ErrorHandlingTest {
        
        @Test
        @DisplayName("資料庫異常不應該中斷調度器")
        void shouldHandleDatabaseExceptionsGracefully() {
            // Given
            when(mockTaskRepository.findTasksWithDueDateBetween(any(), any()))
                .thenThrow(new RuntimeException("Database connection failed"));
            
            // When & Then - 不應該拋出異常
            assertDoesNotThrow(() -> scheduler.checkUpcomingDueDates());
        }
        
        @Test
        @DisplayName("提醒處理異常不應該影響其他任務")
        void shouldContinueProcessingWhenReminderFails() {
            // Given
            Task task1 = Task.builder().id(TaskId.of("task-1")).title("Task 1").description("Desc").priority(Priority.LOW).dueDate(LocalDateTime.now().plusHours(1)).build();
            Task task2 = Task.builder().id(TaskId.of("task-2")).title("Task 2").description("Desc").priority(Priority.LOW).dueDate(LocalDateTime.now().plusHours(2)).build();
            
            when(mockTaskRepository.findTasksWithDueDateBetween(any(), any()))
                .thenReturn(List.of(task1, task2));
            when(mockTaskReminderUseCase.handleTaskReminder(any()))
                .thenThrow(new RuntimeException("Reminder failed"))
                .thenReturn(CompletableFuture.completedFuture(
                    new TaskReminderUseCase.ReminderResult("event-2", "task-2", true, "notif-2", LocalDateTime.now())));
            
            // When & Then
            assertDoesNotThrow(() -> scheduler.checkUpcomingDueDates());
            
            // 應該嘗試處理兩個任務
            verify(mockTaskReminderUseCase, times(2)).handleTaskReminder(any());
        }
        
        @Test
        @DisplayName("空任務清單應該正常處理")
        void shouldHandleEmptyTaskListGracefully() {
            // Given
            when(mockTaskRepository.findTasksWithDueDateBetween(any(), any()))
                .thenReturn(List.of());
            
            // When & Then
            assertDoesNotThrow(() -> scheduler.checkUpcomingDueDates());
            verify(mockTaskReminderUseCase, never()).handleTaskReminder(any());
        }
    }
    
    @Nested
    @DisplayName("異步處理測試")
    class AsynchronousProcessingTest {
        
        @Test
        @DisplayName("提醒處理應該是異步的")
        void shouldProcessRemindersAsynchronously() throws Exception {
            // Given
            when(mockTaskRepository.findTasksWithDueDateBetween(any(), any()))
                .thenReturn(testTasks);
            when(mockTaskReminderUseCase.handleTaskReminder(any()))
                .thenReturn(CompletableFuture.completedFuture(
                    new TaskReminderUseCase.ReminderResult("event-1", "task-001", true, "notif-1", LocalDateTime.now())));
            
            // When
            long startTime = System.currentTimeMillis();
            scheduler.checkUpcomingDueDates();
            long endTime = System.currentTimeMillis();
            
            // Then
            // 由於是異步處理，主執行緒應該很快返回
            assertTrue(endTime - startTime < 1000, "Should return quickly due to async processing");
            verify(mockTaskReminderUseCase, timeout(1000)).handleTaskReminder(any());
        }
    }
}