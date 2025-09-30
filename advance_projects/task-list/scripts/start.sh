#!/bin/bash

# Task List 應用程式啟動腳本

set -e

# 顏色輸出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 日誌函數
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 檢查 Docker 是否運行
check_docker() {
    log_info "檢查 Docker 是否運行..."
    if ! docker info > /dev/null 2>&1; then
        log_error "Docker 未運行，請先啟動 Docker"
        exit 1
    fi
    log_success "Docker 運行正常"
}

# 檢查 Terraform 是否安裝
check_terraform() {
    log_info "檢查 Terraform 是否安裝..."
    if ! command -v terraform &> /dev/null; then
        log_error "Terraform 未安裝，請先安裝 Terraform"
        exit 1
    fi
    log_success "Terraform 已安裝"
}

# 清理函數
cleanup() {
    log_warning "正在清理資源..."
    cd infra
    terraform destroy -auto-approve || true
    cd ..
    docker-compose down -v || true
    docker system prune -f || true
}

# 捕獲中斷信號
trap cleanup EXIT

# 選擇啟動方式
show_menu() {
    echo ""
    echo "=========================================="
    echo "     Task List 應用程式啟動選單"
    echo "=========================================="
    echo "1. 使用 Docker Compose 啟動"
    echo "2. 使用 Terraform 啟動"
    echo "3. 僅啟動資料庫 (Terraform)"
    echo "4. 清理所有資源"
    echo "5. 檢視服務狀態"
    echo "6. 退出"
    echo "=========================================="
    echo -n "請選擇選項 (1-6): "
}

# Docker Compose 啟動
start_with_compose() {
    log_info "使用 Docker Compose 啟動應用程式..."
    
    # 建置並啟動服務
    docker-compose build
    docker-compose up -d
    
    log_info "等待服務啟動..."
    sleep 15
    
    # 檢查服務狀態
    if docker-compose ps | grep -q "Up"; then
        log_success "服務啟動成功！"
        show_service_info
    else
        log_error "服務啟動失敗"
        docker-compose logs
        exit 1
    fi
}

# Terraform 啟動
start_with_terraform() {
    log_info "使用 Terraform 啟動應用程式..."
    
    cd infra
    
    # 初始化 Terraform
    log_info "初始化 Terraform..."
    terraform init
    
    # 規劃部署
    log_info "規劃 Terraform 部署..."
    terraform plan
    
    # 應用部署
    log_info "應用 Terraform 部署..."
    terraform apply -auto-approve
    
    cd ..
    
    log_info "等待服務啟動..."
    sleep 20
    
    log_success "Terraform 部署完成！"
    show_service_info
}

# 僅啟動資料庫
start_database_only() {
    log_info "僅啟動 PostgreSQL 資料庫..."
    
    cd infra
    
    # 創建僅資料庫的 Terraform 配置
    cat > database.tf << EOF
terraform {
  required_providers {
    docker = {
      source  = "kreuzwerker/docker"
      version = "~> 3.0"
    }
  }
}

provider "docker" {
  host = "unix:///var/run/docker.sock"
}

resource "docker_image" "postgres" {
  name = "postgres:15"
}

resource "docker_container" "postgres" {
  name  = "task-list-postgres"
  image = docker_image.postgres.image_id

  ports {
    internal = 5432
    external = 5432
  }

  env = [
    "POSTGRES_DB=demo_task_db",
    "POSTGRES_USER=demo-task",
    "POSTGRES_PASSWORD=demo-pwd"
  ]

  volumes {
    host_path      = "/tmp/postgres_data"
    container_path = "/var/lib/postgresql/data"
  }
}
EOF

    terraform init
    terraform apply -auto-approve -target=docker_container.postgres
    
    cd ..
    
    log_success "資料庫啟動完成！"
    log_info "資料庫連接信息："
    log_info "  主機: localhost"
    log_info "  端口: 5432"
    log_info "  資料庫: demo_task_db"
    log_info "  用戶: demo-task"
    log_info "  密碼: demo-pwd"
}

# 顯示服務信息
show_service_info() {
    echo ""
    log_success "=========================================="
    log_success "           服務啟動成功！"
    log_success "=========================================="
    echo ""
    log_info "📱 Task List 應用程式:"
    log_info "   URL: http://localhost:8080"
    log_info "   健康檢查: http://localhost:8080/actuator/health"
    echo ""
    log_info "🗄️  PostgreSQL 資料庫:"
    log_info "   主機: localhost:5432"
    log_info "   資料庫: demo_task_db"
    log_info "   用戶: demo-task / demo-pwd"
    echo ""
    log_info "🔧 pgAdmin 管理介面:"
    log_info "   URL: http://localhost:5050"
    log_info "   登入: admin@taskapp.com / admin123"
    echo ""
    log_success "=========================================="
}

# 檢視服務狀態
check_status() {
    log_info "檢查服務狀態..."
    echo ""
    
    # 檢查 Docker 容器
    log_info "Docker 容器狀態:"
    docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}" | grep -E "(task-list|postgres)" || log_warning "沒有相關容器運行"
    
    echo ""
    
    # 檢查應用程式健康狀態
    log_info "應用程式健康檢查:"
    if curl -s http://localhost:8080/actuator/health > /dev/null 2>&1; then
        log_success "應用程式健康狀態: 正常"
    else
        log_warning "應用程式健康狀態: 無法連接"
    fi
    
    # 檢查資料庫連接
    log_info "資料庫連接檢查:"
    if docker exec -it task-list-postgres pg_isready -U demo-task -d demo_task_db > /dev/null 2>&1; then
        log_success "資料庫連接: 正常"
    else
        log_warning "資料庫連接: 無法連接"
    fi
}

# 清理所有資源
cleanup_all() {
    log_warning "清理所有資源..."
    
    # 停止 Docker Compose 服務
    if [ -f docker-compose.yml ]; then
        docker-compose down -v
    fi
    
    # 清理 Terraform 資源
    if [ -d infra ]; then
        cd infra
        if [ -f terraform.tfstate ]; then
            terraform destroy -auto-approve
        fi
        rm -f database.tf
        cd ..
    fi
    
    # 清理 Docker 資源
    docker system prune -f
    
    log_success "資源清理完成"
}

# 主函數
main() {
    log_info "Task List 應用程式管理腳本"
    
    # 檢查必要條件
    check_docker
    check_terraform
    
    while true; do
        show_menu
        read -r choice
        
        case $choice in
            1)
                start_with_compose
                ;;
            2)
                start_with_terraform
                ;;
            3)
                start_database_only
                ;;
            4)
                cleanup_all
                ;;
            5)
                check_status
                ;;
            6)
                log_info "退出..."
                exit 0
                ;;
            *)
                log_error "無效選項，請重新選擇"
                ;;
        esac
        
        echo ""
        echo "按任意鍵繼續..."
        read -n 1 -s
    done
}

# 如果直接執行腳本
if [[ "${BASH_SOURCE[0]}" == "${0}" ]]; then
    main "$@"
fi