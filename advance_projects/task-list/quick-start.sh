#!/bin/bash

# ==========================================
# Task List 快速啟動腳本
# 此腳本為開發測試環境快速啟動的簡化版本
# ==========================================

set -e

echo "🚀 Task List 快速啟動..."
echo ""
echo "使用開發測試環境 (Docker Compose)"
echo ""

# 檢查 Docker
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker 未運行，請先啟動 Docker"
    exit 1
fi

echo "✅ Docker 運行正常"
echo ""

# 調用開發環境部署腳本
if [ -f "scripts/deploy-dev.sh" ]; then
    echo "📦 使用 deploy-dev.sh 啟動..."
    chmod +x scripts/deploy-dev.sh
    ./scripts/deploy-dev.sh start
else
    echo "⚠️  未找到 deploy-dev.sh，使用 docker-compose 直接啟動..."
    
    # 建置並啟動
    docker-compose build
    docker-compose up -d
    
    # 等待服務
    echo "⏳ 等待服務啟動..."
    sleep 20
    
    # 簡單健康檢查
    if curl -sf http://localhost:8080/actuator/health > /dev/null 2>&1; then
        echo "✅ 應用程式啟動成功"
    else
        echo "⚠️  應用程式可能尚未完全就緒"
    fi
    
    echo ""
    echo "🎉 服務已啟動！"
    echo "   應用程式: http://localhost:8080"
    echo "   pgAdmin: http://localhost:5050"
    echo ""
    echo "使用 'docker-compose logs -f' 查看日誌"
    echo "使用 'docker-compose down' 停止服務"
fi