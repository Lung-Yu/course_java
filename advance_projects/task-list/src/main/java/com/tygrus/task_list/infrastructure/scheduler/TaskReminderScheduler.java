package com.tygrus.task_list.infrastructure.scheduler;

import com.tygrus.task_list.application.usecase.TaskReminderUseCase;
import com.tygrus.task_list.domain.event.TaskReminderEvent;
import com.tygrus.task_list.domain.model.Task;
import com.tygrus.task_list.domain.model.TaskStatus;
import com.tygrus.task_list.domain.model.Priority;
import com.tygrus.task_list.domain.model.NotificationType;
import com.tygrus.task_list.infrastructure.repository.TaskRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 任務提醒調度器
 * 使用 Spring @Scheduled 註解實作定時任務檢查機制
 */
@Component
public class TaskReminderScheduler {
    
    private static final Logger logger = LoggerFactory.getLogger(TaskReminderScheduler.class);
    
    private final TaskRepository taskRepository;
    private final TaskReminderUseCase taskReminderUseCase;
    
    // 調度器配置
    private boolean schedulerEnabled = true;
    private LocalDateTime lastExecutionTime;
    private int processedTaskCount = 0;
    private int sentNotificationCount = 0;
    
    @Autowired
    public TaskReminderScheduler(TaskRepository taskRepository, TaskReminderUseCase taskReminderUseCase) {
        this.taskRepository = taskRepository;
        this.taskReminderUseCase = taskReminderUseCase;
    }
    
    /**
     * 每5分鐘檢查即將到期的任務
     * cron表達式: 秒 分 時 日 月 周
     */
    @Scheduled(cron = "0 */5 * * * *")
    public void checkUpcomingDueDates() {
        if (!schedulerEnabled) {
            return;
        }
        
        logger.info("Starting upcoming due date check...");
        lastExecutionTime = LocalDateTime.now();
        
        try {
            // 查找未來24小時內到期的任務
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime tomorrow = now.plusHours(24);
            
            List<Task> upcomingTasks = taskRepository.findTasksWithDueDateBetween(now, tomorrow);
            logger.debug("Found {} tasks with upcoming due dates", upcomingTasks.size());
            
            for (Task task : upcomingTasks) {
                processUpcomingTask(task);
            }
            
            logger.info("Completed upcoming due date check - processed {} tasks", upcomingTasks.size());
            
        } catch (Exception e) {
            logger.error("Error during upcoming due date check: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 每小時檢查逾期任務
     */
    @Scheduled(cron = "0 0 * * * *")
    public void checkOverdueTasks() {
        if (!schedulerEnabled) {
            return;
        }
        
        logger.info("Starting overdue task check...");
        
        try {
            LocalDateTime now = LocalDateTime.now();
            List<Task> overdueTasks = taskRepository.findOverdueTasks(now);
            logger.debug("Found {} overdue tasks", overdueTasks.size());
            
            for (Task task : overdueTasks) {
                processOverdueTask(task);
            }
            
            logger.info("Completed overdue task check - processed {} tasks", overdueTasks.size());
            
        } catch (Exception e) {
            logger.error("Error during overdue task check: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 每天早上8點發送每日摘要
     */
    @Scheduled(cron = "0 0 8 * * *")
    public void sendDailySummary() {
        if (!schedulerEnabled) {
            return;
        }
        
        logger.info("Starting daily summary generation...");
        
        try {
            List<Task> activeTasks = taskRepository.findByStatus(TaskStatus.TODO, TaskStatus.IN_PROGRESS);
            logger.debug("Found {} active tasks for daily summary", activeTasks.size());
            
            for (Task task : activeTasks) {
                processDailySummary(task);
            }
            
            logger.info("Completed daily summary generation - processed {} tasks", activeTasks.size());
            
        } catch (Exception e) {
            logger.error("Error during daily summary generation: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 每週一早上9點發送每週摘要
     */
    @Scheduled(cron = "0 0 9 * * MON")
    public void sendWeeklySummary() {
        if (!schedulerEnabled) {
            return;
        }
        
        logger.info("Starting weekly summary generation...");
        
        try {
            List<Task> allTasks = taskRepository.findAll();
            logger.debug("Found {} tasks for weekly summary", allTasks.size());
            
            for (Task task : allTasks) {
                processWeeklySummary(task);
            }
            
            logger.info("Completed weekly summary generation - processed {} tasks", allTasks.size());
            
        } catch (Exception e) {
            logger.error("Error during weekly summary generation: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 每30秒處理失敗的通知重試
     */
    @Scheduled(fixedRate = 30000)
    public void processFailedNotifications() {
        if (!schedulerEnabled) {
            return;
        }
        
        try {
            // 這裡會處理需要重試的通知
            // 實際實作中可能需要查詢通知資料庫
            logger.trace("Processing failed notification retries...");
            
        } catch (Exception e) {
            logger.error("Error processing failed notifications: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 處理即將到期的任務
     */
    private void processUpcomingTask(Task task) {
        try {
            if (task.getDueDate() == null || task.getStatus() == TaskStatus.COMPLETED) {
                return;
            }
            
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime dueDate = task.getDueDate();
            long hoursUntilDue = ChronoUnit.HOURS.between(now, dueDate);
            
            // 根據優先級和剩餘時間決定通知方式
            NotificationType notificationType = determineNotificationType(task, hoursUntilDue);
            
            // 創建提醒事件
            TaskReminderEvent reminderEvent = new TaskReminderEvent(
                task,
                getTaskOwnerEmail(task), // 這裡應該從任務中獲取擁有者資訊
                notificationType,
                TaskReminderEvent.ReminderReason.DUE_DATE_APPROACHING,
                dueDate
            );
            
            // 異步處理提醒
            CompletableFuture.runAsync(() -> {
                try {
                    taskReminderUseCase.handleTaskReminder(reminderEvent);
                    processedTaskCount++;
                    sentNotificationCount++;
                    logger.debug("Sent upcoming due date reminder for task: {}", task.getId().getValue());
                } catch (Exception e) {
                    logger.error("Failed to send upcoming reminder for task {}: {}", 
                        task.getId().getValue(), e.getMessage(), e);
                }
            });
            
        } catch (Exception e) {
            logger.error("Error processing upcoming task {}: {}", task.getId().getValue(), e.getMessage(), e);
        }
    }
    
    /**
     * 處理逾期任務
     */
    private void processOverdueTask(Task task) {
        try {
            if (task.getStatus() == TaskStatus.COMPLETED) {
                return;
            }
            
            // 逾期任務使用更緊急的通知方式
            NotificationType notificationType = task.getPriority() == Priority.HIGH 
                ? NotificationType.SMS : NotificationType.EMAIL;
            
            TaskReminderEvent reminderEvent = new TaskReminderEvent(
                task,
                getTaskOwnerEmail(task),
                notificationType,
                TaskReminderEvent.ReminderReason.OVERDUE,
                task.getDueDate() != null ? task.getDueDate() : LocalDateTime.now()
            );
            
            CompletableFuture.runAsync(() -> {
                try {
                    taskReminderUseCase.handleTaskReminder(reminderEvent);
                    processedTaskCount++;
                    sentNotificationCount++;
                    logger.debug("Sent overdue reminder for task: {}", task.getId().getValue());
                } catch (Exception e) {
                    logger.error("Failed to send overdue reminder for task {}: {}", 
                        task.getId().getValue(), e.getMessage(), e);
                }
            });
            
        } catch (Exception e) {
            logger.error("Error processing overdue task {}: {}", task.getId().getValue(), e.getMessage(), e);
        }
    }
    
    /**
     * 處理每日摘要
     */
    private void processDailySummary(Task task) {
        try {
            TaskReminderEvent reminderEvent = new TaskReminderEvent(
                task,
                getTaskOwnerEmail(task),
                NotificationType.EMAIL,
                TaskReminderEvent.ReminderReason.DAILY_SUMMARY,
                LocalDateTime.now()
            );
            
            CompletableFuture.runAsync(() -> {
                try {
                    taskReminderUseCase.handleTaskReminder(reminderEvent);
                    processedTaskCount++;
                    sentNotificationCount++;
                    logger.debug("Sent daily summary for task: {}", task.getId().getValue());
                } catch (Exception e) {
                    logger.error("Failed to send daily summary for task {}: {}", 
                        task.getId().getValue(), e.getMessage(), e);
                }
            });
            
        } catch (Exception e) {
            logger.error("Error processing daily summary for task {}: {}", task.getId().getValue(), e.getMessage(), e);
        }
    }
    
    /**
     * 處理每週摘要
     */
    private void processWeeklySummary(Task task) {
        try {
            TaskReminderEvent reminderEvent = new TaskReminderEvent(
                task,
                getTaskOwnerEmail(task),
                NotificationType.EMAIL,
                TaskReminderEvent.ReminderReason.WEEKLY_SUMMARY,
                LocalDateTime.now()
            );
            
            CompletableFuture.runAsync(() -> {
                try {
                    taskReminderUseCase.handleTaskReminder(reminderEvent);
                    processedTaskCount++;
                    sentNotificationCount++;
                    logger.debug("Sent weekly summary for task: {}", task.getId().getValue());
                } catch (Exception e) {
                    logger.error("Failed to send weekly summary for task {}: {}", 
                        task.getId().getValue(), e.getMessage(), e);
                }
            });
            
        } catch (Exception e) {
            logger.error("Error processing weekly summary for task {}: {}", task.getId().getValue(), e.getMessage(), e);
        }
    }
    
    /**
     * 根據任務和時間決定通知類型
     */
    private NotificationType determineNotificationType(Task task, long hoursUntilDue) {
        if (hoursUntilDue <= 2 && task.getPriority() == Priority.HIGH) {
            return NotificationType.SMS; // 緊急情況使用簡訊
        } else if (hoursUntilDue <= 1) {
            return NotificationType.PUSH; // 1小時內使用推播
        } else {
            return NotificationType.EMAIL; // 其他情況使用郵件
        }
    }
    
    /**
     * 獲取任務擁有者的電子郵件
     * 這裡暫時使用模擬資料，實際應該從用戶管理系統獲取
     */
    private String getTaskOwnerEmail(Task task) {
        // TODO: 從任務或用戶系統獲取實際的擁有者信息
        return "user@example.com";
    }
    
    /**
     * 啟用調度器
     */
    public void enableScheduler() {
        this.schedulerEnabled = true;
        logger.info("Task reminder scheduler enabled");
    }
    
    /**
     * 禁用調度器
     */
    public void disableScheduler() {
        this.schedulerEnabled = false;
        logger.info("Task reminder scheduler disabled");
    }
    
    /**
     * 檢查調度器是否啟用
     */
    public boolean isSchedulerEnabled() {
        return schedulerEnabled;
    }
    
    /**
     * 獲取調度器統計資訊
     */
    public SchedulerStatistics getStatistics() {
        return new SchedulerStatistics(
            schedulerEnabled,
            lastExecutionTime,
            processedTaskCount,
            sentNotificationCount
        );
    }
    
    /**
     * 重置統計計數器
     */
    public void resetStatistics() {
        processedTaskCount = 0;
        sentNotificationCount = 0;
        logger.info("Scheduler statistics reset");
    }
    
    /**
     * 調度器統計資訊類別
     */
    public static class SchedulerStatistics {
        private final boolean enabled;
        private final LocalDateTime lastExecution;
        private final int processedTasks;
        private final int sentNotifications;
        
        public SchedulerStatistics(boolean enabled, LocalDateTime lastExecution, 
                                 int processedTasks, int sentNotifications) {
            this.enabled = enabled;
            this.lastExecution = lastExecution;
            this.processedTasks = processedTasks;
            this.sentNotifications = sentNotifications;
        }
        
        public boolean isEnabled() { return enabled; }
        public LocalDateTime getLastExecution() { return lastExecution; }
        public int getProcessedTasks() { return processedTasks; }
        public int getSentNotifications() { return sentNotifications; }
        
        @Override
        public String toString() {
            return String.format("SchedulerStatistics{enabled=%s, lastExecution=%s, processedTasks=%d, sentNotifications=%d}",
                enabled, lastExecution, processedTasks, sentNotifications);
        }
    }
}