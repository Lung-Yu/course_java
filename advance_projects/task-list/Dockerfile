# 使用 OpenJDK 21 作為基礎映像檔
FROM openjdk:21-jdk-slim

WORKDIR /app

# 安裝必要工具
RUN apt-get update && apt-get install -y \
    curl \
    netcat-traditional \
    && rm -rf /var/lib/apt/lists/*

# 複製已建置的 JAR 文件
COPY target/*.jar app.jar

# 複製啟動腳本
COPY scripts/wait-for-db.sh /app/wait-for-db.sh
RUN chmod +x /app/wait-for-db.sh

# 創建非 root 用戶
RUN groupadd -r appuser && useradd -r -g appuser appuser

# 切換到非 root 用戶
RUN chown -R appuser:appuser /app
USER appuser

# 暴露端口
EXPOSE 8080

# 健康檢查
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# 啟動應用程式
ENTRYPOINT ["/app/wait-for-db.sh", "java", "-jar", "app.jar"]