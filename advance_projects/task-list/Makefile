# ==========================================
# Task List 應用程式 Makefile
# ==========================================
# 支援兩種部署方式:
#   1. 開發測試: docker-compose (make dev-*)
#   2. 生產部署: terraform (make prod-*)
# ==========================================

.PHONY: help dev-start dev-stop dev-restart dev-status dev-logs dev-clean \
        prod-init prod-plan prod-apply prod-destroy prod-status prod-output \
        build test maven-build maven-test maven-run setup

# 預設目標
help: ## 顯示說明
	@echo "╔════════════════════════════════════════════════════════════╗"
	@echo "║        Task List 應用程式部署管理工具                      ║"
	@echo "╚════════════════════════════════════════════════════════════╝"
	@echo ""
	@echo "📋 部署方式："
	@echo "  1️⃣  開發測試環境: docker-compose (make dev-*)"
	@echo "  2️⃣  生產部署環境: terraform apply (make prod-*)"
	@echo ""
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | \
		awk 'BEGIN {FS = ":.*?## "}; {printf "  \033[36m%-18s\033[0m %s\n", $$1, $$2}'
	@echo ""

# ==========================================
# 🏗️ 開發測試環境 (Docker Compose)
# ==========================================

dev-start: ## 啟動開發測試環境
	@echo "🚀 啟動開發測試環境..."
	@chmod +x scripts/deploy-dev.sh
	@./scripts/deploy-dev.sh start

dev-stop: ## 停止開發測試環境
	@echo "🛑 停止開發測試環境..."
	@chmod +x scripts/deploy-dev.sh
	@./scripts/deploy-dev.sh stop

dev-restart: ## 重啟開發測試環境
	@echo "🔄 重啟開發測試環境..."
	@chmod +x scripts/deploy-dev.sh
	@./scripts/deploy-dev.sh restart

dev-status: ## 查看開發環境狀態
	@chmod +x scripts/deploy-dev.sh
	@./scripts/deploy-dev.sh status

dev-logs: ## 查看開發環境日誌
	@chmod +x scripts/deploy-dev.sh
	@./scripts/deploy-dev.sh logs

dev-logs-app: ## 查看應用程式日誌
	@chmod +x scripts/deploy-dev.sh
	@./scripts/deploy-dev.sh logs app

dev-logs-db: ## 查看資料庫日誌
	@chmod +x scripts/deploy-dev.sh
	@./scripts/deploy-dev.sh logs postgres

dev-clean: ## 清理開發環境和資料
	@chmod +x scripts/deploy-dev.sh
	@./scripts/deploy-dev.sh clean

# ==========================================
# 🚀 生產部署環境 (Terraform)
# ==========================================

prod-init: ## 初始化 Terraform
	@echo "🔧 初始化 Terraform..."
	@chmod +x scripts/deploy-prod.sh
	@./scripts/deploy-prod.sh init

prod-plan: ## 規劃 Terraform 部署
	@echo "📋 規劃 Terraform 部署..."
	@chmod +x scripts/deploy-prod.sh
	@./scripts/deploy-prod.sh plan

prod-apply: ## 應用 Terraform 部署
	@echo "� 應用 Terraform 部署..."
	@chmod +x scripts/deploy-prod.sh
	@./scripts/deploy-prod.sh apply

prod-destroy: ## 銷毀生產環境
	@echo "� 銷毀生產環境..."
	@chmod +x scripts/deploy-prod.sh
	@./scripts/deploy-prod.sh destroy

prod-status: ## 查看生產環境狀態
	@chmod +x scripts/deploy-prod.sh
	@./scripts/deploy-prod.sh status

prod-output: ## 查看 Terraform 輸出
	@chmod +x scripts/deploy-prod.sh
	@./scripts/deploy-prod.sh output

# ==========================================
# 🔧 Maven 建置命令
# ==========================================

maven-build: ## Maven 建置應用程式
	@echo "🔨 Maven 建置..."
	@./mvnw clean package -DskipTests

maven-test: ## 執行 Maven 測試
	@echo "🧪 執行測試..."
	@./mvnw test

maven-run: ## 本地運行 (需資料庫)
	@echo "🏃 本地運行..."
	@./mvnw spring-boot:run

# ==========================================
# 🛠️ 開發工具命令
# ==========================================

setup: ## 初始化開發環境
	@echo "�️  初始化開發環境..."
	@chmod +x mvnw
	@chmod +x scripts/*.sh
	@echo "✅ 開發環境已準備就緒"

build: ## 建置 Docker 映像
	@echo "� 建置 Docker 映像..."
	@docker build -t task-list-app:latest .

test: ## 執行完整測試
	@echo "🧪 執行測試..."
	@./mvnw test

open-app: ## 開啟應用程式
	@echo "🌐 開啟應用程式..."
	@open http://localhost:8080 || xdg-open http://localhost:8080 || echo "http://localhost:8080"

open-pgadmin: ## 開啟 pgAdmin
	@echo "🔧 開啟 pgAdmin..."
	@open http://localhost:5050 || xdg-open http://localhost:5050 || echo "http://localhost:5050"

# 預設目標
.DEFAULT_GOAL := help