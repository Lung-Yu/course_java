import java.util.*;
import java.util.regex.*;
import java.text.DecimalFormat;

/**
 * 字串處理基礎示例
 * 
 * 本類別展示 Java 字串處理的核心概念和操作，包括：
 * 1. String 類別的基本特性和操作方法
 * 2. StringBuilder 和 StringBuffer 的使用
 * 3. Scanner 輸入處理和數據驗證
 * 4. 格式化輸出的各種方法
 * 5. 正則表達式的基本應用
 * 6. 實際的文本處理場景
 * 
 * @author Java Course
 * @version 1.0
 */
public class StringProcessingDemo {
    
    public static void main(String[] args) {
        System.out.println("=== Java 字串處理基礎示例 ===\n");
        
        // 1. String 基本特性示例
        demonstrateStringBasics();
        
        // 2. String 操作方法
        demonstrateStringOperations();
        
        // 3. StringBuilder 使用
        demonstrateStringBuilder();
        
        // 4. Scanner 輸入處理
        demonstrateScannerUsage();
        
        // 5. 格式化輸出
        demonstrateFormatting();
        
        // 6. 正則表達式應用
        demonstrateRegularExpressions();
        
        // 7. 實際應用場景
        demonstratePracticalUseCases();
        
        // 8. 效能比較
        demonstratePerformanceComparison();
    }
    
    /**
     * 示例 1：String 基本特性
     */
    private static void demonstrateStringBasics() {
        System.out.println("1. String 基本特性示例");
        System.out.println("=========================");
        
        // 字串創建方式
        String str1 = "Hello World";              // 字串字面量
        String str2 = new String("Hello World");  // 使用構造函數
        String str3 = "Hello World";              // 另一個字面量
        
        System.out.println("字串內容比較:");
        System.out.println("str1.equals(str2): " + str1.equals(str2));
        System.out.println("str1.equals(str3): " + str1.equals(str3));
        
        System.out.println("\n參考比較（字串常量池）:");
        System.out.println("str1 == str2: " + (str1 == str2));  // false
        System.out.println("str1 == str3: " + (str1 == str3));  // true
        
        // intern() 方法演示
        String str4 = str2.intern();
        System.out.println("str1 == str2.intern(): " + (str1 == str4));  // true
        
        // 字串不可變性演示
        String original = "Java";
        String modified = original.concat(" Programming");
        System.out.println("\n字串不可變性:");
        System.out.println("原始字串: " + original);      // "Java"
        System.out.println("連接後字串: " + modified);    // "Java Programming"
        System.out.println("原始字串未改變: " + original); // "Java"
        
        // 字串長度和字符訪問
        String text = "程式設計";
        System.out.println("\n字串信息:");
        System.out.println("字串: " + text);
        System.out.println("長度: " + text.length());
        System.out.println("第一個字符: " + text.charAt(0));
        System.out.println("最後一個字符: " + text.charAt(text.length() - 1));
        
        System.out.println();
    }
    
    /**
     * 示例 2：String 操作方法
     */
    private static void demonstrateStringOperations() {
        System.out.println("2. String 操作方法示例");
        System.out.println("=========================");
        
        String sentence = "  Java Programming is Fun and Powerful  ";
        System.out.println("原始字串: '" + sentence + "'");
        
        // 大小寫轉換
        System.out.println("\n大小寫轉換:");
        System.out.println("轉大寫: " + sentence.toUpperCase());
        System.out.println("轉小寫: " + sentence.toLowerCase());
        
        // 空白處理
        System.out.println("\n空白處理:");
        System.out.println("去除首尾空白: '" + sentence.trim() + "'");
        System.out.println("去除所有空白: '" + sentence.replace(" ", "") + "'");
        
        // 字串搜尋
        System.out.println("\n字串搜尋:");
        System.out.println("包含 'Program': " + sentence.contains("Program"));
        System.out.println("以 '  Java' 開頭: " + sentence.startsWith("  Java"));
        System.out.println("以 'Powerful  ' 結尾: " + sentence.endsWith("Powerful  "));
        System.out.println("'Java' 的位置: " + sentence.indexOf("Java"));
        System.out.println("'a' 最後出現位置: " + sentence.lastIndexOf("a"));
        
        // 字串提取
        System.out.println("\n字串提取:");
        String trimmed = sentence.trim();
        System.out.println("從位置5開始: '" + trimmed.substring(5) + "'");
        System.out.println("位置5到9: '" + trimmed.substring(5, 9) + "'");
        
        // 字串分割
        System.out.println("\n字串分割:");
        String[] words = trimmed.split(" ");
        System.out.println("分割成單詞: " + Arrays.toString(words));
        System.out.println("單詞數量: " + words.length);
        
        // 字串替換
        System.out.println("\n字串替換:");
        System.out.println("替換 'Java' 為 'Python': " + trimmed.replace("Java", "Python"));
        System.out.println("替換第一個 'a' 為 'A': " + trimmed.replaceFirst("a", "A"));
        System.out.println("替換所有小寫字母: " + trimmed.replaceAll("[a-z]", "*"));
        
        // 字串比較
        System.out.println("\n字串比較:");
        String str1 = "Apple";
        String str2 = "Banana";
        String str3 = "apple";
        
        System.out.println("'" + str1 + "' 與 '" + str2 + "' 比較: " + str1.compareTo(str2));
        System.out.println("'" + str1 + "' 與 '" + str3 + "' 比較: " + str1.compareTo(str3));
        System.out.println("'" + str1 + "' 與 '" + str3 + "' 忽略大小寫比較: " + str1.compareToIgnoreCase(str3));
        
        System.out.println();
    }
    
    /**
     * 示例 3：StringBuilder 使用
     */
    private static void demonstrateStringBuilder() {
        System.out.println("3. StringBuilder 使用示例");
        System.out.println("============================");
        
        // 創建 StringBuilder
        StringBuilder sb = new StringBuilder();
        System.out.println("初始容量: " + sb.capacity());
        System.out.println("初始長度: " + sb.length());
        
        // 追加內容
        sb.append("Hello");
        sb.append(" ");
        sb.append("World");
        sb.append('!');
        sb.append(2024);
        
        System.out.println("\n追加內容後:");
        System.out.println("內容: " + sb.toString());
        System.out.println("長度: " + sb.length());
        System.out.println("容量: " + sb.capacity());
        
        // 插入內容
        sb.insert(6, "Beautiful ");
        System.out.println("插入 'Beautiful ' 後: " + sb.toString());
        
        // 刪除內容
        sb.delete(6, 16);  // 刪除 "Beautiful "
        System.out.println("刪除 'Beautiful ' 後: " + sb.toString());
        
        // 替換內容
        sb.replace(0, 5, "Hi");  // 將 "Hello" 替換為 "Hi"
        System.out.println("替換後: " + sb.toString());
        
        // 反轉字串
        StringBuilder sb2 = new StringBuilder("12345");
        System.out.println("原始: " + sb2.toString());
        sb2.reverse();
        System.out.println("反轉後: " + sb2.toString());
        
        // 構建複雜字串
        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<html>\n")
                   .append("  <head>\n")
                   .append("    <title>My Page</title>\n")
                   .append("  </head>\n")
                   .append("  <body>\n")
                   .append("    <h1>Welcome!</h1>\n")
                   .append("  </body>\n")
                   .append("</html>");
        
        System.out.println("\n構建的 HTML:");
        System.out.println(htmlBuilder.toString());
        
        // 效能友好的字串連接
        String[] fruits = {"蘋果", "香蕉", "橘子", "葡萄", "西瓜"};
        StringBuilder fruitList = new StringBuilder();
        
        for (int i = 0; i < fruits.length; i++) {
            if (i > 0) {
                fruitList.append(", ");
            }
            fruitList.append(fruits[i]);
        }
        
        System.out.println("水果列表: " + fruitList.toString());
        
        System.out.println();
    }
    
    /**
     * 示例 4：Scanner 輸入處理
     */
    private static void demonstrateScannerUsage() {
        System.out.println("4. Scanner 輸入處理示例");
        System.out.println("===========================");
        
        // 模擬用戶輸入數據
        String simulatedInput = "Alice\n25\n50000.75\ntrue\n" +
                               "Java Programming\n" +
                               "apple,banana,orange\n" +
                               "2024-03-15\n";
        
        try (Scanner scanner = new Scanner(simulatedInput)) {
            
            // 讀取基本數據類型
            System.out.println("=== 基本數據類型讀取 ===");
            
            System.out.print("姓名: ");
            String name = scanner.nextLine();
            System.out.println("讀取到姓名: " + name);
            
            System.out.print("年齡: ");
            int age = scanner.nextInt();
            System.out.println("讀取到年齡: " + age);
            
            System.out.print("薪水: ");
            double salary = scanner.nextDouble();
            System.out.println("讀取到薪水: " + salary);
            
            System.out.print("是否為員工: ");
            boolean isEmployee = scanner.nextBoolean();
            System.out.println("讀取到員工狀態: " + isEmployee);
            
            // 消費剩餘的換行符
            scanner.nextLine();
            
            // 讀取整行
            System.out.print("興趣: ");
            String interests = scanner.nextLine();
            System.out.println("讀取到興趣: " + interests);
            
            // 使用分隔符讀取
            System.out.print("喜歡的水果 (逗號分隔): ");
            String fruitLine = scanner.nextLine();
            String[] fruits = fruitLine.split(",");
            System.out.println("讀取到水果: " + Arrays.toString(fruits));
            
            // 使用正則表達式驗證
            System.out.print("生日 (YYYY-MM-DD): ");
            String birthday = scanner.nextLine();
            if (birthday.matches("\\d{4}-\\d{2}-\\d{2}")) {
                System.out.println("日期格式正確: " + birthday);
            } else {
                System.out.println("日期格式錯誤: " + birthday);
            }
            
            // 顯示讀取的用戶資料
            System.out.println("\n=== 用戶資料摘要 ===");
            System.out.println("姓名: " + name);
            System.out.println("年齡: " + age + " 歲");
            System.out.println("薪水: $" + salary);
            System.out.println("員工: " + (isEmployee ? "是" : "否"));
            System.out.println("興趣: " + interests);
            System.out.println("喜歡的水果: " + String.join(", ", fruits));
            System.out.println("生日: " + birthday);
        }
        
        // 演示數據驗證
        demonstrateInputValidation();
        
        System.out.println();
    }
    
    /**
     * 輸入驗證示例
     */
    private static void demonstrateInputValidation() {
        System.out.println("\n=== 輸入驗證示例 ===");
        
        // 模擬包含無效數據的輸入
        String invalidInput = "abc\n-5\n150\n25\ninvalid-email\nuser@example.com\n";
        
        try (Scanner scanner = new Scanner(invalidInput)) {
            
            // 驗證整數輸入
            System.out.println("驗證整數輸入 (1-100):");
            int validNumber = getValidInteger(scanner, 1, 100);
            System.out.println("獲得有效整數: " + validNumber);
            
            // 驗證電子郵件
            System.out.println("\n驗證電子郵件:");
            String validEmail = getValidEmail(scanner);
            System.out.println("獲得有效郵箱: " + validEmail);
        }
    }
    
    /**
     * 獲取有效的整數輸入
     */
    private static int getValidInteger(Scanner scanner, int min, int max) {
        while (true) {
            System.out.print("請輸入整數 (" + min + "-" + max + "): ");
            
            if (scanner.hasNextInt()) {
                int number = scanner.nextInt();
                if (number >= min && number <= max) {
                    return number;
                } else {
                    System.out.println("數字必須在 " + min + " 到 " + max + " 之間");
                }
            } else {
                System.out.println("無效輸入，請輸入整數");
                scanner.next(); // 消費無效輸入
            }
        }
    }
    
    /**
     * 獲取有效的電子郵件
     */
    private static String getValidEmail(Scanner scanner) {
        Pattern emailPattern = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
        
        while (true) {
            System.out.print("請輸入郵箱地址: ");
            String email = scanner.next();
            
            if (emailPattern.matcher(email).matches()) {
                return email;
            } else {
                System.out.println("郵箱格式不正確，請重新輸入");
            }
        }
    }
    
    /**
     * 示例 5：格式化輸出
     */
    private static void demonstrateFormatting() {
        System.out.println("5. 格式化輸出示例");
        System.out.println("=====================");
        
        // 基本 printf 格式化
        String name = "Alice";
        int age = 25;
        double salary = 50000.789;
        boolean isManager = true;
        
        System.out.println("=== printf 格式化 ===");
        System.out.printf("姓名: %s%n", name);
        System.out.printf("年齡: %d 歲%n", age);
        System.out.printf("薪水: $%.2f%n", salary);
        System.out.printf("是否為主管: %b%n", isManager);
        
        // 數字格式化
        int number = 12345;
        System.out.println("\n=== 數字格式化 ===");
        System.out.printf("十進制: %d%n", number);
        System.out.printf("八進制: %o%n", number);
        System.out.printf("十六進制: %x%n", number);
        System.out.printf("科學記數法: %e%n", (double)number);
        System.out.printf("千分位分隔: %,d%n", number);
        System.out.printf("填充零: %08d%n", number);
        
        // 對齊和寬度
        System.out.println("\n=== 對齊和寬度 ===");
        String[] names = {"Alice", "Bob", "Charlie"};
        int[] ages = {25, 30, 35};
        double[] salaries = {50000.0, 60000.5, 75000.75};
        
        System.out.printf("%-10s | %5s | %10s%n", "姓名", "年齡", "薪水");
        System.out.printf("%-10s-+-%-5s-+-%-10s%n", "----------", "-----", "----------");
        
        for (int i = 0; i < names.length; i++) {
            System.out.printf("%-10s | %5d | %10.2f%n", names[i], ages[i], salaries[i]);
        }
        
        // String.format 使用
        System.out.println("\n=== String.format 使用 ===");
        String template = "用戶 %s (年齡: %d) 的薪水是 $%.2f";
        String formatted1 = String.format(template, "Alice", 25, 50000.789);
        String formatted2 = String.format(template, "Bob", 30, 60000.5);
        
        System.out.println(formatted1);
        System.out.println(formatted2);
        
        // 日期時間格式化
        System.out.println("\n=== 日期時間格式化 ===");
        Date now = new Date();
        System.out.printf("完整日期時間: %tc%n", now);
        System.out.printf("日期: %tF%n", now);
        System.out.printf("時間: %tT%n", now);
        System.out.printf("年份: %tY%n", now);
        System.out.printf("月份: %tm%n", now);
        System.out.printf("日期: %td%n", now);
        
        // DecimalFormat 使用
        System.out.println("\n=== DecimalFormat 使用 ===");
        double[] numbers = {123.456, 1234.5, 0.123, 1234567.89};
        
        DecimalFormat df1 = new DecimalFormat("#.##");
        DecimalFormat df2 = new DecimalFormat("0.00");
        DecimalFormat df3 = new DecimalFormat("#,##0.00");
        DecimalFormat df4 = new DecimalFormat("¥#,##0.00");
        
        System.out.println("數字\t\t#.##\t\t0.00\t\t#,##0.00\t¥#,##0.00");
        for (double num : numbers) {
            System.out.printf("%.3f\t\t%s\t\t%s\t\t%s\t\t%s%n",
                            num, df1.format(num), df2.format(num), df3.format(num), df4.format(num));
        }
        
        System.out.println();
    }
    
    /**
     * 示例 6：正則表達式應用
     */
    private static void demonstrateRegularExpressions() {
        System.out.println("6. 正則表達式應用示例");
        System.out.println("=========================");
        
        // 基本正則表達式匹配
        String text = "請聯繫我們：電話 010-12345678，郵箱 contact@example.com 或 info@test.org";
        
        System.out.println("原始文本: " + text);
        
        // 提取電話號碼
        Pattern phonePattern = Pattern.compile("\\d{3}-\\d{8}");
        Matcher phoneMatcher = phonePattern.matcher(text);
        
        System.out.println("\n提取的電話號碼:");
        while (phoneMatcher.find()) {
            System.out.println("  " + phoneMatcher.group() + 
                             " (位置: " + phoneMatcher.start() + "-" + phoneMatcher.end() + ")");
        }
        
        // 提取郵箱地址
        Pattern emailPattern = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
        Matcher emailMatcher = emailPattern.matcher(text);
        
        System.out.println("\n提取的郵箱地址:");
        while (emailMatcher.find()) {
            System.out.println("  " + emailMatcher.group());
        }
        
        // 數據驗證
        System.out.println("\n=== 數據驗證 ===");
        String[] testEmails = {
            "user@example.com",
            "invalid-email",
            "test@test.org",
            "user@",
            "test.user+tag@example.co.uk"
        };
        
        for (String email : testEmails) {
            boolean valid = emailPattern.matcher(email).matches();
            System.out.printf("%-25s: %s%n", email, valid ? "有效" : "無效");
        }
        
        // 字串替換
        System.out.println("\n=== 字串替換 ===");
        String document = "文檔創建於2024年3月15日，修改於2024年3月20日";
        System.out.println("原始: " + document);
        
        // 替換日期格式
        String newDocument = document.replaceAll("(\\d{4})年(\\d{1,2})月(\\d{1,2})日", "$1-$2-$3");
        System.out.println("替換後: " + newDocument);
        
        // 分組捕獲
        System.out.println("\n=== 分組捕獲 ===");
        String logEntry = "2024-03-15 14:30:25 [INFO] 用戶登入成功: user123";
        Pattern logPattern = Pattern.compile("(\\d{4}-\\d{2}-\\d{2}) (\\d{2}:\\d{2}:\\d{2}) \\[(\\w+)\\] (.+)");
        Matcher logMatcher = logPattern.matcher(logEntry);
        
        if (logMatcher.find()) {
            System.out.println("完整匹配: " + logMatcher.group(0));
            System.out.println("日期: " + logMatcher.group(1));
            System.out.println("時間: " + logMatcher.group(2));
            System.out.println("級別: " + logMatcher.group(3));
            System.out.println("消息: " + logMatcher.group(4));
        }
        
        // 字串分割
        System.out.println("\n=== 正則表達式分割 ===");
        String data = "apple123banana456orange789grape";
        String[] parts = data.split("\\d+");
        System.out.println("原始數據: " + data);
        System.out.println("分割結果: " + Arrays.toString(parts));
        
        System.out.println();
    }
    
    /**
     * 示例 7：實際應用場景
     */
    private static void demonstratePracticalUseCases() {
        System.out.println("7. 實際應用場景示例");
        System.out.println("======================");
        
        // 文本分析
        System.out.println("=== 文本分析 ===");
        String article = "Java是一種面向對象的程式語言。Java具有簡單、面向對象、分散式、解釋型、" +
                        "健壯、安全與系統無關、可移植、高效能、多線程、動態等特點。Java可以編寫桌面應用程式、" +
                        "Web應用程式、分散式系統和嵌入式系統應用程式等。";
        
        analyzeText(article);
        
        // 數據清理
        System.out.println("\n=== 數據清理 ===");
        String[] rawData = {
            "  Alice Johnson  ",
            "BOB SMITH",
            "charlie brown   ",
            "Diana Prince-Wayne",
            "  EDWARD  NORTON  "
        };
        
        System.out.println("原始數據:");
        for (String data : rawData) {
            System.out.println("'" + data + "'");
        }
        
        System.out.println("\n清理後數據:");
        for (String data : rawData) {
            String cleaned = cleanName(data);
            System.out.println("'" + cleaned + "'");
        }
        
        // 配置解析
        System.out.println("\n=== 配置文件解析 ===");
        String config = "# 應用程式配置\n" +
                       "app.name=MyApplication\n" +
                       "app.version=1.0.0\n" +
                       "# 資料庫設定\n" +
                       "db.host=localhost\n" +
                       "db.port=3306\n" +
                       "db.username=admin\n" +
                       "debug.enabled=true\n" +
                       "max.connections=100";
        
        Map<String, String> properties = parseConfig(config);
        System.out.println("解析的配置:");
        properties.forEach((key, value) -> System.out.println("  " + key + " = " + value));
        
        // URL 解析
        System.out.println("\n=== URL 解析 ===");
        String url = "https://www.example.com:8080/path/to/resource?param1=value1&param2=value2#section";
        parseUrl(url);
        
        // 密碼強度檢查
        System.out.println("\n=== 密碼強度檢查 ===");
        String[] passwords = {
            "password",
            "Password123",
            "P@ssw0rd!",
            "weak",
            "StrongP@ssw0rd2024!"
        };
        
        for (String password : passwords) {
            PasswordStrength strength = checkPasswordStrength(password);
            System.out.printf("%-20s: %s%n", password, strength);
        }
        
        System.out.println();
    }
    
    /**
     * 文本分析方法
     */
    private static void analyzeText(String text) {
        System.out.println("文本: " + text.substring(0, Math.min(50, text.length())) + "...");
        System.out.println("總字符數: " + text.length());
        System.out.println("非空字符數: " + text.replace(" ", "").length());
        
        // 統計詞頻
        Map<String, Integer> wordCount = new HashMap<>();
        String[] words = text.split("[\\s，。！？；：]+");
        
        for (String word : words) {
            if (!word.isEmpty()) {
                wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
            }
        }
        
        System.out.println("總詞數: " + words.length);
        System.out.println("不重複詞數: " + wordCount.size());
        
        // 顯示最常見的詞
        System.out.println("最常見的詞:");
        wordCount.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(3)
                .forEach(entry -> System.out.println("  " + entry.getKey() + ": " + entry.getValue() + " 次"));
    }
    
    /**
     * 清理姓名數據
     */
    private static String cleanName(String name) {
        if (name == null) {
            return "";
        }
        
        // 去除首尾空白並轉換為適當的大小寫
        name = name.trim();
        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = true;
        
        for (char c : name.toCharArray()) {
            if (Character.isLetter(c)) {
                if (capitalizeNext) {
                    result.append(Character.toUpperCase(c));
                    capitalizeNext = false;
                } else {
                    result.append(Character.toLowerCase(c));
                }
            } else if (Character.isWhitespace(c) || c == '-') {
                result.append(c);
                capitalizeNext = true;
            } else {
                result.append(c);
            }
        }
        
        return result.toString().replaceAll("\\s+", " "); // 合併多個空格
    }
    
    /**
     * 解析配置文件
     */
    private static Map<String, String> parseConfig(String config) {
        Map<String, String> properties = new HashMap<>();
        
        try (Scanner scanner = new Scanner(config)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                
                // 跳過空行和註釋
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                
                // 解析 key=value
                if (line.contains("=")) {
                    String[] parts = line.split("=", 2);
                    if (parts.length == 2) {
                        properties.put(parts[0].trim(), parts[1].trim());
                    }
                }
            }
        }
        
        return properties;
    }
    
    /**
     * 解析 URL
     */
    private static void parseUrl(String url) {
        System.out.println("原始 URL: " + url);
        
        // 簡單的URL解析（實際應用中建議使用 java.net.URL）
        Pattern urlPattern = Pattern.compile("(https?)://([^:/]+)(?::(\\d+))?(/[^?#]*)?(?:\\?([^#]*))?(?:#(.*))?");
        Matcher matcher = urlPattern.matcher(url);
        
        if (matcher.find()) {
            System.out.println("協議: " + matcher.group(1));
            System.out.println("主機: " + matcher.group(2));
            System.out.println("端口: " + (matcher.group(3) != null ? matcher.group(3) : "默認"));
            System.out.println("路徑: " + (matcher.group(4) != null ? matcher.group(4) : "/"));
            System.out.println("查詢參數: " + (matcher.group(5) != null ? matcher.group(5) : "無"));
            System.out.println("片段: " + (matcher.group(6) != null ? matcher.group(6) : "無"));
        }
    }
    
    /**
     * 密碼強度枚舉
     */
    enum PasswordStrength {
        WEAK("弱"),
        MEDIUM("中等"),
        STRONG("強"),
        VERY_STRONG("很強");
        
        private final String description;
        
        PasswordStrength(String description) {
            this.description = description;
        }
        
        @Override
        public String toString() {
            return description;
        }
    }
    
    /**
     * 檢查密碼強度
     */
    private static PasswordStrength checkPasswordStrength(String password) {
        if (password == null || password.length() < 6) {
            return PasswordStrength.WEAK;
        }
        
        int score = 0;
        
        // 長度檢查
        if (password.length() >= 8) score++;
        if (password.length() >= 12) score++;
        
        // 字符類型檢查
        if (password.matches(".*[a-z].*")) score++;  // 小寫字母
        if (password.matches(".*[A-Z].*")) score++;  // 大寫字母
        if (password.matches(".*\\d.*")) score++;    // 數字
        if (password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) score++; // 特殊字符
        
        // 根據得分判斷強度
        if (score < 3) return PasswordStrength.WEAK;
        if (score < 5) return PasswordStrength.MEDIUM;
        if (score < 6) return PasswordStrength.STRONG;
        return PasswordStrength.VERY_STRONG;
    }
    
    /**
     * 示例 8：效能比較
     */
    private static void demonstratePerformanceComparison() {
        System.out.println("8. 效能比較示例");
        System.out.println("====================");
        
        int iterations = 1000;
        
        // String 連接測試
        long startTime = System.currentTimeMillis();
        String result1 = "";
        for (int i = 0; i < iterations; i++) {
            result1 += "a";
        }
        long stringTime = System.currentTimeMillis() - startTime;
        
        // StringBuilder 測試
        startTime = System.currentTimeMillis();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < iterations; i++) {
            sb.append("a");
        }
        String result2 = sb.toString();
        long sbTime = System.currentTimeMillis() - startTime;
        
        // StringBuffer 測試
        startTime = System.currentTimeMillis();
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < iterations; i++) {
            buffer.append("a");
        }
        String result3 = buffer.toString();
        long bufferTime = System.currentTimeMillis() - startTime;
        
        // String.join 測試
        startTime = System.currentTimeMillis();
        String[] array = new String[iterations];
        Arrays.fill(array, "a");
        String result4 = String.join("", array);
        long joinTime = System.currentTimeMillis() - startTime;
        
        System.out.println("字串連接效能比較 (" + iterations + " 次操作):");
        System.out.printf("String +:           %3d ms%n", stringTime);
        System.out.printf("StringBuilder:      %3d ms%n", sbTime);
        System.out.printf("StringBuffer:       %3d ms%n", bufferTime);
        System.out.printf("String.join:        %3d ms%n", joinTime);
        
        // 驗證結果正確性
        System.out.println("\n結果驗證:");
        System.out.println("所有結果長度相同: " + 
                          (result1.length() == result2.length() && 
                           result2.length() == result3.length() && 
                           result3.length() == result4.length()));
        
        // 記憶體使用建議
        System.out.println("\n效能建議:");
        System.out.println("1. 少量字串連接 (<10次): 使用 + 運算符");
        System.out.println("2. 循環中的字串連接: 使用 StringBuilder");
        System.out.println("3. 多執行緒環境: 使用 StringBuffer");
        System.out.println("4. 連接集合元素: 使用 String.join()");
        System.out.println("5. 大量重複字串: 考慮使用 String.intern()");
        
        System.out.println();
    }
}