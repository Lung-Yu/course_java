package com.tygrus.task_list.domain.event;

import com.tygrus.task_list.domain.model.DomainEvent;
import com.tygrus.task_list.domain.model.TaskId;

import java.time.LocalDateTime;

/**
 * 任務刪除事件
 * 
 * 當任務被軟刪除時發布的Domain Event
 * 展示事件驅動架構在軟刪除操作中的應用
 */
public class TaskDeletedEvent implements DomainEvent {
    
    private final TaskId taskId;
    private final String deletedBy;
    private final String reason;
    private final LocalDateTime occurredOn;
    
    public TaskDeletedEvent(TaskId taskId, String deletedBy, String reason) {
        this.taskId = taskId;
        this.deletedBy = deletedBy;
        this.reason = reason;
        this.occurredOn = LocalDateTime.now();
    }
    
    public TaskId getTaskId() {
        return taskId;
    }
    
    public String getDeletedBy() {
        return deletedBy;
    }
    
    public String getReason() {
        return reason;
    }
    
    @Override
    public LocalDateTime getOccurredOn() {
        return occurredOn;
    }
    
    @Override
    public String getEventType() {
        return "TaskDeleted";
    }
    
    @Override
    public String toString() {
        return String.format("TaskDeletedEvent{taskId=%s, deletedBy='%s', reason='%s', occurredOn=%s}", 
            taskId, deletedBy, reason, occurredOn);
    }
}