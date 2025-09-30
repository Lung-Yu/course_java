#!/bin/bash

# 等待資料庫就緒的腳本

set -e

# 使用環境變數或預設值
host="${DB_HOST:-postgres}"
port="${DB_PORT:-5432}"

echo "等待資料庫 $host:$port 就緒..."

until nc -z "$host" "$port"; do
    echo "資料庫尚未就緒 - 等待中..."
    sleep 2
done

echo "資料庫已就緒！啟動應用程式..."
exec "$@"