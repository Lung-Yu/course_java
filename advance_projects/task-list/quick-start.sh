#!/bin/bash

# 快速啟動 Task List 應用程式

set -e

echo "🚀 快速啟動 Task List 應用程式..."

# 檢查 Docker
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker 未運行，請先啟動 Docker"
    exit 1
fi

echo "✅ Docker 運行正常"

# 使用 Docker Compose 快速啟動
echo "📦 建置並啟動服務..."
docker-compose build --no-cache
docker-compose up -d

# 等待服務啟動
echo "⏳ 等待服務啟動..."
sleep 30

# 檢查服務狀態
echo "🔍 檢查服務狀態..."

# 檢查資料庫
if docker exec task-list-postgres pg_isready -U demo-task -d demo_task_db > /dev/null 2>&1; then
    echo "✅ 資料庫連接正常"
else
    echo "❌ 資料庫連接失敗"
    docker-compose logs postgres
    exit 1
fi

# 檢查應用程式
max_attempts=10
attempt=1

while [ $attempt -le $max_attempts ]; do
    if curl -s http://localhost:8080/actuator/health > /dev/null 2>&1; then
        echo "✅ 應用程式啟動成功"
        break
    else
        echo "⏳ 等待應用程式啟動... (嘗試 $attempt/$max_attempts)"
        sleep 10
        ((attempt++))
    fi
done

if [ $attempt -gt $max_attempts ]; then
    echo "❌ 應用程式啟動失敗"
    docker-compose logs app
    exit 1
fi

echo ""
echo "🎉 =========================================="
echo "🎉           服務啟動成功！"
echo "🎉 =========================================="
echo ""
echo "📱 Task List 應用程式:"
echo "   🌐 URL: http://localhost:8080"
echo "   💊 健康檢查: http://localhost:8080/actuator/health"
echo ""
echo "🗄️  PostgreSQL 資料庫:"
echo "   🔗 連接: localhost:5432"
echo "   📊 資料庫: demo_task_db"
echo "   👤 登入: demo-task / demo-pwd"
echo ""
echo "🔧 pgAdmin 管理介面:"
echo "   🌐 URL: http://localhost:5050"
echo "   👤 登入: admin@taskapp.com / admin123"
echo ""
echo "🛑 停止服務: docker-compose down"
echo "🔄 重啟服務: docker-compose restart"
echo "📋 查看日誌: docker-compose logs"
echo ""
echo "🎉 =========================================="