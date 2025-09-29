package com.tygrus.task_list.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

/**
 * TDD測試 - Task領域實體
 * 
 * 測試基於UC-001: 創建任務的需求
 * - 任務必須有標題、描述、優先級、截止日期
 * - 任務創建時預設狀態為PENDING
 * - 任務必須有唯一ID
 */
@DisplayName("Task實體測試")
class TaskTest {

    @Nested
    @DisplayName("任務創建測試")
    class TaskCreationTests {

        @Test
        @DisplayName("應該成功創建任務 - 當提供所有必填欄位時")
        void shouldCreateTask_whenAllRequiredFieldsProvided() {
            // Arrange
            TaskId taskId = TaskId.generate();
            String title = "完成專案文件";
            String description = "撰寫技術架構文件";
            Priority priority = Priority.HIGH;
            LocalDateTime dueDate = LocalDateTime.now().plusDays(7);
            LocalDateTime now = LocalDateTime.now();

            // Act
            Task task = Task.builder()
                .id(taskId)
                .title(title)
                .description(description)
                .priority(priority)
                .dueDate(dueDate)
                .createdAt(now)
                .build();

            // Assert
            assertThat(task.getId()).isEqualTo(taskId);
            assertThat(task.getTitle()).isEqualTo(title);
            assertThat(task.getDescription()).isEqualTo(description);
            assertThat(task.getPriority()).isEqualTo(priority);
            assertThat(task.getDueDate()).isEqualTo(dueDate);
            assertThat(task.getCreatedAt()).isEqualTo(now);
            assertThat(task.getStatus()).isEqualTo(TaskStatus.PENDING); // 預設狀態
            assertThat(task.getUpdatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("應該拋出例外 - 當標題為null時")
        void shouldThrowException_whenTitleIsNull() {
            // Arrange
            TaskId taskId = TaskId.generate();
            LocalDateTime now = LocalDateTime.now();

            // Act & Assert
            assertThatThrownBy(() -> 
                Task.builder()
                    .id(taskId)
                    .title(null) // null標題
                    .description("描述")
                    .priority(Priority.MEDIUM)
                    .dueDate(now.plusDays(1))
                    .createdAt(now)
                    .build()
            ).isInstanceOf(IllegalArgumentException.class)
             .hasMessageContaining("Task title cannot be null or empty");
        }

        @Test
        @DisplayName("應該拋出例外 - 當標題為空字串時")
        void shouldThrowException_whenTitleIsEmpty() {
            // Arrange
            TaskId taskId = TaskId.generate();
            LocalDateTime now = LocalDateTime.now();

            // Act & Assert
            assertThatThrownBy(() -> 
                Task.builder()
                    .id(taskId)
                    .title("   ") // 空白字串
                    .description("描述")
                    .priority(Priority.MEDIUM)
                    .dueDate(now.plusDays(1))
                    .createdAt(now)
                    .build()
            ).isInstanceOf(IllegalArgumentException.class)
             .hasMessageContaining("Task title cannot be null or empty");
        }

        @Test
        @DisplayName("應該拋出例外 - 當TaskId為null時")
        void shouldThrowException_whenTaskIdIsNull() {
            // Arrange
            LocalDateTime now = LocalDateTime.now();

            // Act & Assert
            assertThatThrownBy(() -> 
                Task.builder()
                    .id(null) // null ID
                    .title("有效標題")
                    .description("描述")
                    .priority(Priority.MEDIUM)
                    .dueDate(now.plusDays(1))
                    .createdAt(now)
                    .build()
            ).isInstanceOf(IllegalArgumentException.class)
             .hasMessageContaining("TaskId cannot be null");
        }
    }

    @Nested
    @DisplayName("任務狀態管理測試")
    class TaskStatusManagementTests {

        @Test
        @DisplayName("應該成功更新狀態 - 當狀態轉換合法時")
        void shouldUpdateStatus_whenValidStatusTransition() {
            // Arrange
            Task task = createValidTask();
            TaskStatus newStatus = TaskStatus.IN_PROGRESS;

            // Act
            task.updateStatus(newStatus);

            // Assert
            assertThat(task.getStatus()).isEqualTo(newStatus);
            assertThat(task.getUpdatedAt()).isAfter(task.getCreatedAt());
        }

        @Test
        @DisplayName("應該拋出例外 - 當狀態轉換不合法時")
        void shouldThrowException_whenInvalidStatusTransition() {
            // Arrange
            Task task = createValidTask();
            task.updateStatus(TaskStatus.COMPLETED); // 先完成任務

            // Act & Assert - 不能從COMPLETED轉回IN_PROGRESS
            assertThatThrownBy(() -> task.updateStatus(TaskStatus.IN_PROGRESS))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Invalid status transition from COMPLETED to IN_PROGRESS");
        }
    }

    // Helper method
    private Task createValidTask() {
        return Task.builder()
            .id(TaskId.generate())
            .title("測試任務")
            .description("測試描述")
            .priority(Priority.MEDIUM)
            .dueDate(LocalDateTime.now().plusDays(1))
            .createdAt(LocalDateTime.now())
            .build();
    }
}