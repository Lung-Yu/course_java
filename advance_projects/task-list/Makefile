# Task List 應用程式 Makefile

.PHONY: help build start stop clean status logs test terraform-init terraform-apply terraform-destroy quick-start

# 預設目標
help: ## 顯示說明
	@echo "Task List 應用程式管理命令:"
	@echo ""
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-20s\033[0m %s\n", $$1, $$2}'

quick-start: ## 快速啟動應用程式 (推薦)
	@chmod +x quick-start.sh
	@./quick-start.sh

build: ## 建置 Docker 映像檔
	@echo "🔨 建置 Docker 映像檔..."
	@docker-compose build

start: ## 啟動所有服務
	@echo "🚀 啟動所有服務..."
	@docker-compose up -d

stop: ## 停止所有服務
	@echo "🛑 停止所有服務..."
	@docker-compose down

restart: ## 重啟所有服務
	@echo "🔄 重啟所有服務..."
	@docker-compose restart

status: ## 檢查服務狀態
	@echo "📊 檢查服務狀態..."
	@docker-compose ps
	@echo ""
	@echo "🔍 健康檢查:"
	@curl -s http://localhost:8080/actuator/health || echo "❌ 應用程式無法連接"
	@docker exec -it task-list-postgres pg_isready -U demo-task -d demo_task_db || echo "❌ 資料庫無法連接"

logs: ## 查看服務日誌
	@echo "📋 查看服務日誌..."
	@docker-compose logs -f

logs-app: ## 查看應用程式日誌
	@echo "📋 查看應用程式日誌..."
	@docker-compose logs -f app

logs-db: ## 查看資料庫日誌
	@echo "📋 查看資料庫日誌..."
	@docker-compose logs -f postgres

clean: ## 清理所有資源 (包含 volumes)
	@echo "🧹 清理所有資源..."
	@docker-compose down -v
	@docker system prune -f
	@docker volume prune -f

# Maven 相關命令
maven-build: ## 使用 Maven 建置應用程式
	@echo "🔨 使用 Maven 建置..."
	@./mvnw clean package -DskipTests

maven-test: ## 執行 Maven 測試
	@echo "🧪 執行測試..."
	@./mvnw test

maven-run: ## 本地運行應用程式 (需要先啟動資料庫)
	@echo "🏃 本地運行應用程式..."
	@./mvnw spring-boot:run

# Terraform 相關命令
terraform-init: ## 初始化 Terraform
	@echo "🔧 初始化 Terraform..."
	@cd infra && terraform init

terraform-plan: ## Terraform 規劃
	@echo "📋 Terraform 規劃..."
	@cd infra && terraform plan

terraform-apply: ## 應用 Terraform 配置
	@echo "🚀 應用 Terraform 配置..."
	@cd infra && terraform apply -auto-approve

terraform-destroy: ## 銷毀 Terraform 資源
	@echo "💥 銷毀 Terraform 資源..."
	@cd infra && terraform destroy -auto-approve

# 開發相關命令
dev-setup: ## 開發環境設置
	@echo "🛠️  設置開發環境..."
	@chmod +x mvnw
	@chmod +x scripts/*.sh
	@chmod +x quick-start.sh

db-only: ## 僅啟動資料庫
	@echo "🗄️  啟動資料庫..."
	@docker-compose up -d postgres

open-app: ## 在瀏覽器中打開應用程式
	@echo "🌐 在瀏覽器中打開應用程式..."
	@open http://localhost:8080 || xdg-open http://localhost:8080 || echo "請手動開啟 http://localhost:8080"

open-pgadmin: ## 在瀏覽器中打開 pgAdmin
	@echo "🔧 在瀏覽器中打開 pgAdmin..."
	@open http://localhost:5050 || xdg-open http://localhost:5050 || echo "請手動開啟 http://localhost:5050"

test: ## 執行系統功能測試
	@echo "🧪 執行系統測試..."
	@chmod +x test-system.sh
	@./test-system.sh

# 預設目標
.DEFAULT_GOAL := help