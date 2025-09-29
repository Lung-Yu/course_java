# TDD 開發流程整合策略

## TDD + Trunk-Based Development 結合

### 核心原則
1. **Red-Green-Refactor週期**: 每個功能都從失敗測試開始
2. **小步提交**: 每個TDD週期完成後立即提交
3. **測試先行**: 測試代碼就是需求規格
4. **持續整合**: 每次提交都要通過所有測試

## TDD開發週期與Git整合

### 標準TDD週期 (每個週期約15-30分鐘)

#### 1. Red Phase (寫失敗測試)
```bash
# 1. 寫失敗的測試
# 2. 確保測試失敗
./mvnw test
# 3. 提交失敗測試
git add .
git commit -m "test(domain): add failing test for Task entity creation

- Add test for basic Task construction
- Define expected Task properties
- Test currently fails (Red phase)

TDD: Red phase for UC-001 CreateTask"
```

#### 2. Green Phase (最小實作)
```bash
# 1. 寫最小可行代碼讓測試通過
# 2. 確保測試通過
./mvnw test
# 3. 提交實作代碼
git add .
git commit -m "feat(domain): implement minimal Task entity

- Add basic Task class with required properties
- Implement constructor and getters
- Make tests pass with minimal implementation

TDD: Green phase for UC-001 CreateTask"
```

#### 3. Refactor Phase (重構改進)
```bash
# 1. 重構改善代碼品質
# 2. 確保測試仍然通過
./mvnw test
# 3. 提交重構代碼
git add .
git commit -m "refactor(domain): improve Task entity design

- Add validation logic
- Improve encapsulation
- Add builder pattern
- All tests remain green

TDD: Refactor phase for UC-001 CreateTask"
```

## 專案中的TDD實踐規範

### 測試命名約定
```java
// 測試類別命名: [ClassUnderTest]Test
public class TaskTest {
    
    // 測試方法命名: should[ExpectedBehavior]_when[StateUnderTest]
    @Test
    void shouldCreateTask_whenValidDataProvided() { }
    
    @Test  
    void shouldThrowException_whenTitleIsNull() { }
}
```

### 測試組織結構
```
src/test/java/com/tygrus/task_list/
├── domain/
│   ├── model/
│   │   ├── TaskTest.java           # TDD: Task實體測試
│   │   ├── TaskIdTest.java         # TDD: TaskId值物件測試
│   │   └── TaskStatusTest.java     # TDD: TaskStatus枚舉測試
│   ├── service/
│   │   └── TaskDomainServiceTest.java
│   └── repository/
│       └── TaskRepositoryTest.java (interface測試)
├── application/
│   └── usecase/
│       └── CreateTaskUseCaseTest.java
└── infrastructure/
    └── persistence/
        └── TaskJpaRepositoryTest.java
```

### TDD提交訊息模板

#### Red Phase提交
```
test([scope]): add failing test for [feature]

- [Test description]
- [Expected behavior description]
- Test currently fails (Red phase)

TDD: Red phase for [UC-XXX] [UseCaseName]
```

#### Green Phase提交  
```
feat([scope]): implement minimal [feature]

- [Implementation description]
- [Basic functionality added]
- Make tests pass with minimal implementation

TDD: Green phase for [UC-XXX] [UseCaseName]
```

#### Refactor Phase提交
```
refactor([scope]): improve [feature] design

- [Refactoring description]
- [Quality improvements]
- All tests remain green

TDD: Refactor phase for [UC-XXX] [UseCaseName]
```

## Phase 1 TDD開發計劃

### 1.1 Task實體開發 (TDD週期)
```bash
# Red: 寫Task創建測試
# Green: 實作基本Task類別
# Refactor: 改善Task設計和封裝
```

### 1.2 TaskId值物件開發 (TDD週期)
```bash
# Red: 寫TaskId驗證測試
# Green: 實作TaskId類別
# Refactor: 改善TaskId設計
```

### 1.3 TaskStatus枚舉開發 (TDD週期)
```bash
# Red: 寫TaskStatus轉換測試
# Green: 實作TaskStatus枚舉
# Refactor: 改善狀態轉換邏輯
```

## 測試覆蓋率追蹤

### 每個TDD週期後檢查
```bash
# 運行測試覆蓋率報告
./mvnw jacoco:report

# 檢查覆蓋率
open target/site/jacoco/index.html
```

### 覆蓋率目標
- **Unit Tests**: 100% (TDD自然達成)
- **Integration Tests**: 85%+
- **Overall**: 90%+

## 持續整合檢查點

### 每次提交自動檢查
1. 編譯成功
2. 所有測試通過
3. 測試覆蓋率達標
4. 靜態程式碼分析通過
5. 提交訊息符合規範

### TDD質量檢查清單
- [ ] 測試先於實作代碼撰寫
- [ ] 每個測試都經歷過失敗狀態
- [ ] 實作代碼僅為通過測試的最小需求
- [ ] 重構階段保持測試通過
- [ ] 測試覆蓋所有重要路徑

## IDE設置建議

### VS Code配置
```json
{
  "java.test.config": {
    "workingDirectory": "${workspaceFolder}",
    "args": ["-Dfile.encoding=UTF-8"]
  },
  "java.compile.nullAnalysis.mode": "automatic",
  "editor.formatOnSave": true
}
```

### 推薦擴展套件
- Java Extension Pack
- Java Test Runner
- Coverage Gutters (顯示測試覆蓋率)

## TDD最佳實踐

### Do's (該做的)
- 測試名稱清楚表達意圖
- 一次只測試一個行為
- 使用AAA模式 (Arrange-Act-Assert)
- 保持測試簡單和快速
- 頻繁運行測試

### Don'ts (避免的)
- 不要跳過Red階段
- 不要過度實作 (超出測試需求)
- 不要忽略重構階段
- 不要寫複雜的測試
- 不要測試第三方程式庫

## 里程碑追蹤

### TDD里程碑標籤
- `v0.1.0-tdd-task-entity`: Task實體TDD完成
- `v0.1.1-tdd-task-id`: TaskId值物件TDD完成  
- `v0.1.2-tdd-task-status`: TaskStatus枚舉TDD完成
- `v0.1.3-phase1-complete`: Phase 1 Domain層完成