package com.tygrus.task_list.presentation.controller;

import com.tygrus.task_list.application.dto.*;
import com.tygrus.task_list.application.usecase.*;
import com.tygrus.task_list.domain.model.Priority;
import com.tygrus.task_list.domain.model.TaskId;
import com.tygrus.task_list.domain.model.TaskStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

/**
 * 任務管理MVC控制器
 * 
 * 基於Spring MVC架構實現前端畫面控制
 * 整合各種Use Case提供完整的任務管理功能
 */
@Controller
@RequestMapping("/tasks")
public class TaskViewController {
    
    private final CreateTaskUseCase createTaskUseCase;
    private final QueryTaskListUseCase queryTaskListUseCase;
    private final UpdateTaskStatusUseCase updateTaskStatusUseCase;
    private final DeleteTaskUseCase deleteTaskUseCase;
    private final TaskStatisticsUseCase taskStatisticsUseCase;
    
    public TaskViewController(
            CreateTaskUseCase createTaskUseCase,
            QueryTaskListUseCase queryTaskListUseCase,
            UpdateTaskStatusUseCase updateTaskStatusUseCase,
            DeleteTaskUseCase deleteTaskUseCase,
            TaskStatisticsUseCase taskStatisticsUseCase) {
        this.createTaskUseCase = createTaskUseCase;
        this.queryTaskListUseCase = queryTaskListUseCase;
        this.updateTaskStatusUseCase = updateTaskStatusUseCase;
        this.deleteTaskUseCase = deleteTaskUseCase;
        this.taskStatisticsUseCase = taskStatisticsUseCase;
    }
    
    /**
     * 顯示任務列表主頁
     */
    @GetMapping
    public String listTasks(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "status", required = false) List<TaskStatus> statusFilter,
            @RequestParam(value = "priority", required = false) List<Priority> priorityFilter,
            @RequestParam(value = "title", required = false) String titleContains,
            @RequestParam(value = "sortField", defaultValue = "CREATED_AT") TaskQueryRequest.TaskSortField sortField,
            @RequestParam(value = "sortDirection", defaultValue = "DESC") TaskQueryRequest.SortDirection sortDirection,
            Model model) {
        
        // 建構查詢請求
        TaskQueryRequest queryRequest = TaskQueryRequest.builder()
            .page(page)
            .pageSize(size)
            .statusFilter(statusFilter)
            .priorityFilter(priorityFilter)
            .titleContains(titleContains)
            .sortField(sortField)
            .sortDirection(sortDirection)
            .build();
        
        // 執行查詢
        PagedResult<TaskDTO> tasks = queryTaskListUseCase.execute(queryRequest);
        
        // 添加模型屬性
        model.addAttribute("tasks", tasks);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", tasks.getTotalPages());
        model.addAttribute("statusOptions", Arrays.asList(TaskStatus.values()));
        model.addAttribute("priorityOptions", Arrays.asList(Priority.values()));
        model.addAttribute("sortFieldOptions", Arrays.asList(TaskQueryRequest.TaskSortField.values()));
        model.addAttribute("currentFilters", queryRequest);
        
        // 添加統計資訊
        addStatistics(model);
        
        return "tasks/list";
    }
    
    /**
     * 顯示創建任務表單
     */
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("createTaskRequest", new CreateTaskRequest());
        model.addAttribute("priorityOptions", Arrays.asList(Priority.values()));
        return "tasks/create";
    }
    
    /**
     * 處理創建任務請求
     */
    @PostMapping("/create")
    public String createTask(
            @Valid @ModelAttribute CreateTaskRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("priorityOptions", Arrays.asList(Priority.values()));
            return "tasks/create";
        }
        
        try {
            TaskDTO createdTask = createTaskUseCase.execute(request);
            redirectAttributes.addFlashAttribute("successMessage", 
                "任務 '" + createdTask.getTitle() + "' 創建成功！");
            return "redirect:/tasks";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "創建任務失敗：" + e.getMessage());
            model.addAttribute("priorityOptions", Arrays.asList(Priority.values()));
            return "tasks/create";
        }
    }
    
    /**
     * 顯示任務詳情
     */
    @GetMapping("/{id}")
    public String showTaskDetail(@PathVariable String id, Model model) {
        try {
            // 建構查詢請求，只查詢特定ID的任務
            TaskQueryRequest queryRequest = TaskQueryRequest.builder()
                .page(0)
                .pageSize(1)
                .build();
            
            PagedResult<TaskDTO> result = queryTaskListUseCase.execute(queryRequest);
            TaskDTO task = result.getContent().stream()
                .filter(t -> t.getId().equals(id))
                .findFirst()
                .orElse(null);
            
            if (task == null) {
                return "redirect:/tasks?error=任務不存在";
            }
            
            model.addAttribute("task", task);
            model.addAttribute("statusOptions", Arrays.asList(TaskStatus.values()));
            
            return "tasks/detail";
        } catch (Exception e) {
            return "redirect:/tasks?error=" + e.getMessage();
        }
    }
    
    /**
     * 更新任務狀態
     */
    @PostMapping("/{id}/status")
    public String updateTaskStatus(
            @PathVariable String id,
            @RequestParam TaskStatus status,
            RedirectAttributes redirectAttributes) {
        
        try {
            UpdateTaskStatusRequest request = UpdateTaskStatusRequest.builder()
                .taskId(id)
                .newStatus(status)
                .reason("用戶更新狀態")
                .updatedBy("user")
                .build();
            updateTaskStatusUseCase.execute(request);
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "任務狀態更新成功！");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "更新任務狀態失敗：" + e.getMessage());
        }
        
        return "redirect:/tasks/" + id;
    }
    
    /**
     * 刪除任務
     */
    @PostMapping("/{id}/delete")
    public String deleteTask(
            @PathVariable String id,
            @RequestParam(defaultValue = "用戶刪除") String reason,
            RedirectAttributes redirectAttributes) {
        
        try {
            DeleteTaskRequest request = DeleteTaskRequest.builder()
                .taskId(id)
                .deletedBy("user")
                .reason(reason)
                .build();
            deleteTaskUseCase.execute(request);
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "任務刪除成功！");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "刪除任務失敗：" + e.getMessage());
        }
        
        return "redirect:/tasks";
    }
    
    /**
     * 顯示統計頁面
     */
    @GetMapping("/statistics")
    public String showStatistics(Model model) {
        addStatistics(model);
        return "tasks/statistics";
    }
    
    /**
     * 添加統計資訊到模型
     */
    private void addStatistics(Model model) {
        try {
            StatisticsRequest statisticsRequest = StatisticsRequest.builder()
                .lastDays(30)
                .reportTitle("系統統計")
                .build();
            
            StatisticsReport report = taskStatisticsUseCase.generateReport(statisticsRequest);
            model.addAttribute("statistics", report);
        } catch (Exception e) {
            model.addAttribute("statisticsError", "無法載入統計資訊：" + e.getMessage());
        }
    }
    
    /**
     * 錯誤處理頁面
     */
    @GetMapping("/error")
    public String showError(@RequestParam(required = false) String message, Model model) {
        model.addAttribute("errorMessage", message != null ? message : "發生未知錯誤");
        return "tasks/error";
    }
}