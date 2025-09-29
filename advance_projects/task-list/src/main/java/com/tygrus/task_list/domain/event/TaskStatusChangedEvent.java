package com.tygrus.task_list.domain.event;

import com.tygrus.task_list.domain.model.DomainEvent;
import com.tygrus.task_list.domain.model.TaskId;
import com.tygrus.task_list.domain.model.TaskStatus;

import java.time.LocalDateTime;

/**
 * 任務狀態變更事件
 * 
 * 展示Domain Events模式的應用
 * 用於解耦領域邏輯和副作用
 */
public class TaskStatusChangedEvent implements DomainEvent {
    
    private final TaskId taskId;
    private final TaskStatus fromStatus;
    private final TaskStatus toStatus;
    private final String reason;
    private final String changedBy;
    private final LocalDateTime occurredAt;

    public TaskStatusChangedEvent(TaskId taskId, TaskStatus fromStatus, 
                                TaskStatus toStatus, String reason, String changedBy) {
        this.taskId = taskId;
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
        this.reason = reason;
        this.changedBy = changedBy;
        this.occurredAt = LocalDateTime.now();
    }

    @Override
    public LocalDateTime getOccurredOn() {
        return occurredAt;
    }

    @Override
    public String getEventType() {
        return "TaskStatusChanged";
    }

    // Getters
    public TaskId getTaskId() {
        return taskId;
    }

    public TaskStatus getFromStatus() {
        return fromStatus;
    }

    public TaskStatus getToStatus() {
        return toStatus;
    }

    public String getReason() {
        return reason;
    }

    public String getChangedBy() {
        return changedBy;
    }

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }

    @Override
    public String toString() {
        return String.format("TaskStatusChangedEvent{taskId=%s, %s -> %s, reason='%s', at=%s}",
                taskId.getValue(), fromStatus, toStatus, reason, occurredAt);
    }
}