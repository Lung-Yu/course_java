# 技術架構文件

## DDD 分層架構設計

### 1. Domain Layer (領域層)
包含核心業務邏輯和領域實體，不依賴於任何外部框架。

```
src/main/java/com/tygrus/task_list/domain/
├── model/                          # 領域模型
│   ├── Task.java                  # 任務實體
│   ├── TaskId.java               # 任務ID值物件
│   ├── TaskStatus.java           # 任務狀態枚舉
│   ├── Priority.java             # 優先級枚舉
│   ├── User.java                 # 使用者實體
│   ├── Attachment.java           # 附件實體
│   └── StatusHistory.java        # 狀態歷史記錄
├── repository/                     # 資料存取介面
│   ├── TaskRepository.java
│   ├── UserRepository.java
│   └── AttachmentRepository.java
├── service/                        # 領域服務
│   ├── TaskDomainService.java
│   └── NotificationService.java
└── exception/                      # 領域例外
    ├── TaskNotFoundException.java
    ├── InvalidTaskStatusException.java
    └── DuplicateTaskException.java
```

### 2. Application Layer (應用層)
實現Use Cases，協調領域物件完成業務流程。

```
src/main/java/com/tygrus/task_list/application/
├── usecase/                        # Use Case 實現
│   ├── CreateTaskUseCase.java
│   ├── QueryTaskListUseCase.java
│   ├── UpdateTaskStatusUseCase.java
│   ├── DeleteTaskUseCase.java
│   ├── ImportTasksUseCase.java
│   ├── ExportTasksUseCase.java
│   ├── AttachmentManagementUseCase.java
│   ├── BatchUpdateTasksUseCase.java
│   ├── TaskReminderUseCase.java
│   └── TaskStatisticsUseCase.java
├── dto/                            # 資料傳輸物件
│   ├── TaskDTO.java
│   ├── CreateTaskRequest.java
│   ├── UpdateTaskRequest.java
│   ├── TaskQueryRequest.java
│   └── TaskStatisticsResponse.java
└── port/                           # 輸出埠介面
    ├── TaskRepositoryPort.java
    ├── FileStoragePort.java
    ├── NotificationPort.java
    └── EmailServicePort.java
```

### 3. Infrastructure Layer (基礎設施層)
實現技術細節和外部系統整合。

```
src/main/java/com/tygrus/task_list/infrastructure/
├── persistence/                    # 資料持久化
│   ├── jpa/
│   │   ├── TaskJpaEntity.java
│   │   ├── TaskJpaRepository.java
│   │   └── TaskRepositoryAdapter.java
│   └── config/
│       └── DatabaseConfig.java
├── file/                          # 檔案操作實現
│   ├── CsvFileProcessor.java
│   ├── JsonFileProcessor.java
│   ├── FileStorageService.java
│   └── AttachmentStorageService.java
├── notification/                   # 通知服務實現
│   ├── EmailNotificationService.java
│   └── AsyncNotificationProcessor.java
└── scheduler/                     # 排程服務
    ├── TaskReminderScheduler.java
    └── BatchProcessingScheduler.java
```

### 4. Presentation Layer (展示層)
REST API控制器和輸入驗證。

```
src/main/java/com/tygrus/task_list/presentation/
├── controller/                     # REST控制器
│   ├── TaskController.java
│   ├── FileController.java
│   └── StatisticsController.java
├── dto/                           # API DTO
│   ├── request/
│   └── response/
└── exception/                     # 全域例外處理
    └── GlobalExceptionHandler.java
```

## 技術主軸實現策略

### 1. 物件導向程式設計 (OOP)

#### 設計模式應用:
- **Strategy Pattern**: 不同檔案格式處理器
- **Factory Pattern**: 任務狀態轉換工廠
- **Observer Pattern**: 任務狀態變更通知
- **Builder Pattern**: 複雜任務物件建構
- **Repository Pattern**: 資料存取抽象

#### 核心原則應用:
```java
// 單一職責原則 (SRP)
public class TaskCreationService {
    public Task createTask(CreateTaskRequest request) { ... }
}

// 開放封閉原則 (OCP)
public abstract class FileProcessor {
    public abstract void process(File file);
}

// 介面隔離原則 (ISP)
public interface TaskReader {
    List<Task> readTasks();
}
public interface TaskWriter {
    void writeTasks(List<Task> tasks);
}

// 依賴反轉原則 (DIP)
public class CreateTaskUseCase {
    private final TaskRepositoryPort repository;
    private final NotificationPort notificationService;
}
```

### 2. 集合框架 (Collections Framework)

#### Stream API 應用:
```java
// 任務篩選和排序
public List<Task> filterAndSortTasks(List<Task> tasks, TaskQueryRequest request) {
    return tasks.stream()
        .filter(task -> matchesCriteria(task, request))
        .sorted(Comparator.comparing(Task::getDueDate)
                         .thenComparing(Task::getPriority))
        .collect(Collectors.toList());
}

// 任務統計計算
public Map<TaskStatus, Long> getTaskStatusStatistics(List<Task> tasks) {
    return tasks.stream()
        .collect(Collectors.groupingBy(
            Task::getStatus,
            Collectors.counting()
        ));
}

// 批次處理
public void batchUpdateTasks(List<Task> tasks, TaskStatus newStatus) {
    tasks.parallelStream()
        .forEach(task -> task.updateStatus(newStatus));
}
```

#### 集合選擇策略:
- `List<Task>`: 有序任務列表
- `Set<TaskId>`: 唯一任務ID集合
- `Map<UserId, List<Task>>`: 使用者任務映射
- `Queue<Task>`: 待處理任務佇列
- `ConcurrentHashMap`: 執行緒安全的快取

### 3. 例外處理 (Exception Handling)

#### 自定義例外階層:
```java
// 基礎業務例外
public abstract class TaskManagementException extends RuntimeException {
    protected TaskManagementException(String message) { super(message); }
    protected TaskManagementException(String message, Throwable cause) {
        super(message, cause);
    }
}

// 具體業務例外
public class TaskNotFoundException extends TaskManagementException {
    public TaskNotFoundException(TaskId taskId) {
        super("Task not found with ID: " + taskId);
    }
}

public class InvalidTaskStatusTransitionException extends TaskManagementException {
    public InvalidTaskStatusTransitionException(TaskStatus from, TaskStatus to) {
        super(String.format("Invalid status transition from %s to %s", from, to));
    }
}
```

#### 例外處理策略:
```java
// Use Case 層例外處理
public class UpdateTaskStatusUseCase {
    public void updateStatus(TaskId taskId, TaskStatus newStatus) {
        try {
            Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));
            
            task.updateStatus(newStatus);
            taskRepository.save(task);
            
        } catch (TaskManagementException e) {
            logger.error("Business logic error: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error updating task status", e);
            throw new SystemException("System error occurred", e);
        }
    }
}

// 全域例外處理器
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTaskNotFound(TaskNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse("TASK_NOT_FOUND", e.getMessage()));
    }
    
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(ValidationException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse("VALIDATION_ERROR", e.getMessage()));
    }
}
```

### 4. 檔案操作 (File Operations)

#### 檔案處理架構:
```java
// 抽象檔案處理器
public abstract class FileProcessor {
    public abstract boolean supports(String fileType);
    public abstract List<Task> readTasks(InputStream inputStream) throws FileProcessingException;
    public abstract void writeTasks(List<Task> tasks, OutputStream outputStream) throws FileProcessingException;
}

// CSV 檔案處理器
@Component
public class CsvFileProcessor extends FileProcessor {
    
    @Override
    public boolean supports(String fileType) {
        return "csv".equalsIgnoreCase(fileType);
    }
    
    @Override
    public List<Task> readTasks(InputStream inputStream) throws FileProcessingException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            return reader.lines()
                .skip(1) // 跳過標題行
                .map(this::parseCsvLine)
                .collect(Collectors.toList());
        } catch (IOException e) {
            throw new FileProcessingException("Error reading CSV file", e);
        }
    }
}

// 檔案儲存服務
@Service
public class FileStorageService {
    
    private final String uploadDirectory;
    
    public String storeFile(MultipartFile file) throws FileStorageException {
        try {
            String fileName = generateUniqueFileName(file.getOriginalFilename());
            Path targetPath = Paths.get(uploadDirectory, fileName);
            
            Files.createDirectories(targetPath.getParent());
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            
            return fileName;
        } catch (IOException e) {
            throw new FileStorageException("Failed to store file", e);
        }
    }
}
```

### 5. 執行緒 (Threading)

#### 非同步處理:
```java
// 非同步任務處理服務
@Service
public class AsyncTaskProcessingService {
    
    private final ExecutorService executorService;
    private final TaskRepository taskRepository;
    
    @Async
    @Retryable(value = {Exception.class}, maxAttempts = 3)
    public CompletableFuture<Void> processBatchUpdate(List<TaskId> taskIds, TaskStatus newStatus) {
        
        List<CompletableFuture<Void>> futures = taskIds.parallelStream()
            .map(taskId -> CompletableFuture.runAsync(() -> {
                try {
                    Task task = taskRepository.findById(taskId)
                        .orElseThrow(() -> new TaskNotFoundException(taskId));
                    task.updateStatus(newStatus);
                    taskRepository.save(task);
                } catch (Exception e) {
                    logger.error("Error processing task {}: {}", taskId, e.getMessage());
                }
            }, executorService))
            .collect(Collectors.toList());
            
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }
}

// 排程任務
@Component
public class TaskReminderScheduler {
    
    @Scheduled(fixedRate = 300000) // 每5分鐘執行一次
    public void checkUpcomingDeadlines() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime reminderTime = now.plusHours(24);
        
        List<Task> upcomingTasks = taskRepository.findTasksWithDueDateBetween(now, reminderTime);
        
        upcomingTasks.parallelStream()
            .forEach(this::sendReminderNotification);
    }
}

// 執行緒池配置
@Configuration
@EnableAsync
public class AsyncConfig {
    
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("TaskAsync-");
        executor.initialize();
        return executor;
    }
}
```

## 技術選型說明

### 框架與工具
- **Spring Boot**: 應用框架
- **Spring Data JPA**: 資料存取
- **Spring Security**: 安全認證
- **Spring Scheduler**: 排程任務
- **Jackson**: JSON處理
- **OpenCSV**: CSV檔案處理
- **Apache Commons**: 通用工具

### 資料庫設計
- **PostgreSQL**: 主資料庫
- **Redis**: 快取和Session儲存
- **H2**: 測試資料庫

### 監控與日誌
- **SLF4J + Logback**: 日誌框架
- **Micrometer**: 應用監控
- **Spring Actuator**: 健康檢查