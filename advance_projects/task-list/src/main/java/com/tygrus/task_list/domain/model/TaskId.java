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
        validateValue(value);
        this.value = value != null ? value.trim() : null;
    }
    
    private void validateValue(String value) {
        if (value == null) {
            throw new IllegalArgumentException("TaskId value cannot be null");
        }
        String trimmedValue = value.trim();
        if (trimmedValue.isEmpty()) {
            throw new IllegalArgumentException("TaskId value cannot be empty");
        }
        if (trimmedValue.length() > 255) {
            throw new IllegalArgumentException("TaskId value cannot exceed 255 characters");
        }
    }
    
    /**
     * 檢查字串是否為有效的UUID格式
     */
    public static boolean isValidUuidFormat(String value) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }
        try {
            UUID.fromString(value.trim());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
    
    public static TaskId generate() {
        return new TaskId(UUID.randomUUID().toString());
    }
    
    public static TaskId of(String value) {
        return new TaskId(value);
    }
    
    /**
     * 從UUID物件創建TaskId
     */
    public static TaskId fromUuid(UUID uuid) {
        if (uuid == null) {
            throw new IllegalArgumentException("UUID cannot be null");
        }
        return new TaskId(uuid.toString());
    }
    
    /**
     * 嘗試從字串創建TaskId，如果格式無效則返回空
     */
    public static java.util.Optional<TaskId> tryParse(String value) {
        try {
            return java.util.Optional.of(new TaskId(value));
        } catch (IllegalArgumentException e) {
            return java.util.Optional.empty();
        }
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
    
    /**
     * 檢查此TaskId是否為UUID格式
     */
    public boolean isUuidFormat() {
        return isValidUuidFormat(this.value);
    }
    
    /**
     * 如果是UUID格式，轉換為UUID物件
     */
    public java.util.Optional<UUID> toUuid() {
        try {
            return java.util.Optional.of(UUID.fromString(this.value));
        } catch (IllegalArgumentException e) {
            return java.util.Optional.empty();
        }
    }
    
    @Override
    public String toString() {
        return value;
    }
}