package com.tygrus.task_list.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 任務狀態變更事件
 */
public class TaskStatusChangedEvent implements DomainEvent {
    
    private final TaskId taskId;
    private final TaskStatus previousStatus;
    private final TaskStatus newStatus;
    private final LocalDateTime occurredOn;
    
    public TaskStatusChangedEvent(TaskId taskId, TaskStatus previousStatus, TaskStatus newStatus) {
        this.taskId = Objects.requireNonNull(taskId);
        this.previousStatus = Objects.requireNonNull(previousStatus);
        this.newStatus = Objects.requireNonNull(newStatus);
        this.occurredOn = LocalDateTime.now();
    }
    
    public TaskId getTaskId() {
        return taskId;
    }
    
    public TaskStatus getPreviousStatus() {
        return previousStatus;
    }
    
    public TaskStatus getNewStatus() {
        return newStatus;
    }
    
    @Override
    public LocalDateTime getOccurredOn() {
        return occurredOn;
    }
    
    @Override
    public String getEventType() {
        return "TaskStatusChanged";
    }
    
    @Override
    public String toString() {
        return String.format("TaskStatusChangedEvent{taskId=%s, %s->%s, occurredOn=%s}", 
            taskId, previousStatus, newStatus, occurredOn);
    }
}