# UC-005 ImportTasksUseCase 實作成果

## 概述

本實作展示了 UC-005 ImportTasksUseCase 的完整 TDD 開發，包含：

1. ✅ 支援 CSV 和 JSON 格式匯入
2. ✅ 檔案驗證、解析和批次創建
3. ✅ 創建 ImportResult DTO 記錄成功/失敗統計
4. ✅ 實作 FileParser interface 和具體實作類
5. ✅ 使用 Stream API 進行批次處理
6. ✅ 異常處理：InvalidFileFormatException, FileSizeExceededException
7. ✅ 展示集合框架和檔案 I/O 操作
8. ✅ 確保測試覆蓋率（119個測試全部通過）

## 架構設計

### 核心組件

1. **ImportTasksUseCase** - 主要業務邏輯類
2. **FileParser** - 檔案解析介面
3. **CsvFileParser** - CSV 檔案解析器實作
4. **JsonFileParser** - JSON 檔案解析器實作
5. **ImportResult** - 匯入結果 DTO
6. **異常處理** - 自定義異常類

### 設計模式應用

- **策略模式**: FileParser 介面允許不同格式解析器的策略切換
- **建造者模式**: ImportResult 和 Task 使用 Builder 模式
- **工廠模式**: TaskId.generate() 用於產生唯一識別碼
- **Domain Events**: Task 實體支援領域事件

## 技術特點

### 1. Stream API 活用
```java
// 批次處理使用 Stream API
requestStream
    .peek(request -> totalCount.incrementAndGet())
    .map(request -> {
        try {
            Task task = createTaskFromRequest(request);
            Task savedTask = taskRepository.save(task);
            return ProcessResult.success(TaskDTO.fromTask(savedTask));
        } catch (Exception e) {
            return ProcessResult.failure("Line " + totalCount.get() + ": " + e.getMessage());
        }
    })
    .forEach(result -> {
        if (result.isSuccess()) {
            successfulTasks.add(result.getTaskDTO());
        } else {
            errorMessages.add(result.getErrorMessage());
        }
    });
```

### 2. 檔案格式自動檢測
```java
private FileParser findSuitableParser(String fileName) {
    return fileParsers.stream()
        .filter(parser -> parser.supports(fileName))
        .findFirst()
        .orElseThrow(() -> {
            String supportedFormats = String.join(", ", getSupportedFormats());
            return new InvalidFileFormatException(
                String.format("Unsupported file format for '%s'. Supported formats: %s", 
                    fileName, supportedFormats));
        });
}
```

### 3. 強型別檔案大小限制
```java
private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

private void validateFileSize(byte[] fileContent, String fileName) {
    if (fileContent.length > MAX_FILE_SIZE) {
        throw new FileSizeExceededException(fileName, fileContent.length, MAX_FILE_SIZE);
    }
}
```

### 4. CSV 複雜解析處理
```java
private String[] parseCSVLine(String line) {
    // 處理引號內的逗號和雙引號轉義
    StringBuilder current = new StringBuilder();
    boolean inQuotes = false;
    int fieldIndex = 0;
    
    for (int i = 0; i < line.length(); i++) {
        char c = line.charAt(i);
        
        if (c == '"') {
            if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                current.append('"');
                i++; // 跳過下一個引號
            } else {
                inQuotes = !inQuotes;
            }
        } else if (c == ',' && !inQuotes) {
            // 處理欄位分隔
        }
    }
}
```

## 測試覆蓋率

### UseCase 層測試（13個測試）
- 建構子驗證
- 輸入參數驗證  
- 檔案大小檢查
- 不支援格式處理
- CSV/JSON 檔案成功匯入
- 部分成功處理
- 空檔案處理
- 異常處理

### FileParser 測試（26個測試）
- CSV Parser: 12個測試
- JSON Parser: 14個測試
- 包含格式支援檢測、欄位解析、異常處理等

### Domain 層測試（80個測試）
- Task 實體測試
- TaskId 值物件測試  
- 狀態轉換測試
- Domain Events 測試

## 使用範例

### CSV 檔案格式
```csv
title,description,priority,dueDate
修復登入 Bug,解決使用者無法登入的問題,HIGH,2024-12-31 23:59:59
新增搜尋功能,實作商品搜尋功能,MEDIUM,
優化效能,,LOW,2024-06-15 10:00:00
```

### JSON 檔案格式
```json
[
  {
    "title": "修復登入 Bug",
    "description": "解決使用者無法登入的問題",
    "priority": "HIGH",
    "dueDate": "2024-12-31 23:59:59"
  },
  {
    "title": "新增搜尋功能", 
    "description": "實作商品搜尋功能",
    "priority": "MEDIUM"
  }
]
```

### 程式使用
```java
// 建立 Use Case
List<FileParser> parsers = Arrays.asList(new CsvFileParser(), new JsonFileParser());
ImportTasksUseCase useCase = new ImportTasksUseCase(taskRepository, parsers);

// 執行匯入
byte[] fileContent = Files.readAllBytes(Paths.get("tasks.csv"));
ImportResult result = useCase.execute(fileContent, "tasks.csv");

// 檢查結果
System.out.println("總計: " + result.getTotalCount());
System.out.println("成功: " + result.getSuccessCount());  
System.out.println("失敗: " + result.getFailureCount());
System.out.println("成功率: " + String.format("%.2f%%", result.getSuccessRate() * 100));

if (result.hasErrors()) {
    result.getErrorMessages().forEach(System.out::println);
}
```

## 編譯與測試指令

```bash
# 編譯專案
mvn clean compile

# 執行所有測試
mvn test -Dtest="!TaskListApplicationTests"

# 執行特定測試
mvn test -Dtest=ImportTasksUseCaseTest
mvn test -Dtest=CsvFileParserTest,JsonFileParserTest

# 查看測試報告
ls target/surefire-reports/
```

## TDD 開發流程展示

本專案完全遵循 TDD（測試驅動開發）流程：

1. **Red**: 先寫測試，確保測試失敗
2. **Green**: 寫最少的代碼讓測試通過
3. **Refactor**: 重構代碼，保持測試通過

所有功能都有對應的測試，確保：
- 正常情況的功能正確性
- 邊界條件的處理
- 異常情況的健壯性
- 併發安全性（部分測試）

## 最佳實踐展示

1. **不可變物件**: DTO 和值物件使用不可變設計
2. **Builder 模式**: 複雜物件建構使用 Builder
3. **策略模式**: 檔案解析器的可擴展設計  
4. **Domain Events**: 領域事件的實作
5. **異常處理**: 自定義異常和錯誤資訊
6. **Stream API**: 函數式程式設計風格
7. **單一職責**: 每個類別都有清楚的職責
8. **依賴注入**: 透過建構子注入依賴

## 擴展性設計

系統設計具備良好的擴展性：

1. **新增檔案格式**: 實作 FileParser 介面即可
2. **新增驗證規則**: 在 UseCase 中新增驗證邏輯
3. **批次處理優化**: 調整 BATCH_SIZE 常數
4. **檔案大小限制**: 調整 MAX_FILE_SIZE 常數
5. **異常處理**: 新增自定義異常類別

此實作展示了企業級 Java 開發的最佳實踐，包含完整的測試覆蓋、清晰的架構設計和良好的擴展性。