package com.tygrus.task_list.infrastructure.persistence.repository;

import com.tygrus.task_list.infrastructure.persistence.entity.TaskAttachmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * JPA Repository for TaskAttachmentEntity
 * 提供附件管理的資料庫操作
 */
@Repository
public interface JpaTaskAttachmentRepository extends JpaRepository<TaskAttachmentEntity, String> {

    /**
     * 根據任務 ID 查找所有附件
     */
    List<TaskAttachmentEntity> findByTaskIdOrderByCreatedAtDesc(String taskId);

    /**
     * 根據任務 ID 查找附件數量
     */
    long countByTaskId(String taskId);

    /**
     * 根據任務 ID 刪除所有附件
     */
    @Modifying
    @Query("DELETE FROM TaskAttachmentEntity a WHERE a.taskId = :taskId")
    int deleteByTaskId(@Param("taskId") String taskId);

    /**
     * 根據任務 ID 列表批次刪除附件
     */
    @Modifying
    @Query("DELETE FROM TaskAttachmentEntity a WHERE a.taskId IN :taskIds")
    int deleteByTaskIds(@Param("taskIds") List<String> taskIds);

    /**
     * 查找指定內容類型的附件
     */
    List<TaskAttachmentEntity> findByContentTypeContainingIgnoreCase(String contentType);

    /**
     * 統計各內容類型的附件數量
     */
    @Query("SELECT a.contentType, COUNT(a) FROM TaskAttachmentEntity a GROUP BY a.contentType")
    List<Object[]> countByContentType();

    /**
     * 查找大於指定大小的附件
     */
    @Query("SELECT a FROM TaskAttachmentEntity a WHERE a.fileSize > :maxSize")
    List<TaskAttachmentEntity> findLargeAttachments(@Param("maxSize") Long maxSize);

    /**
     * 統計總附件大小
     */
    @Query("SELECT COALESCE(SUM(a.fileSize), 0) FROM TaskAttachmentEntity a")
    Long getTotalAttachmentSize();

    /**
     * 統計指定任務的總附件大小
     */
    @Query("SELECT COALESCE(SUM(a.fileSize), 0) FROM TaskAttachmentEntity a WHERE a.taskId = :taskId")
    Long getTaskAttachmentSize(@Param("taskId") String taskId);

    /**
     * 查找指定時間範圍內上傳的附件
     */
    List<TaskAttachmentEntity> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 查找重複檔名的附件（同一任務內）
     */
    @Query("SELECT a FROM TaskAttachmentEntity a WHERE a.taskId = :taskId AND a.filename = :filename")
    List<TaskAttachmentEntity> findByTaskIdAndFilename(@Param("taskId") String taskId, 
                                                       @Param("filename") String filename);
}