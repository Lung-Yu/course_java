# ==========================================
# 階段 1: Maven 建置階段
# ==========================================
FROM maven:3.9-eclipse-temurin-21-alpine AS builder

# 設置工作目錄
WORKDIR /build

# 複製 Maven 配置文件（利用 Docker 快取層）
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
COPY mvnw.cmd .

# 下載依賴（這一層會被快取，除非 pom.xml 改變）
RUN ./mvnw dependency:go-offline -B

# 複製源代碼
COPY src ./src

# 建置應用程式（跳過測試以加快建置速度）
RUN ./mvnw clean package -DskipTests -B

# 驗證 JAR 文件是否存在
RUN ls -la /build/target/*.jar

# ==========================================
# 階段 2: 運行時階段
# ==========================================
FROM eclipse-temurin:21-jre-alpine

# 設置工作目錄
WORKDIR /app

# 安裝必要工具（Alpine 使用 apk）
RUN apk add --no-cache \
    curl \
    netcat-openbsd \
    bash

# 從建置階段複製 JAR 文件
COPY --from=builder /build/target/*.jar app.jar

# 複製啟動腳本
COPY scripts/wait-for-db.sh /app/wait-for-db.sh
RUN chmod +x /app/wait-for-db.sh

# 創建日誌和上傳目錄
RUN mkdir -p /app/logs /app/uploads

# 創建非 root 用戶
RUN addgroup -S appuser && adduser -S appuser -G appuser

# 設置目錄權限
RUN chown -R appuser:appuser /app

# 切換到非 root 用戶
USER appuser

# 暴露端口
EXPOSE 8080

# 健康檢查
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# 設置 JVM 參數
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# 啟動應用程式
ENTRYPOINT ["/app/wait-for-db.sh"]
CMD ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]