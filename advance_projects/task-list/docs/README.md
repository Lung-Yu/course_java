# 任務管理系統 (Task Management System)

## 專案概述
本專案是一個基於Spring Boot的任務管理網站工具，採用Domain-Driven Design (DDD) 架構模式開發。專案旨在展示Java核心技術的實際應用，包括物件導向程式設計、集合框架、例外處理、檔案操作和執行緒等技術主軸。

## 文件結構
```
docs/
├── README.md                    # 專案概述
├── use-cases.md                # Use Case 規格文件
├── architecture.md             # 技術架構文件
├── testing-strategy.md         # 測試策略文件
└── development-plan.md         # 開發執行計畫
```

## 技術堆疊
- **後端框架**: Spring Boot 3.x
- **資料庫**: PostgreSQL
- **容器化**: Docker
- **基礎設施**: Terraform
- **測試框架**: JUnit 5, Mockito, Testcontainers
- **Java版本**: Java 17+

## DDD 架構層級
- **Domain Layer**: 核心業務邏輯和實體
- **Application Layer**: Use Case 實現和應用服務
- **Infrastructure Layer**: 資料持久化和外部服務
- **Presentation Layer**: REST API 控制器

## 技術主軸應用
1. **物件導向**: Domain實體設計、設計模式應用
2. **集合框架**: 任務集合操作、排序、過濾
3. **例外處理**: 自定義例外、錯誤處理機制
4. **檔案操作**: 任務匯入/匯出、附件管理
5. **執行緒**: 批次處理、非同步操作

## 開發原則
- Test-Driven Development (TDD)
- Clean Code practices
- SOLID 原則
- 每個Use Case都有對應的測試案例