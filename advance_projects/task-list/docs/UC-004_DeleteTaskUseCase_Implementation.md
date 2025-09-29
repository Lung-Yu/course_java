# UC-004 DeleteTaskUseCase 實作文檔

## 概述

本文檔記錄了使用 **TDD (Test-Driven Development)** 方式實作 UC-004 DeleteTaskUseCase 的完整過程，展示了軟刪除機制、權限驗證、操作日誌記錄和Domain Events架構模式的應用。

## 實作特色

### 1. TDD 開發方式
- ✅ 先編寫完整的測試用例 (`DeleteTaskUseCaseTest`)
- ✅ 後實作業務邏輯 (`DeleteTaskUseCase`)
- ✅ 測試覆蓋率：14個測試用例，涵蓋所有業務場景

### 2. 軟刪除機制
- ✅ 添加 `deleted`、`deletedAt`、`deletedBy` 字段到 Task 領域模型
- ✅ 實作 `markAsDeleted()` 方法，遵循業務規則
- ✅ 防止重複刪除和對已刪除任務的操作

### 3. 權限驗證
- ✅ 創建 `PermissionDeniedException` 例外類
- ✅ 在 UseCase 中實作權限檢查邏輯
- ✅ 模擬認證授權系統集成點

### 4. 異常處理
- ✅ `TaskNotFoundException` - 任務不存在
- ✅ `PermissionDeniedException` - 權限不足
- ✅ `IllegalStateException` - 業務規則違反
- ✅ `OptimisticLockException` - 併發衝突

### 5. Domain Events 架構
- ✅ 創建 `TaskDeletedEvent` 領域事件
- ✅ 實作 `DomainEvent` 介面
- ✅ 在刪除操作時自動發布事件

### 6. 執行緒安全設計
- ✅ 樂觀鎖衝突處理
- ✅ 併發刪除場景測試
- ✅ 資料一致性保證

## 檔案結構

### 新增檔案

```
src/main/java/com/tygrus/task_list/
├── application/
│   ├── dto/
│   │   └── DeleteTaskRequest.java              # 刪除請求DTO
│   ├── exception/
│   │   └── PermissionDeniedException.java      # 權限拒絕例外
│   └── usecase/
│       └── DeleteTaskUseCase.java              # 刪除任務Use Case
└── domain/
    └── event/
        └── TaskDeletedEvent.java               # 任務刪除事件

src/test/java/com/tygrus/task_list/
└── application/usecase/
    └── DeleteTaskUseCaseTest.java              # 完整測試套件
```

### 修改檔案

```
src/main/java/com/tygrus/task_list/domain/model/
└── Task.java                                   # 添加軟刪除支持
```

## 測試覆蓋範圍

### 測試類別劃分

1. **成功刪除測試** (2個測試)
   - PENDING 任務刪除
   - COMPLETED 任務刪除

2. **權限驗證測試** (2個測試)
   - 未授權用戶拒絕
   - 任務創建者允許

3. **輸入驗證測試** (4個測試)
   - 空的任務ID
   - 空白的任務ID
   - 空的用戶ID
   - 空的刪除原因

4. **任務存在性驗證測試** (2個測試)
   - 任務不存在例外
   - 已刪除任務拒絕

5. **Domain Events測試** (2個測試)
   - TaskDeletedEvent 發布
   - 事件資訊正確性

6. **併發安全性測試** (2個測試)
   - 樂觀鎖衝突處理
   - 併發刪除一致性

### 測試執行結果

```
Tests run: 14, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

## 核心設計模式展示

### 1. Builder Pattern (DeleteTaskRequest)
```java
DeleteTaskRequest request = DeleteTaskRequest.builder()
    .taskId(validTaskId.getValue())
    .deletedBy(validUserId)
    .reason("不再需要此任務")
    .build();
```

### 2. Domain Events Pattern
```java
public void markAsDeleted(String deletedBy, String reason) {
    // 業務邏輯驗證
    this.deleted = true;
    this.deletedAt = LocalDateTime.now();
    
    // 發布Domain Event
    this.domainEvents.add(new TaskDeletedEvent(this.id, deletedBy, reason));
}
```

### 3. Exception Handling Pattern
```java
private void validatePermissions(Task task, String userId) {
    if (isUnauthorizedUser(userId)) {
        throw new PermissionDeniedException(
            userId, "delete", task.getId().getValue()
        );
    }
}
```

### 4. DTO Pattern with Validation
```java
private void validate() {
    if (taskId == null || taskId.trim().isEmpty()) {
        throw new IllegalArgumentException("Task ID cannot be null or empty");
    }
    // 其他驗證...
}
```

## 物件導向設計原則應用

### 1. 單一職責原則 (SRP)
- `DeleteTaskUseCase`: 專注於刪除業務邏輯
- `DeleteTaskRequest`: 專注於輸入驗證
- `TaskDeletedEvent`: 專注於事件資訊封裝

### 2. 開放封閉原則 (OCP)
- 透過 Domain Events 支援功能擴展
- 權限驗證邏輯可插拔設計

### 3. 依賴反轉原則 (DIP)
- UseCase 依賴於 TaskRepository 抽象
- 測試中使用 Mock 物件驗證

### 4. 介面隔離原則 (ISP)
- `DomainEvent` 介面最小化定義
- 各類別只依賴所需的介面

## 業務規則實作

### 軟刪除規則
1. 任務只能被標記為刪除，不能物理刪除
2. 已刪除的任務不能再次刪除
3. 已刪除的任務不能更新狀態

### 權限規則
1. 只有任務創建者可以刪除任務
2. 管理員角色可以刪除任何任務
3. 未授權用戶拋出 PermissionDeniedException

### 審計規則
1. 記錄刪除時間 (deletedAt)
2. 記錄刪除用戶 (deletedBy) 
3. 記錄刪除原因 (reason)
4. 發布刪除事件供其他系統處理

## 執行緒安全保證

### 樂觀鎖機制
```java
// 模擬併發衝突
when(taskRepository.save(any(Task.class)))
    .thenThrow(new OptimisticLockException("Task was modified by another user"));
```

### 資料一致性
- 使用 Domain Events 確保操作原子性
- 透過 Repository 層處理交易邊界
- 併發場景下的業務規則驗證

## 最佳實踐展示

### 1. 測試命名規範
```java
@DisplayName("應該成功軟刪除PENDING任務")
void shouldSuccessfullySoftDeletePendingTask()
```

### 2. 異常訊息國際化準備
```java
String.format("User %s does not have permission to delete task %s", 
    userId, task.getId().getValue())
```

### 3. 日誌記錄模式
```java
private void logDeletionOperation(Task task, DeleteTaskRequest request) {
    System.out.printf("Task deleted - ID: %s, DeletedBy: %s, Reason: %s%n",
        task.getId().getValue(), request.getDeletedBy(), request.getReason());
}
```

## 學習要點

### TDD 開發體驗
1. **紅燈階段**: 編寫失敗的測試，明確需求
2. **綠燈階段**: 實作最小可行代碼使測試通過
3. **重構階段**: 優化代碼品質和設計

### Domain-Driven Design
1. **Rich Domain Model**: Task 物件封裝業務邏輯
2. **Domain Events**: 解耦業務操作和副作用
3. **Aggregate Root**: Task 作為聚合根管理一致性

### Clean Architecture
1. **Application Layer**: UseCase 協調業務流程
2. **Domain Layer**: 核心業務邏輯和規則
3. **Infrastructure Layer**: Repository 抽象化資料存取

## 總結

本實作成功展示了：
- ✅ TDD 開發方法論的實際應用
- ✅ 軟刪除模式的最佳實踐
- ✅ 完整的權限驗證和異常處理
- ✅ Domain Events 事件驅動架構
- ✅ 執行緒安全和併發處理
- ✅ 物件導向設計原則的實際運用

透過這個 Use Case 的實作，學習者可以深入理解企業級 Java 應用開發的核心概念和最佳實踐。

## 編譯和執行

```bash
# 編譯項目
mvn clean compile

# 執行特定測試
mvn test -Dtest=DeleteTaskUseCaseTest

# 查看測試結果
# Tests run: 14, Failures: 0, Errors: 0, Skipped: 0
# BUILD SUCCESS
```