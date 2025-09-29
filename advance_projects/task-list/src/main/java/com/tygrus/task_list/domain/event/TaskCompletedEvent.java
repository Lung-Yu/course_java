package com.tygrus.task_list.domain.event;

import com.tygrus.task_list.domain.model.DomainEvent;
import com.tygrus.task_list.domain.model.TaskId;

import java.time.LocalDateTime;

/**
 * 任務完成事件
 * 
 * 當任務狀態變更為COMPLETED時觸發的特殊事件
 * 可用於觸發後續處理，如統計更新、通知發送等
 */
public class TaskCompletedEvent implements DomainEvent {
    
    private final TaskId taskId;
    private final String completedBy;
    private final LocalDateTime occurredAt;

    public TaskCompletedEvent(TaskId taskId, String completedBy) {
        this.taskId = taskId;
        this.completedBy = completedBy;
        this.occurredAt = LocalDateTime.now();
    }

    @Override
    public LocalDateTime getOccurredOn() {
        return occurredAt;
    }

    @Override
    public String getEventType() {
        return "TaskCompleted";
    }

    // Getters
    public TaskId getTaskId() {
        return taskId;
    }

    public String getCompletedBy() {
        return completedBy;
    }

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }

    @Override
    public String toString() {
        return String.format("TaskCompletedEvent{taskId=%s, completedBy='%s', at=%s}",
                taskId.getValue(), completedBy, occurredAt);
    }
}