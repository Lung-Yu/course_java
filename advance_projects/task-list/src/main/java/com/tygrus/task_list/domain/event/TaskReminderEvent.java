package com.tygrus.task_list.domain.event;

import com.tygrus.task_list.domain.model.Task;
import com.tygrus.task_list.domain.model.NotificationType;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 任務提醒事件
 * 當任務需要發送提醒時觸發此事件
 */
public class TaskReminderEvent {
    
    private final String eventId;
    private final Task task;
    private final String recipient;
    private final NotificationType notificationType;
    private final ReminderReason reason;
    private final LocalDateTime scheduledTime;
    private final LocalDateTime eventTime;
    
    /**
     * 建構子
     * 
     * @param task 需要提醒的任務
     * @param recipient 提醒接收者
     * @param notificationType 通知類型
     * @param reason 提醒原因
     * @param scheduledTime 預定提醒時間
     */
    public TaskReminderEvent(Task task, String recipient, NotificationType notificationType, 
                           ReminderReason reason, LocalDateTime scheduledTime) {
        this.eventId = generateEventId(task, reason);
        this.task = Objects.requireNonNull(task, "Task cannot be null");
        this.recipient = Objects.requireNonNull(recipient, "Recipient cannot be null");
        this.notificationType = Objects.requireNonNull(notificationType, "Notification type cannot be null");
        this.reason = Objects.requireNonNull(reason, "Reminder reason cannot be null");
        this.scheduledTime = Objects.requireNonNull(scheduledTime, "Scheduled time cannot be null");
        this.eventTime = LocalDateTime.now();
    }
    
    /**
     * 提醒原因枚舉
     */
    public enum ReminderReason {
        DUE_DATE_APPROACHING("截止日期即將到來"),
        OVERDUE("任務已逾期"),
        STATUS_CHANGE("任務狀態變更"),
        DAILY_SUMMARY("每日摘要"),
        WEEKLY_SUMMARY("每週摘要");
        
        private final String description;
        
        ReminderReason(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    /**
     * 生成事件ID
     * 
     * @param task 任務
     * @param reason 提醒原因
     * @return 事件ID
     */
    private String generateEventId(Task task, ReminderReason reason) {
        return String.format("reminder_%s_%s_%d", 
            task.getId().getValue(), 
            reason.name().toLowerCase(),
            System.currentTimeMillis());
    }
    
    /**
     * 獲取提醒標題
     * 
     * @return 提醒標題
     */
    public String getReminderTitle() {
        return switch (reason) {
            case DUE_DATE_APPROACHING -> String.format("任務提醒：%s 即將到期", task.getTitle());
            case OVERDUE -> String.format("逾期提醒：%s 已逾期", task.getTitle());
            case STATUS_CHANGE -> String.format("狀態更新：%s", task.getTitle());
            case DAILY_SUMMARY -> "每日任務摘要";
            case WEEKLY_SUMMARY -> "每週任務摘要";
        };
    }
    
    /**
     * 獲取提醒內容
     * 
     * @return 提醒內容
     */
    public String getReminderMessage() {
        return switch (reason) {
            case DUE_DATE_APPROACHING -> String.format(
                "您的任務「%s」將於 %s 到期，請及時處理。\n\n任務描述：%s\n優先級：%s",
                task.getTitle(),
                task.getDueDate() != null ? task.getDueDate().toString() : "未設定",
                task.getDescription() != null ? task.getDescription() : "無描述",
                task.getPriority().getDisplayName()
            );
            case OVERDUE -> String.format(
                "您的任務「%s」已逾期，請儘快處理。\n\n截止日期：%s\n任務描述：%s\n優先級：%s",
                task.getTitle(),
                task.getDueDate() != null ? task.getDueDate().toString() : "未設定",
                task.getDescription() != null ? task.getDescription() : "無描述",
                task.getPriority().getDisplayName()
            );
            case STATUS_CHANGE -> String.format(
                "任務「%s」的狀態已更新為：%s\n\n任務描述：%s",
                task.getTitle(),
                task.getStatus().getDisplayName(),
                task.getDescription() != null ? task.getDescription() : "無描述"
            );
            case DAILY_SUMMARY -> generateDailySummaryMessage();
            case WEEKLY_SUMMARY -> generateWeeklySummaryMessage();
        };
    }
    
    /**
     * 生成每日摘要訊息
     * 
     * @return 每日摘要訊息
     */
    private String generateDailySummaryMessage() {
        return String.format("今日任務摘要\n\n任務：%s\n狀態：%s\n優先級：%s",
            task.getTitle(),
            task.getStatus().getDisplayName(),
            task.getPriority().getDisplayName());
    }
    
    /**
     * 生成每週摘要訊息
     * 
     * @return 每週摘要訊息
     */
    private String generateWeeklySummaryMessage() {
        return String.format("每週任務摘要\n\n任務：%s\n狀態：%s\n優先級：%s",
            task.getTitle(),
            task.getStatus().getDisplayName(),
            task.getPriority().getDisplayName());
    }
    
    /**
     * 檢查是否為緊急提醒
     * 
     * @return 如果是緊急提醒返回true
     */
    public boolean isUrgent() {
        return reason == ReminderReason.OVERDUE || 
               (reason == ReminderReason.DUE_DATE_APPROACHING && 
                task.getPriority() == com.tygrus.task_list.domain.model.Priority.HIGH);
    }
    
    // Getters
    public String getEventId() { return eventId; }
    public Task getTask() { return task; }
    public String getRecipient() { return recipient; }
    public NotificationType getNotificationType() { return notificationType; }
    public ReminderReason getReason() { return reason; }
    public LocalDateTime getScheduledTime() { return scheduledTime; }
    public LocalDateTime getEventTime() { return eventTime; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskReminderEvent that = (TaskReminderEvent) o;
        return Objects.equals(eventId, that.eventId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(eventId);
    }
    
    @Override
    public String toString() {
        return String.format("TaskReminderEvent{eventId='%s', taskId='%s', recipient='%s', reason=%s, scheduledTime=%s}",
            eventId, task.getId().getValue(), recipient, reason, scheduledTime);
    }
}