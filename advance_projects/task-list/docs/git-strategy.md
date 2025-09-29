# Git 版本控管策略 (Trunk-Based Development)

## Trunk-Based Development 模式

### 主要分支
- `main` (trunk): 唯一長期分支，所有開發都直接在此進行
- 短期功能分支 (可選): 存活時間 < 2天，用於複雜功能

### Trunk-Based 開發原則
1. **直接在main分支開發**: 大部分變更直接提交到main
2. **小步快跑**: 頻繁提交小的變更
3. **Feature Flag**: 使用功能開關控制未完成功能
4. **持續整合**: 每次提交都觸發自動化測試
5. **快速回饋**: 問題立即可見，快速修復

### 開發階段規劃 (基於Feature Flags)

#### Phase 1: 基礎架構建立
- 直接在 `main` 分支開發
- 使用 Feature Flag: `PHASE_1_DOMAIN_MODEL_ENABLED`
- 標籤: `v0.1.x-phase1-milestone`

#### Phase 2: 核心Use Cases實作  
- 直接在 `main` 分支開發
- 使用 Feature Flag: `PHASE_2_CRUD_ENABLED`
- 標籤: `v0.2.x-phase2-milestone`

#### Phase 3: 檔案操作功能
- 直接在 `main` 分支開發
- 使用 Feature Flag: `PHASE_3_FILE_OPS_ENABLED`
- 標籤: `v0.3.x-phase3-milestone`

#### Phase 4: 非同步處理和批次作業
- 直接在 `main` 分支開發
- 使用 Feature Flag: `PHASE_4_ASYNC_ENABLED`
- 標籤: `v0.4.x-phase4-milestone`

#### Phase 5: Web API和整合
- 直接在 `main` 分支開發
- 使用 Feature Flag: `PHASE_5_API_ENABLED`
- 標籤: `v0.5.x-phase5-milestone`

## 提交規範

### 提交訊息格式
```
<type>(<scope>): <subject>

<body>

<footer>
```

### 提交類型 (Type)
- `feat`: 新功能
- `fix`: 錯誤修復
- `docs`: 文檔更新
- `style`: 程式碼格式調整
- `refactor`: 程式碼重構
- `test`: 測試相關
- `chore`: 建構工具或輔助工具變動

### 提交範例
```bash
feat(domain): add Task entity with status management

- Implement Task aggregate root with DDD principles
- Add TaskStatus enum with validation rules
- Include TaskId value object for type safety
- Add domain events for status changes

Closes #123
```

## 各階段版本控管檢核點

### Phase 1 檢核點
- [ ] Domain模型完成 → `git tag v0.1.0-phase1-domain`
- [ ] 例外處理完成 → `git tag v0.1.1-phase1-exception`
- [ ] 基礎架構完成 → `git tag v0.1.2-phase1-complete`

### Phase 2 檢核點
- [ ] CRUD Use Cases → `git tag v0.2.0-phase2-crud`
- [ ] 所有測試通過 → `git tag v0.2.1-phase2-tested`
- [ ] 整合測試完成 → `git tag v0.2.2-phase2-complete`

### Phase 3 檢核點
- [ ] 檔案處理器 → `git tag v0.3.0-phase3-file`
- [ ] 匯入匯出功能 → `git tag v0.3.1-phase3-import-export`
- [ ] 附件管理完成 → `git tag v0.3.2-phase3-complete`

### Phase 4 檢核點
- [ ] 批次處理 → `git tag v0.4.0-phase4-batch`
- [ ] 非同步功能 → `git tag v0.4.1-phase4-async`
- [ ] 效能優化完成 → `git tag v0.4.2-phase4-complete`

### Phase 5 檢核點
- [ ] REST API → `git tag v0.5.0-phase5-api`
- [ ] 統計功能 → `git tag v0.5.1-phase5-stats`
- [ ] 系統完成 → `git tag v1.0.0-release`

## Trunk-Based Git 工作流程

### 1. 一般開發流程 (直接在main開發)
```bash
# 確保在最新的main分支
git checkout main
git pull origin main

# 直接在main進行開發
# 小步提交，頻繁推送
```

### 2. 小步提交策略
```bash
# 每個小功能完成就提交
git add .
git commit -m "feat(domain): add TaskId value object

- Implement UUID-based TaskId
- Add validation logic
- Include toString method"

# 立即推送到遠端
git push origin main
```

### 3. 複雜功能短期分支 (存活 < 2天)
```bash
# 僅在必要時創建短期分支
git checkout -b temp/complex-domain-model
# 快速開發，儘速合併回main
git checkout main
git merge temp/complex-domain-model
git branch -d temp/complex-domain-model
git push origin main
```

### 4. 里程碑標記
```bash
# 階段完成時打標籤
git tag -a v0.1.0-phase1-domain -m "Phase 1: Domain model complete"
git push origin v0.1.0-phase1-domain
```

## Feature Flag 配置

### 配置檔案 (application.yml)
```yaml
features:
  phase1-domain-model: true
  phase2-crud-operations: false
  phase3-file-operations: false
  phase4-async-processing: false
  phase5-web-api: false
```

### Feature Flag 使用範例
```java
@Component
public class FeatureFlags {
    
    @Value("${features.phase1-domain-model:false}")
    private boolean phase1Enabled;
    
    @Value("${features.phase2-crud-operations:false}")
    private boolean phase2Enabled;
    
    public boolean isPhase1Enabled() {
        return phase1Enabled;
    }
}

// Use Case 中使用
@Service
public class CreateTaskUseCase {
    
    @Autowired
    private FeatureFlags featureFlags;
    
    public TaskDTO execute(CreateTaskRequest request) {
        if (!featureFlags.isPhase2Enabled()) {
            throw new FeatureNotAvailableException("CRUD operations not yet available");
        }
        // 實際邏輯
    }
}
```

## 程式碼品質檢核 (每次提交)

### 自動化檢查
- [ ] 程式碼編譯成功
- [ ] 所有測試通過 (單元+整合)
- [ ] 測試覆蓋率 ≥ 85%
- [ ] 靜態程式碼分析通過
- [ ] 安全性掃描無高風險問題

### 提交前檢查
- [ ] 提交訊息符合規範
- [ ] 變更範圍小且聚焦
- [ ] 相關文檔已更新
- [ ] Feature Flag 正確配置
- [ ] 向後相容性檢查

## 持續整合配置 (Trunk-Based)

### GitHub Actions工作流程
```yaml
name: Trunk-Based CI/CD Pipeline

on:
  push:
    branches: [ main ]  # 只監聽main分支

jobs:
  test:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        
    - name: Run tests
      run: ./mvnw test
      
    - name: Check test coverage
      run: ./mvnw jacoco:report
      
    - name: Static code analysis
      run: ./mvnw sonar:sonar
      
    - name: Security scan
      run: ./mvnw dependency-check:check
```

## 緊急狀況處理 (Trunk-Based)

### 快速回滾策略
```bash
# 方法1: 立即revert有問題的提交
git revert <problematic-commit-hash>
git push origin main

# 方法2: 回滾到特定標籤並強制推送 (緊急情況)
git reset --hard v0.1.0-phase1-domain
git push --force-with-lease origin main
```

### 熱修復流程
```bash
# 直接在main修復
git checkout main
git pull origin main
# 修復問題
git add .
git commit -m "hotfix: resolve critical production issue

- Fix null pointer exception in TaskService
- Add validation for empty task lists
- Update error handling logic

Fixes: #456"
git push origin main
```

### 衝突處理 (應該很少發生)
```bash
# 由於頻繁同步，衝突應該很少
git pull origin main
# 如有衝突，立即解決
git add .
git commit -m "resolve conflicts"
git push origin main
```

## 發布流程 (Trunk-Based)

### 持續發布準備
```bash
# main分支隨時可發布
# 使用Feature Flag控制功能啟用

# 階段性里程碑
git tag -a v0.1.0 -m "Phase 1 Complete: Domain Model"
git push origin v0.1.0
```

### 正式發布
```bash
# 簡單的標籤發布
git checkout main
git pull origin main

# 確保所有測試通過
./mvnw test

# 打上發布標籤
git tag -a v1.0.0 -m "Release version 1.0.0 - Task Management System"
git push origin v1.0.0

# 更新Feature Flags啟用所有功能
# 部署到生產環境
```

## Trunk-Based 開發優勢

### 對本專案的好處
1. **快速反馈**: 每次提交立即可見問題
2. **簡化流程**: 不需要複雜的分支管理
3. **持續整合**: 天然支持CI/CD流程
4. **減少衝突**: 頻繁同步避免大規模衝突
5. **學習友好**: 適合學習專案的單人開發

### 注意事項
- 確保每次提交都是可工作的狀態
- 使用Feature Flag隱藏未完成功能
- 測試必須快速且可靠
- 提交訊息要清晰描述變更