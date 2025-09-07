import java.util.*;
import java.util.stream.Collectors;

/**
 * TreeMap 詳細示範
 * 
 * 這個類別展示了 TreeMap 的各種特性和操作：
 * - 自動排序功能
 * - 導航方法的使用
 * - 自定義比較器
 * - 子映射操作
 * - 實際應用場景
 */
public class TreeMapDemo {
    
    public static void main(String[] args) {
        System.out.println("=== TreeMap 詳細示範 ===\n");
        
        // 基本排序功能
        demonstrateBasicSorting();
        
        // 導航方法
        demonstrateNavigationMethods();
        
        // 子映射操作
        demonstrateSubMapOperations();
        
        // 自定義比較器
        demonstrateCustomComparator();
        
        // 實際應用範例
        demonstratePracticalApplications();
    }
    
    /**
     * 基本排序功能示範
     */
    public static void demonstrateBasicSorting() {
        System.out.println("1. TreeMap 基本排序功能:\n");
        
        // 整數鍵的自然排序
        TreeMap<Integer, String> months = new TreeMap<>();
        months.put(12, "十二月");
        months.put(1, "一月");
        months.put(6, "六月");
        months.put(3, "三月");
        months.put(9, "九月");
        
        System.out.println("月份 (按數字排序): " + months);
        
        // 字串鍵的自然排序
        TreeMap<String, Integer> cityPopulation = new TreeMap<>();
        cityPopulation.put("台北", 2600000);
        cityPopulation.put("高雄", 2750000);
        cityPopulation.put("台中", 2800000);
        cityPopulation.put("桃園", 2250000);
        cityPopulation.put("台南", 1880000);
        
        System.out.println("城市人口 (按城市名排序): " + cityPopulation);
        
        // 比較 HashMap 的無序性
        HashMap<String, Integer> hashMapDemo = new HashMap<>();
        hashMapDemo.putAll(cityPopulation);
        System.out.println("同樣資料的 HashMap (無序): " + hashMapDemo);
        
        // 顯示排序的優勢
        System.out.println("\nTreeMap 遍歷 (有序):");
        cityPopulation.forEach((city, population) -> 
            System.out.println("  " + city + ": " + String.format("%,d", population) + " 人"));
        
        System.out.println();
    }
    
    /**
     * 導航方法示範
     */
    public static void demonstrateNavigationMethods() {
        System.out.println("2. TreeMap 導航方法:\n");
        
        TreeMap<Integer, String> scores = new TreeMap<>();
        scores.put(95, "優秀");
        scores.put(85, "良好");
        scores.put(75, "及格");
        scores.put(65, "不及格");
        scores.put(55, "需改進");
        
        System.out.println("成績等級對照表: " + scores);
        
        // 第一個和最後一個
        System.out.println("\n邊界元素:");
        System.out.println("最低分數: " + scores.firstKey() + " (" + scores.firstEntry().getValue() + ")");
        System.out.println("最高分數: " + scores.lastKey() + " (" + scores.lastEntry().getValue() + ")");
        
        // 導航查詢
        int targetScore = 80;
        System.out.println("\n導航查詢 (目標分數 " + targetScore + "):");
        
        Integer lowerKey = scores.lowerKey(targetScore);
        Integer floorKey = scores.floorKey(targetScore);
        Integer ceilingKey = scores.ceilingKey(targetScore);
        Integer higherKey = scores.higherKey(targetScore);
        
        System.out.println("小於 " + targetScore + " 的最大鍵: " + lowerKey);
        System.out.println("小於等於 " + targetScore + " 的最大鍵: " + floorKey);
        System.out.println("大於等於 " + targetScore + " 的最小鍵: " + ceilingKey);
        System.out.println("大於 " + targetScore + " 的最小鍵: " + higherKey);
        
        // 獲取對應的值
        if (floorKey != null) {
            System.out.println("分數 " + targetScore + " 對應等級: " + scores.get(floorKey));
        }
        
        // 移除邊界元素
        System.out.println("\n移除操作:");
        Map.Entry<Integer, String> removedFirst = scores.pollFirstEntry();
        Map.Entry<Integer, String> removedLast = scores.pollLastEntry();
        
        System.out.println("移除的最低分: " + removedFirst);
        System.out.println("移除的最高分: " + removedLast);
        System.out.println("移除後: " + scores);
        
        System.out.println();
    }
    
    /**
     * 子映射操作示範
     */
    public static void demonstrateSubMapOperations() {
        System.out.println("3. TreeMap 子映射操作:\n");
        
        TreeMap<String, Double> stockPrices = new TreeMap<>();
        stockPrices.put("AAPL", 150.25);
        stockPrices.put("GOOGL", 2800.50);
        stockPrices.put("MSFT", 310.75);
        stockPrices.put("AMZN", 3100.00);
        stockPrices.put("TSLA", 800.30);
        stockPrices.put("META", 320.40);
        
        System.out.println("股票價格 (按代碼排序): " + stockPrices);
        
        // headMap - 小於指定鍵的子映射
        SortedMap<String, Double> headMap = stockPrices.headMap("MSFT");
        System.out.println("\n字母順序小於 'MSFT' 的股票: " + headMap);
        
        // tailMap - 大於等於指定鍵的子映射
        SortedMap<String, Double> tailMap = stockPrices.tailMap("MSFT");
        System.out.println("字母順序大於等於 'MSFT' 的股票: " + tailMap);
        
        // subMap - 指定範圍的子映射
        SortedMap<String, Double> subMap = stockPrices.subMap("AAPL", "MSFT");
        System.out.println("範圍 ['AAPL', 'MSFT') 的股票: " + subMap);
        
        // NavigableMap 的擴展範圍方法
        NavigableMap<String, Double> navigableMap = stockPrices;
        
        // 包含結束鍵的範圍
        NavigableMap<String, Double> inclusiveSubMap = navigableMap.subMap("AAPL", true, "MSFT", true);
        System.out.println("包含範圍 ['AAPL', 'MSFT'] 的股票: " + inclusiveSubMap);
        
        // 反向視圖
        NavigableMap<String, Double> descendingMap = stockPrices.descendingMap();
        System.out.println("\n反向排序: " + descendingMap);
        
        // 反向鍵集合
        NavigableSet<String> descendingKeys = stockPrices.descendingKeySet();
        System.out.println("反向鍵順序: " + descendingKeys);
        
        System.out.println();
    }
    
    /**
     * 自定義比較器示範
     */
    public static void demonstrateCustomComparator() {
        System.out.println("4. 自定義比較器:\n");
        
        // 按字串長度排序
        TreeMap<String, Integer> wordsByLength = new TreeMap<>(
            Comparator.comparing(String::length).thenComparing(String::compareTo)
        );
        
        wordsByLength.put("cat", 3);
        wordsByLength.put("elephant", 8);
        wordsByLength.put("dog", 3);
        wordsByLength.put("butterfly", 9);
        wordsByLength.put("ant", 3);
        
        System.out.println("按長度和字母順序排序的單詞:");
        wordsByLength.forEach((word, length) -> 
            System.out.println("  " + word + " (長度: " + length + ")"));
        
        // 學生成績排序 (自定義物件)
        class Student {
            String name;
            double gpa;
            int age;
            
            Student(String name, double gpa, int age) {
                this.name = name;
                this.gpa = gpa;
                this.age = age;
            }
            
            @Override
            public String toString() {
                return String.format("Student{name='%s', gpa=%.2f, age=%d}", name, gpa, age);
            }
        }
        
        // 按 GPA 降序，GPA 相同則按年齡升序
        TreeMap<Student, String> studentRanking = new TreeMap<>(
            Comparator.comparing((Student s) -> s.gpa).reversed()
                     .thenComparing(s -> s.age)
        );
        
        studentRanking.put(new Student("Alice", 3.8, 20), "二年級");
        studentRanking.put(new Student("Bob", 3.9, 19), "一年級");
        studentRanking.put(new Student("Charlie", 3.8, 21), "三年級");
        studentRanking.put(new Student("Diana", 3.9, 20), "二年級");
        
        System.out.println("\n學生排名 (GPA降序，年齡升序):");
        int rank = 1;
        for (Map.Entry<Student, String> entry : studentRanking.entrySet()) {
            System.out.println("  第" + rank++ + "名: " + entry.getKey() + " - " + entry.getValue());
        }
        
        // 數字倒序排列
        TreeMap<Integer, String> reverseNumbers = new TreeMap<>(Collections.reverseOrder());
        reverseNumbers.put(1, "一");
        reverseNumbers.put(5, "五");
        reverseNumbers.put(3, "三");
        reverseNumbers.put(2, "二");
        reverseNumbers.put(4, "四");
        
        System.out.println("\n數字倒序: " + reverseNumbers);
        
        System.out.println();
    }
    
    /**
     * 實際應用範例
     */
    public static void demonstratePracticalApplications() {
        System.out.println("5. 實際應用範例:\n");
        
        // 範例1: 時間序列數據
        demonstrateTimeSeriesData();
        
        // 範例2: 範圍查詢系統
        demonstrateRangeQuerySystem();
        
        // 範例3: 排行榜系統
        demonstrateLeaderboardSystem();
    }
    
    /**
     * 時間序列數據範例
     */
    public static void demonstrateTimeSeriesData() {
        System.out.println("應用1: 時間序列數據管理\n");
        
        TreeMap<Long, Double> temperatureData = new TreeMap<>();
        
        // 模擬一天的溫度數據 (時間戳 -> 溫度)
        long baseTime = System.currentTimeMillis() - 24 * 60 * 60 * 1000; // 24小時前
        Random random = new Random();
        
        for (int i = 0; i < 24; i++) {
            long timestamp = baseTime + i * 60 * 60 * 1000; // 每小時一個數據點
            double temperature = 20 + random.nextGaussian() * 5; // 平均20度，標準差5度
            temperatureData.put(timestamp, temperature);
        }
        
        System.out.println("24小時溫度數據點數: " + temperatureData.size());
        
        // 查詢最近6小時的數據
        long sixHoursAgo = System.currentTimeMillis() - 6 * 60 * 60 * 1000;
        NavigableMap<Long, Double> recentData = temperatureData.tailMap(sixHoursAgo, true);
        
        System.out.println("最近6小時的數據點數: " + recentData.size());
        
        // 計算平均溫度
        double avgTemp = recentData.values().stream()
            .mapToDouble(Double::doubleValue)
            .average()
            .orElse(0.0);
        
        System.out.printf("最近6小時平均溫度: %.1f°C\n", avgTemp);
        
        // 找出最高和最低溫度
        double maxTemp = temperatureData.values().stream()
            .mapToDouble(Double::doubleValue)
            .max()
            .orElse(Double.MIN_VALUE);
        
        double minTemp = temperatureData.values().stream()
            .mapToDouble(Double::doubleValue)
            .min()
            .orElse(Double.MAX_VALUE);
        
        System.out.printf("24小時內最高溫度: %.1f°C\n", maxTemp);
        System.out.printf("24小時內最低溫度: %.1f°C\n", minTemp);
        
        System.out.println();
    }
    
    /**
     * 範圍查詢系統範例
     */
    public static void demonstrateRangeQuerySystem() {
        System.out.println("應用2: 成績範圍查詢系統\n");
        
        TreeMap<Integer, List<String>> scoreRanges = new TreeMap<>();
        
        // 建立分數區間到學生列表的映射
        scoreRanges.put(90, Arrays.asList("Alice", "Bob", "Charlie"));    // 90-99
        scoreRanges.put(80, Arrays.asList("Diana", "Eve", "Frank"));      // 80-89
        scoreRanges.put(70, Arrays.asList("Grace", "Henry"));             // 70-79
        scoreRanges.put(60, Arrays.asList("Ivy"));                       // 60-69
        scoreRanges.put(50, Arrays.asList("Jack", "Kate"));               // 50-59
        
        System.out.println("成績區間分布:");
        scoreRanges.descendingMap().forEach((score, students) -> 
            System.out.println("  " + score + "-" + (score + 9) + "分: " + students));
        
        // 查詢特定分數範圍的學生
        int minScore = 70;
        NavigableMap<Integer, List<String>> qualifiedStudents = 
            scoreRanges.tailMap(minScore, true);
        
        System.out.println("\n分數 ≥ " + minScore + " 的學生:");
        List<String> allQualified = qualifiedStudents.values().stream()
            .flatMap(List::stream)
            .collect(Collectors.toList());
        
        System.out.println("  " + allQualified);
        System.out.println("  總計: " + allQualified.size() + " 人");
        
        // 查詢分數區間
        NavigableMap<Integer, List<String>> middleRange = 
            scoreRanges.subMap(60, true, 89, true);
        
        System.out.println("\n分數在 60-89 之間的學生:");
        middleRange.forEach((score, students) -> 
            System.out.println("  " + score + "-" + (score + 9) + "分: " + students));
        
        System.out.println();
    }
    
    /**
     * 排行榜系統範例
     */
    public static void demonstrateLeaderboardSystem() {
        System.out.println("應用3: 遊戲排行榜系統\n");
        
        // 玩家分數類
        class PlayerScore {
            String playerName;
            int score;
            long timestamp; // 達成時間
            
            PlayerScore(String playerName, int score) {
                this.playerName = playerName;
                this.score = score;
                this.timestamp = System.currentTimeMillis();
            }
            
            @Override
            public String toString() {
                return String.format("%s: %,d分", playerName, score);
            }
        }
        
        // 按分數降序排列，分數相同則按時間升序 (早達成的排前面)
        TreeMap<PlayerScore, String> leaderboard = new TreeMap<>((p1, p2) -> {
            int scoreCompare = Integer.compare(p2.score, p1.score);
            if (scoreCompare != 0) {
                return scoreCompare;
            }
            return Long.compare(p1.timestamp, p2.timestamp);
        });
        
        // 添加玩家分數
        leaderboard.put(new PlayerScore("Alice", 15000), "高級玩家");
        leaderboard.put(new PlayerScore("Bob", 18000), "專家玩家");
        leaderboard.put(new PlayerScore("Charlie", 12000), "中級玩家");
        leaderboard.put(new PlayerScore("Diana", 18000), "專家玩家"); // 同分但較晚達成
        leaderboard.put(new PlayerScore("Eve", 20000), "大師玩家");
        leaderboard.put(new PlayerScore("Frank", 8000), "初級玩家");
        
        System.out.println("遊戲排行榜:");
        int rank = 1;
        for (Map.Entry<PlayerScore, String> entry : leaderboard.entrySet()) {
            PlayerScore player = entry.getKey();
            String level = entry.getValue();
            System.out.printf("  第%d名: %s (%s)\n", rank++, player, level);
        }
        
        // 查詢前3名
        List<PlayerScore> top3 = leaderboard.keySet().stream()
            .limit(3)
            .collect(Collectors.toList());
        
        System.out.println("\n前3名玩家:");
        for (int i = 0; i < top3.size(); i++) {
            System.out.println("  第" + (i + 1) + "名: " + top3.get(i));
        }
        
        // 查詢特定分數以上的玩家
        int thresholdScore = 15000;
        long countAboveThreshold = leaderboard.keySet().stream()
            .filter(player -> player.score >= thresholdScore)
            .count();
        
        System.out.println("\n分數 ≥ " + String.format("%,d", thresholdScore) + " 的玩家數: " + countAboveThreshold);
        
        System.out.println();
    }
}