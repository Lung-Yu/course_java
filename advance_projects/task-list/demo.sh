#!/bin/bash

# 任務管理系統 MVC 前端演示腳本
# 快速啟動和測試系統

echo "🚀 任務管理系統 MVC 前端演示"
echo "================================"

# 檢查Java版本
echo "📋 檢查環境..."
if ! command -v java &> /dev/null; then
    echo "❌ Java 未安裝，請先安裝 Java 21"
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | head -n1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 17 ]; then
    echo "❌ Java 版本過低，需要 Java 17 或更高版本"
    exit 1
fi

echo "✅ Java 版本: $(java -version 2>&1 | head -n1)"

# 檢查Maven
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven 未安裝，請先安裝 Maven"
    exit 1
fi

echo "✅ Maven 版本: $(mvn -version | head -n1)"

# 編譯項目
echo ""
echo "🔨 編譯項目..."
mvn clean compile -q
if [ $? -ne 0 ]; then
    echo "❌ 編譯失敗"
    exit 1
fi
echo "✅ 編譯成功"

# 運行測試 (可選)
echo ""
echo "🧪 運行測試..."
mvn test -q
if [ $? -ne 0 ]; then
    echo "⚠️  測試失敗，但繼續運行演示"
else
    echo "✅ 測試通過"
fi

# 啟動應用程式
echo ""
echo "🌐 啟動 Spring Boot 應用程式..."
echo "請稍候，系統正在啟動中..."
echo ""
echo "📱 啟動完成後，請在瀏覽器中訪問："
echo "   🏠 首頁: http://localhost:8080"
echo "   📋 任務列表: http://localhost:8080/tasks"
echo "   ➕ 創建任務: http://localhost:8080/tasks/create"
echo "   📊 統計報表: http://localhost:8080/tasks/statistics"
echo ""
echo "💡 提示:"
echo "   - 使用 Ctrl+C 停止應用程式"
echo "   - 首次啟動可能需要下載依賴，請耐心等待"
echo "   - 如遇到端口被占用，請關閉其他使用8080端口的程式"
echo ""
echo "🎉 開始演示！"
echo "================================"

# 啟動應用程式
mvn spring-boot:run