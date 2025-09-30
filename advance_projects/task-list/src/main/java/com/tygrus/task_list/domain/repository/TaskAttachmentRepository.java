package com.tygrus.task_list.domain.repository;

import com.tygrus.task_list.domain.model.AttachmentId;
import com.tygrus.task_list.domain.model.TaskAttachment;
import com.tygrus.task_list.domain.model.TaskId;

import java.util.List;
import java.util.Optional;

/**
 * Task Attachment Repository interface
 * 任務附件倉庫介面
 */
public interface TaskAttachmentRepository {

    /**
     * 保存附件
     */
    TaskAttachment save(TaskAttachment attachment);

    /**
     * 根據 ID 查找附件
     */
    Optional<TaskAttachment> findById(AttachmentId id);

    /**
     * 根據任務 ID 查找所有附件
     */
    List<TaskAttachment> findByTaskId(TaskId taskId);

    /**
     * 根據 ID 刪除附件
     */
    void deleteById(AttachmentId id);

    /**
     * 根據任務 ID 刪除所有附件
     */
    void deleteByTaskId(TaskId taskId);

    /**
     * 檢查附件是否存在
     */
    boolean existsById(AttachmentId id);

    /**
     * 檢查任務是否有附件
     */
    boolean existsByTaskId(TaskId taskId);

    /**
     * 計算任務的總附件數量
     */
    long countByTaskId(TaskId taskId);

    /**
     * 計算任務的總附件大小（位元組）
     */
    long calculateTotalSizeByTaskId(TaskId taskId);

    /**
     * 根據檔名和任務 ID 查找附件
     */
    Optional<TaskAttachment> findByTaskIdAndFilename(TaskId taskId, String filename);

    /**
     * 獲取所有附件的總數量
     */
    long count();
}