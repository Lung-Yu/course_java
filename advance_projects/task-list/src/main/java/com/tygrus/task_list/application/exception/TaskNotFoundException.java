package com.tygrus.task_list.application.exception;

import com.tygrus.task_list.domain.exception.DomainException;

/**
 * 任務未找到例外
 * 
 * 當嘗試操作不存在的任務時拋出
 * 展示Application層的例外處理
 */
public class TaskNotFoundException extends DomainException {

    private final String taskId;

    public TaskNotFoundException(String taskId) {
        super(String.format("Task not found with ID: %s", taskId));
        this.taskId = taskId;
    }

    public TaskNotFoundException(String taskId, String message) {
        super(message);
        this.taskId = taskId;
    }

    public String getTaskId() {
        return taskId;
    }
}