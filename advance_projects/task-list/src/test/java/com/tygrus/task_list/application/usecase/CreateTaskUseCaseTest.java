package com.tygrus.task_list.application.usecase;

import com.tygrus.task_list.application.dto.CreateTaskRequest;
import com.tygrus.task_list.application.dto.TaskDTO;
import com.tygrus.task_list.domain.model.*;
import com.tygrus.task_list.domain.repository.TaskRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * TDD測試 - CreateTaskUseCase
 * 
 * 測試UC-001: 創建任務的完整業務邏輯
 * 整合Domain模型與Application層服務
 */
@DisplayName("CreateTaskUseCase測試")
class CreateTaskUseCaseTest {

    @Mock
    private TaskRepository taskRepository;
    
    private CreateTaskUseCase createTaskUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        createTaskUseCase = new CreateTaskUseCase(taskRepository);
    }

    @Nested
    @DisplayName("成功創建任務測試")
    class SuccessfulTaskCreationTests {

        @Test
        @DisplayName("應該成功創建任務 - 當提供所有必填欄位時")
        void shouldCreateTask_whenAllRequiredFieldsProvided() {
            // Arrange
            CreateTaskRequest request = CreateTaskRequest.builder()
                .title("完成專案文件")
                .description("撰寫技術架構文件")
                .priority(Priority.HIGH)
                .dueDate(LocalDateTime.now().plusDays(7))
                .build();

            Task expectedTask = Task.builder()
                .id(TaskId.generate())
                .title(request.getTitle())
                .description(request.getDescription())
                .priority(request.getPriority())
                .dueDate(request.getDueDate())
                .createdAt(LocalDateTime.now())
                .build();

            when(taskRepository.save(any(Task.class))).thenReturn(expectedTask);

            // Act
            TaskDTO result = createTaskUseCase.execute(request);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isNotNull();
            assertThat(result.getTitle()).isEqualTo(request.getTitle());
            assertThat(result.getDescription()).isEqualTo(request.getDescription());
            assertThat(result.getPriority()).isEqualTo(request.getPriority());
            assertThat(result.getDueDate()).isEqualTo(request.getDueDate());
            assertThat(result.getStatus()).isEqualTo(TaskStatus.PENDING);
            assertThat(result.getCreatedAt()).isNotNull();
            
            // 驗證repository被正確調用
            verify(taskRepository).save(argThat(task -> 
                task.getTitle().equals(request.getTitle()) &&
                task.getDescription().equals(request.getDescription()) &&
                task.getPriority().equals(request.getPriority()) &&
                task.getDueDate().equals(request.getDueDate())
            ));
        }

        @Test
        @DisplayName("應該成功創建任務 - 當只提供必填欄位時")
        void shouldCreateTask_whenOnlyRequiredFieldsProvided() {
            // Arrange
            CreateTaskRequest request = CreateTaskRequest.builder()
                .title("簡單任務")
                .build();

            Task expectedTask = Task.builder()
                .id(TaskId.generate())
                .title(request.getTitle())
                .priority(Priority.MEDIUM) // 預設優先級
                .createdAt(LocalDateTime.now())
                .build();

            when(taskRepository.save(any(Task.class))).thenReturn(expectedTask);

            // Act
            TaskDTO result = createTaskUseCase.execute(request);

            // Assert
            assertThat(result.getTitle()).isEqualTo("簡單任務");
            assertThat(result.getPriority()).isEqualTo(Priority.MEDIUM);
            assertThat(result.getDescription()).isNull();
            assertThat(result.getDueDate()).isNull();
            
            verify(taskRepository).save(any(Task.class));
        }

        @Test
        @DisplayName("應該自動設定創建時間和預設狀態")
        void shouldSetCreationTimeAndDefaultStatus() {
            // Arrange
            LocalDateTime beforeCreation = LocalDateTime.now();
            CreateTaskRequest request = CreateTaskRequest.builder()
                .title("測試任務")
                .build();

            Task expectedTask = Task.builder()
                .id(TaskId.generate())
                .title(request.getTitle())
                .priority(Priority.MEDIUM)
                .createdAt(LocalDateTime.now())
                .build();

            when(taskRepository.save(any(Task.class))).thenReturn(expectedTask);

            // Act
            TaskDTO result = createTaskUseCase.execute(request);
            LocalDateTime afterCreation = LocalDateTime.now();

            // Assert
            assertThat(result.getStatus()).isEqualTo(TaskStatus.PENDING);
            assertThat(result.getCreatedAt()).isBetween(beforeCreation, afterCreation);
            assertThat(result.getUpdatedAt()).isEqualTo(result.getCreatedAt());
        }
    }

    @Nested
    @DisplayName("驗證失敗測試")
    class ValidationFailureTests {

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"", "   ", "\t", "\n"})
        @DisplayName("應該拋出例外 - 當標題為null或空白時")
        void shouldThrowException_whenTitleIsNullOrBlank(String invalidTitle) {
            // Arrange
            CreateTaskRequest request = CreateTaskRequest.builder()
                .title(invalidTitle)
                .description("有效描述")
                .build();

            // Act & Assert
            assertThatThrownBy(() -> createTaskUseCase.execute(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Task title cannot be null or empty");

            // 驗證repository沒有被調用
            verifyNoInteractions(taskRepository);
        }

        @Test
        @DisplayName("應該拋出例外 - 當請求為null時")
        void shouldThrowException_whenRequestIsNull() {
            // Act & Assert
            assertThatThrownBy(() -> createTaskUseCase.execute(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("CreateTaskRequest cannot be null");

            verifyNoInteractions(taskRepository);
        }

        @Test
        @DisplayName("應該拋出例外 - 當標題超過長度限制時")
        void shouldThrowException_whenTitleExceedsMaxLength() {
            // Arrange
            String longTitle = "a".repeat(256); // 超過255字元
            CreateTaskRequest request = CreateTaskRequest.builder()
                .title(longTitle)
                .build();

            // Act & Assert
            assertThatThrownBy(() -> createTaskUseCase.execute(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Task title cannot exceed 255 characters");

            verifyNoInteractions(taskRepository);
        }

        @Test
        @DisplayName("應該拋出例外 - 當描述超過長度限制時")
        void shouldThrowException_whenDescriptionExceedsMaxLength() {
            // Arrange
            String longDescription = "a".repeat(1001); // 超過1000字元
            CreateTaskRequest request = CreateTaskRequest.builder()
                .title("有效標題")
                .description(longDescription)
                .build();

            // Act & Assert
            assertThatThrownBy(() -> createTaskUseCase.execute(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Task description cannot exceed 1000 characters");

            verifyNoInteractions(taskRepository);
        }

        @Test
        @DisplayName("應該拋出例外 - 當截止日期在過去時")
        void shouldThrowException_whenDueDateIsInPast() {
            // Arrange
            CreateTaskRequest request = CreateTaskRequest.builder()
                .title("有效標題")
                .dueDate(LocalDateTime.now().minusDays(1)) // 過去日期
                .build();

            // Act & Assert
            assertThatThrownBy(() -> createTaskUseCase.execute(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Due date cannot be in the past");

            verifyNoInteractions(taskRepository);
        }
    }

    @Nested
    @DisplayName("Repository整合測試")
    class RepositoryIntegrationTests {

        @Test
        @DisplayName("應該處理repository異常")
        void shouldHandleRepositoryException() {
            // Arrange
            CreateTaskRequest request = CreateTaskRequest.builder()
                .title("測試任務")
                .build();

            when(taskRepository.save(any(Task.class)))
                .thenThrow(new RuntimeException("Database connection failed"));

            // Act & Assert
            assertThatThrownBy(() -> createTaskUseCase.execute(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Database connection failed");

            verify(taskRepository).save(any(Task.class));
        }

        @Test
        @DisplayName("應該正確傳遞Task給repository")
        void shouldPassCorrectTaskToRepository() {
            // Arrange
            CreateTaskRequest request = CreateTaskRequest.builder()
                .title("詳細任務")
                .description("詳細描述")
                .priority(Priority.URGENT)
                .dueDate(LocalDateTime.now().plusDays(3))
                .build();

            Task expectedTask = Task.builder()
                .id(TaskId.generate())
                .title(request.getTitle())
                .description(request.getDescription())
                .priority(request.getPriority())
                .dueDate(request.getDueDate())
                .createdAt(LocalDateTime.now())
                .build();

            when(taskRepository.save(any(Task.class))).thenReturn(expectedTask);

            // Act
            createTaskUseCase.execute(request);

            // Assert - 詳細驗證傳給repository的Task物件
            verify(taskRepository).save(argThat(task -> {
                assertThat(task.getId()).isNotNull();
                assertThat(task.getTitle()).isEqualTo("詳細任務");
                assertThat(task.getDescription()).isEqualTo("詳細描述");
                assertThat(task.getPriority()).isEqualTo(Priority.URGENT);
                assertThat(task.getDueDate()).isEqualTo(request.getDueDate());
                assertThat(task.getStatus()).isEqualTo(TaskStatus.PENDING);
                assertThat(task.getCreatedAt()).isNotNull();
                return true;
            }));
        }
    }
}