package com.tygrus.task_list.infrastructure.persistence.repository;

import com.tygrus.task_list.domain.model.Task;
import com.tygrus.task_list.domain.model.TaskId;
import com.tygrus.task_list.domain.model.TaskStatus;
import com.tygrus.task_list.infrastructure.testcontainers.PostgreSQLTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * PostgreSQL TaskRepository 整合測試
 * 測試 Domain Repository 介面的 PostgreSQL 實作
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("PostgreSQL TaskRepository 整合測試")
class PostgreSQLTaskRepositoryIntegrationTest extends PostgreSQLTestBase {

    private static final Logger logger = LoggerFactory.getLogger(PostgreSQLTaskRepositoryIntegrationTest.class);

    @Autowired
    private PostgreSQLTaskRepository repository;

    private Task sampleTask1;
    private Task sampleTask2;
    private Task sampleTask3;

    @BeforeEach
    void setUp() {
        logger.info("設置測試資料");
        
        // 準備測試任務
        LocalDateTime now = LocalDateTime.now();
        
        sampleTask1 = Task.restoreFromPersistence(
            TaskId.of("domain-task-1"),
            "實作 PostgreSQL Repository",
            "開發和測試 PostgreSQL 資料存取層",
            TaskStatus.IN_PROGRESS,
            now.plusDays(5),
            now
        );
        
        sampleTask2 = Task.restoreFromPersistence(
            TaskId.of("domain-task-2"),
            "Testcontainers 整合",
            "設定 Testcontainers 進行整合測試",
            TaskStatus.PENDING,
            now.plusDays(3),
            now
        );
        
        sampleTask3 = Task.restoreFromPersistence(
            TaskId.of("domain-task-3"),
            "效能最佳化",
            "優化資料庫查詢效能",
            TaskStatus.COMPLETED,
            now.plusDays(1),
            now.minusDays(2)
        );
        
        // 儲存測試資料
        repository.save(sampleTask1);
        repository.save(sampleTask2);
        repository.save(sampleTask3);
        
        logger.info("測試資料設置完成，共建立 {} 個 Domain Task", 3);
    }

    @Nested
    @DisplayName("Domain Repository 基本操作測試")
    class DomainRepositoryBasicOperationsTest {

        @Test
        @DisplayName("應該能夠儲存 Domain Task")
        void shouldSaveDomainTask() {
            // Given
            Task newTask = Task.restoreFromPersistence(
                TaskId.generate(),
                "新的 Domain Task",
                "測試儲存功能",
                TaskStatus.PENDING,
                LocalDateTime.now().plusDays(7),
                LocalDateTime.now()
            );
            
            // When
            Task savedTask = repository.save(newTask);
            
            // Then
            assertThat(savedTask).isNotNull();
            assertThat(savedTask.getId()).isEqualTo(newTask.getId());
            assertThat(savedTask.getTitle()).isEqualTo("新的 Domain Task");
            assertThat(savedTask.getStatus()).isEqualTo(TaskStatus.PENDING);
            
            // 驗證能夠從資料庫讀取
            Optional<Task> retrieved = repository.findById(savedTask.getId());
            assertThat(retrieved).isPresent();
            assertThat(retrieved.get().getTitle()).isEqualTo("新的 Domain Task");
            
            logger.info("成功儲存並驗證 Domain Task: {}", savedTask.getId().getValue());
        }

        @Test
        @DisplayName("應該能夠根據 TaskId 查找 Domain Task")
        void shouldFindDomainTaskById() {
            // When
            Optional<Task> found = repository.findById(TaskId.of("domain-task-1"));
            
            // Then
            assertThat(found).isPresent();
            assertThat(found.get().getTitle()).isEqualTo("實作 PostgreSQL Repository");
            assertThat(found.get().getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
            assertThat(found.get().getId().getValue()).isEqualTo("domain-task-1");
            
            logger.info("成功查找 Domain Task: {}", found.get().getId().getValue());
        }

        @Test
        @DisplayName("查找不存在的 TaskId 應該回傳空值")
        void shouldReturnEmptyForNonExistentTaskId() {
            // When
            Optional<Task> found = repository.findById(TaskId.of("non-existent-id"));
            
            // Then
            assertThat(found).isEmpty();
            
            logger.info("正確處理不存在的 TaskId 查詢");
        }

        @Test
        @DisplayName("應該能夠查找所有 Domain Task")
        void shouldFindAllDomainTasks() {
            // When
            List<Task> allTasks = repository.findAll();
            
            // Then
            assertThat(allTasks).hasSize(3);
            assertThat(allTasks).extracting(Task::getTitle)
                .containsExactlyInAnyOrder(
                    "實作 PostgreSQL Repository",
                    "Testcontainers 整合", 
                    "效能最佳化"
                );
            
            logger.info("查找到所有 {} 個 Domain Task", allTasks.size());
        }

        @Test
        @DisplayName("應該能夠根據狀態查找 Domain Task")
        void shouldFindDomainTasksByStatus() {
            // When
            List<Task> pendingTasks = repository.findByStatus(TaskStatus.PENDING);
            List<Task> completedTasks = repository.findByStatus(TaskStatus.COMPLETED);
            
            // Then
            assertThat(pendingTasks).hasSize(1);
            assertThat(pendingTasks.get(0).getTitle()).isEqualTo("Testcontainers 整合");
            
            assertThat(completedTasks).hasSize(1);
            assertThat(completedTasks.get(0).getTitle()).isEqualTo("效能最佳化");
            
            logger.info("根據狀態查找 Domain Task: PENDING={}, COMPLETED={}", 
                       pendingTasks.size(), completedTasks.size());
        }

        @Test
        @DisplayName("應該能夠軟刪除 Domain Task")
        void shouldSoftDeleteDomainTask() {
            // Given
            TaskId taskId = TaskId.of("domain-task-1");
            
            // 確認任務存在
            assertThat(repository.existsById(taskId)).isTrue();
            
            // When
            repository.deleteById(taskId);
            
            // Then
            // 確認任務已被軟刪除（在 domain 層面不可見）
            assertThat(repository.existsById(taskId)).isFalse();
            assertThat(repository.findById(taskId)).isEmpty();
            
            // 總數應該減少
            assertThat(repository.count()).isEqualTo(2);
            
            logger.info("成功軟刪除 Domain Task: {}", taskId.getValue());
        }

        @Test
        @DisplayName("應該能夠根據標題搜尋 Domain Task")
        void shouldFindDomainTasksByTitle() {
            // When
            List<Task> foundTasks = repository.findByTitle("PostgreSQL");
            
            // Then
            assertThat(foundTasks).hasSize(1);
            assertThat(foundTasks.get(0).getTitle()).contains("PostgreSQL");
            
            logger.info("根據標題搜尋找到 {} 個 Domain Task", foundTasks.size());
        }

        @Test
        @DisplayName("應該能夠統計 Domain Task 數量")
        void shouldCountDomainTasks() {
            // When
            long count = repository.count();
            
            // Then
            assertThat(count).isEqualTo(3);
            
            logger.info("Domain Task 總數: {}", count);
        }
    }

    @Nested
    @DisplayName("進階查詢功能測試")
    class AdvancedQueryTest {

        @Test
        @DisplayName("應該能夠查找過期的任務")
        void shouldFindOverdueTasks() {
            // Given - 建立一個已過期的任務
            Task overdueTask = Task.restoreFromPersistence(
                TaskId.generate(),
                "過期任務",
                "這是一個已過期的任務",
                TaskStatus.IN_PROGRESS,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusDays(5)
            );
            repository.save(overdueTask);
            
            // When
            List<Task> overdueTasks = repository.findOverdueTasks();
            
            // Then
            assertThat(overdueTasks).hasSizeGreaterThanOrEqualTo(1);
            assertThat(overdueTasks).anyMatch(task -> task.getTitle().equals("過期任務"));
            
            logger.info("查找到 {} 個過期任務", overdueTasks.size());
        }

        @Test
        @DisplayName("應該能夠查找即將到期的任務")
        void shouldFindTasksDueSoon() {
            // Given - 建立一個即將到期的任務
            Task soonDueTask = Task.restoreFromPersistence(
                TaskId.generate(),
                "即將到期任務",
                "這是一個即將到期的任務",
                TaskStatus.PENDING,
                LocalDateTime.now().plusHours(2),
                LocalDateTime.now()
            );
            repository.save(soonDueTask);
            
            // When
            List<Task> tasksDueSoon = repository.findTasksDueSoon(24); // 24小時內
            
            // Then
            assertThat(tasksDueSoon).hasSizeGreaterThanOrEqualTo(1);
            assertThat(tasksDueSoon).anyMatch(task -> task.getTitle().equals("即將到期任務"));
            
            logger.info("查找到 {} 個即將到期的任務", tasksDueSoon.size());
        }
    }

    @Nested
    @DisplayName("批次操作測試")
    class BatchOperationTest {

        @Test
        @DisplayName("應該能夠批次軟刪除多個任務")
        void shouldBatchSoftDeleteTasks() {
            // Given
            List<TaskId> taskIds = Arrays.asList(
                TaskId.of("domain-task-1"),
                TaskId.of("domain-task-2")
            );
            
            // 確認任務存在
            assertThat(repository.existsById(taskIds.get(0))).isTrue();
            assertThat(repository.existsById(taskIds.get(1))).isTrue();
            
            // When
            int deletedCount = repository.softDeleteTasks(taskIds);
            
            // Then
            assertThat(deletedCount).isEqualTo(2);
            
            // 確認任務已被軟刪除
            assertThat(repository.existsById(taskIds.get(0))).isFalse();
            assertThat(repository.existsById(taskIds.get(1))).isFalse();
            
            // 總數應該減少
            assertThat(repository.count()).isEqualTo(1);
            
            logger.info("成功批次軟刪除 {} 個任務", deletedCount);
        }

        @Test
        @DisplayName("應該能夠批次更新任務狀態")
        void shouldBatchUpdateTaskStatus() {
            // Given
            List<TaskId> taskIds = Arrays.asList(
                TaskId.of("domain-task-1"),
                TaskId.of("domain-task-2")
            );
            
            // When
            int updatedCount = repository.updateTaskStatus(taskIds, TaskStatus.COMPLETED);
            
            // Then
            assertThat(updatedCount).isEqualTo(2);
            
            // 驗證狀態已更新
            Optional<Task> task1 = repository.findById(taskIds.get(0));
            Optional<Task> task2 = repository.findById(taskIds.get(1));
            
            assertThat(task1).isPresent();
            assertThat(task1.get().getStatus()).isEqualTo(TaskStatus.COMPLETED);
            
            assertThat(task2).isPresent();
            assertThat(task2.get().getStatus()).isEqualTo(TaskStatus.COMPLETED);
            
            logger.info("成功批次更新 {} 個任務狀態", updatedCount);
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
                Task task = Task.restoreFromPersistence(
                    TaskId.of("page-task-" + i),
                    "分頁任務 " + i,
                    "測試分頁功能的任務 " + i,
                    TaskStatus.PENDING,
                    LocalDateTime.now().plusDays(i),
                    LocalDateTime.now()
                );
                repository.save(task);
            }
            
            // When
            List<Task> firstPage = repository.findAllWithPaging(0, 5, "createdAt", "DESC");
            List<Task> secondPage = repository.findAllWithPaging(1, 5, "createdAt", "DESC");
            
            // Then
            assertThat(firstPage).hasSize(5);
            assertThat(secondPage).hasSize(5);
            
            // 驗證分頁結果不重複
            assertThat(firstPage).doesNotContainAnyElementsOf(secondPage);
            
            logger.info("分頁查詢結果: 第1頁 {} 個任務，第2頁 {} 個任務", 
                       firstPage.size(), secondPage.size());
        }
    }

    @Nested
    @DisplayName("統計功能測試")
    class StatisticsTest {

        @Test
        @DisplayName("應該能夠獲取任務狀態統計")
        void shouldGetTaskStatisticsByStatus() {
            // When
            List<Object[]> statistics = repository.getTaskStatisticsByStatus();
            
            // Then
            assertThat(statistics).isNotEmpty();
            
            // 驗證統計結果
            for (Object[] stat : statistics) {
                TaskStatus status = (TaskStatus) stat[0];
                Long count = (Long) stat[1];
                
                logger.info("狀態 {} 的任務數量: {}", status, count);
                assertThat(count).isGreaterThan(0);
            }
        }
    }

    @Nested
    @DisplayName("資料一致性測試")
    class DataConsistencyTest {

        @Test
        @DisplayName("Domain Model 和 Entity 之間的轉換應該保持一致")
        void shouldMaintainConsistencyBetweenDomainAndEntity() {
            // Given
            Task originalTask = Task.restoreFromPersistence(
                TaskId.generate(),
                "一致性測試任務",
                "測試 Domain Model 和 Entity 轉換的一致性",
                TaskStatus.IN_PROGRESS,
                LocalDateTime.now().plusDays(3),
                LocalDateTime.now()
            );
            
            // When
            Task savedTask = repository.save(originalTask);
            Optional<Task> retrievedTask = repository.findById(savedTask.getId());
            
            // Then
            assertThat(retrievedTask).isPresent();
            
            Task retrieved = retrievedTask.get();
            assertThat(retrieved.getId()).isEqualTo(originalTask.getId());
            assertThat(retrieved.getTitle()).isEqualTo(originalTask.getTitle());
            assertThat(retrieved.getDescription()).isEqualTo(originalTask.getDescription());
            assertThat(retrieved.getStatus()).isEqualTo(originalTask.getStatus());
            assertThat(retrieved.getDueDate()).isEqualTo(originalTask.getDueDate());
            // Note: createdAt 可能會有微小差異，所以不做嚴格比對
            
            logger.info("Domain Model 和 Entity 轉換保持一致: {}", retrieved.getId().getValue());
        }

        @Test
        @DisplayName("應該正確處理空值和邊界條件")
        void shouldHandleNullValuesAndEdgeCases() {
            // Given
            Task taskWithOptionalFields = Task.builder()
                .id(TaskId.generate())
                .title("最小任務")
                .description(null) // 可選欄位
                .dueDate(null) // 可選欄位
                .createdAt(LocalDateTime.now())
                .build();
            
            // When
            Task savedTask = repository.save(taskWithOptionalFields);
            Optional<Task> retrievedTask = repository.findById(savedTask.getId());
            
            // Then
            assertThat(retrievedTask).isPresent();
            
            Task retrieved = retrievedTask.get();
            assertThat(retrieved.getDescription()).isNull();
            assertThat(retrieved.getDueDate()).isNull();
            assertThat(retrieved.getTitle()).isEqualTo("最小任務");
            assertThat(retrieved.getStatus()).isEqualTo(TaskStatus.PENDING);
            
            logger.info("正確處理可選欄位的 null 值: {}", retrieved.getId().getValue());
        }
    }

    @Nested
    @DisplayName("Testcontainers 整合驗證")
    class TestcontainersIntegrationTest {

        @Test
        @DisplayName("應該確認 PostgreSQL 容器正常運行")
        void shouldVerifyPostgreSQLContainerIsRunning() {
            // Then
            assertThat(isContainerRunning()).isTrue();
            assertThat(getJdbcUrl()).contains("postgresql");
            assertThat(getUsername()).isEqualTo("testuser");
            assertThat(getPassword()).isEqualTo("testpass");
            
            logger.info("PostgreSQL Testcontainer 運行狀態: {}", isContainerRunning());
            logger.info("容器 JDBC URL: {}", getJdbcUrl());
            logger.info("容器用戶名: {}", getUsername());
        }

        @Test
        @DisplayName("應該在真實 PostgreSQL 環境中執行所有操作")
        void shouldExecuteOperationsInRealPostgreSQL() {
            // Given - 執行一系列複雜的資料庫操作
            Task complexTask = Task.restoreFromPersistence(
                TaskId.generate(),
                "複雜任務測試",
                "在真實 PostgreSQL 環境中測試複雜操作",
                TaskStatus.PENDING,
                LocalDateTime.now().plusDays(5),
                LocalDateTime.now()
            );
            
            // When - 執行完整的 CRUD 週期
            Task savedTask = repository.save(complexTask);
            Optional<Task> foundTask = repository.findById(savedTask.getId());
            
            // 更新任務狀態
            foundTask.get().updateStatus(TaskStatus.IN_PROGRESS);
            repository.save(foundTask.get());
            
            // 再次查找確認更新
            Optional<Task> updatedFoundTask = repository.findById(savedTask.getId());
            
            // 軟刪除
            repository.deleteById(savedTask.getId());
            
            // Then
            assertThat(foundTask).isPresent();
            assertThat(updatedFoundTask).isPresent();
            assertThat(updatedFoundTask.get().getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
            
            // 確認軟刪除生效
            assertThat(repository.existsById(savedTask.getId())).isFalse();
            
            logger.info("在真實 PostgreSQL 環境中成功完成完整的 CRUD 操作週期");
        }
    }
}