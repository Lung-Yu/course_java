package com.tygrus.task_list.infrastructure.persistence.entity;

import com.tygrus.task_list.domain.model.Task;
import com.tygrus.task_list.domain.model.TaskId;
import com.tygrus.task_list.domain.model.TaskStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * JPA Entity for Task persistence
 * 將 Domain Task 物件映射到關聯式資料庫
 */
@Entity
@Table(name = "tasks", indexes = {
    @Index(name = "idx_task_status", columnList = "status"),
    @Index(name = "idx_task_due_date", columnList = "dueDate"),
    @Index(name = "idx_task_created_at", columnList = "createdAt"),
    @Index(name = "idx_task_deleted", columnList = "deleted"),
    @Index(name = "idx_task_title", columnList = "title")
})
public class TaskEntity {

    @Id
    @Column(name = "id", length = 36, nullable = false)
    private String id;

    @NotBlank
    @Size(max = 255)
    @Column(name = "title", nullable = false)
    private String title;

    @Size(max = 2000)
    @Column(name = "description", length = 2000)
    private String description;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private TaskStatus status;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Version
    @Column(name = "version")
    private Long version = 0L;

    // 為了支援 attachment 關聯
    @Column(name = "attachment_count", nullable = false)
    private Integer attachmentCount = 0;

    protected TaskEntity() {
        // JPA 需要的默認建構子
    }

    public TaskEntity(String id, String title, String description, TaskStatus status, 
                     LocalDateTime dueDate, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.dueDate = dueDate;
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
        this.deleted = false;
    }

    /**
     * 從 Domain Model 建立 Entity
     */
    public static TaskEntity fromDomain(Task task) {
        TaskEntity entity = new TaskEntity();
        entity.id = task.getId().getValue();
        entity.title = task.getTitle();
        entity.description = task.getDescription();
        entity.status = task.getStatus();
        entity.dueDate = task.getDueDate();
        entity.createdAt = task.getCreatedAt();
        entity.updatedAt = LocalDateTime.now();
        entity.deleted = false;
        return entity;
    }

    /**
     * 轉換為 Domain Model
     * 使用特殊的靜態方法來恢復狀態，避免狀態轉換驗證
     */
    public Task toDomain() {
        return Task.restoreFromPersistence(
            TaskId.of(this.id),
            this.title,
            this.description,
            this.status,
            this.dueDate,
            this.createdAt
        );
    }

    /**
     * 軟刪除
     */
    public void markAsDeleted() {
        this.deleted = true;
        this.deletedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 恢復軟刪除
     */
    public void unmarkAsDeleted() {
        this.deleted = false;
        this.deletedAt = null;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 更新實體
     */
    public void updateFromDomain(Task task) {
        this.title = task.getTitle();
        this.description = task.getDescription();
        this.status = task.getStatus();
        this.dueDate = task.getDueDate();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public TaskStatus getStatus() { return status; }
    public LocalDateTime getDueDate() { return dueDate; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public Boolean getDeleted() { return deleted; }
    public LocalDateTime getDeletedAt() { return deletedAt; }
    public Long getVersion() { return version; }
    public Integer getAttachmentCount() { return attachmentCount; }

    // Setters (for JPA)
    public void setId(String id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setStatus(TaskStatus status) { this.status = status; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public void setDeleted(Boolean deleted) { this.deleted = deleted; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
    public void setVersion(Long version) { this.version = version; }
    public void setAttachmentCount(Integer attachmentCount) { this.attachmentCount = attachmentCount; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskEntity that = (TaskEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "TaskEntity{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", status=" + status +
                ", deleted=" + deleted +
                ", version=" + version +
                '}';
    }
}