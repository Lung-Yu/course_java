import java.util.*;
import java.util.regex.*;
import java.util.stream.Collectors;
import java.nio.charset.StandardCharsets;

/**
 * 字串處理進階應用示例
 * 
 * 本類別展示字串處理的進階應用和實際場景，包括：
 * 1. 複雜的文本解析和處理
 * 2. 字串國際化和編碼處理
 * 3. 高效的字串搜尋和替換
 * 4. 文本格式轉換工具
 * 5. 字串安全處理
 * 6. 實際項目中的字串應用
 * 
 * @author Java Course
 * @version 1.0
 */
public class StringAdvancedDemo {
    
    public static void main(String[] args) {
        System.out.println("=== Java 字串處理進階應用 ===\n");
        
        // 1. 複雜文本解析
        demonstrateComplexTextParsing();
        
        // 2. 字串編碼和國際化
        demonstrateEncodingAndI18n();
        
        // 3. 高效搜尋和替換
        demonstrateEfficientSearchAndReplace();
        
        // 4. 文本格式轉換
        demonstrateTextFormatConversion();
        
        // 5. 字串安全處理
        demonstrateStringSecurity();
        
        // 6. 實際項目應用
        demonstrateRealWorldApplications();
        
        // 7. 效能優化技巧
        demonstratePerformanceOptimization();
    }
    
    /**
     * 示例 1：複雜文本解析
     */
    private static void demonstrateComplexTextParsing() {
        System.out.println("1. 複雜文本解析示例");
        System.out.println("======================");
        
        // CSV 數據解析（處理嵌套引號和特殊字符）
        String csvData = "\"姓名\",\"年齡\",\"城市\",\"備註\"\n" +
                        "\"張三\",25,\"北京\",\"軟體工程師\"\n" +
                        "\"李四\",30,\"上海\",\"數據分析師，擅長Python\"\n" +
                        "\"王五\",28,\"深圳\",\"前端開發者，\"\"全棧\"\"工程師\"\n" +
                        "\"趙六\",35,\"廣州\",\"項目經理\"\n";
        
        System.out.println("=== CSV 數據解析 ===");
        List<Map<String, String>> csvRecords = parseCSV(csvData);
        
        for (Map<String, String> record : csvRecords) {
            System.out.println(record);
        }
        
        // JSON 字串解析（簡化版）
        String jsonData = "{\"name\":\"Alice\",\"age\":25,\"skills\":[\"Java\",\"Python\",\"SQL\"],\"active\":true}";
        System.out.println("\n=== JSON 數據解析 ===");
        Map<String, Object> jsonObject = parseSimpleJSON(jsonData);
        jsonObject.forEach((key, value) -> System.out.println(key + ": " + value));
        
        // 日誌解析
        String logData = "2024-03-15 10:30:25 [INFO] UserService - 用戶登入成功: alice@example.com from 192.168.1.100\n" +
                        "2024-03-15 10:31:10 [WARN] DatabaseService - 連接池使用率過高: 85%\n" +
                        "2024-03-15 10:32:15 [ERROR] PaymentService - 支付處理失敗: 餘額不足 user_id=12345\n" +
                        "2024-03-15 10:33:20 [INFO] UserService - 用戶登出: bob@example.com";
        
        System.out.println("\n=== 日誌解析 ===");
        List<LogEntry> logEntries = parseLogEntries(logData);
        
        for (LogEntry entry : logEntries) {
            System.out.println(entry);
        }
        
        // 統計日誌級別
        Map<String, Long> logLevelCount = logEntries.stream()
                .collect(Collectors.groupingBy(LogEntry::getLevel, Collectors.counting()));
        
        System.out.println("\n日誌級別統計:");
        logLevelCount.forEach((level, count) -> System.out.println(level + ": " + count + " 條"));
        
        System.out.println();
    }
    
    /**
     * 示例 2：字串編碼和國際化
     */
    private static void demonstrateEncodingAndI18n() {
        System.out.println("2. 字串編碼和國際化示例");
        System.out.println("===========================");
        
        // 字符編碼處理
        String chineseText = "你好，世界！";
        System.out.println("原始文本: " + chineseText);
        
        // 不同編碼的字節表示
        byte[] utf8Bytes = chineseText.getBytes(StandardCharsets.UTF_8);
        byte[] gbkBytes = chineseText.getBytes(java.nio.charset.Charset.forName("GBK"));
        
        System.out.println("UTF-8 編碼字節數: " + utf8Bytes.length);
        System.out.println("GBK 編碼字節數: " + gbkBytes.length);
        
        // 從字節恢復字串
        String fromUtf8 = new String(utf8Bytes, StandardCharsets.UTF_8);
        String fromGbk = new String(gbkBytes, java.nio.charset.Charset.forName("GBK"));
        
        System.out.println("UTF-8 解碼: " + fromUtf8);
        System.out.println("GBK 解碼: " + fromGbk);
        
        // Unicode 處理
        System.out.println("\n=== Unicode 處理 ===");
        String unicodeText = "Caf\\u00e9";  // Café
        String unescaped = unescapeUnicode(unicodeText);
        System.out.println("Unicode 轉義: " + unicodeText);
        System.out.println("轉義後文本: " + unescaped);
        
        // 多語言文本處理
        System.out.println("\n=== 多語言文本 ===");
        String[] multiLanguageTexts = {
            "Hello World",           // 英語
            "你好世界",              // 中文
            "こんにちは世界",        // 日語
            "안녕하세요 세계",       // 韓語
            "Привет мир",           // 俄語
            "مرحبا بالعالم",         // 阿拉伯語
            "🌍🌎🌏"                // 表情符號
        };
        
        for (String text : multiLanguageTexts) {
            analyzeString(text);
        }
        
        // 字串正規化
        System.out.println("\n=== 字串正規化 ===");
        String text1 = "café";                    // 組合字符 é
        String text2 = "cafe\u0301";              // 分解字符 e + 重音符
        
        System.out.println("text1: " + text1 + " (長度: " + text1.length() + ")");
        System.out.println("text2: " + text2 + " (長度: " + text2.length() + ")");
        System.out.println("equals: " + text1.equals(text2));
        
        // 正規化後比較
        String normalized1 = java.text.Normalizer.normalize(text1, java.text.Normalizer.Form.NFC);
        String normalized2 = java.text.Normalizer.normalize(text2, java.text.Normalizer.Form.NFC);
        System.out.println("正規化後 equals: " + normalized1.equals(normalized2));
        
        System.out.println();
    }
    
    /**
     * 示例 3：高效搜尋和替換
     */
    private static void demonstrateEfficientSearchAndReplace() {
        System.out.println("3. 高效搜尋和替換示例");
        System.out.println("==========================");
        
        String largeText = generateLargeText();
        System.out.println("大文本長度: " + largeText.length() + " 字符");
        
        // KMP 字串搜尋算法演示
        String pattern = "Java";
        long startTime = System.currentTimeMillis();
        List<Integer> positions = kmpSearch(largeText, pattern);
        long kmpTime = System.currentTimeMillis() - startTime;
        
        System.out.println("KMP 搜尋 '" + pattern + "' 找到 " + positions.size() + " 個匹配");
        System.out.println("KMP 搜尋時間: " + kmpTime + " ms");
        
        // 比較 indexOf 方法
        startTime = System.currentTimeMillis();
        int count = 0;
        int index = 0;
        while ((index = largeText.indexOf(pattern, index)) != -1) {
            count++;
            index++;
        }
        long indexOfTime = System.currentTimeMillis() - startTime;
        
        System.out.println("indexOf 搜尋找到 " + count + " 個匹配");
        System.out.println("indexOf 搜尋時間: " + indexOfTime + " ms");
        
        // 多模式字串搜尋
        System.out.println("\n=== 多模式搜尋 ===");
        String[] patterns = {"Java", "Python", "C++", "JavaScript"};
        Map<String, Integer> patternCounts = new HashMap<>();
        
        startTime = System.currentTimeMillis();
        for (String p : patterns) {
            int c = 0;
            int idx = 0;
            while ((idx = largeText.indexOf(p, idx)) != -1) {
                c++;
                idx++;
            }
            patternCounts.put(p, c);
        }
        long multiSearchTime = System.currentTimeMillis() - startTime;
        
        System.out.println("多模式搜尋結果:");
        patternCounts.forEach((p, c) -> System.out.println("  " + p + ": " + c + " 次"));
        System.out.println("多模式搜尋時間: " + multiSearchTime + " ms");
        
        // 字串替換優化
        System.out.println("\n=== 字串替換優化 ===");
        String text = "Java is great. Java is powerful. Java is everywhere.";
        
        // StringBuilder 替換
        startTime = System.nanoTime();
        String result1 = efficientReplace(text, "Java", "Python");
        long efficientTime = System.nanoTime() - startTime;
        
        // 標準 replace 方法
        startTime = System.nanoTime();
        String result2 = text.replace("Java", "Python");
        long standardTime = System.nanoTime() - startTime;
        
        System.out.println("原始: " + text);
        System.out.println("替換後: " + result1);
        System.out.println("結果相同: " + result1.equals(result2));
        System.out.println("高效替換時間: " + efficientTime + " ns");
        System.out.println("標準替換時間: " + standardTime + " ns");
        
        System.out.println();
    }
    
    /**
     * 示例 4：文本格式轉換
     */
    private static void demonstrateTextFormatConversion() {
        System.out.println("4. 文本格式轉換示例");
        System.out.println("======================");
        
        // Markdown 轉 HTML
        String markdown = "# 標題\n\n" +
                         "這是一個 **粗體** 和 *斜體* 的範例。\n\n" +
                         "- 列表項目 1\n" +
                         "- 列表項目 2\n" +
                         "- 列表項目 3\n\n" +
                         "這是一個 [連結](https://example.com)。\n\n" +
                         "```java\n" +
                         "System.out.println(\"Hello World\");\n" +
                         "```";
        
        System.out.println("=== Markdown 轉 HTML ===");
        String html = markdownToHtml(markdown);
        System.out.println("Markdown 原始文本:");
        System.out.println(markdown);
        System.out.println("\nHTML 轉換結果:");
        System.out.println(html);
        
        // SQL 格式化
        String sql = "select u.name,u.email,p.title from users u join posts p on u.id=p.user_id where u.active=1 and p.published=1 order by p.created_at desc;";
        
        System.out.println("\n=== SQL 格式化 ===");
        String formattedSql = formatSql(sql);
        System.out.println("原始 SQL:");
        System.out.println(sql);
        System.out.println("\n格式化後 SQL:");
        System.out.println(formattedSql);
        
        // XML 格式化
        String xml = "<root><user id=\"1\"><name>Alice</name><email>alice@example.com</email></user><user id=\"2\"><name>Bob</name><email>bob@example.com</email></user></root>";
        
        System.out.println("\n=== XML 格式化 ===");
        String formattedXml = formatXml(xml);
        System.out.println("原始 XML:");
        System.out.println(xml);
        System.out.println("\n格式化後 XML:");
        System.out.println(formattedXml);
        
        // 駝峰命名轉換
        System.out.println("\n=== 命名約定轉換 ===");
        String[] testNames = {
            "user_name",
            "first-name", 
            "EMAIL_ADDRESS",
            "phoneNumber",
            "date_of_birth"
        };
        
        for (String name : testNames) {
            System.out.printf("%-15s -> 駝峰: %-15s, 蛇形: %-15s, 短橫: %s%n",
                            name,
                            toCamelCase(name),
                            toSnakeCase(name),
                            toKebabCase(name));
        }
        
        System.out.println();
    }
    
    /**
     * 示例 5：字串安全處理
     */
    private static void demonstrateStringSecurity() {
        System.out.println("5. 字串安全處理示例");
        System.out.println("======================");
        
        // HTML 轉義
        String userInput = "<script>alert('XSS Attack!');</script>";
        System.out.println("=== HTML 轉義 ===");
        System.out.println("用戶輸入: " + userInput);
        System.out.println("轉義後: " + escapeHtml(userInput));
        
        // SQL 注入防護
        String sqlTemplate = "SELECT * FROM users WHERE name = ? AND email = ?";
        String userName = "'; DROP TABLE users; --";
        String userEmail = "user@example.com";
        
        System.out.println("\n=== SQL 注入防護 ===");
        System.out.println("SQL 模板: " + sqlTemplate);
        System.out.println("惡意用戶名: " + userName);
        System.out.println("用戶郵箱: " + userEmail);
        System.out.println("安全的預處理語句會自動轉義參數");
        
        // 密碼遮蔽
        String password = "MySecretPassword123!";
        System.out.println("\n=== 敏感信息處理 ===");
        System.out.println("原始密碼: " + password);
        System.out.println("遮蔽密碼: " + maskSensitiveInfo(password));
        
        // 信用卡號碼遮蔽
        String creditCard = "1234567890123456";
        System.out.println("信用卡號: " + creditCard);
        System.out.println("遮蔽後: " + maskCreditCard(creditCard));
        
        // 郵箱地址遮蔽
        String email = "user@example.com";
        System.out.println("郵箱地址: " + email);
        System.out.println("遮蔽後: " + maskEmail(email));
        
        // 輸入驗證
        System.out.println("\n=== 輸入驗證 ===");
        String[] testInputs = {
            "validInput123",
            "invalid<script>",
            "also/invalid",
            "normal-input_ok",
            "toolongtobelongtoolongtobelongtoolongtobelongtoolongtobelongtoolongtobelon"
        };
        
        for (String input : testInputs) {
            ValidationResult result = validateInput(input);
            System.out.printf("%-20s: %s%n", 
                            input.length() > 15 ? input.substring(0, 15) + "..." : input,
                            result.isValid() ? "有效" : "無效 - " + result.getError());
        }
        
        System.out.println();
    }
    
    /**
     * 示例 6：實際項目應用
     */
    private static void demonstrateRealWorldApplications() {
        System.out.println("6. 實際項目應用示例");
        System.out.println("======================");
        
        // 模板引擎
        System.out.println("=== 簡單模板引擎 ===");
        String template = "親愛的 {{name}}，\n\n" +
                         "您的訂單 {{orderId}} 已經確認。\n" +
                         "訂單金額：${{amount}}\n" +
                         "預計送達時間：{{deliveryDate}}\n\n" +
                         "感謝您的購買！";
        
        Map<String, String> variables = new HashMap<>();
        variables.put("name", "張三");
        variables.put("orderId", "ORD-12345");
        variables.put("amount", "299.99");
        variables.put("deliveryDate", "2024-03-20");
        
        String result = renderTemplate(template, variables);
        System.out.println("模板:");
        System.out.println(template);
        System.out.println("\n渲染結果:");
        System.out.println(result);
        
        // URL 建構器
        System.out.println("\n=== URL 建構器 ===");
        UrlBuilder urlBuilder = new UrlBuilder("https://api.example.com")
                .path("/users")
                .queryParam("page", "1")
                .queryParam("size", "10")
                .queryParam("sort", "name")
                .queryParam("filter", "active=true");
        
        System.out.println("建構的 URL: " + urlBuilder.build());
        
        // 文件路徑處理
        System.out.println("\n=== 文件路徑處理 ===");
        String[] paths = {
            "/home/user/documents/file.txt",
            "C:\\Users\\User\\Documents\\file.txt",
            "../relative/path/file.txt",
            "./current/file.txt"
        };
        
        for (String path : paths) {
            PathInfo pathInfo = analyzePath(path);
            System.out.println("路徑: " + path);
            System.out.println("  目錄: " + pathInfo.getDirectory());
            System.out.println("  文件名: " + pathInfo.getFileName());
            System.out.println("  擴展名: " + pathInfo.getExtension());
            System.out.println("  是否絕對路徑: " + pathInfo.isAbsolute());
            System.out.println();
        }
        
        // 數據序列化
        System.out.println("=== 簡單數據序列化 ===");
        Map<String, Object> data = new HashMap<>();
        data.put("id", 1);
        data.put("name", "Alice");
        data.put("active", true);
        data.put("scores", Arrays.asList(85, 92, 78));
        
        String serialized = serializeToQueryString(data);
        System.out.println("序列化數據: " + serialized);
        
        Map<String, String> parsed = parseQueryString(serialized);
        System.out.println("解析結果: " + parsed);
        
        System.out.println();
    }
    
    /**
     * 示例 7：效能優化技巧
     */
    private static void demonstratePerformanceOptimization() {
        System.out.println("7. 效能優化技巧示例");
        System.out.println("======================");
        
        // 字串拼接效能比較
        System.out.println("=== 字串拼接效能比較 ===");
        int iterations = 10000;
        
        // 測試不同的字串拼接方法
        String[] methods = {"String +", "StringBuilder", "StringBuffer", "String.join", "StringFormatter"};
        long[] times = new long[methods.length];
        
                // String + 運算符
        long start = System.nanoTime();
        concatenateWithPlus(iterations);
        times[0] = System.nanoTime() - start;
        
        // StringBuilder
        start = System.nanoTime();
        concatenateWithStringBuilder(iterations);
        times[1] = System.nanoTime() - start;
        
        // StringBuffer
        start = System.nanoTime();
        concatenateWithStringBuffer(iterations);
        times[2] = System.nanoTime() - start;
        
        // String.join
        start = System.nanoTime();
        concatenateWithStringJoin(iterations);
        times[3] = System.nanoTime() - start;
        
        // StringFormatter
        start = System.nanoTime();
        concatenateWithStringFormat(iterations);
        times[4] = System.nanoTime() - start;
        
        System.out.printf("%-20s %15s %15s%n", "方法", "時間 (ns)", "相對效能");
        System.out.println("--------------------------------------------------------");
        
        long minTime = Arrays.stream(times).min().orElse(1);
        for (int i = 0; i < methods.length; i++) {
            double relative = (double) times[i] / minTime;
            System.out.printf("%-20s %15d %15.2fx%n", methods[i], times[i], relative);
        }
        
        // 記憶體使用優化
        System.out.println("\n=== 記憶體使用優化 ===");
        demonstrateStringInternOptimization();
        
        // 字串搜尋效能
        System.out.println("\n=== 字串搜尋效能 ===");
        String largeText = generateLargeText();
        String pattern = "performance";
        
        // 預編譯正則表達式
        Pattern compiledPattern = Pattern.compile(Pattern.quote(pattern));
        
        start = System.nanoTime();
        boolean found1 = largeText.contains(pattern);
        long containsTime = System.nanoTime() - start;
        
        start = System.nanoTime();
        boolean found2 = largeText.indexOf(pattern) != -1;
        long indexOfTime = System.nanoTime() - start;
        
        start = System.nanoTime();
        boolean found3 = compiledPattern.matcher(largeText).find();
        long regexTime = System.nanoTime() - start;
        
        System.out.println("搜尋結果一致性: " + (found1 == found2 && found2 == found3));
        System.out.printf("contains():     %10d ns%n", containsTime);
        System.out.printf("indexOf():      %10d ns%n", indexOfTime);
        System.out.printf("regex.find():   %10d ns%n", regexTime);
        
        System.out.println("\n效能優化建議:");
        System.out.println("1. 大量字串操作使用 StringBuilder");
        System.out.println("2. 重複使用的字串考慮 intern()");
        System.out.println("3. 簡單搜尋使用 indexOf()，複雜模式使用預編譯正則");
        System.out.println("4. 避免在循環中創建不必要的字串物件");
        System.out.println("5. 使用 String.join() 連接集合元素");
        
        System.out.println();
    }
    
    // ==================== 輔助方法 ====================
    
    /**
     * 解析 CSV 數據
     */
    private static List<Map<String, String>> parseCSV(String csvData) {
        List<Map<String, String>> records = new ArrayList<>();
        String[] lines = csvData.split("\\r?\\n");
        
        if (lines.length == 0) return records;
        
        // 解析表頭
        String[] headers = parseCsvLine(lines[0]);
        
        // 解析數據行
        for (int i = 1; i < lines.length; i++) {
            String[] values = parseCsvLine(lines[i]);
            if (values.length == headers.length) {
                Map<String, String> record = new HashMap<>();
                for (int j = 0; j < headers.length; j++) {
                    record.put(headers[j], values[j]);
                }
                records.add(record);
            }
        }
        
        return records;
    }
    
    /**
     * 解析 CSV 行（處理引號）
     */
    private static String[] parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean inQuotes = false;
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    currentField.append('"');
                    i++; // 跳過下一個引號
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                fields.add(currentField.toString());
                currentField.setLength(0);
            } else {
                currentField.append(c);
            }
        }
        
        fields.add(currentField.toString());
        return fields.toArray(new String[0]);
    }
    
    /**
     * 簡單的 JSON 解析器
     */
    private static Map<String, Object> parseSimpleJSON(String json) {
        Map<String, Object> result = new HashMap<>();
        
        // 移除花括號
        json = json.trim();
        if (json.startsWith("{") && json.endsWith("}")) {
            json = json.substring(1, json.length() - 1);
        }
        
        // 簡單的鍵值對解析
        String[] pairs = json.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
        
        for (String pair : pairs) {
            String[] keyValue = pair.split(":", 2);
            if (keyValue.length == 2) {
                String key = keyValue[0].trim().replaceAll("\"", "");
                String value = keyValue[1].trim();
                
                // 解析值類型
                Object parsedValue;
                if (value.equals("true") || value.equals("false")) {
                    parsedValue = Boolean.parseBoolean(value);
                } else if (value.startsWith("\"") && value.endsWith("\"")) {
                    parsedValue = value.substring(1, value.length() - 1);
                } else if (value.startsWith("[") && value.endsWith("]")) {
                    String arrayContent = value.substring(1, value.length() - 1);
                    String[] elements = arrayContent.split(",");
                    List<String> list = new ArrayList<>();
                    for (String element : elements) {
                        list.add(element.trim().replaceAll("\"", ""));
                    }
                    parsedValue = list;
                } else {
                    try {
                        if (value.contains(".")) {
                            parsedValue = Double.parseDouble(value);
                        } else {
                            parsedValue = Integer.parseInt(value);
                        }
                    } catch (NumberFormatException e) {
                        parsedValue = value;
                    }
                }
                
                result.put(key, parsedValue);
            }
        }
        
        return result;
    }
    
    /**
     * 日誌條目類
     */
    static class LogEntry {
        private final String timestamp;
        private final String level;
        private final String service;
        private final String message;
        
        public LogEntry(String timestamp, String level, String service, String message) {
            this.timestamp = timestamp;
            this.level = level;
            this.service = service;
            this.message = message;
        }
        
        public String getTimestamp() { return timestamp; }
        public String getLevel() { return level; }
        public String getService() { return service; }
        public String getMessage() { return message; }
        
        @Override
        public String toString() {
            return String.format("[%s] %s %s - %s", timestamp, level, service, message);
        }
    }
    
    /**
     * 解析日誌條目
     */
    private static List<LogEntry> parseLogEntries(String logData) {
        List<LogEntry> entries = new ArrayList<>();
        String[] lines = logData.split("\\r?\\n");
        
        Pattern logPattern = Pattern.compile(
            "(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}) \\[(\\w+)\\] (\\w+) - (.+)"
        );
        
        for (String line : lines) {
            Matcher matcher = logPattern.matcher(line);
            if (matcher.find()) {
                entries.add(new LogEntry(
                    matcher.group(1),
                    matcher.group(2),
                    matcher.group(3),
                    matcher.group(4)
                ));
            }
        }
        
        return entries;
    }
    
    /**
     * 分析字串特性
     */
    private static void analyzeString(String text) {
        System.out.printf("文本: %-20s | 長度: %2d | 字節數: %3d | 類型: %s%n",
                         text.length() > 15 ? text.substring(0, 15) + "..." : text,
                         text.length(),
                         text.getBytes(StandardCharsets.UTF_8).length,
                         detectTextType(text));
    }
    
    /**
     * 檢測文本類型
     */
    private static String detectTextType(String text) {
        if (text.matches("^[\\x00-\\x7F]*$")) {
            return "ASCII";
        } else if (text.matches(".*[\\u4e00-\\u9fff].*")) {
            return "包含中文";
        } else if (text.matches(".*[\\u3040-\\u309f\\u30a0-\\u30ff].*")) {
            return "包含日文";
        } else if (text.matches(".*[\\uac00-\\ud7af].*")) {
            return "包含韓文";
        } else if (text.matches(".*[\\u0400-\\u04ff].*")) {
            return "包含俄文";
        } else if (text.matches(".*[\\u0600-\\u06ff].*")) {
            return "包含阿拉伯文";
        } else if (text.matches(".*[\\ud800-\\udfff].*")) {
            return "包含表情符號";
        } else {
            return "其他Unicode";
        }
    }
    
    /**
     * Unicode 轉義處理
     */
    private static String unescapeUnicode(String text) {
        StringBuilder result = new StringBuilder();
        int i = 0;
        
        while (i < text.length()) {
            if (i < text.length() - 5 && text.charAt(i) == '\\' && text.charAt(i + 1) == 'u') {
                String hex = text.substring(i + 2, i + 6);
                try {
                    int codePoint = Integer.parseInt(hex, 16);
                    result.append((char) codePoint);
                    i += 6;
                } catch (NumberFormatException e) {
                    result.append(text.charAt(i));
                    i++;
                }
            } else {
                result.append(text.charAt(i));
                i++;
            }
        }
        
        return result.toString();
    }
    
    /**
     * 生成大文本用於測試
     */
    private static String generateLargeText() {
        StringBuilder sb = new StringBuilder();
        String[] sentences = {
            "Java is a powerful programming language.",
            "Python is great for data science.",
            "JavaScript runs in the browser.",
            "C++ offers high performance.",
            "Go is designed for modern applications."
        };
        
        for (int i = 0; i < 1000; i++) {
            sb.append(sentences[i % sentences.length]).append(" ");
        }
        
        return sb.toString();
    }
    
    /**
     * KMP 字串搜尋算法
     */
    private static List<Integer> kmpSearch(String text, String pattern) {
        List<Integer> matches = new ArrayList<>();
        if (pattern.isEmpty()) return matches;
        
        // 構建部分匹配表
        int[] lps = buildLPSArray(pattern);
        
        int i = 0; // text 索引
        int j = 0; // pattern 索引
        
        while (i < text.length()) {
            if (text.charAt(i) == pattern.charAt(j)) {
                i++;
                j++;
            }
            
            if (j == pattern.length()) {
                matches.add(i - j);
                j = lps[j - 1];
            } else if (i < text.length() && text.charAt(i) != pattern.charAt(j)) {
                if (j != 0) {
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }
        
        return matches;
    }
    
    /**
     * 構建 LPS（最長前綴後綴）數組
     */
    private static int[] buildLPSArray(String pattern) {
        int[] lps = new int[pattern.length()];
        int len = 0;
        int i = 1;
        
        lps[0] = 0;
        
        while (i < pattern.length()) {
            if (pattern.charAt(i) == pattern.charAt(len)) {
                len++;
                lps[i] = len;
                i++;
            } else {
                if (len != 0) {
                    len = lps[len - 1];
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }
        
        return lps;
    }
    
    /**
     * 高效字串替換
     */
    private static String efficientReplace(String text, String target, String replacement) {
        StringBuilder result = new StringBuilder();
        int start = 0;
        int index;
        
        while ((index = text.indexOf(target, start)) != -1) {
            result.append(text, start, index);
            result.append(replacement);
            start = index + target.length();
        }
        
        result.append(text, start, text.length());
        return result.toString();
    }
    
    /**
     * 其他輔助方法的簡化實現...
     */
    
    // 為了保持代碼長度合理，這裡省略了一些輔助方法的完整實現
    // 實際項目中這些方法會有更完整的實現
    
    private static String markdownToHtml(String markdown) {
        return markdown
                .replaceAll("^# (.+)$", "<h1>$1</h1>")
                .replaceAll("\\*\\*(.+?)\\*\\*", "<strong>$1</strong>")
                .replaceAll("\\*(.+?)\\*", "<em>$1</em>")
                .replaceAll("^- (.+)$", "<li>$1</li>")
                .replaceAll("\\[(.+?)\\]\\((.+?)\\)", "<a href=\"$2\">$1</a>")
                .replaceAll("```(\\w+)\\n(.+?)```", "<pre><code class=\"$1\">$2</code></pre>");
    }
    
    private static String formatSql(String sql) {
        return sql.toUpperCase()
                .replace("SELECT", "\nSELECT")
                .replace("FROM", "\nFROM")
                .replace("WHERE", "\nWHERE")
                .replace("JOIN", "\nJOIN")
                .replace("ORDER BY", "\nORDER BY")
                .trim();
    }
    
    private static String formatXml(String xml) {
        return xml.replaceAll("><", ">\n<")
                .replaceAll("<(\\w+)", "\n<$1")
                .trim();
    }
    
    private static String toCamelCase(String text) {
        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = false;
        
        for (char c : text.toCharArray()) {
            if (c == '_' || c == '-') {
                capitalizeNext = true;
            } else if (capitalizeNext) {
                result.append(Character.toUpperCase(c));
                capitalizeNext = false;
            } else {
                result.append(Character.toLowerCase(c));
            }
        }
        
        return result.toString();
    }
    
    private static String toSnakeCase(String text) {
        return text.replaceAll("([a-z])([A-Z])", "$1_$2")
                  .replaceAll("-", "_")
                  .toLowerCase();
    }
    
    private static String toKebabCase(String text) {
        return text.replaceAll("([a-z])([A-Z])", "$1-$2")
                  .replaceAll("_", "-")
                  .toLowerCase();
    }
    
    private static String escapeHtml(String text) {
        return text.replace("&", "&amp;")
                  .replace("<", "&lt;")
                  .replace(">", "&gt;")
                  .replace("\"", "&quot;")
                  .replace("'", "&#x27;");
    }
    
    private static String maskSensitiveInfo(String text) {
        if (text.length() <= 4) {
            return "*".repeat(text.length());
        }
        return text.substring(0, 2) + "*".repeat(text.length() - 4) + text.substring(text.length() - 2);
    }
    
    private static String maskCreditCard(String cardNumber) {
        if (cardNumber.length() != 16) {
            return cardNumber;
        }
        return "**** **** **** " + cardNumber.substring(12);
    }
    
    private static String maskEmail(String email) {
        int atIndex = email.indexOf('@');
        if (atIndex <= 1) {
            return email;
        }
        
        String localPart = email.substring(0, atIndex);
        String domainPart = email.substring(atIndex);
        
        if (localPart.length() <= 2) {
            return localPart.charAt(0) + "*" + domainPart;
        } else {
            return localPart.charAt(0) + "*".repeat(localPart.length() - 2) + 
                   localPart.charAt(localPart.length() - 1) + domainPart;
        }
    }
    
    /**
     * 驗證結果類
     */
    static class ValidationResult {
        private final boolean valid;
        private final String error;
        
        public ValidationResult(boolean valid, String error) {
            this.valid = valid;
            this.error = error;
        }
        
        public boolean isValid() { return valid; }
        public String getError() { return error; }
        
        public static ValidationResult valid() {
            return new ValidationResult(true, null);
        }
        
        public static ValidationResult invalid(String error) {
            return new ValidationResult(false, error);
        }
    }
    
    private static ValidationResult validateInput(String input) {
        if (input == null) {
            return ValidationResult.invalid("輸入不能為空");
        }
        
        if (input.length() > 50) {
            return ValidationResult.invalid("輸入過長");
        }
        
        if (input.matches(".*[<>\"'/\\\\].*")) {
            return ValidationResult.invalid("包含非法字符");
        }
        
        return ValidationResult.valid();
    }
    
    private static String renderTemplate(String template, Map<String, String> variables) {
        String result = template;
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            result = result.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        return result;
    }
    
    /**
     * URL 建構器類
     */
    static class UrlBuilder {
        private final StringBuilder url;
        private boolean hasQuery = false;
        
        public UrlBuilder(String baseUrl) {
            this.url = new StringBuilder(baseUrl);
        }
        
        public UrlBuilder path(String path) {
            if (!path.startsWith("/")) {
                url.append("/");
            }
            url.append(path);
            return this;
        }
        
        public UrlBuilder queryParam(String name, String value) {
            url.append(hasQuery ? "&" : "?");
            url.append(name).append("=").append(value);
            hasQuery = true;
            return this;
        }
        
        public String build() {
            return url.toString();
        }
    }
    
    /**
     * 路徑信息類
     */
    static class PathInfo {
        private final String directory;
        private final String fileName;
        private final String extension;
        private final boolean absolute;
        
        public PathInfo(String directory, String fileName, String extension, boolean absolute) {
            this.directory = directory;
            this.fileName = fileName;
            this.extension = extension;
            this.absolute = absolute;
        }
        
        public String getDirectory() { return directory; }
        public String getFileName() { return fileName; }
        public String getExtension() { return extension; }
        public boolean isAbsolute() { return absolute; }
    }
    
    private static PathInfo analyzePath(String path) {
        boolean isAbsolute = path.startsWith("/") || path.matches("^[A-Za-z]:\\\\.*");
        
        int lastSeparator = Math.max(path.lastIndexOf('/'), path.lastIndexOf('\\'));
        String directory = lastSeparator == -1 ? "" : path.substring(0, lastSeparator);
        String fileNameWithExt = lastSeparator == -1 ? path : path.substring(lastSeparator + 1);
        
        int lastDot = fileNameWithExt.lastIndexOf('.');
        String fileName = lastDot == -1 ? fileNameWithExt : fileNameWithExt.substring(0, lastDot);
        String extension = lastDot == -1 ? "" : fileNameWithExt.substring(lastDot + 1);
        
        return new PathInfo(directory, fileName, extension, isAbsolute);
    }
    
    private static String serializeToQueryString(Map<String, Object> data) {
        return data.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue().toString())
                .collect(Collectors.joining("&"));
    }
    
    private static Map<String, String> parseQueryString(String queryString) {
        Map<String, String> params = new HashMap<>();
        String[] pairs = queryString.split("&");
        
        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2) {
                params.put(keyValue[0], keyValue[1]);
            }
        }
        
        return params;
    }
    
    // 效能測試方法
    private static String concatenateWithPlus(int iterations) {
        String result = "";
        for (int i = 0; i < iterations; i++) {
            result += "a";
        }
        return result;
    }
    
    private static String concatenateWithStringBuilder(int iterations) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < iterations; i++) {
            sb.append("a");
        }
        return sb.toString();
    }
    
    private static String concatenateWithStringBuffer(int iterations) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < iterations; i++) {
            buffer.append("a");
        }
        return buffer.toString();
    }
    
    private static String concatenateWithStringJoin(int iterations) {
        String[] array = new String[iterations];
        Arrays.fill(array, "a");
        return String.join("", array);
    }
    
    private static String concatenateWithStringFormat(int iterations) {
        return String.format("%" + iterations + "s", "").replace(' ', 'a');
    }
    
    private static void demonstrateStringInternOptimization() {
        System.out.println("String.intern() 優化示例:");
        
        // 模擬大量重複字串
        String[] duplicateStrings = new String[1000];
        
        // 不使用 intern
        long start = System.currentTimeMillis();
        for (int i = 0; i < duplicateStrings.length; i++) {
            duplicateStrings[i] = new String("repeated_string_" + (i % 10));
        }
        long withoutIntern = System.currentTimeMillis() - start;
        
        // 使用 intern
        start = System.currentTimeMillis();
        for (int i = 0; i < duplicateStrings.length; i++) {
            duplicateStrings[i] = new String("repeated_string_" + (i % 10)).intern();
        }
        long withIntern = System.currentTimeMillis() - start;
        
        System.out.println("不使用 intern: " + withoutIntern + " ms");
        System.out.println("使用 intern: " + withIntern + " ms");
        System.out.println("intern() 適用於大量重複字串的場景");
    }
}