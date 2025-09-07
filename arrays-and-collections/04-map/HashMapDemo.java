import java.util.*;
import java.util.stream.Collectors;

/**
 * HashMap 詳細示範
 * 
 * 這個類別展示了 HashMap 的各種特性和操作：
 * - 基本的增刪改查操作
 * - 批量操作和合併操作
 * - 內部機制演示
 * - 性能優化技巧
 */
public class HashMapDemo {
    
    public static void main(String[] args) {
        System.out.println("=== HashMap 詳細示範 ===\n");
        
        // 基本操作
        demonstrateBasicOperations();
        
        // 批量操作
        demonstrateBulkOperations();
        
        // Java 8+ 新方法
        demonstrateJava8Methods();
        
        // 內部機制
        demonstrateInternalMechanics();
        
        // 實際應用範例
        demonstratePracticalExample();
    }
    
    /**
     * 基本操作示範
     */
    public static void demonstrateBasicOperations() {
        System.out.println("1. HashMap 基本操作:\n");
        
        // 創建 HashMap
        HashMap<String, Double> productPrices = new HashMap<>();
        
        // 添加元素
        productPrices.put("筆記本電腦", 25000.0);
        productPrices.put("智慧手機", 15000.0);
        productPrices.put("平板電腦", 12000.0);
        productPrices.put("智慧手錶", 8000.0);
        
        System.out.println("產品價格表: " + productPrices);
        
        // 獲取值
        Double laptopPrice = productPrices.get("筆記本電腦");
        System.out.println("筆記本電腦價格: $" + laptopPrice);
        
        // 安全獲取 (避免 null)
        Double headphonePrice = productPrices.getOrDefault("耳機", 0.0);
        System.out.println("耳機價格 (預設0): $" + headphonePrice);
        
        // 檢查是否包含
        boolean hasLaptop = productPrices.containsKey("筆記本電腦");
        boolean hasExpensiveItem = productPrices.containsValue(25000.0);
        System.out.println("是否有筆記本電腦: " + hasLaptop);
        System.out.println("是否有價格為25000的商品: " + hasExpensiveItem);
        
        // 更新價格
        productPrices.put("智慧手機", 14000.0); // 降價
        System.out.println("智慧手機降價後: " + productPrices.get("智慧手機"));
        
        // 條件性添加
        Double oldPrice = productPrices.putIfAbsent("耳機", 3000.0);
        System.out.println("putIfAbsent 返回值: " + oldPrice);
        System.out.println("添加耳機後: " + productPrices);
        
        // 移除元素
        Double removedPrice = productPrices.remove("平板電腦");
        System.out.println("移除的平板電腦價格: $" + removedPrice);
        System.out.println("移除後: " + productPrices);
        
        // 條件性移除
        boolean removed = productPrices.remove("智慧手錶", 8000.0);
        System.out.println("條件性移除智慧手錶: " + removed);
        System.out.println("最終結果: " + productPrices);
        
        System.out.println();
    }
    
    /**
     * 批量操作示範
     */
    public static void demonstrateBulkOperations() {
        System.out.println("2. 批量操作:\n");
        
        Map<String, Integer> inventory1 = new HashMap<>();
        inventory1.put("蘋果", 100);
        inventory1.put("香蕉", 80);
        inventory1.put("橘子", 60);
        
        Map<String, Integer> inventory2 = new HashMap<>();
        inventory2.put("橘子", 40);  // 已存在，會覆蓋
        inventory2.put("葡萄", 50);  // 新增
        inventory2.put("草莓", 30);  // 新增
        
        System.out.println("倉庫1: " + inventory1);
        System.out.println("倉庫2: " + inventory2);
        
        // 批量添加 (putAll)
        Map<String, Integer> totalInventory = new HashMap<>(inventory1);
        totalInventory.putAll(inventory2);
        System.out.println("合併後 (putAll): " + totalInventory);
        
        // 使用 merge 方法進行智能合併
        Map<String, Integer> smartMerge = new HashMap<>(inventory1);
        inventory2.forEach((fruit, quantity) -> 
            smartMerge.merge(fruit, quantity, Integer::sum)); // 相加而不是覆蓋
        
        System.out.println("智能合併 (數量相加): " + smartMerge);
        
        // 批量處理
        Map<String, String> fruitCategories = new HashMap<>();
        fruitCategories.put("蘋果", "溫帶水果");
        fruitCategories.put("香蕉", "熱帶水果");
        fruitCategories.put("橘子", "柑橘類");
        fruitCategories.put("葡萄", "漿果類");
        
        System.out.println("\n水果分類:");
        fruitCategories.forEach((fruit, category) -> 
            System.out.println("  " + fruit + " -> " + category));
        
        // 批量替換
        fruitCategories.replaceAll((fruit, category) -> category.toUpperCase());
        System.out.println("轉大寫後: " + fruitCategories);
        
        System.out.println();
    }
    
    /**
     * Java 8+ 新方法示範
     */
    public static void demonstrateJava8Methods() {
        System.out.println("3. Java 8+ 新方法:\n");
        
        Map<String, Integer> scores = new HashMap<>();
        scores.put("Alice", 85);
        scores.put("Bob", 92);
        scores.put("Charlie", 78);
        scores.put("Diana", 88);
        
        System.out.println("學生成績: " + scores);
        
        // compute 方法 - 計算新值
        scores.compute("Alice", (name, score) -> score + 5); // 加分
        System.out.println("Alice 加分後: " + scores);
        
        // computeIfAbsent 方法 - 鍵不存在時計算
        scores.computeIfAbsent("Eve", name -> name.length() * 10);
        System.out.println("添加 Eve 後: " + scores);
        
        // computeIfPresent 方法 - 鍵存在時重新計算
        scores.computeIfPresent("Bob", (name, score) -> score > 90 ? score + 3 : score);
        System.out.println("Bob 優秀加分後: " + scores);
        
        // merge 方法 - 合併值
        Map<String, Integer> bonusPoints = new HashMap<>();
        bonusPoints.put("Alice", 10);
        bonusPoints.put("Frank", 15);
        
        bonusPoints.forEach((name, bonus) -> 
            scores.merge(name, bonus, Integer::sum));
        
        System.out.println("添加獎勵分數後: " + scores);
        
        // replace 方法
        scores.replace("Charlie", 78, 80); // 條件性替換
        scores.replace("Diana", 95); // 直接替換
        System.out.println("替換後: " + scores);
        
        // 使用 Stream 進行複雜操作
        System.out.println("\nStream 操作範例:");
        
        // 找出高分學生
        List<String> highScorers = scores.entrySet().stream()
            .filter(entry -> entry.getValue() >= 85)
            .map(Map.Entry::getKey)
            .sorted()
            .collect(Collectors.toList());
        
        System.out.println("高分學生 (≥85): " + highScorers);
        
        // 計算平均分
        double average = scores.values().stream()
            .mapToInt(Integer::intValue)
            .average()
            .orElse(0.0);
        
        System.out.println("平均分: " + String.format("%.1f", average));
        
        // 按分數分組
        Map<String, List<String>> groupedByScore = scores.entrySet().stream()
            .collect(Collectors.groupingBy(
                entry -> entry.getValue() >= 90 ? "優秀" : 
                         entry.getValue() >= 80 ? "良好" : "需改進",
                Collectors.mapping(Map.Entry::getKey, Collectors.toList())
            ));
        
        System.out.println("成績分組: " + groupedByScore);
        
        System.out.println();
    }
    
    /**
     * 內部機制演示
     */
    public static void demonstrateInternalMechanics() {
        System.out.println("4. HashMap 內部機制:\n");
        
        // 演示雜湊衝突
        Map<String, String> hashConflictDemo = new HashMap<>();
        
        // 這些字串的 hashCode 相同
        String str1 = "Aa";
        String str2 = "BB";
        
        System.out.println("雜湊衝突演示:");
        System.out.println(str1 + " hashCode: " + str1.hashCode());
        System.out.println(str2 + " hashCode: " + str2.hashCode());
        System.out.println("雜湊碼是否相同: " + (str1.hashCode() == str2.hashCode()));
        System.out.println("字串是否相等: " + str1.equals(str2));
        
        hashConflictDemo.put(str1, "Value for Aa");
        hashConflictDemo.put(str2, "Value for BB");
        
        System.out.println("儘管雜湊碼相同，HashMap 仍能正確處理: " + hashConflictDemo);
        
        // 演示 null 鍵值處理
        System.out.println("\nNull 處理:");
        Map<String, String> nullDemo = new HashMap<>();
        nullDemo.put(null, "null 鍵的值");
        nullDemo.put("key1", null);
        nullDemo.put("key2", "正常值");
        
        System.out.println("包含 null 的 Map: " + nullDemo);
        System.out.println("獲取 null 鍵的值: " + nullDemo.get(null));
        
        // 演示容量調整
        System.out.println("\n容量調整建議:");
        
        // 如果預知大概的元素數量，設定合適的初始容量
        int expectedSize = 1000;
        int optimalCapacity = (int) (expectedSize / 0.75) + 1;
        
        System.out.println("對於 " + expectedSize + " 個元素，建議初始容量: " + optimalCapacity);
        
        // 性能對比測試
        testCapacityImpact();
        
        System.out.println();
    }
    
    /**
     * 容量影響測試
     */
    public static void testCapacityImpact() {
        final int ELEMENT_COUNT = 10000;
        
        // 預設容量
        long startTime = System.nanoTime();
        Map<Integer, String> defaultMap = new HashMap<>();
        for (int i = 0; i < ELEMENT_COUNT; i++) {
            defaultMap.put(i, "Value" + i);
        }
        long defaultTime = System.nanoTime() - startTime;
        
        // 優化容量
        int optimalCapacity = (int) (ELEMENT_COUNT / 0.75) + 1;
        startTime = System.nanoTime();
        Map<Integer, String> optimizedMap = new HashMap<>(optimalCapacity);
        for (int i = 0; i < ELEMENT_COUNT; i++) {
            optimizedMap.put(i, "Value" + i);
        }
        long optimizedTime = System.nanoTime() - startTime;
        
        System.out.printf("添加 %d 個元素:\n", ELEMENT_COUNT);
        System.out.printf("預設容量: %.2f ms\n", defaultTime / 1_000_000.0);
        System.out.printf("優化容量: %.2f ms\n", optimizedTime / 1_000_000.0);
        System.out.printf("性能提升: %.1f%%\n", 
            (1.0 - (double)optimizedTime / defaultTime) * 100);
    }
    
    /**
     * 實際應用範例 - 快取系統
     */
    public static void demonstratePracticalExample() {
        System.out.println("5. 實際應用 - 簡單快取系統:\n");
        
        // 實現一個簡單的快取
        class SimpleCache<K, V> {
            private final Map<K, V> cache = new HashMap<>();
            private final Map<K, Long> timestamps = new HashMap<>();
            private final long ttl; // 存活時間 (毫秒)
            
            public SimpleCache(long ttlMillis) {
                this.ttl = ttlMillis;
            }
            
            public void put(K key, V value) {
                cache.put(key, value);
                timestamps.put(key, System.currentTimeMillis());
            }
            
            public V get(K key) {
                Long timestamp = timestamps.get(key);
                if (timestamp == null) {
                    return null; // 鍵不存在
                }
                
                if (System.currentTimeMillis() - timestamp > ttl) {
                    // 已過期，移除
                    cache.remove(key);
                    timestamps.remove(key);
                    return null;
                }
                
                return cache.get(key);
            }
            
            public void clearExpired() {
                long currentTime = System.currentTimeMillis();
                timestamps.entrySet().removeIf(entry -> {
                    boolean expired = currentTime - entry.getValue() > ttl;
                    if (expired) {
                        cache.remove(entry.getKey());
                    }
                    return expired;
                });
            }
            
            public int size() {
                return cache.size();
            }
            
            public Map<K, V> getAll() {
                return new HashMap<>(cache);
            }
        }
        
        // 使用快取
        SimpleCache<String, String> userCache = new SimpleCache<>(2000); // 2秒TTL
        
        // 添加用戶資料
        userCache.put("user1", "Alice");
        userCache.put("user2", "Bob");
        userCache.put("user3", "Charlie");
        
        System.out.println("快取內容: " + userCache.getAll());
        System.out.println("快取大小: " + userCache.size());
        
        // 立即獲取
        System.out.println("獲取 user1: " + userCache.get("user1"));
        
        // 等待過期
        try {
            Thread.sleep(2100); // 等待超過 TTL
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        System.out.println("2秒後獲取 user1: " + userCache.get("user1"));
        System.out.println("清理過期資料後快取大小: " + userCache.size());
        
        userCache.clearExpired();
        System.out.println("最終快取內容: " + userCache.getAll());
        
        System.out.println();
    }
}