#!/bin/bash

# ==========================================
# Task List 開發測試環境部署腳本
# 使用方式: ./scripts/deploy-dev.sh [start|stop|restart|status|logs|clean]
# ==========================================

set -e

# 顏色定義
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m'

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

# 檢查 Docker
check_docker() {
    if ! docker info > /dev/null 2>&1; then
        log_error "Docker 未運行，請先啟動 Docker"
        exit 1
    fi
}

# 啟動服務
start_services() {
    log_info "啟動開發測試環境..."
    
    # 檢查 Docker Compose 配置
    log_info "驗證 Docker Compose 配置..."
    if ! docker-compose config > /dev/null 2>&1; then
        log_error "Docker Compose 配置有誤"
        exit 1
    fi
    
    # 建置映像（使用多階段建置，會自動在 Docker 內編譯）
    log_info "建置 Docker 映像（包含編譯 Java 應用程式）..."
    log_info "這可能需要幾分鐘，請稍候..."
    
    if ! docker-compose build --progress=plain; then
        log_error "Docker 映像建置失敗"
        log_info "提示: 檢查網路連接和 Docker 資源配置"
        exit 1
    fi
    
    log_success "Docker 映像建置完成"
    
    # 啟動服務
    log_info "啟動服務容器..."
    docker-compose up -d
    
    # 等待服務就緒
    log_info "等待服務啟動（這可能需要 30-60 秒）..."
    sleep 20
    
    # 檢查健康狀態
    check_health
}

# 停止服務
stop_services() {
    log_info "停止開發測試環境..."
    docker-compose down
    log_success "服務已停止"
}

# 重啟服務
restart_services() {
    log_info "重啟開發測試環境..."
    docker-compose restart
    sleep 10
    check_health
}

# 檢查健康狀態
check_health() {
    log_info "檢查服務健康狀態..."
    
    # 檢查資料庫
    if docker exec task-list-postgres-dev pg_isready -U demo-task -d demo_task_db > /dev/null 2>&1; then
        log_success "✓ 資料庫連接正常"
    else
        log_error "✗ 資料庫連接失敗"
        return 1
    fi
    
    # 檢查應用程式
    local max_attempts=12
    local attempt=1
    
    while [ $attempt -le $max_attempts ]; do
        if curl -sf http://localhost:8080/actuator/health > /dev/null 2>&1; then
            log_success "✓ 應用程式運行正常"
            break
        else
            if [ $attempt -eq $max_attempts ]; then
                log_error "✗ 應用程式啟動失敗"
                return 1
            fi
            log_info "等待應用程式就緒... ($attempt/$max_attempts)"
            sleep 5
            ((attempt++))
        fi
    done
    
    show_info
}

# 查看狀態
show_status() {
    log_info "開發測試環境狀態："
    echo ""
    docker-compose ps
    echo ""
    
    # 詳細健康檢查
    log_info "健康檢查："
    
    # 資料庫
    if docker exec task-list-postgres-dev pg_isready -U demo-task > /dev/null 2>&1; then
        echo -e "${GREEN}✓${NC} PostgreSQL: 運行中"
    else
        echo -e "${RED}✗${NC} PostgreSQL: 未運行"
    fi
    
    # 應用程式
    if curl -sf http://localhost:8080/actuator/health > /dev/null 2>&1; then
        echo -e "${GREEN}✓${NC} Application: 運行中"
    else
        echo -e "${RED}✗${NC} Application: 未運行"
    fi
    
    # pgAdmin
    if curl -sf http://localhost:5050 > /dev/null 2>&1; then
        echo -e "${GREEN}✓${NC} pgAdmin: 運行中"
    else
        echo -e "${YELLOW}⚠${NC} pgAdmin: 未運行"
    fi
}

# 查看日誌
show_logs() {
    local service=${1:-}
    
    if [ -z "$service" ]; then
        log_info "查看所有服務日誌..."
        docker-compose logs -f
    else
        log_info "查看 $service 服務日誌..."
        docker-compose logs -f "$service"
    fi
}

# 清理環境
clean_environment() {
    log_warning "清理開發測試環境..."
    read -p "確定要清理所有資料嗎？(y/N) " -n 1 -r
    echo
    
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        docker-compose down -v
        docker system prune -f
        log_success "環境已清理"
    else
        log_info "取消清理"
    fi
}

# 顯示訪問信息
show_info() {
    echo ""
    log_success "=========================================="
    log_success "  開發測試環境部署成功！"
    log_success "=========================================="
    echo ""
    echo -e "${CYAN}📱 Task List 應用程式${NC}"
    echo "   URL: http://localhost:8080"
    echo "   健康檢查: http://localhost:8080/actuator/health"
    echo "   H2 控制台: http://localhost:8080/h2-console"
    echo ""
    echo -e "${CYAN}🗄️  PostgreSQL 資料庫${NC}"
    echo "   主機: localhost:5432"
    echo "   資料庫: demo_task_db"
    echo "   用戶: demo-task"
    echo "   密碼: demo-pwd"
    echo ""
    echo -e "${CYAN}🔧 pgAdmin 管理介面${NC}"
    echo "   URL: http://localhost:5050"
    echo "   登入: admin@taskapp.local / admin123"
    echo ""
    echo -e "${CYAN}📋 常用命令${NC}"
    echo "   停止服務: ./scripts/deploy-dev.sh stop"
    echo "   重啟服務: ./scripts/deploy-dev.sh restart"
    echo "   查看狀態: ./scripts/deploy-dev.sh status"
    echo "   查看日誌: ./scripts/deploy-dev.sh logs"
    echo "   清理環境: ./scripts/deploy-dev.sh clean"
    echo ""
    log_success "=========================================="
}

# 顯示幫助
show_help() {
    echo "Task List 開發測試環境部署工具"
    echo ""
    echo "使用方式: ./scripts/deploy-dev.sh [命令]"
    echo ""
    echo "命令:"
    echo "  start    - 啟動開發測試環境"
    echo "  stop     - 停止開發測試環境"
    echo "  restart  - 重啟開發測試環境"
    echo "  status   - 查看服務狀態"
    echo "  logs     - 查看服務日誌（可選服務名稱）"
    echo "  clean    - 清理環境和資料"
    echo "  help     - 顯示此幫助信息"
    echo ""
    echo "範例:"
    echo "  ./scripts/deploy-dev.sh start"
    echo "  ./scripts/deploy-dev.sh logs app"
    echo "  ./scripts/deploy-dev.sh status"
}

# 主函數
main() {
    local command=${1:-start}
    
    # 切換到項目根目錄
    cd "$(dirname "$0")/.."
    
    case "$command" in
        start)
            check_docker
            start_services
            ;;
        stop)
            stop_services
            ;;
        restart)
            check_docker
            restart_services
            ;;
        status)
            show_status
            ;;
        logs)
            show_logs "$2"
            ;;
        clean)
            clean_environment
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
