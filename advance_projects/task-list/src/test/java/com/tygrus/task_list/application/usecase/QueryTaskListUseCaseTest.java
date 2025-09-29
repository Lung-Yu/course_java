package com.tygrus.task_list.application.usecase;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.tygrus.task_list.application.dto.TaskDTO;
import com.tygrus.task_list.application.dto.TaskQueryRequest;
import com.tygrus.task_list.application.dto.TaskQueryRequest.TaskSortField;
import com.tygrus.task_list.application.dto.TaskQueryRequest.SortDirection;
import com.tygrus.task_list.application.dto.PagedResult;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * QueryTaskListUseCase測試
 * 
 * 測試Collections Framework的各種應用：
 * - Stream API過濾和轉換
 * - 複雜的Predicate組合
 * - Comparator排序
 * - 分頁操作
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("QueryTaskListUseCase 測試")
class QueryTaskListUseCaseTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private QueryTaskListUseCase queryTaskListUseCase;

    private List<Task> sampleTasks;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        
        // 建立測試資料
        sampleTasks = Arrays.asList(
            createTask("Task A", "Description A", TaskStatus.PENDING, Priority.HIGH, now.minusDays(2)),
            createTask("Task B", "Description B", TaskStatus.IN_PROGRESS, Priority.MEDIUM, now.minusDays(1)),
            createTask("Task C", "Description C", TaskStatus.COMPLETED, Priority.LOW, now)
        );
    }

    @Test
    @DisplayName("應該返回所有任務 - 當無過濾條件時")
    void shouldReturnAllTasks_whenNoFilters() {
        // Arrange
        when(taskRepository.findAll()).thenReturn(sampleTasks);
        TaskQueryRequest request = TaskQueryRequest.allTasks();

        // Act
        PagedResult<TaskDTO> result = queryTaskListUseCase.execute(request);

        // Assert
        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.getPageInfo().getPage()).isEqualTo(0);
        assertThat(result.isFirst()).isTrue();
        assertThat(result.isLast()).isTrue();
        verify(taskRepository).findAll();
    }

    @Test
    @DisplayName("應該返回空結果 - 當沒有任務時")
    void shouldReturnEmptyResult_whenNoTasksExist() {
        // Arrange
        when(taskRepository.findAll()).thenReturn(Collections.emptyList());
        TaskQueryRequest request = TaskQueryRequest.allTasks();

        // Act
        PagedResult<TaskDTO> result = queryTaskListUseCase.execute(request);

        // Assert
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
        assertThat(result.getTotalPages()).isEqualTo(0);
        verify(taskRepository).findAll();
    }

    @Test
    @DisplayName("應該過濾特定狀態的任務")
    void shouldFilterTasksByStatus() {
        // Arrange
        when(taskRepository.findAll()).thenReturn(sampleTasks);
        TaskQueryRequest request = TaskQueryRequest.builder()
            .statusFilter(TaskStatus.PENDING)
            .build();

        // Act
        PagedResult<TaskDTO> result = queryTaskListUseCase.execute(request);

        // Assert
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getStatus()).isEqualTo(TaskStatus.PENDING);
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("應該過濾高優先級任務")
    void shouldFilterHighPriorityTasks() {
        // Arrange
        when(taskRepository.findAll()).thenReturn(sampleTasks);
        TaskQueryRequest request = TaskQueryRequest.builder()
            .priorityFilter(Priority.HIGH)
            .build();

        // Act
        PagedResult<TaskDTO> result = queryTaskListUseCase.execute(request);

        // Assert
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getPriority()).isEqualTo(Priority.HIGH);
    }

    @Test
    @DisplayName("應該按標題升序排序")
    void shouldSortByTitleAscending() {
        // Arrange
        when(taskRepository.findAll()).thenReturn(sampleTasks);
        TaskQueryRequest request = TaskQueryRequest.builder()
            .sortBy(TaskSortField.TITLE, SortDirection.ASC)
            .build();

        // Act
        PagedResult<TaskDTO> result = queryTaskListUseCase.execute(request);

        // Assert
        assertThat(result.getContent())
            .extracting(TaskDTO::getTitle)
            .containsExactly("Task A", "Task B", "Task C");
    }

    @Test
    @DisplayName("應該正確分頁 - 第一頁")
    void shouldPaginateCorrectly_firstPage() {
        // Arrange
        when(taskRepository.findAll()).thenReturn(sampleTasks);
        TaskQueryRequest request = TaskQueryRequest.builder()
            .page(0)
            .pageSize(2)
            .build();

        // Act
        PagedResult<TaskDTO> result = queryTaskListUseCase.execute(request);

        // Assert
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getTotalPages()).isEqualTo(2);
        assertThat(result.getPageInfo().getPage()).isEqualTo(0);
        assertThat(result.isFirst()).isTrue();
        assertThat(result.isLast()).isFalse();
    }

    @Test
    @DisplayName("應該處理負數頁碼")
    void shouldHandleNegativePageNumber() {
        // Arrange
        when(taskRepository.findAll()).thenReturn(sampleTasks);
        TaskQueryRequest request = TaskQueryRequest.builder()
            .page(-1)
            .build();

        // Act
        PagedResult<TaskDTO> result = queryTaskListUseCase.execute(request);

        // Assert
        assertThat(result.getPageInfo().getPage()).isEqualTo(0); // 應該被修正為0
    }

    // 測試資料建立輔助方法
    private Task createTask(String title, String description, TaskStatus status, 
                           Priority priority, LocalDateTime createdAt) {
        Task task = Task.builder()
            .id(TaskId.generate())
            .title(title)
            .description(description)
            .priority(priority)
            .createdAt(createdAt)
            .build();
        
        // 根據業務規則進行狀態轉換
        if (status == TaskStatus.IN_PROGRESS) {
            task.updateStatus(TaskStatus.IN_PROGRESS);
        } else if (status == TaskStatus.COMPLETED) {
            // 必須先轉換到IN_PROGRESS，再到COMPLETED
            task.updateStatus(TaskStatus.IN_PROGRESS);
            task.updateStatus(TaskStatus.COMPLETED);
        } else if (status == TaskStatus.CANCELLED) {
            task.updateStatus(TaskStatus.CANCELLED);
        }
        // PENDING是預設狀態，不需要轉換
        
        return task;
    }
}