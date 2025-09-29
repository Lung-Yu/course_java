# Use Cases 規格文件

## 核心Use Cases

### 1. 任務管理 (Task Management)

#### UC-001: 創建任務 (Create Task)
- **主要參與者**: 使用者
- **前置條件**: 使用者已登入系統
- **成功情境**:
  1. 使用者輸入任務標題、描述、優先級、截止日期
  2. 系統驗證輸入資料
  3. 系統生成唯一任務ID
  4. 系統儲存任務至資料庫
  5. 系統回傳創建成功訊息
- **例外情境**:
  - 必填欄位為空
  - 截止日期格式錯誤
  - 標題重複
- **技術應用**: 物件導向(Task實體)、例外處理(驗證錯誤)

#### UC-002: 查詢任務列表 (Query Task List)
- **主要參與者**: 使用者
- **前置條件**: 系統中存在任務資料
- **成功情境**:
  1. 使用者設定查詢條件(狀態、優先級、日期範圍)
  2. 系統根據條件查詢資料庫
  3. 系統對結果進行排序和分頁
  4. 系統回傳任務列表
- **例外情境**:
  - 查詢條件無效
  - 資料庫連線失敗
- **技術應用**: 集合框架(List、Stream API)、例外處理

#### UC-003: 更新任務狀態 (Update Task Status)
- **主要參與者**: 使用者
- **前置條件**: 任務存在且使用者有權限
- **成功情境**:
  1. 使用者選擇任務並指定新狀態
  2. 系統驗證狀態轉換規則
  3. 系統更新任務狀態和時間戳記
  4. 系統記錄狀態變更歷史
- **例外情境**:
  - 任務不存在
  - 狀態轉換不合法
  - 權限不足
- **技術應用**: 物件導向(State Pattern)、例外處理

#### UC-004: 刪除任務 (Delete Task)
- **主要參與者**: 使用者
- **前置條件**: 任務存在且使用者有權限
- **成功情境**:
  1. 使用者選擇要刪除的任務
  2. 系統確認刪除權限
  3. 系統軟刪除任務(標記為已刪除)
  4. 系統記錄刪除操作日誌
- **例外情境**:
  - 任務不存在
  - 權限不足
  - 任務已被其他使用者刪除
- **技術應用**: 物件導向、例外處理、執行緒安全

### 2. 檔案操作 (File Operations)

#### UC-005: 匯入任務資料 (Import Tasks)
- **主要參與者**: 使用者
- **前置條件**: 使用者已準備CSV或JSON格式檔案
- **成功情境**:
  1. 使用者上傳檔案
  2. 系統驗證檔案格式和大小
  3. 系統解析檔案內容
  4. 系統驗證每筆任務資料
  5. 系統批次創建任務
  6. 系統回傳匯入結果報告
- **例外情境**:
  - 檔案格式不支援
  - 檔案損毀或無法解析
  - 資料驗證失敗
- **技術應用**: 檔案操作、集合框架、例外處理、執行緒

#### UC-006: 匯出任務資料 (Export Tasks)
- **主要參與者**: 使用者
- **前置條件**: 系統中存在任務資料
- **成功情境**:
  1. 使用者選擇匯出格式和篩選條件
  2. 系統查詢符合條件的任務
  3. 系統將資料轉換為指定格式
  4. 系統生成下載檔案
  5. 系統提供檔案下載連結
- **例外情境**:
  - 查詢結果為空
  - 檔案生成失敗
  - 磁碟空間不足
- **技術應用**: 檔案操作、集合框架、執行緒

#### UC-007: 附件管理 (Attachment Management)
- **主要參與者**: 使用者
- **前置條件**: 任務已存在
- **成功情境**:
  1. 使用者選擇任務並上傳附件
  2. 系統驗證檔案類型和大小
  3. 系統儲存檔案並建立關聯
  4. 系統更新任務附件列表
- **例外情境**:
  - 檔案類型不允許
  - 檔案大小超過限制
  - 儲存空間不足
- **技術應用**: 檔案操作、物件導向、例外處理

### 3. 批次處理 (Batch Processing)

#### UC-008: 批次更新任務 (Batch Update Tasks)
- **主要參與者**: 系統管理員
- **前置條件**: 存在需要批次處理的任務
- **成功情境**:
  1. 系統排程啟動批次作業
  2. 系統查詢符合條件的任務
  3. 系統並行處理任務更新
  4. 系統記錄處理結果
  5. 系統發送完成通知
- **例外情境**:
  - 處理過程中發生錯誤
  - 系統資源不足
  - 資料庫鎖定衝突
- **技術應用**: 執行緒、集合框架、例外處理

#### UC-009: 任務提醒通知 (Task Reminder Notification)
- **主要參與者**: 系統
- **前置條件**: 存在即將到期的任務
- **成功情境**:
  1. 系統定時檢查任務截止日期
  2. 系統識別需要提醒的任務
  3. 系統生成通知訊息
  4. 系統非同步發送通知
  5. 系統記錄通知狀態
- **例外情境**:
  - 通知服務不可用
  - 使用者聯絡資訊無效
- **技術應用**: 執行緒、物件導向(Observer Pattern)

### 4. 資料統計 (Data Analytics)

#### UC-010: 任務統計報表 (Task Statistics Report)
- **主要參與者**: 使用者
- **前置條件**: 系統中存在任務歷史資料
- **成功情境**:
  1. 使用者指定統計時間範圍和維度
  2. 系統查詢相關資料
  3. 系統計算統計指標
  4. 系統生成圖表和報表
  5. 系統提供資料下載選項
- **例外情境**:
  - 查詢時間範圍過大
  - 資料計算超時
- **技術應用**: 集合框架(Stream API)、檔案操作、執行緒

## Domain Model

### 核心實體 (Core Entities)

#### Task (任務)
```java
public class Task {
    private TaskId id;
    private String title;
    private String description;
    private TaskStatus status;
    private Priority priority;
    private LocalDateTime dueDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserId assignedTo;
    private List<Attachment> attachments;
    private List<StatusHistory> statusHistory;
}
```

#### TaskStatus (任務狀態)
```java
public enum TaskStatus {
    PENDING, IN_PROGRESS, COMPLETED, CANCELLED
}
```

#### Priority (優先級)
```java
public enum Priority {
    LOW, MEDIUM, HIGH, URGENT
}
```

## Application Services

每個Use Case對應一個Application Service:
- `CreateTaskUseCase`
- `QueryTaskListUseCase`
- `UpdateTaskStatusUseCase`
- `DeleteTaskUseCase`
- `ImportTasksUseCase`
- `ExportTasksUseCase`
- `AttachmentManagementUseCase`
- `BatchUpdateTasksUseCase`
- `TaskReminderUseCase`
- `TaskStatisticsUseCase`