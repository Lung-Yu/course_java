#!/bin/bash

# ==========================================
# Task List 生產環境部署腳本
# 使用方式: ./scripts/deploy-prod.sh [init|plan|apply|destroy|status|output]
# ==========================================

set -e

# 顏色定義
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m'

# 常量
INFRA_DIR="infra"
TERRAFORM_STATE="terraform.tfstate"

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

# 檢查必要工具
check_requirements() {
    # 檢查 Docker
    if ! docker info > /dev/null 2>&1; then
        log_error "Docker 未運行，請先啟動 Docker"
        exit 1
    fi
    
    # 檢查 Terraform
    if ! command -v terraform &> /dev/null; then
        log_error "Terraform 未安裝，請先安裝 Terraform"
        echo "訪問: https://www.terraform.io/downloads"
        exit 1
    fi
    
    local tf_version=$(terraform version -json | jq -r '.terraform_version')
    log_info "Terraform 版本: $tf_version"
}

# 初始化 Terraform
init_terraform() {
    log_info "初始化 Terraform..."
    
    cd "$INFRA_DIR"
    
    # 初始化
    terraform init -upgrade
    
    # 驗證配置
    terraform validate
    
    cd ..
    
    log_success "Terraform 初始化完成"
}

# 規劃部署
plan_deployment() {
    log_info "規劃 Terraform 部署..."
    
    cd "$INFRA_DIR"
    
    # 生成部署計劃
    terraform plan -out=tfplan
    
    cd ..
    
    log_success "部署計劃已生成"
    log_info "查看計劃: cd infra && terraform show tfplan"
}

# 應用部署
apply_deployment() {
    log_warning "準備部署生產環境..."
    
    # 確認操作
    read -p "確定要部署到生產環境嗎？(y/N) " -n 1 -r
    echo
    
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        log_info "取消部署"
        exit 0
    fi
    
    log_info "開始部署..."
    
    cd "$INFRA_DIR"
    
    # 先建置 Docker 映像（使用多階段建置）
    log_info "建置生產版本 Docker 映像（包含編譯 Java 應用程式）..."
    log_info "這可能需要幾分鐘，請稍候..."
    
    cd ..
    if ! docker build -t task-list-app:prod \
        --build-arg BUILD_ENV=production \
        --progress=plain \
        .; then
        log_error "Docker 映像建置失敗"
        exit 1
    fi
    
    log_success "Docker 映像建置完成"
    
    cd "$INFRA_DIR"
    
    # 應用配置
    log_info "應用 Terraform 配置..."
    terraform apply -auto-approve
    
    cd ..
    
    # 等待服務就緒
    log_info "等待服務啟動..."
    sleep 20
    
    # 檢查健康狀態
    check_health
    
    # 顯示部署信息
    show_deployment_info
}

# 銷毀環境
destroy_environment() {
    log_warning "準備銷毀生產環境..."
    
    # 確認操作
    read -p "確定要銷毀所有生產資源嗎？此操作不可恢復！(yes/N) " -r
    echo
    
    if [[ ! $REPLY == "yes" ]]; then
        log_info "取消銷毀"
        exit 0
    fi
    
    log_warning "開始銷毀..."
    
    cd "$INFRA_DIR"
    
    terraform destroy -auto-approve
    
    cd ..
    
    log_success "生產環境已銷毀"
}

# 檢查健康狀態
check_health() {
    log_info "檢查服務健康狀態..."
    
    # 檢查資料庫
    if docker exec task-list-postgres-prod pg_isready -U demo-task -d demo_task_db > /dev/null 2>&1; then
        log_success "✓ 資料庫連接正常"
    else
        log_error "✗ 資料庫連接失敗"
        return 1
    fi
    
    # 檢查應用程式
    local max_attempts=15
    local attempt=1
    
    while [ $attempt -le $max_attempts ]; do
        if curl -sf http://localhost:8080/actuator/health > /dev/null 2>&1; then
            log_success "✓ 應用程式運行正常"
            return 0
        else
            if [ $attempt -eq $max_attempts ]; then
                log_error "✗ 應用程式啟動失敗"
                log_info "查看日誌: docker logs task-list-app-prod"
                return 1
            fi
            log_info "等待應用程式就緒... ($attempt/$max_attempts)"
            sleep 5
            ((attempt++))
        fi
    done
}

# 查看狀態
show_status() {
    log_info "生產環境狀態："
    echo ""
    
    cd "$INFRA_DIR"
    
    # Terraform 狀態
    if [ -f "$TERRAFORM_STATE" ]; then
        log_info "Terraform 資源狀態："
        terraform show -json | jq -r '.values.root_module.resources[] | "\(.type).\(.name): \(.values.name // "N/A")"'
    else
        log_warning "未找到 Terraform 狀態文件"
    fi
    
    cd ..
    
    echo ""
    
    # Docker 容器狀態
    log_info "Docker 容器狀態："
    docker ps --filter "label=environment=production" --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
    
    echo ""
    
    # 健康檢查
    log_info "健康檢查："
    
    # 資料庫
    if docker exec task-list-postgres-prod pg_isready -U demo-task > /dev/null 2>&1; then
        echo -e "${GREEN}✓${NC} PostgreSQL: 運行中"
    else
        echo -e "${RED}✗${NC} PostgreSQL: 未運行"
    fi
    
    # 應用程式
    if curl -sf http://localhost:8080/actuator/health > /dev/null 2>&1; then
        echo -e "${GREEN}✓${NC} Application: 運行中"
        
        # 顯示健康詳情
        local health_status=$(curl -s http://localhost:8080/actuator/health | jq -r '.status')
        echo "   狀態: $health_status"
    else
        echo -e "${RED}✗${NC} Application: 未運行"
    fi
}

# 查看輸出
show_output() {
    log_info "Terraform 輸出："
    
    cd "$INFRA_DIR"
    
    if [ -f "$TERRAFORM_STATE" ]; then
        terraform output -json | jq '.'
    else
        log_warning "未找到 Terraform 狀態文件"
    fi
    
    cd ..
}

# 顯示部署信息
show_deployment_info() {
    echo ""
    log_success "=========================================="
    log_success "  生產環境部署成功！"
    log_success "=========================================="
    echo ""
    
    cd "$INFRA_DIR"
    
    # 從 Terraform 輸出獲取信息
    if [ -f "$TERRAFORM_STATE" ]; then
        local app_url=$(terraform output -raw application_url 2>/dev/null || echo "http://localhost:8080")
        
        echo -e "${CYAN}📱 應用程式訪問${NC}"
        echo "   URL: $app_url"
        echo "   健康檢查: $app_url/actuator/health"
        echo ""
        echo -e "${CYAN}🗄️  資料庫信息${NC}"
        echo "   主機: localhost:5432"
        echo "   資料庫: demo_task_db"
        echo "   (使用 terraform output database_connection 查看詳情)"
        echo ""
        echo -e "${CYAN}🐳 Docker 管理${NC}"
        echo "   查看容器: docker ps --filter 'label=environment=production'"
        echo "   查看日誌: docker logs task-list-app-prod"
        echo "   查看網路: docker network inspect task-list-prod-network"
        echo ""
        echo -e "${CYAN}📋 Terraform 管理${NC}"
        echo "   查看狀態: ./scripts/deploy-prod.sh status"
        echo "   查看輸出: ./scripts/deploy-prod.sh output"
        echo "   銷毀環境: ./scripts/deploy-prod.sh destroy"
        echo ""
    fi
    
    cd ..
    
    log_success "=========================================="
}

# 顯示幫助
show_help() {
    echo "Task List 生產環境部署工具"
    echo ""
    echo "使用方式: ./scripts/deploy-prod.sh [命令]"
    echo ""
    echo "命令:"
    echo "  init     - 初始化 Terraform"
    echo "  plan     - 規劃部署變更"
    echo "  apply    - 應用部署配置"
    echo "  destroy  - 銷毀生產環境"
    echo "  status   - 查看環境狀態"
    echo "  output   - 查看 Terraform 輸出"
    echo "  help     - 顯示此幫助信息"
    echo ""
    echo "部署流程:"
    echo "  1. ./scripts/deploy-prod.sh init    # 初始化"
    echo "  2. ./scripts/deploy-prod.sh plan    # 規劃"
    echo "  3. ./scripts/deploy-prod.sh apply   # 部署"
    echo ""
    echo "管理命令:"
    echo "  ./scripts/deploy-prod.sh status     # 查看狀態"
    echo "  ./scripts/deploy-prod.sh destroy    # 銷毀環境"
}

# 主函數
main() {
    local command=${1:-help}
    
    # 切換到項目根目錄
    cd "$(dirname "$0")/.."
    
    case "$command" in
        init)
            check_requirements
            init_terraform
            ;;
        plan)
            check_requirements
            plan_deployment
            ;;
        apply)
            check_requirements
            apply_deployment
            ;;
        destroy)
            destroy_environment
            ;;
        status)
            show_status
            ;;
        output)
            show_output
            ;;
        help|--help|-h)
            show_help
            ;;
        *)
            log_error "未知命令: $command"
            show_help
            exit 1
            ;;
    esac
}

# 執行主函數
main "$@"
