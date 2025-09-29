package com.tygrus.task_list.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * TaskId值物件
 * 
 * 提供類型安全的任務ID，基於UUID實作
 */
public final class TaskId {
    
    private final String value;
    
    private TaskId(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("TaskId value cannot be null or empty");
        }
        this.value = value;
    }
    
    public static TaskId generate() {
        return new TaskId(UUID.randomUUID().toString());
    }
    
    public static TaskId of(String value) {
        return new TaskId(value);
    }
    
    public String getValue() {
        return value;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        TaskId taskId = (TaskId) obj;
        return Objects.equals(value, taskId.value);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
    
    @Override
    public String toString() {
        return value;
    }
}