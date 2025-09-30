package com.tygrus.task_list.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Task Attachment Domain Model
 * 任務附件領域模型
 */
public class TaskAttachment {
    
    private final AttachmentId id;
    private final TaskId taskId;
    private final String filename;
    private final String contentType;
    private final long fileSize;
    private final byte[] fileData;
    private final String filePath; // 檔案系統路徑（可選）
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final long version;

    private TaskAttachment(Builder builder) {
        this.id = builder.id;
        this.taskId = builder.taskId;
        this.filename = builder.filename;
        this.contentType = builder.contentType;
        this.fileSize = builder.fileSize;
        this.fileData = builder.fileData;
        this.filePath = builder.filePath;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
        this.version = builder.version;
        
        validate();
    }

    public static Builder builder() {
        return new Builder();
    }

    private void validate() {
        if (id == null) {
            throw new IllegalArgumentException("Attachment ID cannot be null");
        }
        if (taskId == null) {
            throw new IllegalArgumentException("Task ID cannot be null");
        }
        if (filename == null || filename.trim().isEmpty()) {
            throw new IllegalArgumentException("Filename cannot be null or empty");
        }
        if (contentType == null || contentType.trim().isEmpty()) {
            throw new IllegalArgumentException("Content type cannot be null or empty");
        }
        if (fileSize < 0) {
            throw new IllegalArgumentException("File size cannot be negative");
        }
        if (fileData == null && filePath == null) {
            throw new IllegalArgumentException("Either file data or file path must be provided");
        }
        if (createdAt == null) {
            throw new IllegalArgumentException("Created at cannot be null");
        }
    }

    /**
     * 檢查檔案是否為圖片類型
     */
    public boolean isImage() {
        return contentType.startsWith("image/");
    }

    /**
     * 檢查檔案是否為文件類型
     */
    public boolean isDocument() {
        return contentType.startsWith("application/") || 
               contentType.startsWith("text/") ||
               contentType.equals("application/pdf") ||
               contentType.equals("application/msword") ||
               contentType.contains("spreadsheet") ||
               contentType.contains("presentation");
    }

    /**
     * 獲取檔案大小的可讀格式
     */
    public String getFormattedFileSize() {
        if (fileSize < 1024) {
            return fileSize + " B";
        } else if (fileSize < 1024 * 1024) {
            return String.format("%.1f KB", fileSize / 1024.0);
        } else if (fileSize < 1024 * 1024 * 1024) {
            return String.format("%.1f MB", fileSize / (1024.0 * 1024.0));
        } else {
            return String.format("%.1f GB", fileSize / (1024.0 * 1024.0 * 1024.0));
        }
    }

    /**
     * 獲取檔案副檔名
     */
    public String getFileExtension() {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < filename.length() - 1) {
            return filename.substring(lastDotIndex + 1).toLowerCase();
        }
        return "";
    }

    // Getters
    public AttachmentId getId() { return id; }
    public TaskId getTaskId() { return taskId; }
    public String getFilename() { return filename; }
    public String getContentType() { return contentType; }
    public long getFileSize() { return fileSize; }
    public byte[] getFileData() { return fileData; }
    public String getFilePath() { return filePath; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public long getVersion() { return version; }

    public static class Builder {
        private AttachmentId id;
        private TaskId taskId;
        private String filename;
        private String contentType;
        private long fileSize;
        private byte[] fileData;
        private String filePath;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private long version = 0L;

        public Builder id(AttachmentId id) {
            this.id = id;
            return this;
        }

        public Builder taskId(TaskId taskId) {
            this.taskId = taskId;
            return this;
        }

        public Builder filename(String filename) {
            this.filename = filename;
            return this;
        }

        public Builder contentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        public Builder fileSize(long fileSize) {
            this.fileSize = fileSize;
            return this;
        }

        public Builder fileData(byte[] fileData) {
            this.fileData = fileData;
            return this;
        }

        public Builder filePath(String filePath) {
            this.filePath = filePath;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Builder version(long version) {
            this.version = version;
            return this;
        }

        public TaskAttachment build() {
            if (createdAt == null) {
                createdAt = LocalDateTime.now();
            }
            if (updatedAt == null) {
                updatedAt = createdAt;
            }
            return new TaskAttachment(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskAttachment that = (TaskAttachment) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "TaskAttachment{" +
                "id=" + id +
                ", taskId=" + taskId +
                ", filename='" + filename + '\'' +
                ", contentType='" + contentType + '\'' +
                ", fileSize=" + fileSize +
                ", formattedSize='" + getFormattedFileSize() + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}