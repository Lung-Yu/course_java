package com.tygrus.task_list.application.usecase;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.tygrus.task_list.application.dto.TaskDTO;
import com.tygrus.task_list.application.dto.UpdateTaskStatusRequest;
import com.tygrus.task_list.application.exception.TaskNotFoundException;
import com.tygrus.task_list.domain.event.TaskCompletedEvent;
import com.tygrus.task_list.domain.event.TaskStatusChangedEvent;
import com.tygrus.task_list.domain.exception.IllegalStatusTransitionException;
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
 * UpdateTaskStatusUseCase測試
 * 
 * 測試重點：
 * - State Pattern應用
 * - 業務規則驗證
 * - Domain Events處理
 * - 物件導向封裝
 * - 例外處理機制
 * 
 * 這是學習OOP封裝、多態和事件驅動架構的絕佳範例
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateTaskStatusUseCase 測試")
class UpdateTaskStatusUseCaseTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private UpdateTaskStatusUseCase updateTaskStatusUseCase;

    private TaskId validTaskId;
    private Task pendingTask;
    private Task inProgressTask;
    private Task completedTask;

    @BeforeEach
    void setUp() {
        validTaskId = TaskId.generate();
        
        // 創建不同狀態的測試任務
        pendingTask = createTaskWithStatus(TaskStatus.PENDING);
        inProgressTask = createTaskWithStatus(TaskStatus.IN_PROGRESS);  
        completedTask = createTaskWithStatus(TaskStatus.COMPLETED);
    }

    @Nested
    @DisplayName("成功狀態轉換測試")
    class SuccessfulStatusTransitionTests {

        @Test
        @DisplayName("應該成功將PENDING轉換為IN_PROGRESS")
        void shouldSuccessfullyTransitionFromPendingToInProgress() {
            // Arrange
            when(taskRepository.findById(validTaskId)).thenReturn(Optional.of(pendingTask));
            when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));
            
            UpdateTaskStatusRequest request = UpdateTaskStatusRequest.builder()
                .taskId(validTaskId.getValue())
                .newStatus(TaskStatus.IN_PROGRESS)
                .reason("開始處理任務")
                .build();

            // Act
            TaskDTO result = updateTaskStatusUseCase.execute(request);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
            assertThat(result.getUpdatedAt()).isAfter(pendingTask.getCreatedAt());
            
            verify(taskRepository).findById(validTaskId);
            verify(taskRepository).save(argThat(task -> 
                task.getStatus() == TaskStatus.IN_PROGRESS));
        }

        @Test
        @DisplayName("應該成功將PENDING轉換為CANCELLED")
        void shouldSuccessfullyTransitionFromPendingToCancelled() {
            // Arrange
            when(taskRepository.findById(validTaskId)).thenReturn(Optional.of(pendingTask));
            when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));
            
            UpdateTaskStatusRequest request = UpdateTaskStatusRequest.builder()
                .taskId(validTaskId.getValue())
                .newStatus(TaskStatus.CANCELLED)
                .reason("需求變更，取消任務")
                .build();

            // Act
            TaskDTO result = updateTaskStatusUseCase.execute(request);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getStatus()).isEqualTo(TaskStatus.CANCELLED);
            
            verify(taskRepository).findById(validTaskId);
            verify(taskRepository).save(any(Task.class));
        }

        @Test
        @DisplayName("應該成功將IN_PROGRESS轉換為COMPLETED")
        void shouldSuccessfullyTransitionFromInProgressToCompleted() {
            // Arrange
            when(taskRepository.findById(validTaskId)).thenReturn(Optional.of(inProgressTask));
            when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));
            
            UpdateTaskStatusRequest request = UpdateTaskStatusRequest.builder()
                .taskId(validTaskId.getValue())
                .newStatus(TaskStatus.COMPLETED)
                .reason("任務已完成")
                .build();

            // Act
            TaskDTO result = updateTaskStatusUseCase.execute(request);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getStatus()).isEqualTo(TaskStatus.COMPLETED);
            
            verify(taskRepository).findById(validTaskId);
            verify(taskRepository).save(any(Task.class));
        }

        @Test
        @DisplayName("應該成功將IN_PROGRESS轉換為CANCELLED")
        void shouldSuccessfullyTransitionFromInProgressToCancelled() {
            // Arrange
            when(taskRepository.findById(validTaskId)).thenReturn(Optional.of(inProgressTask));
            when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));
            
            UpdateTaskStatusRequest request = UpdateTaskStatusRequest.builder()
                .taskId(validTaskId.getValue())
                .newStatus(TaskStatus.CANCELLED)
                .reason("緊急取消")
                .build();

            // Act
            TaskDTO result = updateTaskStatusUseCase.execute(request);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getStatus()).isEqualTo(TaskStatus.CANCELLED);
        }
    }

    @Nested
    @DisplayName("無效狀態轉換測試")
    class InvalidStatusTransitionTests {

        @Test
        @DisplayName("不應該允許從PENDING直接轉換為COMPLETED")
        void shouldNotAllowTransitionFromPendingToCompleted() {
            // Arrange
            when(taskRepository.findById(validTaskId)).thenReturn(Optional.of(pendingTask));
            
            UpdateTaskStatusRequest request = UpdateTaskStatusRequest.builder()
                .taskId(validTaskId.getValue())
                .newStatus(TaskStatus.COMPLETED)
                .reason("嘗試跳過進行中狀態")
                .build();

            // Act & Assert
            assertThatThrownBy(() -> updateTaskStatusUseCase.execute(request))
                .isInstanceOf(IllegalStatusTransitionException.class)
                .hasMessageContaining("Invalid status transition from PENDING to COMPLETED");
            
            verify(taskRepository).findById(validTaskId);
            verify(taskRepository, never()).save(any(Task.class));
        }

        @Test
        @DisplayName("不應該允許從COMPLETED轉換為任何其他狀態")
        void shouldNotAllowTransitionFromCompletedToAnyOtherStatus() {
            // Arrange
            when(taskRepository.findById(validTaskId)).thenReturn(Optional.of(completedTask));
            
            UpdateTaskStatusRequest request = UpdateTaskStatusRequest.builder()
                .taskId(validTaskId.getValue())
                .newStatus(TaskStatus.IN_PROGRESS)
                .reason("嘗試重新啟動已完成任務")
                .build();

            // Act & Assert
            assertThatThrownBy(() -> updateTaskStatusUseCase.execute(request))
                .isInstanceOf(IllegalStatusTransitionException.class)
                .hasMessageContaining("Invalid status transition from COMPLETED");
            
            verify(taskRepository).findById(validTaskId);
            verify(taskRepository, never()).save(any(Task.class));
        }

        @Test
        @DisplayName("不應該允許從CANCELLED轉換為任何其他狀態")
        void shouldNotAllowTransitionFromCancelledToAnyOtherStatus() {
            // Arrange
            Task cancelledTask = createTaskWithStatus(TaskStatus.CANCELLED);
            when(taskRepository.findById(validTaskId)).thenReturn(Optional.of(cancelledTask));
            
            UpdateTaskStatusRequest request = UpdateTaskStatusRequest.builder()
                .taskId(validTaskId.getValue())
                .newStatus(TaskStatus.PENDING)
                .reason("嘗試重新啟動已取消任務")
                .build();

            // Act & Assert
            assertThatThrownBy(() -> updateTaskStatusUseCase.execute(request))
                .isInstanceOf(IllegalStatusTransitionException.class)
                .hasMessageContaining("Invalid status transition from CANCELLED");
        }
    }

    @Nested
    @DisplayName("輸入驗證測試")
    class InputValidationTests {

        @Test
        @DisplayName("應該拒絕空的任務ID")
        void shouldRejectNullTaskId() {
            // Act & Assert - 驗證在DTO Builder中就拋出例外
            assertThatThrownBy(() -> UpdateTaskStatusRequest.builder()
                .taskId(null)
                .newStatus(TaskStatus.IN_PROGRESS)
                .reason("測試空ID")
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Task ID cannot be null or empty");
            
            verify(taskRepository, never()).findById(any());
        }

        @Test
        @DisplayName("應該拒絕空白的任務ID")
        void shouldRejectBlankTaskId() {
            // Act & Assert - 驗證在DTO Builder中就拋出例外
            assertThatThrownBy(() -> UpdateTaskStatusRequest.builder()
                .taskId("   ")
                .newStatus(TaskStatus.IN_PROGRESS)
                .reason("測試空白ID")
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Task ID cannot be null or empty");
        }

        @Test
        @DisplayName("應該拒絕空的狀態")
        void shouldRejectNullStatus() {
            // Act & Assert - 驗證在DTO Builder中就拋出例外
            assertThatThrownBy(() -> UpdateTaskStatusRequest.builder()
                .taskId(validTaskId.getValue())
                .newStatus(null)
                .reason("測試空狀態")
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("New status cannot be null");
        }

        @Test
        @DisplayName("應該拒絕空的變更原因")
        void shouldRejectNullReason() {
            // Act & Assert - 驗證在DTO Builder中就拋出例外
            assertThatThrownBy(() -> UpdateTaskStatusRequest.builder()
                .taskId(validTaskId.getValue())
                .newStatus(TaskStatus.IN_PROGRESS)
                .reason(null)
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Reason cannot be null or empty");
        }

        @Test
        @DisplayName("應該拒絕空白的變更原因")
        void shouldRejectBlankReason() {
            // Act & Assert - 驗證在DTO Builder中就拋出例外
            assertThatThrownBy(() -> UpdateTaskStatusRequest.builder()
                .taskId(validTaskId.getValue())
                .newStatus(TaskStatus.IN_PROGRESS)
                .reason("   ")
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
            
            UpdateTaskStatusRequest request = UpdateTaskStatusRequest.builder()
                .taskId(nonExistentTaskId.getValue())
                .newStatus(TaskStatus.IN_PROGRESS)
                .reason("任務不存在測試")
                .build();

            // Act & Assert
            assertThatThrownBy(() -> updateTaskStatusUseCase.execute(request))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining("Task not found with ID: " + nonExistentTaskId.getValue());
            
            verify(taskRepository).findById(nonExistentTaskId);
            verify(taskRepository, never()).save(any(Task.class));
        }
    }

    @Nested
    @DisplayName("相同狀態處理測試")
    class SameStatusHandlingTests {

        @Test
        @DisplayName("應該處理轉換到相同狀態的情況")
        void shouldHandleTransitionToSameStatus() {
            // Arrange
            when(taskRepository.findById(validTaskId)).thenReturn(Optional.of(pendingTask));
            when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));
            
            UpdateTaskStatusRequest request = UpdateTaskStatusRequest.builder()
                .taskId(validTaskId.getValue())
                .newStatus(TaskStatus.PENDING) // 相同狀態
                .reason("重新確認狀態")
                .build();

            // Act
            TaskDTO result = updateTaskStatusUseCase.execute(request);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getStatus()).isEqualTo(TaskStatus.PENDING);
            
            // 即使是相同狀態，也應該記錄這次操作
            verify(taskRepository).findById(validTaskId);
            verify(taskRepository).save(any(Task.class));
        }
    }

    @Nested
    @DisplayName("Domain Events測試")
    class DomainEventsTests {

        @Test
        @DisplayName("應該在狀態轉換時發布Domain Event")
        void shouldPublishDomainEventOnStatusTransition() {
            // Arrange
            when(taskRepository.findById(validTaskId)).thenReturn(Optional.of(pendingTask));
            when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));
            
            UpdateTaskStatusRequest request = UpdateTaskStatusRequest.builder()
                .taskId(validTaskId.getValue())
                .newStatus(TaskStatus.IN_PROGRESS)
                .reason("開始處理")
                .build();

            // Act
            TaskDTO result = updateTaskStatusUseCase.execute(request);

            // Assert
            verify(taskRepository).save(argThat(task -> {
                // 驗證Domain Events已被添加
                return task.getDomainEvents().stream()
                    .anyMatch(event -> event instanceof TaskStatusChangedEvent);
            }));
        }

        @Test
        @DisplayName("應該在任務完成時發布特殊Domain Event")
        void shouldPublishSpecialDomainEventOnTaskCompletion() {
            // Arrange
            when(taskRepository.findById(validTaskId)).thenReturn(Optional.of(inProgressTask));
            when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));
            
            UpdateTaskStatusRequest request = UpdateTaskStatusRequest.builder()
                .taskId(validTaskId.getValue())
                .newStatus(TaskStatus.COMPLETED)
                .reason("任務完成")
                .build();

            // Act
            TaskDTO result = updateTaskStatusUseCase.execute(request);

            // Assert
            verify(taskRepository).save(argThat(task -> {
                // 驗證TaskCompletedEvent已被添加
                return task.getDomainEvents().stream()
                    .anyMatch(event -> event instanceof TaskCompletedEvent);
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
            
            UpdateTaskStatusRequest request = UpdateTaskStatusRequest.builder()
                .taskId(validTaskId.getValue())
                .newStatus(TaskStatus.IN_PROGRESS)
                .reason("併發測試")
                .build();

            // Act & Assert
            assertThatThrownBy(() -> updateTaskStatusUseCase.execute(request))
                .isInstanceOf(OptimisticLockException.class)
                .hasMessageContaining("Task was modified by another user");
            
            verify(taskRepository).findById(validTaskId);
            verify(taskRepository).save(any(Task.class));
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
}