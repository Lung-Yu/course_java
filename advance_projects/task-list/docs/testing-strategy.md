# 測試策略文件

## 測試原則與方法

### 測試金字塔策略
- **單元測試 (70%)**: 測試個別類別和方法
- **整合測試 (20%)**: 測試組件間互動
- **端到端測試 (10%)**: 測試完整業務流程

### 測試驅動開發 (TDD) 流程
1. **Red**: 編寫失敗的測試
2. **Green**: 實作最小可行代碼
3. **Refactor**: 重構改善代碼品質

## Use Case 測試規格

### UC-001: 創建任務 (Create Task)

#### Happy Case 測試
```java
@Test
@DisplayName("成功創建任務 - 所有必填欄位正確")
class CreateTaskUseCaseTest {
    
    @Test
    void shouldCreateTaskSuccessfully_WhenAllRequiredFieldsProvided() {
        // Given - 物件導向: 測試資料建構
        CreateTaskRequest request = CreateTaskRequest.builder()
            .title("完成專案文件")
            .description("撰寫技術架構文件")
            .priority(Priority.HIGH)
            .dueDate(LocalDateTime.now().plusDays(7))
            .assignedTo(UserId.of("user123"))
            .build();
            
        // When
        TaskDTO result = createTaskUseCase.execute(request);
        
        // Then - 集合框架: 驗證結果
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getTitle()).isEqualTo("完成專案文件");
        assertThat(result.getStatus()).isEqualTo(TaskStatus.PENDING);
        
        // 驗證資料庫狀態
        Task savedTask = taskRepository.findById(TaskId.of(result.getId())).orElseThrow();
        assertThat(savedTask.getTitle()).isEqualTo(request.getTitle());
    }
}
```

#### Edge Case 測試
```java
@ParameterizedTest
@DisplayName("創建任務失敗 - 必填欄位驗證")
@ValueSource(strings = {"", "   ", "\t", "\n"})
void shouldThrowException_WhenTitleIsBlankOrEmpty(String invalidTitle) {
    // Given - 例外處理: 測試無效輸入
    CreateTaskRequest request = CreateTaskRequest.builder()
        .title(invalidTitle)
        .description("描述")
        .priority(Priority.MEDIUM)
        .dueDate(LocalDateTime.now().plusDays(1))
        .build();
        
    // When & Then
    assertThatThrownBy(() -> createTaskUseCase.execute(request))
        .isInstanceOf(ValidationException.class)
        .hasMessageContaining("任務標題不能為空");
}

@Test
@DisplayName("創建任務失敗 - 截止日期在過去")
void shouldThrowException_WhenDueDateIsInPast() {
    // Given
    CreateTaskRequest request = CreateTaskRequest.builder()
        .title("有效標題")
        .description("描述")
        .priority(Priority.MEDIUM)
        .dueDate(LocalDateTime.now().minusDays(1)) // 過去日期
        .build();
        
    // When & Then - 例外處理: 業務邏輯驗證
    assertThatThrownBy(() -> createTaskUseCase.execute(request))
        .isInstanceOf(InvalidDueDateException.class)
        .hasMessageContaining("截止日期不能是過去時間");
}

@Test
@DisplayName("創建任務失敗 - 重複標題")
void shouldThrowException_WhenTitleAlreadyExists() {
    // Given - 集合框架: 預設資料
    String duplicateTitle = "重複的標題";
    taskRepository.save(createSampleTask(duplicateTitle));
    
    CreateTaskRequest request = CreateTaskRequest.builder()
        .title(duplicateTitle)
        .description("不同描述")
        .priority(Priority.LOW)
        .dueDate(LocalDateTime.now().plusDays(1))
        .build();
        
    // When & Then
    assertThatThrownBy(() -> createTaskUseCase.execute(request))
        .isInstanceOf(DuplicateTaskException.class);
}
```

### UC-002: 查詢任務列表 (Query Task List)

#### Happy Case 測試
```java
@Test
@DisplayName("成功查詢任務列表 - 依狀態篩選並排序")



void shouldReturnFilteredAndSortedTasks_WhenValidCriteriaProvided() {
    // Given - 集合框架: 測試資料準備
    List<Task> sampleTasks = Arrays.asList(
        createTaskWithStatus(TaskStatus.PENDING, Priority.HIGH, LocalDateTime.now().plusDays(1)),
        createTaskWithStatus(TaskStatus.IN_PROGRESS, Priority.MEDIUM, LocalDateTime.now().plusDays(2)),
        createTaskWithStatus(TaskStatus.PENDING, Priority.LOW, LocalDateTime.now().plusDays(3))
    );
    taskRepository.saveAll(sampleTasks);
    
    TaskQueryRequest queryRequest = TaskQueryRequest.builder()
        .status(TaskStatus.PENDING)
        .sortBy(TaskSortField.DUE_DATE)
        .sortDirection(SortDirection.ASC)
        .pageSize(10)
        .pageNumber(0)
        .build();
        
    // When - 集合框架: Stream API 測試
    PagedResult<TaskDTO> result = queryTaskListUseCase.execute(queryRequest);
    
    // Then
    assertThat(result.getContent()).hasSize(2);
    assertThat(result.getContent())
        .extracting(TaskDTO::getStatus)
        .containsOnly(TaskStatus.PENDING);
    
    // 驗證排序 - 集合框架: 順序驗證
    List<LocalDateTime> dueDates = result.getContent().stream()
        .map(TaskDTO::getDueDate)
        .collect(Collectors.toList());
    assertThat(dueDates).isSorted();
}
```

#### Edge Case 測試
```java
@Test
@DisplayName("查詢任務列表 - 空結果處理")
void shouldReturnEmptyResult_WhenNoTasksMatchCriteria() {
    // Given - 集合框架: 空資料測試
    TaskQueryRequest queryRequest = TaskQueryRequest.builder()
        .status(TaskStatus.COMPLETED)
        .build();
        
    // When
    PagedResult<TaskDTO> result = queryTaskListUseCase.execute(queryRequest);
    
    // Then
    assertThat(result.getContent()).isEmpty();
    assertThat(result.getTotalElements()).isZero();
}

@Test
@DisplayName("查詢任務列表 - 分頁邊界測試")
void shouldHandlePaginationCorrectly_WhenPageSizeExceedsAvailableData() {
    // Given - 集合框架: 分頁測試
    List<Task> tasks = IntStream.range(0, 5)
        .mapToObj(i -> createSampleTask("Task " + i))
        .collect(Collectors.toList());
    taskRepository.saveAll(tasks);
    
    TaskQueryRequest queryRequest = TaskQueryRequest.builder()
        .pageSize(10) // 大於資料總數
        .pageNumber(0)
        .build();
        
    // When
    PagedResult<TaskDTO> result = queryTaskListUseCase.execute(queryRequest);
    
    // Then
    assertThat(result.getContent()).hasSize(5);
    assertThat(result.isLastPage()).isTrue();
}
```

### UC-005: 匯入任務資料 (Import Tasks)

#### Happy Case 測試
```java
@Test
@DisplayName("成功匯入CSV任務資料")
void shouldImportTasksSuccessfully_WhenValidCsvFileProvided() throws IOException {
    // Given - 檔案操作: 測試檔案準備
    String csvContent = """
        title,description,priority,dueDate,status
        任務1,描述1,HIGH,2024-12-31T23:59:59,PENDING
        任務2,描述2,MEDIUM,2024-12-30T12:00:00,IN_PROGRESS
        """;
    MockMultipartFile csvFile = new MockMultipartFile(
        "file", "tasks.csv", "text/csv", csvContent.getBytes()
    );
    
    // When - 檔案操作 + 集合框架
    ImportResult result = importTasksUseCase.execute(csvFile);
    
    // Then
    assertThat(result.getSuccessCount()).isEqualTo(2);
    assertThat(result.getFailureCount()).isZero();
    assertThat(result.getErrors()).isEmpty();
    
    // 驗證資料庫狀態 - 集合框架
    List<Task> importedTasks = taskRepository.findAll();
    assertThat(importedTasks).hasSize(2);
    assertThat(importedTasks)
        .extracting(Task::getTitle)
        .containsExactlyInAnyOrder("任務1", "任務2");
}
```

#### Edge Case 測試
```java
@Test
@DisplayName("匯入失敗 - 檔案格式錯誤")
void shouldHandleInvalidFileFormat_WhenCorruptedCsvProvided() {
    // Given - 檔案操作 + 例外處理
    String invalidCsv = """
        title,description,priority
        任務1,描述1  // 缺少欄位
        ,,HIGH,2024-12-31T23:59:59,PENDING  // 空值
        """;
    MockMultipartFile invalidFile = new MockMultipartFile(
        "file", "invalid.csv", "text/csv", invalidCsv.getBytes()
    );
    
    // When
    ImportResult result = importTasksUseCase.execute(invalidFile);
    
    // Then - 例外處理: 錯誤收集
    assertThat(result.getSuccessCount()).isZero();
    assertThat(result.getFailureCount()).isEqualTo(2);
    assertThat(result.getErrors())
        .hasSize(2)
        .extracting(ImportError::getMessage)
        .contains("第2行: 缺少必要欄位", "第3行: 標題不能為空");
}

@Test
@DisplayName("匯入失敗 - 檔案大小超限")
void shouldThrowException_WhenFileSizeExceedsLimit() {
    // Given - 檔案操作: 大檔案測試
    byte[] largeContent = new byte[10 * 1024 * 1024]; // 10MB
    MockMultipartFile largeFile = new MockMultipartFile(
        "file", "large.csv", "text/csv", largeContent
    );
    
    // When & Then - 例外處理
    assertThatThrownBy(() -> importTasksUseCase.execute(largeFile))
        .isInstanceOf(FileSizeExceededException.class)
        .hasMessageContaining("檔案大小超過限制");
}
```

### UC-008: 批次更新任務 (Batch Update Tasks)

#### Happy Case 測試
```java
@Test
@DisplayName("成功批次更新任務狀態")
void shouldUpdateTasksInBatch_WhenValidTaskIdsProvided() {
    // Given - 執行緒 + 集合框架
    List<Task> tasks = IntStream.range(0, 100)
        .mapToObj(i -> createSampleTask("Task " + i))
        .collect(Collectors.toList());
    taskRepository.saveAll(tasks);
    
    List<TaskId> taskIds = tasks.stream()
        .map(Task::getId)
        .collect(Collectors.toList());
    
    // When - 執行緒: 並行處理測試
    CompletableFuture<BatchUpdateResult> future = 
        batchUpdateTasksUseCase.executeAsync(taskIds, TaskStatus.COMPLETED);
    
    BatchUpdateResult result = future.join();
    
    // Then
    assertThat(result.getSuccessCount()).isEqualTo(100);
    assertThat(result.getFailureCount()).isZero();
    
    // 驗證所有任務狀態已更新
    List<Task> updatedTasks = taskRepository.findAllById(taskIds);
    assertThat(updatedTasks)
        .extracting(Task::getStatus)
        .containsOnly(TaskStatus.COMPLETED);
}
```

#### Edge Case 測試
```java
@Test
@DisplayName("批次更新 - 部分失敗處理")
void shouldHandlePartialFailures_WhenSomeTasksCannotBeUpdated() {
    // Given - 執行緒 + 例外處理
    List<TaskId> mixedTaskIds = Arrays.asList(
        TaskId.of("valid-task-1"),
        TaskId.of("non-existent-task"),
        TaskId.of("valid-task-2")
    );
    
    // 只創建部分有效任務
    taskRepository.saveAll(Arrays.asList(
        createTaskWithId("valid-task-1"),
        createTaskWithId("valid-task-2")
    ));
    
    // When - 執行緒: 錯誤處理測試
    CompletableFuture<BatchUpdateResult> future = 
        batchUpdateTasksUseCase.executeAsync(mixedTaskIds, TaskStatus.COMPLETED);
    
    BatchUpdateResult result = future.join();
    
    // Then
    assertThat(result.getSuccessCount()).isEqualTo(2);
    assertThat(result.getFailureCount()).isEqualTo(1);
    assertThat(result.getErrors())
        .hasSize(1)
        .extracting(BatchError::getTaskId)
        .contains("non-existent-task");
}

@Test
@DisplayName("批次更新 - 執行緒安全測試")
@Execution(ExecutionMode.CONCURRENT)
void shouldHandleConcurrentUpdates_WhenMultipleThreadsUpdateSameTask() {
    // Given - 執行緒: 併發測試
    Task sharedTask = createSampleTask("Shared Task");
    taskRepository.save(sharedTask);
    
    // When - 執行緒: 併發執行
    List<CompletableFuture<Void>> futures = IntStream.range(0, 10)
        .mapToObj(i -> CompletableFuture.runAsync(() -> {
            updateTaskStatusUseCase.execute(sharedTask.getId(), TaskStatus.IN_PROGRESS);
        }))
        .collect(Collectors.toList());
    
    // Then - 執行緒: 等待完成
    CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    
    // 驗證最終狀態一致性
    Task finalTask = taskRepository.findById(sharedTask.getId()).orElseThrow();
    assertThat(finalTask.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
    assertThat(finalTask.getVersion()).isGreaterThan(sharedTask.getVersion()); // 樂觀鎖檢查
}
```

## 測試工具與框架

### 單元測試
- **JUnit 5**: 測試框架
- **AssertJ**: 流暢的斷言API
- **Mockito**: Mock框架
- **TestContainers**: 整合測試容器

### 測試資料管理
```java
@TestConfiguration
public class TestDataConfiguration {
    
    @Bean
    @Primary
    public TaskRepository testTaskRepository() {
        return new InMemoryTaskRepository(); // 記憶體實作用於快速測試
    }
}

// 測試資料建構器 - 物件導向
public class TaskTestDataBuilder {
    private String title = "預設標題";
    private TaskStatus status = TaskStatus.PENDING;
    private Priority priority = Priority.MEDIUM;
    
    public TaskTestDataBuilder withTitle(String title) {
        this.title = title;
        return this;
    }
    
    public TaskTestDataBuilder withStatus(TaskStatus status) {
        this.status = status;
        return this;
    }
    
    public Task build() {
        return Task.builder()
            .id(TaskId.generate())
            .title(title)
            .status(status)
            .priority(priority)
            .createdAt(LocalDateTime.now())
            .build();
    }
}
```

### 效能測試
```java
@Test
@DisplayName("查詢效能測試 - 大量資料")
void shouldPerformWell_WhenQueryingLargeDataset() {
    // Given - 集合框架: 大量測試資料
    List<Task> largeBatch = IntStream.range(0, 10000)
        .mapToObj(i -> TaskTestDataBuilder.create()
            .withTitle("Task " + i)
            .withStatus(TaskStatus.values()[i % TaskStatus.values().length])
            .build())
        .collect(Collectors.toList());
    
    taskRepository.saveAll(largeBatch);
    
    // When - 效能測試
    long startTime = System.currentTimeMillis();
    
    PagedResult<TaskDTO> result = queryTaskListUseCase.execute(
        TaskQueryRequest.builder()
            .status(TaskStatus.PENDING)
            .pageSize(100)
            .build()
    );
    
    long executionTime = System.currentTimeMillis() - startTime;
    
    // Then
    assertThat(executionTime).isLessThan(1000); // 1秒內完成
    assertThat(result.getContent()).isNotEmpty();
}
```

## 測試覆蓋率目標
- **程式碼覆蓋率**: 最小85%
- **分支覆蓋率**: 最小80%
- **Use Case覆蓋率**: 100%
- **例外情境覆蓋率**: 100%

## 持續整合測試
- 每次提交自動執行所有測試
- 效能基準測試
- 安全性掃描
- 程式碼品質檢查