package com.tygrus.task_list.application.usecase;

import com.tygrus.task_list.application.dto.AttachmentDownloadResponse;
import com.tygrus.task_list.application.dto.AttachmentResponse;
import com.tygrus.task_list.application.dto.AttachmentUploadRequest;
import com.tygrus.task_list.domain.model.*;
import com.tygrus.task_list.domain.repository.TaskAttachmentRepository;
import com.tygrus.task_list.domain.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 附件管理 Use Case
 * Attachment Management Use Case - UC-007
 */
@Service
@Transactional
public class AttachmentManagementUseCase {

    private static final Logger log = LoggerFactory.getLogger(AttachmentManagementUseCase.class);
    
    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024; // 50MB
    private static final int MAX_ATTACHMENTS_PER_TASK = 10;
    
    // 允許的檔案類型
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
        // 圖片
        "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp",
        // 文件
        "application/pdf", "application/msword", 
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        "application/vnd.ms-excel",
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        "application/vnd.ms-powerpoint",
        "application/vnd.openxmlformats-officedocument.presentationml.presentation",
        "text/plain", "text/csv",
        // 壓縮檔
        "application/zip", "application/x-rar-compressed", "application/x-7z-compressed"
    );

    private final TaskAttachmentRepository attachmentRepository;
    private final TaskRepository taskRepository;

    public AttachmentManagementUseCase(TaskAttachmentRepository attachmentRepository,
                                     TaskRepository taskRepository) {
        this.attachmentRepository = attachmentRepository;
        this.taskRepository = taskRepository;
        log.info("AttachmentManagementUseCase initialized");
    }

    /**
     * 上傳附件到任務
     */
    public AttachmentResponse uploadAttachment(AttachmentUploadRequest request) {
        log.info("Starting attachment upload for task: {}, filename: {}", 
                request.getTaskId(), request.getFilename());
        
        try {
            // 驗證請求
            validateUploadRequest(request);
            
            // 檢查任務是否存在
            TaskId taskId = TaskId.of(request.getTaskId());
            if (!taskRepository.existsById(taskId)) {
                throw new IllegalArgumentException("Task not found: " + request.getTaskId());
            }
            
            // 檢查附件數量限制
            long currentAttachmentCount = attachmentRepository.countByTaskId(taskId);
            if (currentAttachmentCount >= MAX_ATTACHMENTS_PER_TASK) {
                throw new IllegalArgumentException("Maximum number of attachments per task exceeded: " + MAX_ATTACHMENTS_PER_TASK);
            }
            
            // 檢查同名檔案是否已存在
            Optional<TaskAttachment> existingAttachment = 
                attachmentRepository.findByTaskIdAndFilename(taskId, request.getFilename());
            if (existingAttachment.isPresent()) {
                throw new IllegalArgumentException("File with the same name already exists: " + request.getFilename());
            }
            
            // 創建附件
            TaskAttachment attachment = TaskAttachment.builder()
                .id(AttachmentId.generate())
                .taskId(taskId)
                .filename(request.getFilename())
                .contentType(request.getContentType())
                .fileSize(request.getFileSize())
                .fileData(request.getFileData())
                .build();
            
            // 保存附件
            TaskAttachment savedAttachment = attachmentRepository.save(attachment);
            
            log.info("Attachment uploaded successfully: {}", savedAttachment.getId());
            return convertToResponse(savedAttachment);
            
        } catch (Exception e) {
            log.error("Failed to upload attachment for task {}: {}", request.getTaskId(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 下載附件
     */
    @Transactional(readOnly = true)
    public AttachmentDownloadResponse downloadAttachment(String attachmentId) {
        log.info("Starting attachment download: {}", attachmentId);
        
        try {
            AttachmentId id = AttachmentId.of(attachmentId);
            Optional<TaskAttachment> attachment = attachmentRepository.findById(id);
            
            if (attachment.isEmpty()) {
                log.warn("Attachment not found: {}", attachmentId);
                return AttachmentDownloadResponse.error("Attachment not found: " + attachmentId);
            }
            
            TaskAttachment att = attachment.get();
            log.info("Attachment download completed: {}", attachmentId);
            
            return AttachmentDownloadResponse.success(
                att.getFilename(),
                att.getContentType(),
                att.getFileSize(),
                att.getFileData()
            );
            
        } catch (Exception e) {
            log.error("Failed to download attachment {}: {}", attachmentId, e.getMessage(), e);
            return AttachmentDownloadResponse.error("Failed to download attachment: " + e.getMessage());
        }
    }

    /**
     * 獲取任務的所有附件
     */
    @Transactional(readOnly = true)
    public List<AttachmentResponse> getTaskAttachments(String taskId) {
        log.info("Getting attachments for task: {}", taskId);
        
        try {
            TaskId id = TaskId.of(taskId);
            List<TaskAttachment> attachments = attachmentRepository.findByTaskId(id);
            
            log.info("Found {} attachments for task: {}", attachments.size(), taskId);
            return attachments.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
                
        } catch (Exception e) {
            log.error("Failed to get attachments for task {}: {}", taskId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 刪除附件
     */
    public void deleteAttachment(String attachmentId) {
        log.info("Starting attachment deletion: {}", attachmentId);
        
        try {
            AttachmentId id = AttachmentId.of(attachmentId);
            
            if (!attachmentRepository.existsById(id)) {
                throw new IllegalArgumentException("Attachment not found: " + attachmentId);
            }
            
            attachmentRepository.deleteById(id);
            log.info("Attachment deleted successfully: {}", attachmentId);
            
        } catch (Exception e) {
            log.error("Failed to delete attachment {}: {}", attachmentId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 刪除任務的所有附件
     */
    public void deleteTaskAttachments(String taskId) {
        log.info("Starting deletion of all attachments for task: {}", taskId);
        
        try {
            TaskId id = TaskId.of(taskId);
            long count = attachmentRepository.countByTaskId(id);
            
            attachmentRepository.deleteByTaskId(id);
            log.info("Deleted {} attachments for task: {}", count, taskId);
            
        } catch (Exception e) {
            log.error("Failed to delete attachments for task {}: {}", taskId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 獲取附件資訊（不包含檔案內容）
     */
    @Transactional(readOnly = true)
    public Optional<AttachmentResponse> getAttachmentInfo(String attachmentId) {
        log.info("Getting attachment info: {}", attachmentId);
        
        try {
            AttachmentId id = AttachmentId.of(attachmentId);
            Optional<TaskAttachment> attachment = attachmentRepository.findById(id);
            
            return attachment.map(this::convertToResponse);
            
        } catch (Exception e) {
            log.error("Failed to get attachment info {}: {}", attachmentId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 驗證上傳請求
     */
    private void validateUploadRequest(AttachmentUploadRequest request) {
        if (request.getFileSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds maximum limit: " + MAX_FILE_SIZE + " bytes");
        }
        
        if (!ALLOWED_CONTENT_TYPES.contains(request.getContentType())) {
            throw new IllegalArgumentException("Unsupported file type: " + request.getContentType());
        }
        
        if (request.getFileData() == null || request.getFileData().length == 0) {
            throw new IllegalArgumentException("File data cannot be empty");
        }
        
        if (request.getFileData().length != request.getFileSize()) {
            throw new IllegalArgumentException("File size mismatch: expected " + 
                request.getFileSize() + " bytes, got " + request.getFileData().length);
        }
        
        // 檢查檔名中的危險字符
        String filename = request.getFilename();
        if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
            throw new IllegalArgumentException("Invalid filename: " + filename);
        }
    }

    /**
     * 轉換為回應 DTO
     */
    private AttachmentResponse convertToResponse(TaskAttachment attachment) {
        return new AttachmentResponse(
            attachment.getId().getValue(),
            attachment.getTaskId().getValue(),
            attachment.getFilename(),
            attachment.getContentType(),
            attachment.getFileSize(),
            attachment.getFormattedFileSize(),
            attachment.getFileExtension(),
            attachment.isImage(),
            attachment.isDocument(),
            attachment.getCreatedAt(),
            attachment.getUpdatedAt(),
            attachment.getVersion()
        );
    }

    /**
     * 獲取支援的檔案類型
     */
    @Transactional(readOnly = true)
    public Set<String> getSupportedContentTypes() {
        return ALLOWED_CONTENT_TYPES;
    }

    /**
     * 獲取檔案大小限制
     */
    @Transactional(readOnly = true)
    public long getMaxFileSize() {
        return MAX_FILE_SIZE;
    }

    /**
     * 獲取每個任務的最大附件數量
     */
    @Transactional(readOnly = true)
    public int getMaxAttachmentsPerTask() {
        return MAX_ATTACHMENTS_PER_TASK;
    }
}