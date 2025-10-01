# Task List 部署方式

本系統支援兩種標準化的部署方式：

## 🏗️ 方式一：開發測試環境

**使用 Docker Compose**

```bash
# 快速啟動
make dev-start

# 或使用腳本
./scripts/deploy-dev.sh start

# 或直接使用
docker-compose up -d
```

適用於：
- 本地開發
- 功能測試
- 快速演示

## 🚀 方式二：生產部署環境

**使用 Terraform**

```bash
# 初始化
make prod-init

# 規劃部署
make prod-plan

# 應用部署
make prod-apply
```

適用於：
- 生產環境
- 預生產環境
- 基礎設施即代碼 (IaC)

## 📖 詳細文檔

查看完整部署指南：[DEPLOYMENT.md](./DEPLOYMENT.md)

## 🔧 快速命令

```bash
# 查看所有可用命令
make help

# 開發環境
make dev-start     # 啟動
make dev-status    # 狀態
make dev-logs      # 日誌
make dev-stop      # 停止

# 生產環境
make prod-apply    # 部署
make prod-status   # 狀態
make prod-destroy  # 銷毀
```

## 📋 項目結構

```
task-list/
├── docker-compose.yml      # 開發測試環境配置
├── infra/
│   └── main.tf            # 生產環境 Terraform 配置
├── scripts/
│   ├── deploy-dev.sh      # 開發環境部署腳本
│   └── deploy-prod.sh     # 生產環境部署腳本
├── Makefile               # 統一管理命令
└── DEPLOYMENT.md          # 詳細部署文檔
```

---

**僅支援這兩種部署方式，不支援其他部署方法。**
