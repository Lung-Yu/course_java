package com.tygrus.task_list.presentation.controller;

import com.tygrus.task_list.application.dto.*;
import com.tygrus.task_list.application.usecase.QueryTaskListUseCase;
import com.tygrus.task_list.application.usecase.UpdateTaskStatusUseCase;
import com.tygrus.task_list.domain.model.TaskStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 任務看板控制器
 * 實現類似 Jira 的看板視圖，支援拖放操作
 */
@Controller
@RequestMapping("/tasks")
public class TaskBoardController {
    
    private static final Logger logger = LoggerFactory.getLogger(TaskBoardController.class);
    
    private final QueryTaskListUseCase queryTaskListUseCase;
    private final UpdateTaskStatusUseCase updateTaskStatusUseCase;
    
    public TaskBoardController(QueryTaskListUseCase queryTaskListUseCase, 
                              UpdateTaskStatusUseCase updateTaskStatusUseCase) {
        this.queryTaskListUseCase = queryTaskListUseCase;
        this.updateTaskStatusUseCase = updateTaskStatusUseCase;
    }
    
    /**
     * 顯示看板視圖
     */
    @GetMapping("/board")
    public String showBoard(Model model) {
        logger.debug("Displaying task board view");
        
        // 取得所有任務（不分頁）
        TaskQueryRequest queryRequest = TaskQueryRequest.builder()
            .page(0)
            .pageSize(1000)  // 取得大量任務用於看板顯示
            .sortField(TaskQueryRequest.TaskSortField.CREATED_AT)
            .sortDirection(TaskQueryRequest.SortDirection.DESC)
            .build();
        
        PagedResult<TaskDTO> result = queryTaskListUseCase.execute(queryRequest);
        List<TaskDTO> allTasks = result.getContent();
        
        // 按狀態分組
        Map<TaskStatus, List<TaskDTO>> tasksByStatus = allTasks.stream()
            .collect(Collectors.groupingBy(TaskDTO::getStatus));
        
        // 確保所有狀態都有對應的列表（即使為空）
        for (TaskStatus status : TaskStatus.values()) {
            tasksByStatus.putIfAbsent(status, List.of());
        }
        
        model.addAttribute("tasksByStatus", tasksByStatus);
        model.addAttribute("statuses", TaskStatus.values());
        
        // 統計數據
        model.addAttribute("totalTasks", allTasks.size());
        model.addAttribute("pendingCount", 
            tasksByStatus.getOrDefault(TaskStatus.PENDING, List.of()).size());
        model.addAttribute("inProgressCount", 
            tasksByStatus.getOrDefault(TaskStatus.IN_PROGRESS, List.of()).size());
        model.addAttribute("completedCount", 
            tasksByStatus.getOrDefault(TaskStatus.COMPLETED, List.of()).size());
        
        return "tasks/board";
    }
    
    /**
     * 更新任務狀態（拖放操作）
     */
    @PatchMapping("/{id}/status")
    @ResponseBody
    public ResponseEntity<?> updateTaskStatus(
            @PathVariable String id,
            @RequestBody Map<String, String> payload) {
        
        String newStatusStr = payload.get("status");
        logger.debug("Updating task {} status to {}", id, newStatusStr);
        
        try {
            TaskStatus newStatus = TaskStatus.valueOf(newStatusStr);
            
            // 使用 UpdateTaskStatusUseCase 更新狀態
            UpdateTaskStatusRequest request = UpdateTaskStatusRequest.builder()
                .taskId(id)
                .newStatus(newStatus)
                .reason("看板拖放更新")
                .updatedBy("user")
                .build();
            
            updateTaskStatusUseCase.execute(request);
            
            logger.info("Task {} status updated to {} successfully", id, newStatus);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "任務狀態已更新",
                "taskId", id,
                "newStatus", newStatus.name()
            ));
            
        } catch (IllegalArgumentException e) {
            logger.error("Invalid status value: {}", newStatusStr, e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "無效的狀態值: " + newStatusStr
            ));
        } catch (Exception e) {
            logger.error("Failed to update task status", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "更新失敗: " + e.getMessage()
            ));
        }
    }
}
