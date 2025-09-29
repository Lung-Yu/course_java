# UC-004 DeleteTaskUseCase 使用範例

## 基本使用方式

### 1. 成功刪除任務

```java
// 準備刪除請求
DeleteTaskRequest request = DeleteTaskRequest.builder()
    .taskId("task-123")
    .deletedBy("user-456") 
    .reason("任務已完成，不再需要")
    .build();

// 執行刪除操作
TaskDTO deletedTask = deleteTaskUseCase.execute(request);

// 驗證結果
assertThat(deletedTask.getId()).isEqualTo("task-123");
// 注意：軟刪除後任務仍存在，只是標記為已刪除
```

### 2. 權限驗證範例

```java
// 未授權用戶嘗試刪除
DeleteTaskRequest unauthorizedRequest = DeleteTaskRequest.builder()
    .taskId("task-123")
    .deletedBy("unauthorizedUser")
    .reason("嘗試未授權刪除")
    .build();

// 會拋出 PermissionDeniedException
assertThatThrownBy(() -> deleteTaskUseCase.execute(unauthorizedRequest))
    .isInstanceOf(PermissionDeniedException.class)
    .hasMessageContaining("unauthorizedUser")
    .hasMessageContaining("does not have permission");
```

### 3. 異常處理範例

```java
public void handleTaskDeletion(String taskId, String userId, String reason) {
    try {
        DeleteTaskRequest request = DeleteTaskRequest.builder()
            .taskId(taskId)
            .deletedBy(userId)
            .reason(reason)
            .build();
            
        TaskDTO result = deleteTaskUseCase.execute(request);
        
        // 成功處理
        logger.info("Task {} successfully deleted by {}", taskId, userId);
        
    } catch (TaskNotFoundException e) {
        // 任務不存在
        logger.warn("Task {} not found", taskId);
        throw new BusinessException("指定的任務不存在");
        
    } catch (PermissionDeniedException e) {
        // 權限不足
        logger.warn("User {} has no permission to delete task {}", userId, taskId);
        throw new BusinessException("您沒有權限刪除此任務");
        
    } catch (IllegalStateException e) {
        // 業務規則違反 (如任務已被刪除)
        logger.warn("Cannot delete task {}: {}", taskId, e.getMessage());
        throw new BusinessException("無法刪除任務: " + e.getMessage());
        
    } catch (OptimisticLockException e) {
        // 併發衝突
        logger.warn("Concurrent modification detected for task {}", taskId);
        throw new BusinessException("任務已被其他用戶修改，請重試");
    }
}
```

## 在 Spring Boot Controller 中的使用

```java
@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    
    private final DeleteTaskUseCase deleteTaskUseCase;
    
    public TaskController(DeleteTaskUseCase deleteTaskUseCase) {
        this.deleteTaskUseCase = deleteTaskUseCase;
    }
    
    @DeleteMapping("/{taskId}")
    public ResponseEntity<?> deleteTask(
            @PathVariable String taskId,
            @RequestParam String reason,
            Authentication authentication) {
        
        try {
            // 從認證信息中獲取用戶ID
            String userId = authentication.getName();
            
            // 構建刪除請求
            DeleteTaskRequest request = DeleteTaskRequest.builder()
                .taskId(taskId)
                .deletedBy(userId)
                .reason(reason)
                .build();
            
            // 執行刪除
            TaskDTO deletedTask = deleteTaskUseCase.execute(request);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "任務已成功刪除",
                "task", deletedTask
            ));
            
        } catch (TaskNotFoundException e) {
            return ResponseEntity.notFound().build();
            
        } catch (PermissionDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "權限不足", "message", e.getMessage()));
                
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "操作失敗", "message", e.getMessage()));
                
        } catch (Exception e) {
            logger.error("Unexpected error during task deletion", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "內部錯誤", "message", "請聯繫系統管理員"));
        }
    }
}
```

## Domain Events 處理範例

### 1. 事件監聽器

```java
@Component
public class TaskDeletionEventHandler {
    
    private final NotificationService notificationService;
    private final AuditLogService auditLogService;
    
    public TaskDeletionEventHandler(
            NotificationService notificationService,
            AuditLogService auditLogService) {
        this.notificationService = notificationService;
        this.auditLogService = auditLogService;
    }
    
    @EventHandler
    public void handleTaskDeleted(TaskDeletedEvent event) {
        // 記錄審計日誌
        auditLogService.logTaskDeletion(
            event.getTaskId().getValue(),
            event.getDeletedBy(),
            event.getReason(),
            event.getOccurredOn()
        );
        
        // 發送通知給相關人員
        notificationService.notifyTaskDeletion(
            event.getTaskId().getValue(),
            event.getDeletedBy(),
            event.getReason()
        );
        
        // 更新統計指標
        metricsService.incrementDeletedTasksCount();
    }
}
```

### 2. 事件發布器

```java
@Service
public class DomainEventPublisher {
    
    private final ApplicationEventPublisher eventPublisher;
    
    public DomainEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }
    
    public void publishEvents(Task task) {
        // 發布任務上的所有Domain Events
        task.getDomainEvents().forEach(event -> {
            eventPublisher.publishEvent(event);
        });
        
        // 清除已發布的事件
        task.clearDomainEvents();
    }
}
```

## 測試範例

### 1. 集成測試

```java
@SpringBootTest
@Transactional
class DeleteTaskIntegrationTest {
    
    @Autowired
    private DeleteTaskUseCase deleteTaskUseCase;
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Test
    void shouldDeleteTaskSuccessfully() {
        // Given
        Task task = createAndSaveTestTask();
        
        DeleteTaskRequest request = DeleteTaskRequest.builder()
            .taskId(task.getId().getValue())
            .deletedBy("testUser")
            .reason("Integration test")
            .build();
        
        // When
        TaskDTO result = deleteTaskUseCase.execute(request);
        
        // Then
        assertThat(result).isNotNull();
        
        // 驗證資料庫中的任務已被軟刪除
        Task deletedTask = taskRepository.findById(task.getId()).orElseThrow();
        assertThat(deletedTask.isDeleted()).isTrue();
        assertThat(deletedTask.getDeletedBy()).isEqualTo("testUser");
        assertThat(deletedTask.getDeletedAt()).isNotNull();
    }
}
```

### 2. 性能測試

```java
@Test
void performanceTest() {
    // 準備大量測試資料
    List<Task> tasks = createTestTasks(1000);
    
    // 記錄開始時間
    long startTime = System.currentTimeMillis();
    
    // 執行批量刪除
    tasks.forEach(task -> {
        DeleteTaskRequest request = DeleteTaskRequest.builder()
            .taskId(task.getId().getValue())
            .deletedBy("performanceTest")
            .reason("Performance test deletion")
            .build();
            
        deleteTaskUseCase.execute(request);
    });
    
    // 計算執行時間
    long executionTime = System.currentTimeMillis() - startTime;
    
    // 驗證性能要求 (例如: 1000個任務應在5秒內完成)
    assertThat(executionTime).isLessThan(5000);
}
```

## 最佳實踐建議

### 1. 輸入驗證
- 總是在DTO層進行輸入驗證
- 使用Builder pattern確保物件完整性
- 提供清楚的錯誤訊息

### 2. 權限控制
- 在UseCase層實作業務權限邏輯
- 考慮角色基礎存取控制 (RBAC)
- 記錄權限檢查結果供審計使用

### 3. 異常處理
- 使用特定的業務異常類型
- 提供有用的錯誤訊息
- 在適當的層級處理異常

### 4. 事件處理
- 保持事件處理器的輕量級
- 考慮異步處理長時間運行的操作
- 實作事件重試機制

### 5. 測試策略
- 使用TDD方法論
- 測試所有業務場景
- 包含邊界條件和異常情況

這些範例展示了如何在實際項目中正確使用DeleteTaskUseCase，遵循企業級Java開發的最佳實踐。