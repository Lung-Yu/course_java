package com.tygrus.task_list.application.usecase;

import com.tygrus.task_list.application.dto.PagedResult;
import com.tygrus.task_list.application.dto.TaskDTO;
import com.tygrus.task_list.application.dto.TaskQueryRequest;
import com.tygrus.task_list.application.dto.TaskQueryRequest.TaskSortField;
import com.tygrus.task_list.application.dto.TaskQueryRequest.SortDirection;
import com.tygrus.task_list.domain.model.Task;
import com.tygrus.task_list.domain.repository.TaskRepository;

import org.springframework.stereotype.Service;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * 查詢任務列表的Use Case (UC-002)
 * 
 * 展示Collections Framework的強大功能：
 * - Stream API進行複雜的過濾和轉換
 * - Predicate組合進行多條件過濾
 * - Comparator進行多欄位排序
 * - 手動分頁實現展示List的subList操作
 * 
 * 這個類是學習Collections Framework的絕佳範例
 */
@Service
public class QueryTaskListUseCase {

    private final TaskRepository taskRepository;

    public QueryTaskListUseCase(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    /**
     * 執行查詢任務列表
     * 
     * @param request 查詢請求參數
     * @return 分頁的任務列表
     */
    public PagedResult<TaskDTO> execute(TaskQueryRequest request) {
        // 1. 從Repository獲取所有任務
        List<Task> allTasks = taskRepository.findAll();

        // 2. 使用Stream API進行過濾
        Stream<Task> filteredStream = allTasks.stream()
            .filter(buildFilterPredicate(request));

        // 3. 轉換為DTO並收集到List
        List<TaskDTO> filteredTasks = filteredStream
            .map(this::convertToDTO)
            .toList();

        // 4. 排序 (使用Comparator)
        List<TaskDTO> sortedTasks = applySorting(filteredTasks, request);

        // 5. 分頁 (手動實現展示List操作)
        return applyPagination(sortedTasks, request);
    }

    /**
     * 建立複合過濾條件
     * 展示Predicate的組合使用和方法引用
     */
    private Predicate<Task> buildFilterPredicate(TaskQueryRequest request) {
        Predicate<Task> predicate = task -> true; // 初始條件：接受所有

        // 狀態過濾 - 展示List.contains的使用
        if (request.hasStatusFilter()) {
            predicate = predicate.and(task -> 
                request.getStatusFilter().contains(task.getStatus()));
        }

        // 優先級過濾
        if (request.hasPriorityFilter()) {
            predicate = predicate.and(task -> 
                request.getPriorityFilter().contains(task.getPriority()));
        }

        // 標題包含 - 展示String操作和Optional處理
        if (request.hasTitleFilter()) {
            String searchTerm = request.getTitleContains().toLowerCase().trim();
            predicate = predicate.and(task -> 
                task.getTitle().toLowerCase().contains(searchTerm));
        }

        // 描述包含 - 處理null值的情況
        if (request.hasDescriptionFilter()) {
            String searchTerm = request.getDescriptionContains().toLowerCase().trim();
            predicate = predicate.and(task -> {
                String description = task.getDescription();
                return description != null && 
                       description.toLowerCase().contains(searchTerm);
            });
        }

        return predicate;
    }

    /**
     * 應用排序邏輯
     * 展示Comparator的使用和方法引用
     */
    private List<TaskDTO> applySorting(List<TaskDTO> tasks, TaskQueryRequest request) {
        if (tasks.isEmpty()) {
            return tasks;
        }

        Comparator<TaskDTO> comparator = createComparator(request.getSortField());
        
        // 根據排序方向決定是否反轉
        if (request.getSortDirection() == SortDirection.DESC) {
            comparator = comparator.reversed();
        }

        // 使用Stream進行排序並收集結果
        return tasks.stream()
            .sorted(comparator)
            .toList();
    }

    /**
     * 建立排序比較器
     * 展示Comparator的多種用法和方法引用
     */
    private Comparator<TaskDTO> createComparator(TaskSortField sortField) {
        return switch (sortField) {
            case TITLE -> Comparator.comparing(TaskDTO::getTitle, 
                String.CASE_INSENSITIVE_ORDER);
            case STATUS -> Comparator.comparing(dto -> dto.getStatus().name());
            case PRIORITY -> Comparator.comparing(dto -> dto.getPriority().ordinal());
            case CREATED_AT -> Comparator.comparing(TaskDTO::getCreatedAt);
            case UPDATED_AT -> Comparator.comparing(TaskDTO::getUpdatedAt);
        };
    }

    /**
     * 應用分頁邏輯
     * 展示List的subList操作和邊界處理
     */
    private PagedResult<TaskDTO> applyPagination(List<TaskDTO> tasks, TaskQueryRequest request) {
        int totalElements = tasks.size();
        int offset = request.getOffset();
        int pageSize = request.getPageSize();

        // 處理邊界情況
        if (offset >= totalElements) {
            return PagedResult.empty(request.getPage(), pageSize);
        }

        // 計算結束位置，使用Math.min避免越界
        int endIndex = Math.min(offset + pageSize, totalElements);
        
        // 使用subList進行分頁切片
        List<TaskDTO> pageContent = tasks.subList(offset, endIndex);

        return PagedResult.of(pageContent, request.getPage(), pageSize, totalElements);
    }

    /**
     * 轉換Task為TaskDTO
     * 展示Optional的處理和建造者模式
     */
    private TaskDTO convertToDTO(Task task) {
        return TaskDTO.builder()
            .id(task.getId().getValue())
            .title(task.getTitle())
            .description(task.getDescription()) // description已經是String，可能為null
            .status(task.getStatus())
            .priority(task.getPriority())
            .createdAt(task.getCreatedAt())
            .updatedAt(task.getUpdatedAt())
            .build();
    }

    // === 以下是測試支援方法，展示Collections Framework的更多用法 ===

    /**
     * 統計不同狀態的任務數量
     * 展示Stream的groupingBy和counting收集器
     */
    @SuppressWarnings("unused") // 為測試準備的方法
    private java.util.Map<com.tygrus.task_list.domain.model.TaskStatus, Long> getTaskStatusStatistics() {
        return taskRepository.findAll().stream()
            .collect(java.util.stream.Collectors.groupingBy(
                Task::getStatus,
                java.util.stream.Collectors.counting()
            ));
    }

    /**
     * 獲取高優先級任務
     * 展示Stream的filter和method reference
     */
    @SuppressWarnings("unused") // 為測試準備的方法  
    private List<Task> getHighPriorityTasks() {
        return taskRepository.findAll().stream()
            .filter(task -> task.getPriority() == 
                com.tygrus.task_list.domain.model.Priority.HIGH)
            .toList();
    }
}