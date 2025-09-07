import java.util.*;

/**
 * LinkedHashMap 詳細示範
 * 
 * 這個類別展示了 LinkedHashMap 的特性和應用：
 * - 保持插入順序
 * - 訪問順序模式
 * - LRU 快取實現
 * - 效能比較
 * - 實際應用場景
 */
public class LinkedHashMapDemo {
    
    public static void main(String[] args) {
        System.out.println("=== LinkedHashMap 詳細示範 ===\n");
        
        // 插入順序保持
        demonstrateInsertionOrder();
        
        // 訪問順序模式
        demonstrateAccessOrder();
        
        // LRU 快取實現
        demonstrateLRUCache();
        
        // 效能比較
        demonstratePerformance();
        
        // 實際應用範例
        demonstratePracticalApplications();
    }
    
    /**
     * 插入順序保持示範
     */
    public static void demonstrateInsertionOrder() {
        System.out.println("1. LinkedHashMap 插入順序保持:\n");
        
        // HashMap vs LinkedHashMap 比較
        Map<String, Integer> hashMap = new HashMap<>();
        Map<String, Integer> linkedHashMap = new LinkedHashMap<>();
        
        String[] fruits = {"蘋果", "香蕉", "橘子", "葡萄", "草莓", "芒果"};
        int[] prices = {30, 25, 20, 35, 40, 45};
        
        // 同樣的插入順序
        for (int i = 0; i < fruits.length; i++) {
            hashMap.put(fruits[i], prices[i]);
            linkedHashMap.put(fruits[i], prices[i]);
        }
        
        System.out.println("HashMap 遍歷順序 (不可預測):");
        hashMap.forEach((fruit, price) -> 
            System.out.println("  " + fruit + ": " + price + "元"));
        
        System.out.println("\nLinkedHashMap 遍歷順序 (保持插入順序):");
        linkedHashMap.forEach((fruit, price) -> 
            System.out.println("  " + fruit + ": " + price + "元"));
        
        // 驗證順序一致性
        List<String> insertionOrder = Arrays.asList(fruits);
        List<String> linkedHashMapOrder = new ArrayList<>(linkedHashMap.keySet());
        
        System.out.println("\n插入順序: " + insertionOrder);
        System.out.println("LinkedHashMap 順序: " + linkedHashMapOrder);
        System.out.println("順序是否一致: " + insertionOrder.equals(linkedHashMapOrder));
        
        // 中間插入和替換
        System.out.println("\n中間操作後的順序變化:");
        linkedHashMap.put("木瓜", 28); // 新增
        linkedHashMap.put("香蕉", 27); // 替換值 (位置不變)
        
        System.out.println("新增木瓜和修改香蕉價格後:");
        linkedHashMap.forEach((fruit, price) -> 
            System.out.println("  " + fruit + ": " + price + "元"));
        
        System.out.println();
    }
    
    /**
     * 訪問順序模式示範
     */
    public static void demonstrateAccessOrder() {
        System.out.println("2. LinkedHashMap 訪問順序模式:\n");
        
        // 建立訪問順序模式的 LinkedHashMap
        Map<String, String> accessOrderMap = new LinkedHashMap<>(16, 0.75f, true);
        
        // 初始化資料
        accessOrderMap.put("用戶1", "Alice");
        accessOrderMap.put("用戶2", "Bob");
        accessOrderMap.put("用戶3", "Charlie");
        accessOrderMap.put("用戶4", "Diana");
        accessOrderMap.put("用戶5", "Eve");
        
        System.out.println("初始順序:");
        printMapOrder(accessOrderMap);
        
        // 訪問某些元素
        System.out.println("\n訪問 '用戶2' 和 '用戶4':");
        accessOrderMap.get("用戶2");
        accessOrderMap.get("用戶4");
        
        System.out.println("訪問後的順序 (被訪問的移到最後):");
        printMapOrder(accessOrderMap);
        
        // 再次訪問
        System.out.println("\n再次訪問 '用戶1':");
        accessOrderMap.get("用戶1");
        
        System.out.println("再次訪問後的順序:");
        printMapOrder(accessOrderMap);
        
        // put 操作也算訪問
        System.out.println("\n更新 '用戶3' 的值:");
        accessOrderMap.put("用戶3", "Charlie Updated");
        
        System.out.println("更新後的順序:");
        printMapOrder(accessOrderMap);
        
        System.out.println();
    }
    
    /**
     * 輔助方法：打印 Map 的順序
     */
    private static void printMapOrder(Map<String, String> map) {
        map.forEach((key, value) -> System.out.println("  " + key + " -> " + value));
    }
    
    /**
     * LRU 快取實現示範
     */
    public static void demonstrateLRUCache() {
        System.out.println("3. LRU 快取實現:\n");
        
        // 簡單的 LRU 快取實現
        class LRUCache<K, V> extends LinkedHashMap<K, V> {
            private final int maxSize;
            
            public LRUCache(int maxSize) {
                // 使用訪問順序模式
                super(16, 0.75f, true);
                this.maxSize = maxSize;
            }
            
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > maxSize;
            }
            
            public void printCache() {
                System.out.println("  快取內容 (最新使用的在最後): " + this);
            }
        }
        
        // 建立容量為 3 的 LRU 快取
        LRUCache<String, String> cache = new LRUCache<>(3);
        
        System.out.println("建立容量為 3 的 LRU 快取\n");
        
        // 添加元素
        System.out.println("添加元素:");
        cache.put("頁面A", "內容A");
        System.out.println("添加 頁面A");
        cache.printCache();
        
        cache.put("頁面B", "內容B");
        System.out.println("添加 頁面B");
        cache.printCache();
        
        cache.put("頁面C", "內容C");
        System.out.println("添加 頁面C");
        cache.printCache();
        
        // 快取已滿，再添加會淘汰最老的
        System.out.println("\n快取已滿，添加新元素:");
        cache.put("頁面D", "內容D");
        System.out.println("添加 頁面D (頁面A 被淘汰)");
        cache.printCache();
        
        // 訪問現有元素
        System.out.println("\n訪問現有元素:");
        cache.get("頁面B");
        System.out.println("訪問 頁面B (移到最後)");
        cache.printCache();
        
        // 再添加新元素
        cache.put("頁面E", "內容E");
        System.out.println("添加 頁面E (頁面C 被淘汰)");
        cache.printCache();
        
        System.out.println();
    }
    
    /**
     * 效能比較示範
     */
    public static void demonstratePerformance() {
        System.out.println("4. 效能比較:\n");
        
        int testSize = 100000;
        
        // 測試插入效能
        System.out.println("插入 " + String.format("%,d", testSize) + " 個元素的效能:");
        
        // HashMap
        long startTime = System.nanoTime();
        Map<Integer, String> hashMap = new HashMap<>();
        for (int i = 0; i < testSize; i++) {
            hashMap.put(i, "Value" + i);
        }
        long hashMapTime = System.nanoTime() - startTime;
        
        // LinkedHashMap
        startTime = System.nanoTime();
        Map<Integer, String> linkedHashMap = new LinkedHashMap<>();
        for (int i = 0; i < testSize; i++) {
            linkedHashMap.put(i, "Value" + i);
        }
        long linkedHashMapTime = System.nanoTime() - startTime;
        
        // TreeMap
        startTime = System.nanoTime();
        Map<Integer, String> treeMap = new TreeMap<>();
        for (int i = 0; i < testSize; i++) {
            treeMap.put(i, "Value" + i);
        }
        long treeMapTime = System.nanoTime() - startTime;
        
        System.out.printf("  HashMap: %.2f ms\n", hashMapTime / 1_000_000.0);
        System.out.printf("  LinkedHashMap: %.2f ms\n", linkedHashMapTime / 1_000_000.0);
        System.out.printf("  TreeMap: %.2f ms\n", treeMapTime / 1_000_000.0);
        
        // 測試隨機訪問效能
        System.out.println("\n隨機訪問效能 (10,000 次查詢):");
        Random random = new Random(42); // 固定種子確保一致性
        int[] randomKeys = new int[10000];
        for (int i = 0; i < randomKeys.length; i++) {
            randomKeys[i] = random.nextInt(testSize);
        }
        
        // HashMap 查詢
        startTime = System.nanoTime();
        for (int key : randomKeys) {
            hashMap.get(key);
        }
        long hashMapQueryTime = System.nanoTime() - startTime;
        
        // LinkedHashMap 查詢
        startTime = System.nanoTime();
        for (int key : randomKeys) {
            linkedHashMap.get(key);
        }
        long linkedHashMapQueryTime = System.nanoTime() - startTime;
        
        // TreeMap 查詢
        startTime = System.nanoTime();
        for (int key : randomKeys) {
            treeMap.get(key);
        }
        long treeMapQueryTime = System.nanoTime() - startTime;
        
        System.out.printf("  HashMap: %.2f ms\n", hashMapQueryTime / 1_000_000.0);
        System.out.printf("  LinkedHashMap: %.2f ms\n", linkedHashMapQueryTime / 1_000_000.0);
        System.out.printf("  TreeMap: %.2f ms\n", treeMapQueryTime / 1_000_000.0);
        
        // 記憶體使用比較
        System.out.println("\n記憶體使用比較:");
        Runtime runtime = Runtime.getRuntime();
        
        // 強制垃圾回收
        System.gc();
        long baseMemory = runtime.totalMemory() - runtime.freeMemory();
        
        Map<Integer, String> memoryTestMap = new LinkedHashMap<>();
        for (int i = 0; i < 50000; i++) {
            memoryTestMap.put(i, "Value" + i);
        }
        
        System.gc();
        long usedMemory = runtime.totalMemory() - runtime.freeMemory() - baseMemory;
        
        System.out.printf("  LinkedHashMap (50,000 元素): ~%.2f MB\n", usedMemory / (1024.0 * 1024.0));
        System.out.println("  (LinkedHashMap 比 HashMap 多用約 40-50% 記憶體，因為需要維護雙向鏈表)");
        
        System.out.println();
    }
    
    /**
     * 實際應用範例
     */
    public static void demonstratePracticalApplications() {
        System.out.println("5. 實際應用範例:\n");
        
        // 應用1: 網頁瀏覽歷史
        demonstrateBrowsingHistory();
        
        // 應用2: 配置文件處理
        demonstrateConfigurationFile();
        
        // 應用3: 資料快取系統
        demonstrateDataCache();
    }
    
    /**
     * 網頁瀏覽歷史範例
     */
    public static void demonstrateBrowsingHistory() {
        System.out.println("應用1: 網頁瀏覽歷史管理\n");
        
        class BrowsingHistory {
            private final Map<String, Long> history;
            private final int maxHistorySize;
            
            public BrowsingHistory(int maxSize) {
                this.maxHistorySize = maxSize;
                this.history = new LinkedHashMap<String, Long>(16, 0.75f, true) {
                    @Override
                    protected boolean removeEldestEntry(Map.Entry<String, Long> eldest) {
                        return size() > maxHistorySize;
                    }
                };
            }
            
            public void visitPage(String url) {
                history.put(url, System.currentTimeMillis());
                System.out.println("訪問: " + url);
            }
            
            public void printHistory() {
                System.out.println("瀏覽歷史 (按最近訪問排序):");
                List<String> urls = new ArrayList<>(history.keySet());
                Collections.reverse(urls); // 最新的顯示在前面
                
                for (int i = 0; i < urls.size(); i++) {
                    String url = urls.get(i);
                    System.out.println("  " + (i + 1) + ". " + url);
                }
            }
            
            public List<String> getRecentPages(int count) {
                List<String> urls = new ArrayList<>(history.keySet());
                Collections.reverse(urls);
                return urls.subList(0, Math.min(count, urls.size()));
            }
        }
        
        BrowsingHistory browser = new BrowsingHistory(5);
        
        // 模擬瀏覽行為
        browser.visitPage("https://www.google.com");
        browser.visitPage("https://stackoverflow.com");
        browser.visitPage("https://github.com");
        browser.visitPage("https://docs.oracle.com");
        browser.visitPage("https://www.baeldung.com");
        
        System.out.println();
        browser.printHistory();
        
        // 再次訪問已有的頁面
        System.out.println("\n再次訪問 GitHub:");
        browser.visitPage("https://github.com");
        browser.printHistory();
        
        // 訪問新頁面，超出容量限制
        System.out.println("\n訪問新頁面 (超出容量):");
        browser.visitPage("https://leetcode.com");
        browser.printHistory();
        
        // 獲取最近訪問的頁面
        System.out.println("\n最近訪問的 3 個頁面:");
        List<String> recent = browser.getRecentPages(3);
        for (int i = 0; i < recent.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + recent.get(i));
        }
        
        System.out.println();
    }
    
    /**
     * 配置文件處理範例
     */
    public static void demonstrateConfigurationFile() {
        System.out.println("應用2: 配置文件處理\n");
        
        class ConfigurationManager {
            private final Map<String, String> config;
            
            public ConfigurationManager() {
                // 使用 LinkedHashMap 保持配置項的順序
                this.config = new LinkedHashMap<>();
            }
            
            public void loadConfig() {
                // 模擬從文件讀取配置
                System.out.println("載入配置文件...");
                
                config.put("app.name", "My Application");
                config.put("app.version", "1.0.0");
                config.put("server.port", "8080");
                config.put("server.host", "localhost");
                config.put("database.url", "jdbc:mysql://localhost:3306/mydb");
                config.put("database.username", "admin");
                config.put("database.password", "password123");
                config.put("logging.level", "INFO");
                config.put("cache.enabled", "true");
                config.put("cache.size", "1000");
                
                System.out.println("配置載入完成！");
            }
            
            public void printConfig() {
                System.out.println("應用程式配置:");
                config.forEach((key, value) -> {
                    if (key.contains("password")) {
                        System.out.println("  " + key + " = ******");
                    } else {
                        System.out.println("  " + key + " = " + value);
                    }
                });
            }
            
            public void updateConfig(String key, String value) {
                String oldValue = config.put(key, value);
                System.out.println("更新配置: " + key + " = " + value + 
                    (oldValue != null ? " (原值: " + oldValue + ")" : " (新增)"));
            }
            
            public Map<String, String> getConfigByPrefix(String prefix) {
                Map<String, String> result = new LinkedHashMap<>();
                config.entrySet().stream()
                    .filter(entry -> entry.getKey().startsWith(prefix))
                    .forEach(entry -> result.put(entry.getKey(), entry.getValue()));
                return result;
            }
            
            public void exportConfig() {
                System.out.println("\n匯出配置文件內容:");
                System.out.println("# Application Configuration File");
                System.out.println("# Generated on " + new Date());
                System.out.println();
                
                config.forEach((key, value) -> 
                    System.out.println(key + "=" + value));
            }
        }
        
        ConfigurationManager configManager = new ConfigurationManager();
        configManager.loadConfig();
        System.out.println();
        
        configManager.printConfig();
        
        // 更新配置
        System.out.println("\n更新配置:");
        configManager.updateConfig("server.port", "9090");
        configManager.updateConfig("debug.enabled", "true");
        
        // 按前綴查詢
        System.out.println("\n資料庫相關配置:");
        Map<String, String> dbConfig = configManager.getConfigByPrefix("database.");
        dbConfig.forEach((key, value) -> 
            System.out.println("  " + key + " = " + (key.contains("password") ? "******" : value)));
        
        // 匯出配置
        configManager.exportConfig();
        
        System.out.println();
    }
    
    /**
     * 資料快取系統範例
     */
    public static void demonstrateDataCache() {
        System.out.println("應用3: 智能資料快取系統\n");
        
        class SmartCache<K, V> {
            private final Map<K, CacheEntry> cache;
            private final int maxSize;
            private final long ttlMillis;
            
            class CacheEntry {
                final V value;
                final long createTime;
                long lastAccessTime;
                int accessCount;
                
                CacheEntry(V value) {
                    this.value = value;
                    this.createTime = System.currentTimeMillis();
                    this.lastAccessTime = createTime;
                    this.accessCount = 1;
                }
                
                void updateAccess() {
                    this.lastAccessTime = System.currentTimeMillis();
                    this.accessCount++;
                }
                
                boolean isExpired(long ttl) {
                    return System.currentTimeMillis() - createTime > ttl;
                }
            }
            
            public SmartCache(int maxSize, long ttlSeconds) {
                this.maxSize = maxSize;
                this.ttlMillis = ttlSeconds * 1000;
                this.cache = new LinkedHashMap<K, CacheEntry>(16, 0.75f, true) {
                    @Override
                    protected boolean removeEldestEntry(Map.Entry<K, CacheEntry> eldest) {
                        return size() > maxSize;
                    }
                };
            }
            
            public V get(K key) {
                CacheEntry entry = cache.get(key);
                if (entry == null) {
                    return null;
                }
                
                if (entry.isExpired(ttlMillis)) {
                    cache.remove(key);
                    return null;
                }
                
                entry.updateAccess();
                return entry.value;
            }
            
            public void put(K key, V value) {
                cache.put(key, new CacheEntry(value));
            }
            
            public void printCacheStats() {
                System.out.println("快取統計:");
                System.out.println("  快取大小: " + cache.size() + "/" + maxSize);
                System.out.println("  TTL: " + (ttlMillis / 1000) + " 秒");
                
                if (!cache.isEmpty()) {
                    System.out.println("  快取內容 (按訪問順序):");
                    cache.forEach((key, entry) -> {
                        long age = (System.currentTimeMillis() - entry.createTime) / 1000;
                        long lastAccess = (System.currentTimeMillis() - entry.lastAccessTime) / 1000;
                        System.out.printf("    %s: 訪問%d次, 存在%d秒, 上次訪問%d秒前\n", 
                            key, entry.accessCount, age, lastAccess);
                    });
                }
            }
            
            public void cleanup() {
                // 清理過期項目
                cache.entrySet().removeIf(entry -> entry.getValue().isExpired(ttlMillis));
            }
        }
        
        // 建立快取 (容量 3，TTL 5秒)
        SmartCache<String, String> cache = new SmartCache<>(3, 5);
        
        System.out.println("建立智能快取 (容量: 3, TTL: 5秒)\n");
        
        // 添加資料
        cache.put("用戶:1001", "Alice的資料");
        cache.put("用戶:1002", "Bob的資料");
        cache.put("用戶:1003", "Charlie的資料");
        
        System.out.println("添加 3 個用戶資料:");
        cache.printCacheStats();
        
        // 模擬訪問
        System.out.println("\n訪問用戶 1001 和 1002:");
        cache.get("用戶:1001");
        cache.get("用戶:1002");
        cache.printCacheStats();
        
        // 添加新資料 (觸發 LRU 淘汰)
        System.out.println("\n添加新用戶 1004 (觸發 LRU 淘汰):");
        cache.put("用戶:1004", "Diana的資料");
        cache.printCacheStats();
        
        // 模擬時間過去 (TTL 測試)
        System.out.println("\n等待 TTL 過期...");
        try {
            Thread.sleep(2000); // 等待 2 秒
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        System.out.println("2秒後的快取狀態:");
        cache.printCacheStats();
        
        // 訪問測試 (刷新 TTL)
        System.out.println("\n訪問用戶 1002 (刷新其訪問時間):");
        String userData = cache.get("用戶:1002");
        System.out.println("獲取到資料: " + userData);
        cache.printCacheStats();
        
        // 手動清理過期項目
        System.out.println("\n手動清理過期項目:");
        cache.cleanup();
        cache.printCacheStats();
        
        System.out.println();
    }
}