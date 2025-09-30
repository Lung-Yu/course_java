package com.tygrus.task_list.infrastructure.persistence.repository;

import com.tygrus.task_list.infrastructure.persistence.entity.TaskEntity;
import com.tygrus.task_list.domain.model.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * JPA Repository for TaskEntity
 * 提供進階查詢功能和批次操作
 */
@Repository
public interface JpaTaskRepository extends JpaRepository<TaskEntity, String> {

    /**
     * 查找所有未刪除的任務
     */
    @Query("SELECT t FROM TaskEntity t WHERE t.deleted = false")
    List<TaskEntity> findAllActive();

    /**
     * 查找所有未刪除的任務（分頁）
     */
    @Query("SELECT t FROM TaskEntity t WHERE t.deleted = false")
    Page<TaskEntity> findAllActive(Pageable pageable);

    /**
     * 根據狀態查找未刪除的任務
     */
    @Query("SELECT t FROM TaskEntity t WHERE t.deleted = false AND t.status = :status")
    List<TaskEntity> findByStatusAndNotDeleted(@Param("status") TaskStatus status);

    /**
     * 根據標題模糊搜尋未刪除的任務
     */
    @Query("SELECT t FROM TaskEntity t WHERE t.deleted = false AND LOWER(t.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    List<TaskEntity> findByTitleContainingAndNotDeleted(@Param("title") String title);

    /**
     * 查找到期的任務
     */
    @Query("SELECT t FROM TaskEntity t WHERE t.deleted = false AND t.dueDate <= :dateTime AND t.status != 'COMPLETED'")
    List<TaskEntity> findOverdueTasks(@Param("dateTime") LocalDateTime dateTime);

    /**
     * 查找即將到期的任務（指定時間範圍內）
     */
    @Query("SELECT t FROM TaskEntity t WHERE t.deleted = false AND t.dueDate BETWEEN :startDate AND :endDate AND t.status != 'COMPLETED'")
    List<TaskEntity> findTasksDueBetween(@Param("startDate") LocalDateTime startDate, 
                                       @Param("endDate") LocalDateTime endDate);

    /**
     * 軟刪除任務
     */
    @Modifying
    @Query("UPDATE TaskEntity t SET t.deleted = true, t.deletedAt = :deleteTime, t.updatedAt = :updateTime WHERE t.id = :id AND t.deleted = false")
    int softDeleteById(@Param("id") String id, 
                      @Param("deleteTime") LocalDateTime deleteTime,
                      @Param("updateTime") LocalDateTime updateTime);

    /**
     * 批次軟刪除任務
     */
    @Modifying
    @Query("UPDATE TaskEntity t SET t.deleted = true, t.deletedAt = :deleteTime, t.updatedAt = :updateTime WHERE t.id IN :ids AND t.deleted = false")
    int softDeleteByIds(@Param("ids") List<String> ids,
                       @Param("deleteTime") LocalDateTime deleteTime,
                       @Param("updateTime") LocalDateTime updateTime);

    /**
     * 批次更新任務狀態
     */
    @Modifying
    @Query("UPDATE TaskEntity t SET t.status = :status, t.updatedAt = :updateTime WHERE t.id IN :ids AND t.deleted = false")
    int updateStatusByIds(@Param("ids") List<String> ids,
                         @Param("status") TaskStatus status,
                         @Param("updateTime") LocalDateTime updateTime);

    /**
     * 查找指定 ID 的未刪除任務
     */
    @Query("SELECT t FROM TaskEntity t WHERE t.id = :id AND t.deleted = false")
    Optional<TaskEntity> findByIdAndNotDeleted(@Param("id") String id);

    /**
     * 統計各狀態的任務數量
     */
    @Query("SELECT t.status, COUNT(t) FROM TaskEntity t WHERE t.deleted = false GROUP BY t.status")
    List<Object[]> countByStatus();

    /**
     * 統計指定日期範圍內創建的任務數量
     */
    @Query("SELECT COUNT(t) FROM TaskEntity t WHERE t.deleted = false AND t.createdAt BETWEEN :startDate AND :endDate")
    Long countTasksCreatedBetween(@Param("startDate") LocalDateTime startDate, 
                                 @Param("endDate") LocalDateTime endDate);

    /**
     * 查找最近更新的任務
     */
    @Query("SELECT t FROM TaskEntity t WHERE t.deleted = false ORDER BY t.updatedAt DESC")
    Page<TaskEntity> findRecentlyUpdated(Pageable pageable);

    /**
     * 檢查任務是否存在且未刪除
     */
    @Query("SELECT COUNT(t) > 0 FROM TaskEntity t WHERE t.id = :id AND t.deleted = false")
    boolean existsByIdAndNotDeleted(@Param("id") String id);

    /**
     * 查找有附件的任務
     */
    @Query("SELECT t FROM TaskEntity t WHERE t.deleted = false AND t.attachmentCount > 0")
    List<TaskEntity> findTasksWithAttachments();

    /**
     * 更新任務的附件數量
     */
    @Modifying
    @Query("UPDATE TaskEntity t SET t.attachmentCount = :count, t.updatedAt = :updateTime WHERE t.id = :id")
    int updateAttachmentCount(@Param("id") String id, 
                             @Param("count") Integer count,
                             @Param("updateTime") LocalDateTime updateTime);

    /**
     * 查找指定狀態的任務統計資料
     */
    @Query("SELECT t.status, COUNT(t), AVG(CASE WHEN t.dueDate IS NOT NULL THEN " +
           "CAST((EXTRACT(EPOCH FROM t.dueDate) - EXTRACT(EPOCH FROM t.createdAt))/86400 AS double) " +
           "ELSE NULL END) " +
           "FROM TaskEntity t WHERE t.deleted = false GROUP BY t.status")
    List<Object[]> getTaskStatistics();

    /**
     * 清理已刪除的任務（物理刪除，用於定期清理）
     */
    @Modifying
    @Query("DELETE FROM TaskEntity t WHERE t.deleted = true AND t.deletedAt < :cutoffDate")
    int physicalDeleteOldTasks(@Param("cutoffDate") LocalDateTime cutoffDate);
}