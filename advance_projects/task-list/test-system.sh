#!/bin/bash

# Task List 系統測試腳本

echo "🧪 Task List 應用程式功能測試"
echo "================================"

# 基本健康檢查
echo "1. 🔍 健康檢查..."
health_status=$(curl -s http://localhost:8080/actuator/health | jq -r '.status' 2>/dev/null)
if [ "$health_status" = "UP" ] || [ "$health_status" = "DOWN" ]; then
    echo "   ✅ 應用程式回應正常 (狀態: $health_status)"
else
    echo "   ❌ 應用程式無回應"
    exit 1
fi

# 檢查資料庫連接
echo "2. 🗄️  資料庫連接檢查..."
db_status=$(curl -s http://localhost:8080/actuator/health | jq -r '.components.db.status' 2>/dev/null)
if [ "$db_status" = "UP" ]; then
    echo "   ✅ 資料庫連接正常"
else
    echo "   ❌ 資料庫連接失敗"
fi

# 檢查服務端口
echo "3. 🌐 服務端口檢查..."
if nc -z localhost 8080; then
    echo "   ✅ 應用程式端口 8080 可訪問"
else
    echo "   ❌ 應用程式端口 8080 無法訪問"
fi

if nc -z localhost 5432; then
    echo "   ✅ 資料庫端口 5432 可訪問"
else
    echo "   ❌ 資料庫端口 5432 無法訪問"
fi

if nc -z localhost 5050; then
    echo "   ✅ pgAdmin 端口 5050 可訪問"
else
    echo "   ❌ pgAdmin 端口 5050 無法訪問"
fi

# 檢查容器狀態
echo "4. 🐳 Docker 容器狀態..."
app_status=$(docker inspect task-list-app --format='{{.State.Status}}' 2>/dev/null)
db_status=$(docker inspect task-list-postgres --format='{{.State.Status}}' 2>/dev/null)
pgadmin_status=$(docker inspect task-list-pgadmin --format='{{.State.Status}}' 2>/dev/null)

echo "   📱 應用程式容器: $app_status"
echo "   🗄️  資料庫容器: $db_status"
echo "   🔧 pgAdmin 容器: $pgadmin_status"

# 建立基本的 API 測試
echo "5. 🔌 API 測試..."

# 測試基本端點
echo "   測試根路徑..."
root_response=$(curl -s -w "%{http_code}" -o /dev/null http://localhost:8080/)
echo "   根路徑回應代碼: $root_response"

echo ""
echo "🎉 測試完成！"
echo "================================"
echo ""

if [ "$app_status" = "running" ] && [ "$db_status" = "running" ]; then
    echo "✅ 系統運行正常！"
    echo ""
    echo "📋 可用服務:"
    echo "   🌐 Task List 應用程式: http://localhost:8080"
    echo "   💊 健康檢查: http://localhost:8080/actuator/health"
    echo "   🗄️  PostgreSQL 資料庫: localhost:5432"
    echo "   🔧 pgAdmin 管理介面: http://localhost:5050"
    echo ""
    echo "📊 資料庫連接資訊:"
    echo "   資料庫名稱: demo_task_db"
    echo "   用戶名稱: demo-task"
    echo "   密碼: demo-pwd"
    echo ""
    echo "🔧 pgAdmin 登入資訊:"
    echo "   Email: admin@taskapp.com"
    echo "   密碼: admin123"
else
    echo "⚠️  系統可能有問題，請檢查容器狀態"
fi

echo ""
echo "🛠️  管理命令:"
echo "   停止服務: docker-compose down"
echo "   重啟服務: docker-compose restart"
echo "   查看日誌: docker-compose logs"
echo "   查看狀態: docker-compose ps"