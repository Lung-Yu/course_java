# Task List 應用程式 - Docker 部署指南

這個專案包含了完整的 Docker 化 Task List 應用程式，支持本地開發和部署。

## 🚀 快速開始

### 最簡單的方式
```bash
make quick-start
```
或
```bash
./quick-start.sh
```

## 📋 系統需求

- Docker & Docker Compose
- Make (可選)
- Terraform (如果使用 Terraform 部署)
- Java 21 (如果要本地開發)
- Maven (如果要本地建置)

## 🛠️ 安裝指南

### 1. 檢查 Docker 安裝
```bash
docker --version
docker-compose --version
```

### 2. 設置開發環境
```bash
make dev-setup
```

## 🎯 使用方式

### 使用 Make 命令 (推薦)

查看所有可用命令：
```bash
make help
```

常用命令：
```bash
# 快速啟動
make quick-start

# 建置應用程式
make build

# 啟動服務
make start

# 檢查狀態
make status

# 查看日誌
make logs

# 停止服務
make stop

# 清理資源
make clean
```

### 使用 Docker Compose

```bash
# 建置並啟動
docker-compose build
docker-compose up -d

# 查看狀態
docker-compose ps

# 查看日誌
docker-compose logs -f

# 停止服務
docker-compose down
```

### 使用 Terraform

```bash
# 初始化
cd infra
terraform init

# 應用配置
terraform apply

# 銷毀資源
terraform destroy
```

## 🌐 服務端點

### 應用程式
- **主要應用**: http://localhost:8080
- **健康檢查**: http://localhost:8080/actuator/health
- **所有端點**: http://localhost:8080/actuator

### 資料庫
- **PostgreSQL**: localhost:5432
  - 資料庫: `demo_task_db`
  - 用戶: `demo-task`
  - 密碼: `demo-pwd`

### 管理工具
- **pgAdmin**: http://localhost:5050
  - 用戶: `admin@taskapp.com`
  - 密碼: `admin123`

## 🏗️ 架構說明

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Task List     │    │   PostgreSQL    │    │    pgAdmin      │
│   Application   │◄──►│    Database     │◄──►│   (Optional)    │
│   (Port 8080)   │    │   (Port 5432)   │    │   (Port 5050)   │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## 📁 檔案結構

```
task-list/
├── Dockerfile                 # 應用程式 Docker 映像檔
├── docker-compose.yml         # Docker Compose 配置
├── Makefile                   # Make 命令
├── quick-start.sh             # 快速啟動腳本
├── scripts/
│   ├── start.sh              # 完整啟動腳本
│   └── wait-for-db.sh        # 等待資料庫腳本
├── infra/
│   └── main.tf               # Terraform 配置
└── src/
    └── main/
        └── resources/
            └── application-docker.properties
```

## 🔧 配置說明

### 環境變數

可以透過環境變數覆蓋配置：

```bash
# 資料庫配置
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/demo_task_db
SPRING_DATASOURCE_USERNAME=demo-task
SPRING_DATASOURCE_PASSWORD=demo-pwd

# JPA 配置
SPRING_JPA_HIBERNATE_DDL_AUTO=create-drop
SPRING_JPA_SHOW_SQL=true

# 日誌等級
LOGGING_LEVEL_COM_TYGRUS_TASK_LIST=DEBUG

# Profile
SPRING_PROFILES_ACTIVE=docker
```

### Docker Compose 環境

應用程式在 Docker 環境中會自動使用 `application-docker.properties` 配置檔。

## 🐛 故障排除

### 應用程式無法啟動

1. 檢查 Docker 是否運行：
```bash
docker info
```

2. 檢查端口是否被佔用：
```bash
lsof -i :8080
lsof -i :5432
```

3. 查看應用程式日誌：
```bash
make logs-app
# 或
docker-compose logs app
```

### 資料庫連接問題

1. 檢查資料庫狀態：
```bash
docker exec -it task-list-postgres pg_isready -U demo-task -d demo_task_db
```

2. 查看資料庫日誌：
```bash
make logs-db
# 或
docker-compose logs postgres
```

### 清理並重新開始

```bash
make clean
make quick-start
```

## 🧪 開發模式

### 本地開發 (不使用 Docker)

1. 先啟動資料庫：
```bash
make db-only
```

2. 本地運行應用程式：
```bash
make maven-run
```

### 測試

```bash
# 執行測試
make maven-test

# 建置應用程式
make maven-build
```

## 📊 監控和日誌

### 健康檢查

- 應用程式: http://localhost:8080/actuator/health
- 資料庫: `docker exec -it task-list-postgres pg_isready`

### 日誌檔案

日誌會儲存在 `./logs` 目錄中，也可以透過 Docker 命令查看：

```bash
# 查看所有服務日誌
make logs

# 查看特定服務日誌
make logs-app
make logs-db
```

## 🔐 安全注意事項

- 這是開發/演示環境配置
- 生產環境請更改預設密碼
- 考慮使用 Docker Secrets 或環境變數檔案管理敏感資訊

## 🤝 貢獻

歡迎提交 Issue 和 Pull Request！

## 📄 授權

請參考專案根目錄的 LICENSE 檔案。