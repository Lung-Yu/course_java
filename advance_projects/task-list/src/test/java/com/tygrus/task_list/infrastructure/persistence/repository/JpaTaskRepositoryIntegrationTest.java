package com.tygrus.task_list.infrastructure.persistence.repository;

import com.tygrus.task_list.domain.model.TaskStatus;
import com.tygrus.task_list.infrastructure.persistence.entity.TaskEntity;
import com.tygrus.task_list.infrastructure.testcontainers.PostgreSQLDataJpaTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * PostgreSQL JPA Repository 整合測試
 * 使用 Testcontainers 進行真實資料庫測試
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@DisplayName("PostgreSQL JPA Repository 整合測試")
class JpaTaskRepositoryIntegrationTest extends PostgreSQLDataJpaTestBase {

    private static final Logger logger = LoggerFactory.getLogger(JpaTaskRepositoryIntegrationTest.class);

    @Autowired
    private JpaTaskRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private TaskEntity sampleTask1;
    private TaskEntity sampleTask2;
    private TaskEntity sampleTask3;

    @BeforeEach
    void setUp() {
        logger.info("設置測試資料");
        
        // 清空資料庫
        repository.deleteAll();
        
        // 準備測試資料
        LocalDateTime now = LocalDateTime.now();
        
        sampleTask1 = new TaskEntity("task-1", "完成專案文件", "撰寫技術文件和使用者手冊", 
                                   TaskStatus.IN_PROGRESS, now.plusDays(3), now);
        
        sampleTask2 = new TaskEntity("task-2", "程式碼重構", "重構舊有程式碼以提升效能", 
                                   TaskStatus.PENDING, now.plusDays(7), now);
        
        sampleTask3 = new TaskEntity("task-3", "測試案例撰寫", "為新功能撰寫單元測試", 
                                   TaskStatus.COMPLETED, now.minusDays(1), now.minusDays(5));
        
        // 儲存測試資料
        repository.saveAll(Arrays.asList(sampleTask1, sampleTask2, sampleTask3));
        repository.flush();
        
        logger.info("測試資料設置完成，共插入 {} 筆資料", 3);
    }

    @Nested
    @DisplayName("基本 CRUD 操作測試")
    class BasicCrudOperationsTest {

        @Test
        @DisplayName("應該能夠儲存新的任務")
        void shouldSaveNewTask() {
            // Given
            LocalDateTime now = LocalDateTime.now();
            TaskEntity newTask = new TaskEntity("task-4", "新增功能", "開發新的使用者功能", 
                                               TaskStatus.PENDING, now.plusDays(5), now);
            
            // When
            TaskEntity savedTask = repository.save(newTask);
            
            // Then
            assertThat(savedTask).isNotNull();
            assertThat(savedTask.getId()).isEqualTo("task-4");
            assertThat(savedTask.getTitle()).isEqualTo("新增功能");
            assertThat(savedTask.getStatus()).isEqualTo(TaskStatus.PENDING);
            assertThat(savedTask.getVersion()).isEqualTo(0L);
            assertThat(savedTask.getDeleted()).isFalse();
            
            logger.info("成功儲存新任務: {}", savedTask.getId());
        }

        @Test
        @DisplayName("應該能夠根據 ID 查找任務")
        void shouldFindTaskById() {
            // When
            Optional<TaskEntity> found = repository.findById("task-1");
            
            // Then
            assertThat(found).isPresent();
            assertThat(found.get().getTitle()).isEqualTo("完成專案文件");
            assertThat(found.get().getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
            
            logger.info("成功查找任務: {}", found.get().getId());
        }

        @Test
        @DisplayName("查找不存在的任務應該回傳空值")
        void shouldReturnEmptyForNonExistentTask() {
            // When
            Optional<TaskEntity> found = repository.findById("non-existent");
            
            // Then
            assertThat(found).isEmpty();
            
            logger.info("正確處理不存在的任務查詢");
        }

        @Test
        @DisplayName("應該能夠更新現有任務")
        void shouldUpdateExistingTask() {
            // Given
            TaskEntity task = repository.findById("task-1").orElseThrow();
            String originalTitle = task.getTitle();
            
            // When
            task.setTitle("更新後的標題");
            task.setStatus(TaskStatus.COMPLETED);
            TaskEntity updatedTask = repository.save(task);
            
            // Then
            assertThat(updatedTask.getTitle()).isEqualTo("更新後的標題");
            assertThat(updatedTask.getStatus()).isEqualTo(TaskStatus.COMPLETED);
            assertThat(updatedTask.getVersion()).isGreaterThan(0L);
            assertThat(updatedTask.getUpdatedAt()).isAfter(updatedTask.getCreatedAt());
            
            logger.info("成功更新任務: {} -> {}", originalTitle, updatedTask.getTitle());
        }
    }

    @Nested
    @DisplayName("自定義查詢測試")
    class CustomQueryTest {

        @Test
        @DisplayName("應該能夠查找所有未刪除的任務")
        void shouldFindAllActiveTasks() {
            // When
            List<TaskEntity> activeTasks = repository.findAllActive();
            
            // Then
            assertThat(activeTasks).hasSize(3);
            assertThat(activeTasks).allMatch(task -> !task.getDeleted());
            
            logger.info("查找到 {} 個未刪除的任務", activeTasks.size());
        }

        @Test
        @DisplayName("應該能夠根據狀態查找任務")
        void shouldFindTasksByStatus() {
            // When
            List<TaskEntity> pendingTasks = repository.findByStatusAndNotDeleted(TaskStatus.PENDING);
            List<TaskEntity> completedTasks = repository.findByStatusAndNotDeleted(TaskStatus.COMPLETED);
            
            // Then
            assertThat(pendingTasks).hasSize(1);
            assertThat(pendingTasks.get(0).getTitle()).isEqualTo("程式碼重構");
            
            assertThat(completedTasks).hasSize(1);
            assertThat(completedTasks.get(0).getTitle()).isEqualTo("測試案例撰寫");
            
            logger.info("根據狀態查找任務: PENDING={}, COMPLETED={}", 
                       pendingTasks.size(), completedTasks.size());
        }

        @Test
        @DisplayName("應該能夠根據標題模糊搜尋任務")
        void shouldFindTasksByTitleContaining() {
            // When
            List<TaskEntity> foundTasks = repository.findByTitleContainingAndNotDeleted("程式");
            
            // Then
            assertThat(foundTasks).hasSize(1);
            assertThat(foundTasks.get(0).getTitle()).contains("程式");
            
            logger.info("根據標題模糊搜尋找到 {} 個任務", foundTasks.size());
        }

        @Test
        @DisplayName("應該能夠查找到期的任務")
        void shouldFindOverdueTasks() {
            // Given - 建立一個已過期的任務
            LocalDateTime pastDate = LocalDateTime.now().minusDays(1);
            TaskEntity overdueTask = new TaskEntity("overdue-task", "過期任務", "這是一個過期的任務", 
                                                   TaskStatus.PENDING, pastDate, pastDate.minusDays(5));
            repository.save(overdueTask);
            
            // When
            List<TaskEntity> overdueTasks = repository.findOverdueTasks(LocalDateTime.now());
            
            // Then
            assertThat(overdueTasks).hasSizeGreaterThanOrEqualTo(1);
            assertThat(overdueTasks).anyMatch(task -> task.getId().equals("overdue-task"));
            
            logger.info("查找到 {} 個過期任務", overdueTasks.size());
        }
    }

    @Nested
    @DisplayName("軟刪除功能測試")
    class SoftDeleteTest {

        @Test
        @DisplayName("應該能夠軟刪除單個任務")
        void shouldSoftDeleteSingleTask() {
            // When
            LocalDateTime deleteTime = LocalDateTime.now();
            int deleted = repository.softDeleteById("task-1", deleteTime, deleteTime);
            
            // Then
            assertThat(deleted).isEqualTo(1);
            
            // 驗證任務被軟刪除
            Optional<TaskEntity> deletedTask = repository.findById("task-1");
            assertThat(deletedTask).isPresent();
            assertThat(deletedTask.get().getDeleted()).isTrue();
            assertThat(deletedTask.get().getDeletedAt()).isNotNull();
            
            // 確認在未刪除任務清單中找不到
            Optional<TaskEntity> activeTask = repository.findByIdAndNotDeleted("task-1");
            assertThat(activeTask).isEmpty();
            
            logger.info("成功軟刪除任務: task-1");
        }

        @Test
        @DisplayName("應該能夠批次軟刪除多個任務")
        void shouldSoftDeleteMultipleTasks() {
            // When
            LocalDateTime deleteTime = LocalDateTime.now();
            List<String> idsToDelete = Arrays.asList("task-1", "task-2");
            int deleted = repository.softDeleteByIds(idsToDelete, deleteTime, deleteTime);
            
            // Then
            assertThat(deleted).isEqualTo(2);
            
            // 驗證任務被軟刪除
            List<TaskEntity> activeTasks = repository.findAllActive();
            assertThat(activeTasks).hasSize(1);
            assertThat(activeTasks.get(0).getId()).isEqualTo("task-3");
            
            logger.info("成功批次軟刪除 {} 個任務", deleted);
        }

        @Test
        @DisplayName("重複軟刪除應該不影響結果")
        void shouldHandleDuplicateSoftDelete() {
            // Given - 先軟刪除一次
            LocalDateTime deleteTime = LocalDateTime.now();
            repository.softDeleteById("task-1", deleteTime, deleteTime);
            
            // When - 再次嘗試軟刪除
            int deleted = repository.softDeleteById("task-1", deleteTime, deleteTime);
            
            // Then - 應該回傳 0，因為任務已經被刪除
            assertThat(deleted).isEqualTo(0);
            
            logger.info("正確處理重複軟刪除操作");
        }
    }

    @Nested
    @DisplayName("批次操作測試")
    class BatchOperationTest {

        @Test
        @DisplayName("應該能夠批次更新任務狀態")
        void shouldBatchUpdateTaskStatus() {
            // When
            LocalDateTime updateTime = LocalDateTime.now();
            List<String> idsToUpdate = Arrays.asList("task-1", "task-2");
            int updated = repository.updateStatusByIds(idsToUpdate, TaskStatus.COMPLETED, updateTime);
            
            // Then
            assertThat(updated).isEqualTo(2);
            
            // 驗證狀態已更新
            Optional<TaskEntity> task1 = repository.findById("task-1");
            Optional<TaskEntity> task2 = repository.findById("task-2");
            
            assertThat(task1).isPresent();
            assertThat(task1.get().getStatus()).isEqualTo(TaskStatus.COMPLETED);
            
            assertThat(task2).isPresent();
            assertThat(task2.get().getStatus()).isEqualTo(TaskStatus.COMPLETED);
            
            logger.info("成功批次更新 {} 個任務狀態", updated);
        }
    }

    @Nested
    @DisplayName("分頁查詢測試")
    class PaginationTest {

        @Test
        @DisplayName("應該能夠進行分頁查詢")
        void shouldSupportPaginatedQuery() {
            // Given - 新增更多測試資料
            for (int i = 4; i <= 10; i++) {
                TaskEntity task = new TaskEntity("task-" + i, "任務 " + i, "描述 " + i, 
                                                TaskStatus.PENDING, LocalDateTime.now().plusDays(i), 
                                                LocalDateTime.now());
                repository.save(task);
            }
            
            // When
            PageRequest pageRequest = PageRequest.of(0, 5);
            Page<TaskEntity> firstPage = repository.findAllActive(pageRequest);
            
            // Then
            assertThat(firstPage.getContent()).hasSize(5);
            assertThat(firstPage.getTotalElements()).isEqualTo(10); // 3 original + 7 new
            assertThat(firstPage.getTotalPages()).isEqualTo(2);
            assertThat(firstPage.hasNext()).isTrue();
            
            logger.info("分頁查詢結果: 第1頁 {}/{} 個任務，共 {} 頁", 
                       firstPage.getContent().size(), firstPage.getTotalElements(), firstPage.getTotalPages());
        }
    }

    @Nested
    @DisplayName("統計查詢測試")
    class StatisticsTest {

        @Test
        @DisplayName("應該能夠統計各狀態的任務數量")
        void shouldCountTasksByStatus() {
            // When
            List<Object[]> statistics = repository.countByStatus();
            
            // Then
            assertThat(statistics).isNotEmpty();
            
            // 驗證統計結果
            for (Object[] stat : statistics) {
                TaskStatus status = (TaskStatus) stat[0];
                Long count = (Long) stat[1];
                
                logger.info("狀態 {} 的任務數量: {}", status, count);
                
                switch (status) {
                    case PENDING -> assertThat(count).isEqualTo(1L);
                    case IN_PROGRESS -> assertThat(count).isEqualTo(1L);
                    case COMPLETED -> assertThat(count).isEqualTo(1L);
                    case TODO, CANCELLED -> {
                        // 這些狀態在測試資料中不存在，不需要特別處理
                    }
                }
            }
        }

        @Test
        @DisplayName("應該能夠統計指定時間範圍內創建的任務")
        void shouldCountTasksCreatedInDateRange() {
            // Given
            LocalDateTime startDate = LocalDateTime.now().minusDays(10);
            LocalDateTime endDate = LocalDateTime.now().plusDays(1);
            
            // When
            Long count = repository.countTasksCreatedBetween(startDate, endDate);
            
            // Then
            assertThat(count).isEqualTo(3L);
            
            logger.info("指定時間範圍內創建的任務數量: {}", count);
        }
    }

    @Nested
    @DisplayName("資料完整性測試")
    class DataIntegrityTest {

        @Test
        @DisplayName("應該驗證容器正在運行")
        void shouldVerifyContainerIsRunning() {
            // Then
            assertThat(isContainerRunning()).isTrue();
            assertThat(getJdbcUrl()).contains("postgresql");
            assertThat(getUsername()).isEqualTo("testuser");
            
            logger.info("PostgreSQL 容器運行狀態: {}", isContainerRunning());
            logger.info("JDBC URL: {}", getJdbcUrl());
        }

        @Test
        @DisplayName("應該正確處理樂觀鎖")
        void shouldHandleOptimisticLocking() {
            // Given
            TaskEntity task = repository.findById("task-1").orElseThrow();
            Long originalVersion = task.getVersion();
            
            // When
            task.setTitle("更新標題");
            TaskEntity updatedTask = repository.save(task);
            
            // Then
            assertThat(updatedTask.getVersion()).isGreaterThan(originalVersion);
            
            logger.info("樂觀鎖版本號從 {} 更新到 {}", originalVersion, updatedTask.getVersion());
        }
    }
}