package com.tygrus.task_list.infrastructure.persistence.repository;

import com.tygrus.task_list.domain.model.AttachmentId;
import com.tygrus.task_list.domain.model.TaskAttachment;
import com.tygrus.task_list.domain.model.TaskId;
import com.tygrus.task_list.domain.repository.TaskAttachmentRepository;
import com.tygrus.task_list.infrastructure.persistence.entity.TaskAttachmentEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * PostgreSQL implementation of TaskAttachmentRepository
 * 任務附件倉庫的 PostgreSQL 實作
 */
@Repository
@Transactional
public class PostgreSQLTaskAttachmentRepository implements TaskAttachmentRepository {

    private final JpaTaskAttachmentRepository jpaRepository;

    public PostgreSQLTaskAttachmentRepository(JpaTaskAttachmentRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public TaskAttachment save(TaskAttachment attachment) {
        TaskAttachmentEntity entity = convertToEntity(attachment);
        TaskAttachmentEntity savedEntity = jpaRepository.save(entity);
        return convertToDomain(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TaskAttachment> findById(AttachmentId id) {
        return jpaRepository.findById(id.getValue())
                .map(this::convertToDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskAttachment> findByTaskId(TaskId taskId) {
        return jpaRepository.findByTaskIdOrderByCreatedAtDesc(taskId.getValue())
                .stream()
                .map(this::convertToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(AttachmentId id) {
        jpaRepository.deleteById(id.getValue());
    }

    @Override
    public void deleteByTaskId(TaskId taskId) {
        jpaRepository.deleteByTaskId(taskId.getValue());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(AttachmentId id) {
        return jpaRepository.existsById(id.getValue());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByTaskId(TaskId taskId) {
        return jpaRepository.countByTaskId(taskId.getValue()) > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public long countByTaskId(TaskId taskId) {
        return jpaRepository.countByTaskId(taskId.getValue());
    }

    @Override
    @Transactional(readOnly = true)
    public long calculateTotalSizeByTaskId(TaskId taskId) {
        Long totalSize = jpaRepository.getTaskAttachmentSize(taskId.getValue());
        return totalSize != null ? totalSize : 0L;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TaskAttachment> findByTaskIdAndFilename(TaskId taskId, String filename) {
        List<TaskAttachmentEntity> entities = jpaRepository.findByTaskIdAndFilename(
                taskId.getValue(), filename);
        return entities.isEmpty() ? Optional.empty() : 
               Optional.of(convertToDomain(entities.get(0)));
    }

    @Override
    @Transactional(readOnly = true)
    public long count() {
        return jpaRepository.count();
    }

    /**
     * 轉換為領域模型
     */
    private TaskAttachment convertToDomain(TaskAttachmentEntity entity) {
        return TaskAttachment.builder()
                .id(AttachmentId.of(entity.getId()))
                .taskId(TaskId.of(entity.getTaskId()))
                .filename(entity.getFilename())
                .contentType(entity.getContentType())
                .fileSize(entity.getFileSize())
                .fileData(entity.getFileData())
                .filePath(entity.getFilePath())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .version(entity.getVersion())
                .build();
    }

    /**
     * 轉換為實體
     */
    private TaskAttachmentEntity convertToEntity(TaskAttachment attachment) {
        TaskAttachmentEntity entity = new TaskAttachmentEntity(
                attachment.getId().getValue(),
                attachment.getTaskId().getValue(),
                attachment.getFilename(),
                attachment.getContentType(),
                attachment.getFileSize(),
                attachment.getFileData()
        );
        
        if (attachment.getFilePath() != null) {
            entity.setFilePath(attachment.getFilePath());
        }
        
        if (attachment.getCreatedAt() != null) {
            entity.setCreatedAt(attachment.getCreatedAt());
        }
        
        if (attachment.getUpdatedAt() != null) {
            entity.setUpdatedAt(attachment.getUpdatedAt());
        }
        
        entity.setVersion(attachment.getVersion());
        
        return entity;
    }
}