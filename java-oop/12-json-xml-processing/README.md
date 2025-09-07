# 主題 12：JSON 和 XML 處理

## 學習目標
- 掌握 JSON 解析和生成技術
- 學習 XML 文件處理方法
- 了解資料交換格式的應用
- 實作 REST API 資料處理

## 課程內容

### 12.1 JSON 基礎
- JSON 語法結構
- JSON 與 Java 物件的對應關係
- 使用第三方 JSON 函式庫

### 12.2 JSON 解析
- 從 JSON 字串解析資料
- 處理複雜的 JSON 結構
- 錯誤處理和資料驗證

### 12.3 JSON 生成
- 將 Java 物件轉換為 JSON
- 自訂序列化規則
- 格式化輸出

### 12.4 XML 基礎
- XML 文檔結構
- XML Schema 和 DTD
- 命名空間的使用

### 12.5 XML 解析技術
- DOM 解析器
- SAX 解析器
- StAX 解析器
- 各種解析器的比較

### 12.6 XML 生成和操作
- 建立 XML 文檔
- 修改現有 XML 結構
- XML 轉換 (XSLT)

### 12.7 實際應用
- 設定檔處理
- REST API 資料交換
- 資料儲存和載入
- 網路通訊協定

## 實作練習

### 練習 1：學生成績管理系統
建立學生成績管理系統，使用 JSON 格式儲存和載入學生資料。

### 練習 2：設定檔處理器
實作應用程式設定檔處理，支援 JSON 和 XML 格式。

### 練習 3：API 資料處理
模擬 REST API 回應處理，包含 JSON 解析和錯誤處理。

### 練習 4：XML 文檔轉換
實作 XML 文檔的解析、修改和輸出功能。

## 編譯和執行

### 編譯指令
```bash
# 編譯示範程式
javac JsonXmlProcessingDemo.java

# 如果使用外部函式庫（如 Jackson 或 Gson）
javac -cp ".:json-library.jar" JsonXmlProcessingDemo.java
```

### 執行指令
```bash
# 執行示範
java JsonXmlProcessingDemo

# 如果使用外部函式庫
java -cp ".:json-library.jar" JsonXmlProcessingDemo
```

## 重要概念

### JSON 處理最佳實務
- 使用成熟的函式庫（如 Jackson、Gson）
- 適當的錯誤處理
- 資料驗證和型別檢查
- 效能考量

### XML 處理注意事項
- 選擇適合的解析器類型
- 記憶體使用量管理
- 命名空間處理
- 安全性考量（XXE 攻擊防護）

### 設計模式應用
- Builder 模式用於建構複雜結構
- Factory 模式用於建立解析器
- Strategy 模式用於不同格式處理

## 延伸學習
- JSON Schema 驗證
- XML Schema (XSD) 設計
- RESTful Web Services
- 微服務架構中的資料交換
- 效能優化技巧