#!/bin/bash

# Terraform 啟動腳本

set -e

echo "🚀 使用 Terraform 啟動 Task List 應用程式..."

# 切換到 infra 目錄
cd infra

# 檢查 Terraform
if ! command -v terraform &> /dev/null; then
    echo "❌ Terraform 未安裝，請先安裝 Terraform"
    exit 1
fi

echo "✅ Terraform 已安裝"

# 初始化 Terraform
echo "🔧 初始化 Terraform..."
terraform init

# 先建置應用程式
echo "🔨 建置應用程式..."
cd ..
./mvnw clean package -DskipTests
cd infra

# 規劃 Terraform 部署
echo "📋 規劃 Terraform 部署..."
terraform plan

# 確認是否要繼續
echo ""
read -p "是否要繼續部署？(y/N): " -n 1 -r
echo ""

if [[ $REPLY =~ ^[Yy]$ ]]; then
    # 應用 Terraform 配置
    echo "🚀 應用 Terraform 配置..."
    terraform apply -auto-approve
    
    echo "⏳ 等待服務啟動..."
    sleep 30
    
    # 回到主目錄執行測試
    cd ..
    ./test-system.sh
else
    echo "❌ 部署已取消"
    exit 0
fi