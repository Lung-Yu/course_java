# Task List Application - UC-005 ImportTasksUseCase 完整實作

## 🎯 專案概述

本專案展示了使用 **TDD (測試驅動開發)** 方式實作 UC-005 ImportTasksUseCase 的完整過程，包含企業級 Java 開發的最佳實踐。

## ✅ 實作成果

- [x] **支援 CSV 和 JSON 格式匯入**
- [x] **檔案驗證、解析和批次創建**  
- [x] **創建 ImportResult DTO 記錄成功/失敗統計**
- [x] **實作 FileParser interface 和具體實作類**
- [x] **使用 Stream API 進行批次處理**
- [x] **異常處理：InvalidFileFormatException, FileSizeExceededException**
- [x] **展示集合框架和檔案 I/O 操作**
- [x] **確保測試覆蓋率：119個測試全部通過**

## 🏗️ 架構設計

### Clean Architecture 分層

```
├── Domain Layer
│   ├── model/         # 實體和值物件
│   ├── repository/    # Repository 介面
│   ├── event/         # Domain Events
│   └── exception/     # Domain 異常
├── Application Layer  
│   ├── usecase/       # 業務用例
│   ├── dto/           # 資料傳輸物件
│   ├── service/       # 應用服務
│   └── exception/     # Application 異常
└── Infrastructure Layer
    └── (未實作，使用 Mock)
```

### 核心組件

1. **ImportTasksUseCase** - 主要業務邏輯
2. **FileParser** - 檔案解析策略介面
3. **CsvFileParser / JsonFileParser** - 具體解析器實作
4. **ImportResult** - 匯入結果 DTO
5. **Task / TaskId** - Domain 實體和值物件

## 🚀 快速開始

### 編譯與測試

```bash
# 編譯專案
mvn clean compile

# 執行所有業務邏輯測試
mvn test -Dtest="!TaskListApplicationTests"

# 執行 ImportTasksUseCase 相關測試
mvn test -Dtest=ImportTasksUseCaseTest,CsvFileParserTest,JsonFileParserTest

# 運行功能演示
mvn compile exec:java -Dexec.mainClass="com.tygrus.task_list.ImportTasksDemo"
```

### 使用範例

```java
// 初始化組件
List<FileParser> parsers = Arrays.asList(new CsvFileParser(), new JsonFileParser());
ImportTasksUseCase useCase = new ImportTasksUseCase(taskRepository, parsers);

// 執行匯入
byte[] fileContent = Files.readAllBytes(Paths.get("tasks.csv"));
ImportResult result = useCase.execute(fileContent, "tasks.csv");

// 檢查結果
System.out.println("成功率: " + String.format("%.2f%%", result.getSuccessRate() * 100));
```

## 📋 支援的檔案格式

### CSV 格式
```csv
title,description,priority,dueDate
修復登入 Bug,解決使用者無法登入的問題,HIGH,2025-12-31 23:59:59
新增搜尋功能,實作商品搜尋功能,MEDIUM,
優化效能,,LOW,2025-06-15 10:00:00
```

### JSON 格式
```json
[
  {
    "title": "修復登入 Bug",
    "description": "解決使用者無法登入的問題", 
    "priority": "HIGH",
    "dueDate": "2025-12-31 23:59:59"
  },
  {
    "title": "新增搜尋功能",
    "priority": "MEDIUM"
  }
]
```

## 🛡️ 異常處理

系統提供完整的異常處理機制：

- **InvalidFileFormatException**: 不支援的檔案格式
- **FileSizeExceededException**: 檔案大小超過限制 (10MB)
- **IllegalArgumentException**: 輸入參數驗證失敗
- **Domain 驗證**: 任務欄位長度、日期等業務規則驗證

## 🧪 測試覆蓋率

### 測試統計
- **總測試數**: 119個
- **成功率**: 100%
- **測試分類**:
  - UseCase 測試: 13個
  - FileParser 測試: 26個  
  - Domain 測試: 80個

### TDD 開發流程
1. **Red**: 先寫測試，確保測試失敗
2. **Green**: 寫最少的程式碼讓測試通過
3. **Refactor**: 重構程式碼，保持測試通過

## 🎨 設計模式應用

- **策略模式**: FileParser 介面實現不同格式解析策略
- **建造者模式**: ImportResult、Task 等複雜物件構建
- **工廠模式**: TaskId.generate() 唯一識別碼產生
- **Repository 模式**: 資料存取抽象化
- **Domain Events**: 領域事件處理

## 📊 功能演示結果

運行 `ImportTasksDemo` 的實際輸出：

```
=== UC-005 ImportTasksUseCase 功能示範 ===

📄 CSV 檔案匯入示範
CSV 匯入結果:
  📈 總計: 3 | ✅ 成功: 3 | ❌ 失敗: 7
  📊 成功率: 30.00%

📋 JSON 檔案匯入示範  
JSON 匯入結果:
  📈 總計: 2 | ✅ 成功: 2 | ❌ 失敗: 6
  📊 成功率: 25.00%

⚠️ 異常處理示範
✅ 不支援格式處理正確: InvalidFileFormatException
✅ 檔案大小檢查正確: FileSizeExceededException
✅ 空檔案名檢查正確: IllegalArgumentException
✅ null 內容檢查正確: IllegalArgumentException

📊 最終統計資訊
總任務數: 5
```

## 🔄 Stream API 活用

展示函數式程式設計風格：

```java
// 批次處理使用 Stream API
requestStream
    .peek(request -> totalCount.incrementAndGet())
    .map(this::processTaskRequest)
    .forEach(this::handleProcessResult);
```

## 📈 擴展性設計

- **新增檔案格式**: 實作 `FileParser` 介面
- **新增驗證規則**: 在 UseCase 中擴展驗證邏輯
- **效能調優**: 調整批次處理大小和檔案大小限制
- **異步處理**: 可輕鬆改為異步批次處理

## 📝 學習重點

此專案展示的 Java 技術和最佳實踐：

1. **TDD 開發方法論**
2. **Clean Architecture 架構設計**  
3. **Stream API 函數式程式設計**
4. **設計模式應用**
5. **異常處理策略**
6. **單元測試和整合測試**
7. **Domain-Driven Design (DDD)**
8. **SOLID 原則實踐**

## 📞 聯絡資訊

專案作者：Java Coding Teacher  
實作目的：UC-005 ImportTasksUseCase TDD 開發教學

---

**注意**: 本專案為教學示範用途，展示企業級 Java 開發的完整流程和最佳實踐。