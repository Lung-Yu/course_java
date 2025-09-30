package com.tygrus.task_list.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * 附件 ID 值對象
 * Attachment identifier value object
 */
public class AttachmentId {
    
    private final String value;

    private AttachmentId(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Attachment ID cannot be null or empty");
        }
        this.value = value.trim();
    }

    /**
     * 從字符串創建 AttachmentId
     */
    public static AttachmentId of(String value) {
        return new AttachmentId(value);
    }

    /**
     * 生成新的 AttachmentId
     */
    public static AttachmentId generate() {
        return new AttachmentId(UUID.randomUUID().toString());
    }

    /**
     * 獲取 ID 值
     */
    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AttachmentId that = (AttachmentId) o;
        return Objects.equals(value, that.value);
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