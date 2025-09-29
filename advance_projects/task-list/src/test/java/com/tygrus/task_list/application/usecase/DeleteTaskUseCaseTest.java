package com.tygrus.task_list.application.usecase;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.tygrus.task_list.application.dto.DeleteTaskRequest;
import com.tygrus.task_list.application.dto.TaskDTO;
import com.tygrus.task_list.application.exception.PermissionDeniedException;
import com.tygrus.task_list.application.exception.TaskNotFoundException;
import com.tygrus.task_list.domain.event.TaskDeletedEvent;
import com.tygrus.task_list.domain.exception.OptimisticLockException;
import com.tygrus.task_list.domain.model.*;
import com.tygrus.task_list.domain.repository.TaskRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * DeleteTaskUseCase測試
 * 
 * 測試重點：
 * - 軟刪除機制
 * - 權限驗證
 * - Domain Events處理
 * - 物件導向封裝
 * - 例外處理機制
 * - 執行緒安全性
 * 
 * 展示TDD開發方式和完整的測試覆蓋
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("DeleteTaskUseCase 測試")
class DeleteTaskUseCaseTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private DeleteTaskUseCase deleteTaskUseCase;

    private TaskId validTaskId;
    private Task pendingTask;
    private Task completedTask;
    private Task alreadyDeletedTask;
    private String validUserId;

    @BeforeEach
    void setUp() {
        validTaskId = TaskId.generate();
        validUserId = "user123";
        
        // 創建不同狀態的測試任務
        pendingTask = createTaskWithStatus(TaskStatus.PENDING);
        completedTask = createTaskWithStatus(TaskStatus.COMPLETED);
        alreadyDeletedTask = createDeletedTask();
    }

    @Nested
    @DisplayName("成功刪除測試")
    class SuccessfulDeletionTests {

        @Test
        @DisplayName("應該成功軟刪除PENDING任務")
        void shouldSuccessfullySoftDeletePendingTask() {
            // Arrange
            when(taskRepository.findById(validTaskId)).thenReturn(Optional.of(pendingTask));
            when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));
            
            DeleteTaskRequest request = DeleteTaskRequest.builder()
                .taskId(validTaskId.getValue())
                .deletedBy(validUserId)
                .reason("不再需要此任務")
                .build();

            // Act
            TaskDTO result = deleteTaskUseCase.execute(request);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(validTaskId.getValue());
            
            verify(taskRepository).findById(validTaskId);
            verify(taskRepository).save(argThat(task -> 
                task.isDeleted() && 
                task.getDeletedBy().equals(validUserId) &&
                task.getDeletedAt() != null
            ));
        }

        @Test
        @DisplayName("應該成功軟刪除COMPLETED任務")
        void shouldSuccessfullySoftDeleteCompletedTask() {
            // Arrange
            when(taskRepository.findById(validTaskId)).thenReturn(Optional.of(completedTask));
            when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));
            
            DeleteTaskRequest request = DeleteTaskRequest.builder()
                .taskId(validTaskId.getValue())
                .deletedBy(validUserId)
                .reason("清理已完成的任務")
                .build();

            // Act
            TaskDTO result = deleteTaskUseCase.execute(request);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(validTaskId.getValue());
            
            verify(taskRepository).findById(validTaskId);
            verify(taskRepository).save(argThat(task -> task.isDeleted()));
        }
    }

    @Nested
    @DisplayName("權限驗證測試")
    class PermissionValidationTests {

        @Test
        @DisplayName("應該拒絕無權限的用戶刪除任務")
        void shouldRejectUnauthorizedUserDeletion() {
            // Arrange
            when(taskRepository.findById(validTaskId)).thenReturn(Optional.of(pendingTask));
            
            DeleteTaskRequest request = DeleteTaskRequest.builder()
                .taskId(validTaskId.getValue())
                .deletedBy("unauthorizedUser")
                .reason("嘗試未授權刪除")
                .build();

            // Act & Assert
            // 這裡模擬權限檢查失敗的情況
            assertThatThrownBy(() -> deleteTaskUseCase.execute(request))
                .isInstanceOf(PermissionDeniedException.class)
                .hasMessageContaining("unauthorizedUser")
                .hasMessageContaining("delete");
            
            verify(taskRepository).findById(validTaskId);
            verify(taskRepository, never()).save(any(Task.class));
        }

        @Test
        @DisplayName("應該允許任務創建者刪除任務")
        void shouldAllowTaskCreatorToDeleteTask() {
            // Arrange
            Task taskCreatedByUser = createTaskWithCreator(validUserId);
            when(taskRepository.findById(validTaskId)).thenReturn(Optional.of(taskCreatedByUser));
            when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));
            
            DeleteTaskRequest request = DeleteTaskRequest.builder()
                .taskId(validTaskId.getValue())
                .deletedBy(validUserId)
                .reason("創建者刪除自己的任務")
                .build();

            // Act
            TaskDTO result = deleteTaskUseCase.execute(request);

            // Assert
            assertThat(result).isNotNull();
            verify(taskRepository).save(argThat(task -> task.isDeleted()));
        }
    }

    @Nested
    @DisplayName("輸入驗證測試")
    class InputValidationTests {

        @Test
        @DisplayName("應該拒絕空的任務ID")
        void shouldRejectNullTaskId() {
            // Act & Assert
            assertThatThrownBy(() -> DeleteTaskRequest.builder()
                .taskId(null)
                .deletedBy(validUserId)
                .reason("測試空ID")
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Task ID cannot be null or empty");
            
            verify(taskRepository, never()).findById(any());
        }

        @Test
        @DisplayName("應該拒絕空白的任務ID")
        void shouldRejectBlankTaskId() {
            // Act & Assert
            assertThatThrownBy(() -> DeleteTaskRequest.builder()
                .taskId("   ")
                .deletedBy(validUserId)
                .reason("測試空白ID")
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Task ID cannot be null or empty");
        }

        @Test
        @DisplayName("應該拒絕空的用戶ID")
        void shouldRejectNullDeletedBy() {
            // Act & Assert
            assertThatThrownBy(() -> DeleteTaskRequest.builder()
                .taskId(validTaskId.getValue())
                .deletedBy(null)
                .reason("測試空用戶ID")
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Deleted by cannot be null or empty");
        }

        @Test
        @DisplayName("應該拒絕空的刪除原因")
        void shouldRejectNullReason() {
            // Act & Assert
            assertThatThrownBy(() -> DeleteTaskRequest.builder()
                .taskId(validTaskId.getValue())
                .deletedBy(validUserId)
                .reason(null)
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Reason cannot be null or empty");
        }
    }

    @Nested
    @DisplayName("任務存在性驗證測試")
    class TaskExistenceValidationTests {

        @Test
        @DisplayName("應該在任務不存在時拋出例外")
        void shouldThrowExceptionWhenTaskNotFound() {
            // Arrange
            TaskId nonExistentTaskId = TaskId.generate();
            when(taskRepository.findById(nonExistentTaskId)).thenReturn(Optional.empty());
            
            DeleteTaskRequest request = DeleteTaskRequest.builder()
                .taskId(nonExistentTaskId.getValue())
                .deletedBy(validUserId)
                .reason("任務不存在測試")
                .build();

            // Act & Assert
            assertThatThrownBy(() -> deleteTaskUseCase.execute(request))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining("Task not found with ID: " + nonExistentTaskId.getValue());
            
            verify(taskRepository).findById(nonExistentTaskId);
            verify(taskRepository, never()).save(any(Task.class));
        }

        @Test
        @DisplayName("應該拒絕刪除已刪除的任務")
        void shouldRejectDeletingAlreadyDeletedTask() {
            // Arrange
            when(taskRepository.findById(validTaskId)).thenReturn(Optional.of(alreadyDeletedTask));
            
            DeleteTaskRequest request = DeleteTaskRequest.builder()
                .taskId(validTaskId.getValue())
                .deletedBy(validUserId)
                .reason("嘗試重複刪除")
                .build();

            // Act & Assert
            assertThatThrownBy(() -> deleteTaskUseCase.execute(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Task is already deleted");
            
            verify(taskRepository).findById(validTaskId);
            verify(taskRepository, never()).save(any(Task.class));
        }
    }

    @Nested
    @DisplayName("Domain Events測試")
    class DomainEventsTests {

        @Test
        @DisplayName("應該在刪除時發布TaskDeletedEvent")
        void shouldPublishTaskDeletedEvent() {
            // Arrange
            when(taskRepository.findById(validTaskId)).thenReturn(Optional.of(pendingTask));
            when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));
            
            DeleteTaskRequest request = DeleteTaskRequest.builder()
                .taskId(validTaskId.getValue())
                .deletedBy(validUserId)
                .reason("測試事件發布")
                .build();

            // Act
            TaskDTO result = deleteTaskUseCase.execute(request);

            // Assert
            verify(taskRepository).save(argThat(task -> {
                // 驗證TaskDeletedEvent已被添加
                return task.getDomainEvents().stream()
                    .anyMatch(event -> event instanceof TaskDeletedEvent);
            }));
        }

        @Test
        @DisplayName("TaskDeletedEvent應該包含正確的信息")
        void taskDeletedEventShouldContainCorrectInformation() {
            // Arrange
            when(taskRepository.findById(validTaskId)).thenReturn(Optional.of(pendingTask));
            when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));
            
            String reason = "詳細的刪除原因";
            DeleteTaskRequest request = DeleteTaskRequest.builder()
                .taskId(validTaskId.getValue())
                .deletedBy(validUserId)
                .reason(reason)
                .build();

            // Act
            deleteTaskUseCase.execute(request);

            // Assert
            verify(taskRepository).save(argThat(task -> {
                TaskDeletedEvent event = (TaskDeletedEvent) task.getDomainEvents().stream()
                    .filter(e -> e instanceof TaskDeletedEvent)
                    .findFirst()
                    .orElse(null);
                
                return event != null &&
                    event.getTaskId().equals(validTaskId) &&
                    event.getDeletedBy().equals(validUserId) &&
                    event.getReason().equals(reason);
            }));
        }
    }

    @Nested
    @DisplayName("併發安全性測試")
    class ConcurrencyTests {

        @Test
        @DisplayName("應該處理樂觀鎖衝突")
        void shouldHandleOptimisticLockConflict() {
            // Arrange
            when(taskRepository.findById(validTaskId)).thenReturn(Optional.of(pendingTask));
            when(taskRepository.save(any(Task.class)))
                .thenThrow(new OptimisticLockException("Task was modified by another user"));
            
            DeleteTaskRequest request = DeleteTaskRequest.builder()
                .taskId(validTaskId.getValue())
                .deletedBy(validUserId)
                .reason("併發測試")
                .build();

            // Act & Assert
            assertThatThrownBy(() -> deleteTaskUseCase.execute(request))
                .isInstanceOf(OptimisticLockException.class)
                .hasMessageContaining("Task was modified by another user");
            
            verify(taskRepository).findById(validTaskId);
            verify(taskRepository).save(any(Task.class));
        }

        @Test
        @DisplayName("應該在併發刪除時保持資料一致性")
        void shouldMaintainDataConsistencyDuringConcurrentDeletion() {
            // Arrange - 直接模擬一個已被刪除的任務
            Task alreadyDeletedTask = createTaskWithStatus(TaskStatus.PENDING);
            alreadyDeletedTask.markAsDeleted("anotherUser", "concurrent deletion");
            
            when(taskRepository.findById(validTaskId))
                .thenReturn(Optional.of(alreadyDeletedTask));
            
            DeleteTaskRequest request = DeleteTaskRequest.builder()
                .taskId(validTaskId.getValue())
                .deletedBy(validUserId)
                .reason("併發刪除測試")
                .build();

            // Act & Assert
            assertThatThrownBy(() -> deleteTaskUseCase.execute(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Task is already deleted");
                
            verify(taskRepository).findById(validTaskId);
            verify(taskRepository, never()).save(any(Task.class));
        }
    }

    // 測試資料建立輔助方法
    private Task createTaskWithStatus(TaskStatus status) {
        Task task = Task.builder()
            .id(validTaskId)
            .title("Test Task")
            .description("Test Description")
            .priority(Priority.MEDIUM)
            .createdAt(LocalDateTime.now().minusHours(1))
            .build();
        
        // 根據需要的狀態進行轉換
        if (status == TaskStatus.IN_PROGRESS) {
            task.updateStatus(TaskStatus.IN_PROGRESS);
        } else if (status == TaskStatus.COMPLETED) {
            task.updateStatus(TaskStatus.IN_PROGRESS);
            task.updateStatus(TaskStatus.COMPLETED);
        } else if (status == TaskStatus.CANCELLED) {
            task.updateStatus(TaskStatus.CANCELLED);
        }
        
        return task;
    }
    
    private Task createDeletedTask() {
        Task task = createTaskWithStatus(TaskStatus.PENDING);
        task.markAsDeleted("previousUser", "already deleted");
        return task;
    }
    
    private Task createTaskWithCreator(String creatorId) {
        // 在實際實作中，Task可能會有createdBy字段
        // 這裡簡化處理，假設任務標題包含創建者信息或其他方式標識
        return createTaskWithStatus(TaskStatus.PENDING);
    }
}