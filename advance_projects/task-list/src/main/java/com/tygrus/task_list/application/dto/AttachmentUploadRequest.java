package com.tygrus.task_list.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * 附件上傳請求 DTO
 * Attachment upload request data transfer object
 */
public class AttachmentUploadRequest {

    @NotBlank(message = "Task ID cannot be blank")
    @Size(max = 36, message = "Task ID must not exceed 36 characters")
    private String taskId;

    @NotBlank(message = "Filename cannot be blank")
    @Size(max = 255, message = "Filename must not exceed 255 characters")
    private String filename;

    @NotBlank(message = "Content type cannot be blank")
    @Size(max = 100, message = "Content type must not exceed 100 characters")
    private String contentType;

    @NotNull(message = "File size cannot be null")
    @Positive(message = "File size must be positive")
    private Long fileSize;

    @NotNull(message = "File data cannot be null")
    private byte[] fileData;

    // 可選的描述
    @Size(max = 512, message = "Description must not exceed 512 characters")
    private String description;

    public AttachmentUploadRequest() {}

    public AttachmentUploadRequest(String taskId, String filename, String contentType, 
                                 Long fileSize, byte[] fileData) {
        this.taskId = taskId;
        this.filename = filename;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.fileData = fileData;
    }

    // Getters and Setters
    public String getTaskId() { return taskId; }
    public void setTaskId(String taskId) { this.taskId = taskId; }

    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }

    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }

    public byte[] getFileData() { return fileData; }
    public void setFileData(byte[] fileData) { this.fileData = fileData; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return "AttachmentUploadRequest{" +
                "taskId='" + taskId + '\'' +
                ", filename='" + filename + '\'' +
                ", contentType='" + contentType + '\'' +
                ", fileSize=" + fileSize +
                ", description='" + description + '\'' +
                '}';
    }
}