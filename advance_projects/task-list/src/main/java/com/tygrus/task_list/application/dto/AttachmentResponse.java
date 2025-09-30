package com.tygrus.task_list.application.dto;

import java.time.LocalDateTime;

/**
 * 附件回應 DTO
 * Attachment response data transfer object
 */
public class AttachmentResponse {

    private String id;
    private String taskId;
    private String filename;
    private String contentType;
    private long fileSize;
    private String formattedFileSize;
    private String fileExtension;
    private boolean isImage;
    private boolean isDocument;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private long version;

    public AttachmentResponse() {}

    public AttachmentResponse(String id, String taskId, String filename, 
                            String contentType, long fileSize, String formattedFileSize,
                            String fileExtension, boolean isImage, boolean isDocument,
                            LocalDateTime createdAt, LocalDateTime updatedAt, long version) {
        this.id = id;
        this.taskId = taskId;
        this.filename = filename;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.formattedFileSize = formattedFileSize;
        this.fileExtension = fileExtension;
        this.isImage = isImage;
        this.isDocument = isDocument;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.version = version;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTaskId() { return taskId; }
    public void setTaskId(String taskId) { this.taskId = taskId; }

    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }

    public long getFileSize() { return fileSize; }
    public void setFileSize(long fileSize) { this.fileSize = fileSize; }

    public String getFormattedFileSize() { return formattedFileSize; }
    public void setFormattedFileSize(String formattedFileSize) { this.formattedFileSize = formattedFileSize; }

    public String getFileExtension() { return fileExtension; }
    public void setFileExtension(String fileExtension) { this.fileExtension = fileExtension; }

    public boolean isImage() { return isImage; }
    public void setImage(boolean image) { isImage = image; }

    public boolean isDocument() { return isDocument; }
    public void setDocument(boolean document) { isDocument = document; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public long getVersion() { return version; }
    public void setVersion(long version) { this.version = version; }

    @Override
    public String toString() {
        return "AttachmentResponse{" +
                "id='" + id + '\'' +
                ", taskId='" + taskId + '\'' +
                ", filename='" + filename + '\'' +
                ", contentType='" + contentType + '\'' +
                ", fileSize=" + fileSize +
                ", formattedFileSize='" + formattedFileSize + '\'' +
                ", fileExtension='" + fileExtension + '\'' +
                ", isImage=" + isImage +
                ", isDocument=" + isDocument +
                ", createdAt=" + createdAt +
                '}';
    }
}