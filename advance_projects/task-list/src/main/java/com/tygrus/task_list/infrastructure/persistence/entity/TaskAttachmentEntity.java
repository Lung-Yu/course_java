package com.tygrus.task_list.infrastructure.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * JPA Entity for Task Attachment persistence
 * 支援檔案附件的資料庫映射
 */
@Entity
@Table(name = "task_attachments", indexes = {
    @Index(name = "idx_attachment_task_id", columnList = "taskId"),
    @Index(name = "idx_attachment_filename", columnList = "filename"),
    @Index(name = "idx_attachment_content_type", columnList = "contentType"),
    @Index(name = "idx_attachment_created_at", columnList = "createdAt")
})
public class TaskAttachmentEntity {

    @Id
    @Column(name = "id", length = 36, nullable = false)
    private String id;

    @NotBlank
    @Size(max = 36)
    @Column(name = "task_id", nullable = false, length = 36)
    private String taskId;

    @NotBlank
    @Size(max = 255)
    @Column(name = "filename", nullable = false)
    private String filename;

    @NotBlank
    @Size(max = 100)
    @Column(name = "content_type", nullable = false, length = 100)
    private String contentType;

    @NotNull
    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Lob
    @Column(name = "file_data")
    private byte[] fileData;

    @Size(max = 512)
    @Column(name = "file_path", length = 512)
    private String filePath; // 如果使用檔案系統存儲

    @NotNull
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Version
    @Column(name = "version")
    private Long version = 0L;

    protected TaskAttachmentEntity() {
        // JPA 需要的默認建構子
    }

    public TaskAttachmentEntity(String id, String taskId, String filename, 
                               String contentType, Long fileSize, byte[] fileData) {
        this.id = id;
        this.taskId = taskId;
        this.filename = filename;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.fileData = fileData;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters
    public String getId() { return id; }
    public String getTaskId() { return taskId; }
    public String getFilename() { return filename; }
    public String getContentType() { return contentType; }
    public Long getFileSize() { return fileSize; }
    public byte[] getFileData() { return fileData; }
    public String getFilePath() { return filePath; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public Long getVersion() { return version; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setTaskId(String taskId) { this.taskId = taskId; }
    public void setFilename(String filename) { this.filename = filename; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    public void setFileData(byte[] fileData) { this.fileData = fileData; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public void setVersion(Long version) { this.version = version; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskAttachmentEntity that = (TaskAttachmentEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "TaskAttachmentEntity{" +
                "id='" + id + '\'' +
                ", taskId='" + taskId + '\'' +
                ", filename='" + filename + '\'' +
                ", contentType='" + contentType + '\'' +
                ", fileSize=" + fileSize +
                ", version=" + version +
                '}';
    }
}