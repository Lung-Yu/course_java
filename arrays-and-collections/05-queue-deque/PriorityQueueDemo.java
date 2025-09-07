import java.util.*;
import java.util.stream.Collectors;

/**
 * PriorityQueue 優先佇列詳細示範
 * 
 * 這個類別展示了 PriorityQueue 的各種特性和應用：
 * - 堆積結構和自然排序
 * - 自定義比較器
 * - 堆積操作的時間複雜度
 * - 實際應用場景
 * - 效能分析
 */
public class PriorityQueueDemo {
    
    public static void main(String[] args) {
        System.out.println("=== PriorityQueue 優先佇列詳細示範 ===\n");
        
        // 基本優先佇列操作
        demonstrateBasicPriorityQueue();
        
        // 自定義比較器
        demonstrateCustomComparator();
        
        // 堆積操作分析
        demonstrateHeapOperations();
        
        // 效能分析
        performanceAnalysis();
        
        // 實際應用範例
        demonstratePracticalApplications();
    }
    
    /**
     * 基本優先佇列操作示範
     */
    public static void demonstrateBasicPriorityQueue() {
        System.out.println("1. PriorityQueue 基本操作:\n");
        
        // 預設最小堆積
        PriorityQueue<Integer> minHeap = new PriorityQueue<>();
        
        System.out.println("建立最小堆積（預設行為）");
        System.out.println("空堆積: " + minHeap);
        
        // 添加元素
        System.out.println("\n=== 添加元素 ===");
        int[] numbers = {15, 10, 20, 8, 25, 5, 30};
        
        for (int num : numbers) {
            minHeap.offer(num);
            System.out.println("添加 " + num + ": " + minHeap);
        }
        
        System.out.println("\n注意：PriorityQueue 的 toString() 不保證顯示排序順序！");
        System.out.println("內部堆積結構: " + minHeap);
        System.out.println("堆積頂部（最小值）: " + minHeap.peek());
        
        // 按優先順序取出元素
        System.out.println("\n=== 按優先順序取出元素 ===");
        PriorityQueue<Integer> copy = new PriorityQueue<>(minHeap);
        while (!copy.isEmpty()) {
            System.out.print(copy.poll() + " ");
        }
        System.out.println("← 排序順序（最小到最大）");
        
        // 遍歷 vs 排序取出的差異
        System.out.println("\n=== 遍歷 vs 排序取出的差異 ===");
        System.out.println("使用 for-each 遍歷（無序）:");
        for (Integer num : minHeap) {
            System.out.print(num + " ");
        }
        System.out.println();
        
        System.out.println("使用 poll() 取出（有序）:");
        while (!minHeap.isEmpty()) {
            System.out.print(minHeap.poll() + " ");
        }
        System.out.println();
        
        System.out.println();
    }
    
    /**
     * 自定義比較器示範
     */
    public static void demonstrateCustomComparator() {
        System.out.println("2. 自定義比較器:\n");
        
        // 最大堆積
        demonstrateMaxHeap();
        
        // 字串長度優先
        demonstrateStringLengthPriority();
        
        // 自定義物件排序
        demonstrateCustomObjectSorting();
    }
    
    /**
     * 最大堆積示範
     */
    public static void demonstrateMaxHeap() {
        System.out.println("=== 最大堆積 ===");
        
        // 使用 Collections.reverseOrder() 創建最大堆積
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
        
        int[] numbers = {15, 10, 20, 8, 25, 5, 30};
        
        System.out.println("添加元素到最大堆積:");
        for (int num : numbers) {
            maxHeap.offer(num);
        }
        
        System.out.println("最大堆積: " + maxHeap);
        System.out.println("堆積頂部（最大值）: " + maxHeap.peek());
        
        System.out.println("按優先順序取出（最大到最小）:");
        while (!maxHeap.isEmpty()) {
            System.out.print(maxHeap.poll() + " ");
        }
        System.out.println();
        
        System.out.println();
    }
    
    /**
     * 字串長度優先示範
     */
    public static void demonstrateStringLengthPriority() {
        System.out.println("=== 字串長度優先 ===");
        
        // 按字串長度排序，長度相同則按字典序
        PriorityQueue<String> stringQueue = new PriorityQueue<>(
            Comparator.comparing(String::length)
                     .thenComparing(String::compareTo)
        );
        
        String[] words = {"elephant", "cat", "dog", "butterfly", "ant", "zebra"};
        
        System.out.println("添加單詞:");
        for (String word : words) {
            stringQueue.offer(word);
            System.out.println("添加 '" + word + "': " + stringQueue);
        }
        
        System.out.println("\n按長度優先順序取出:");
        while (!stringQueue.isEmpty()) {
            String word = stringQueue.poll();
            System.out.println("'" + word + "' (長度: " + word.length() + ")");
        }
        
        System.out.println();
    }
    
    /**
     * 自定義物件排序示範
     */
    public static void demonstrateCustomObjectSorting() {
        System.out.println("=== 自定義物件排序 ===");
        
        // 任務優先級系統
        class Task {
            String name;
            int priority;
            int duration;
            
            Task(String name, int priority, int duration) {
                this.name = name;
                this.priority = priority;
                this.duration = duration;
            }
            
            @Override
            public String toString() {
                return String.format("%s(P%d,%dmin)", name, priority, duration);
            }
        }
        
        // 按優先級排序（數字越小優先級越高），優先級相同則按持續時間排序
        PriorityQueue<Task> taskQueue = new PriorityQueue<>(
            Comparator.comparing((Task t) -> t.priority)
                     .thenComparing(t -> t.duration)
        );
        
        // 添加任務
        taskQueue.offer(new Task("資料備份", 3, 45));
        taskQueue.offer(new Task("系統更新", 1, 30));
        taskQueue.offer(new Task("日誌清理", 2, 15));
        taskQueue.offer(new Task("監控檢查", 1, 10));
        taskQueue.offer(new Task("報告生成", 2, 20));
        
        System.out.println("任務佇列: " + taskQueue);
        
        System.out.println("\n按優先級執行任務:");
        while (!taskQueue.isEmpty()) {
            Task task = taskQueue.poll();
            System.out.printf("執行: %s（優先級 %d，需時 %d 分鐘）\n", 
                task.name, task.priority, task.duration);
        }
        
        System.out.println();
    }
    
    /**
     * 堆積操作分析
     */
    public static void demonstrateHeapOperations() {
        System.out.println("3. 堆積操作分析:\n");
        
        // 內部結構展示
        demonstrateInternalStructure();
        
        // 時間複雜度測試
        demonstrateTimeComplexity();
    }
    
    /**
     * 內部結構展示
     */
    public static void demonstrateInternalStructure() {
        System.out.println("=== 堆積內部結構 ===");
        
        PriorityQueue<Integer> heap = new PriorityQueue<>();
        
        System.out.println("PriorityQueue 基於二元堆積實現：");
        System.out.println("- 完全二元樹結構");
        System.out.println("- 父節點 ≤ 子節點（最小堆積）");
        System.out.println("- 使用陣列存儲");
        
        int[] values = {20, 15, 8, 10, 5, 7, 6, 2, 9, 1};
        
        System.out.println("\n逐步添加元素觀察堆積化過程:");
        for (int value : values) {
            heap.offer(value);
            System.out.println("添加 " + value + ": " + heap);
        }
        
        System.out.println("\n堆積性質驗證:");
        System.out.println("最小元素總是在頂部: " + heap.peek());
        
        // 移除元素觀察重新堆積化
        System.out.println("\n移除元素觀察重新堆積化:");
        for (int i = 0; i < 3; i++) {
            int removed = heap.poll();
            System.out.println("移除 " + removed + ": " + heap);
        }
        
        System.out.println();
    }
    
    /**
     * 時間複雜度測試
     */
    public static void demonstrateTimeComplexity() {
        System.out.println("=== 時間複雜度實測 ===");
        
        int[] testSizes = {1000, 10000, 100000};
        
        System.out.println("操作時間複雜度理論值:");
        System.out.println("- offer(): O(log n)");
        System.out.println("- poll(): O(log n)");
        System.out.println("- peek(): O(1)");
        System.out.println("- remove(object): O(n)");
        System.out.println("- contains(object): O(n)");
        
        System.out.println("\n實際測試結果:");
        
        for (int size : testSizes) {
            testOperationPerformance(size);
        }
        
        System.out.println();
    }
    
    /**
     * 單次效能測試
     */
    public static void testOperationPerformance(int size) {
        PriorityQueue<Integer> heap = new PriorityQueue<>();
        Random random = new Random(42); // 固定種子確保一致性
        
        // 填充堆積
        for (int i = 0; i < size; i++) {
            heap.offer(random.nextInt(size));
        }
        
        // 測試 offer 操作
        long startTime = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            heap.offer(random.nextInt(size));
            heap.poll(); // 保持大小穩定
        }
        long offerTime = System.nanoTime() - startTime;
        
        // 測試 peek 操作
        startTime = System.nanoTime();
        for (int i = 0; i < 10000; i++) {
            heap.peek();
        }
        long peekTime = System.nanoTime() - startTime;
        
        // 測試 contains 操作
        startTime = System.nanoTime();
        for (int i = 0; i < 100; i++) {
            heap.contains(random.nextInt(size));
        }
        long containsTime = System.nanoTime() - startTime;
        
        System.out.printf("大小 %,d: offer %.2fμs, peek %.2fμs, contains %.2fμs\n",
            size,
            offerTime / 1000.0 / 1000,
            peekTime / 10000.0 / 1000,
            containsTime / 100.0 / 1000);
    }
    
    /**
     * 效能分析
     */
    public static void performanceAnalysis() {
        System.out.println("4. 效能分析:\n");
        
        // 與其他資料結構比較
        compareWithOtherDataStructures();
        
        // 記憶體使用分析
        analyzeMemoryUsage();
    }
    
    /**
     * 與其他資料結構比較
     */
    public static void compareWithOtherDataStructures() {
        System.out.println("=== 與其他資料結構比較 ===");
        
        int testSize = 50000;
        Random random = new Random(42);
        
        // 準備測試資料
        List<Integer> testData = new ArrayList<>();
        for (int i = 0; i < testSize; i++) {
            testData.add(random.nextInt(testSize));
        }
        
        System.out.println("測試項目：插入 " + String.format("%,d", testSize) + " 個元素後取出最小的 1000 個");
        
        // PriorityQueue 測試
        long startTime = System.nanoTime();
        PriorityQueue<Integer> pq = new PriorityQueue<>(testData);
        List<Integer> pqResult = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            pqResult.add(pq.poll());
        }
        long pqTime = System.nanoTime() - startTime;
        
        // ArrayList + Sort 測試
        startTime = System.nanoTime();
        List<Integer> list = new ArrayList<>(testData);
        Collections.sort(list);
        List<Integer> listResult = list.subList(0, 1000);
        long listTime = System.nanoTime() - startTime;
        
        // TreeSet 測試
        startTime = System.nanoTime();
        TreeSet<Integer> treeSet = new TreeSet<>(testData);
        List<Integer> treeResult = new ArrayList<>();
        Iterator<Integer> it = treeSet.iterator();
        for (int i = 0; i < 1000 && it.hasNext(); i++) {
            treeResult.add(it.next());
        }
        long treeTime = System.nanoTime() - startTime;
        
        System.out.printf("PriorityQueue: %.2f ms\n", pqTime / 1_000_000.0);
        System.out.printf("ArrayList+Sort: %.2f ms\n", listTime / 1_000_000.0);
        System.out.printf("TreeSet: %.2f ms\n", treeTime / 1_000_000.0);
        
        // 驗證結果一致性
        boolean resultsEqual = pqResult.equals(listResult) && listResult.equals(treeResult);
        System.out.println("結果一致性: " + (resultsEqual ? "✓" : "✗"));
        
        System.out.println("\n結論:");
        System.out.println("- PriorityQueue: 適合需要逐步獲取排序元素的場景");
        System.out.println("- ArrayList+Sort: 適合一次性獲取全部排序結果");
        System.out.println("- TreeSet: 適合需要去重且維護排序的場景");
        
        System.out.println();
    }
    
    /**
     * 記憶體使用分析
     */
    public static void analyzeMemoryUsage() {
        System.out.println("=== 記憶體使用分析 ===");
        
        int elementCount = 100000;
        
        Runtime runtime = Runtime.getRuntime();
        System.gc();
        long baseMem = runtime.totalMemory() - runtime.freeMemory();
        
        PriorityQueue<Integer> heap = new PriorityQueue<>();
        for (int i = 0; i < elementCount; i++) {
            heap.offer(i);
        }
        
        System.gc();
        long heapMem = runtime.totalMemory() - runtime.freeMemory() - baseMem;
        
        System.out.printf("PriorityQueue (%,d 元素): ~%.2f MB\n", 
            elementCount, heapMem / (1024.0 * 1024.0));
        
        System.out.println("\nPriorityQueue 記憶體特性:");
        System.out.println("- 緊湊的陣列存儲");
        System.out.println("- 動態擴容（類似 ArrayList）");
        System.out.println("- 無額外的指標開銷");
        System.out.println("- 記憶體局部性好，快取友善");
        
        System.out.println();
    }
    
    /**
     * 實際應用範例
     */
    public static void demonstratePracticalApplications() {
        System.out.println("5. 實際應用範例:\n");
        
        // 應用1: Dijkstra 最短路徑演算法
        demonstrateDijkstraAlgorithm();
        
        // 應用2: 任務調度系統
        demonstrateTaskScheduler();
        
        // 應用3: Top K 問題
        demonstrateTopKProblem();
        
        // 應用4: 合併 K 個排序陣列
        demonstrateMergeKSortedArrays();
    }
    
    /**
     * Dijkstra 最短路徑演算法示範
     */
    public static void demonstrateDijkstraAlgorithm() {
        System.out.println("應用1: Dijkstra 最短路徑演算法\n");
        
        class Edge {
            int to;
            int weight;
            
            Edge(int to, int weight) {
                this.to = to;
                this.weight = weight;
            }
        }
        
        class Node {
            int id;
            int distance;
            
            Node(int id, int distance) {
                this.id = id;
                this.distance = distance;
            }
        }
        
        // 簡化的圖結構
        Map<Integer, List<Edge>> graph = new HashMap<>();
        graph.put(0, Arrays.asList(new Edge(1, 4), new Edge(2, 2)));
        graph.put(1, Arrays.asList(new Edge(2, 1), new Edge(3, 5)));
        graph.put(2, Arrays.asList(new Edge(3, 8), new Edge(4, 10)));
        graph.put(3, Arrays.asList(new Edge(4, 2)));
        graph.put(4, Arrays.asList());
        
        int start = 0;
        int[] distances = new int[5];
        Arrays.fill(distances, Integer.MAX_VALUE);
        distances[start] = 0;
        
        PriorityQueue<Node> pq = new PriorityQueue<>(
            Comparator.comparing(node -> node.distance)
        );
        pq.offer(new Node(start, 0));
        
        System.out.println("圖結構 (節點 -> [(鄰居, 權重), ...]):");
        graph.forEach((node, edges) -> {
            System.out.println("  " + node + " -> " + 
                edges.stream()
                     .map(e -> "(" + e.to + "," + e.weight + ")")
                     .collect(Collectors.joining(", ")));
        });
        
        System.out.println("\nDijkstra 演算法執行過程:");
        
        while (!pq.isEmpty()) {
            Node current = pq.poll();
            
            if (current.distance > distances[current.id]) {
                continue; // 已找到更短路徑
            }
            
            System.out.println("處理節點 " + current.id + "，距離: " + current.distance);
            
            for (Edge edge : graph.get(current.id)) {
                int newDistance = distances[current.id] + edge.weight;
                
                if (newDistance < distances[edge.to]) {
                    distances[edge.to] = newDistance;
                    pq.offer(new Node(edge.to, newDistance));
                    System.out.println("  更新到節點 " + edge.to + " 的距離: " + newDistance);
                }
            }
        }
        
        System.out.println("\n從節點 " + start + " 到各節點的最短距離:");
        for (int i = 0; i < distances.length; i++) {
            System.out.println("  到節點 " + i + ": " + 
                (distances[i] == Integer.MAX_VALUE ? "無法到達" : distances[i]));
        }
        
        System.out.println();
    }
    
    /**
     * 任務調度系統示範
     */
    public static void demonstrateTaskScheduler() {
        System.out.println("應用2: 任務調度系統\n");
        
        class ScheduledTask {
            String name;
            long executeTime;
            int priority;
            
            ScheduledTask(String name, long delay, int priority) {
                this.name = name;
                this.executeTime = System.currentTimeMillis() + delay;
                this.priority = priority;
            }
            
            boolean isReady() {
                return System.currentTimeMillis() >= executeTime;
            }
            
            @Override
            public String toString() {
                long remaining = Math.max(0, executeTime - System.currentTimeMillis());
                return String.format("%s(P%d, %dms後執行)", name, priority, remaining);
            }
        }
        
        class TaskScheduler {
            private PriorityQueue<ScheduledTask> taskQueue = new PriorityQueue<>(
                Comparator.comparing((ScheduledTask t) -> t.executeTime)
                         .thenComparing(t -> t.priority)
            );
            
            void schedule(ScheduledTask task) {
                taskQueue.offer(task);
                System.out.println("調度任務: " + task);
            }
            
            void executeReadyTasks() {
                System.out.println("\n檢查並執行就緒任務:");
                
                while (!taskQueue.isEmpty() && taskQueue.peek().isReady()) {
                    ScheduledTask task = taskQueue.poll();
                    System.out.println("執行: " + task.name);
                }
                
                if (!taskQueue.isEmpty()) {
                    System.out.println("下一個任務: " + taskQueue.peek());
                } else {
                    System.out.println("無待執行任務");
                }
            }
            
            void showPendingTasks() {
                System.out.println("待執行任務 (" + taskQueue.size() + " 個):");
                taskQueue.forEach(task -> System.out.println("  " + task));
            }
        }
        
        TaskScheduler scheduler = new TaskScheduler();
        
        // 調度任務
        scheduler.schedule(new ScheduledTask("系統備份", 3000, 2));
        scheduler.schedule(new ScheduledTask("發送通知", 1000, 1));
        scheduler.schedule(new ScheduledTask("清理快取", 2000, 3));
        scheduler.schedule(new ScheduledTask("更新統計", 1500, 1));
        
        scheduler.showPendingTasks();
        
        // 模擬時間推移
        try {
            Thread.sleep(1100);
            scheduler.executeReadyTasks();
            
            Thread.sleep(600);
            scheduler.executeReadyTasks();
            
            Thread.sleep(1400);
            scheduler.executeReadyTasks();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        System.out.println();
    }
    
    /**
     * Top K 問題示範
     */
    public static void demonstrateTopKProblem() {
        System.out.println("應用3: Top K 問題\n");
        
        // 範例：找出陣列中前 K 大的元素
        int[] nums = {3, 1, 4, 1, 5, 9, 2, 6, 5, 3, 5};
        int k = 3;
        
        System.out.println("陣列: " + Arrays.toString(nums));
        System.out.println("找出前 " + k + " 大的元素");
        
        // 方法1：使用最小堆積
        PriorityQueue<Integer> minHeap = new PriorityQueue<>();
        
        for (int num : nums) {
            minHeap.offer(num);
            if (minHeap.size() > k) {
                minHeap.poll();
            }
        }
        
        List<Integer> topK = new ArrayList<>(minHeap);
        Collections.sort(topK, Collections.reverseOrder());
        
        System.out.println("前 " + k + " 大元素: " + topK);
        
        // 方法2：頻率統計 + 優先佇列
        demonstrateTopKFrequent();
        
        System.out.println();
    }
    
    /**
     * Top K 頻率元素
     */
    public static void demonstrateTopKFrequent() {
        System.out.println("\n=== Top K 頻率元素 ===");
        
        String text = "apple banana apple orange apple banana grape apple";
        String[] words = text.split(" ");
        int k = 2;
        
        System.out.println("文本: " + text);
        System.out.println("找出前 " + k + " 高頻單詞");
        
        // 統計頻率
        Map<String, Integer> frequency = new HashMap<>();
        for (String word : words) {
            frequency.merge(word, 1, Integer::sum);
        }
        
        System.out.println("頻率統計: " + frequency);
        
        // 使用優先佇列找 Top K
        PriorityQueue<Map.Entry<String, Integer>> pq = new PriorityQueue<>(
            Comparator.comparing((Map.Entry<String, Integer> e) -> e.getValue())
                     .thenComparing(e -> e.getKey(), Collections.reverseOrder())
        );
        
        for (Map.Entry<String, Integer> entry : frequency.entrySet()) {
            pq.offer(entry);
            if (pq.size() > k) {
                pq.poll();
            }
        }
        
        List<String> result = new ArrayList<>();
        while (!pq.isEmpty()) {
            result.add(0, pq.poll().getKey()); // 插入到開頭以得到降序
        }
        
        System.out.println("前 " + k + " 高頻單詞: " + result);
    }
    
    /**
     * 合併 K 個排序陣列示範
     */
    public static void demonstrateMergeKSortedArrays() {
        System.out.println("應用4: 合併 K 個排序陣列\n");
        
        class ArrayElement {
            int value;
            int arrayIndex;
            int elementIndex;
            
            ArrayElement(int value, int arrayIndex, int elementIndex) {
                this.value = value;
                this.arrayIndex = arrayIndex;
                this.elementIndex = elementIndex;
            }
        }
        
        int[][] arrays = {
            {1, 4, 7, 10},
            {2, 5, 8, 11},
            {3, 6, 9, 12}
        };
        
        System.out.println("要合併的排序陣列:");
        for (int i = 0; i < arrays.length; i++) {
            System.out.println("  陣列 " + i + ": " + Arrays.toString(arrays[i]));
        }
        
        // 使用優先佇列合併
        PriorityQueue<ArrayElement> pq = new PriorityQueue<>(
            Comparator.comparing(elem -> elem.value)
        );
        
        // 初始化：將每個陣列的第一個元素加入優先佇列
        for (int i = 0; i < arrays.length; i++) {
            if (arrays[i].length > 0) {
                pq.offer(new ArrayElement(arrays[i][0], i, 0));
            }
        }
        
        List<Integer> merged = new ArrayList<>();
        
        System.out.println("\n合併過程:");
        while (!pq.isEmpty()) {
            ArrayElement current = pq.poll();
            merged.add(current.value);
            
            System.out.printf("取出: %d (來自陣列 %d)\n", 
                current.value, current.arrayIndex);
            
            // 如果該陣列還有下一個元素，加入優先佇列
            if (current.elementIndex + 1 < arrays[current.arrayIndex].length) {
                int nextValue = arrays[current.arrayIndex][current.elementIndex + 1];
                pq.offer(new ArrayElement(nextValue, current.arrayIndex, current.elementIndex + 1));
                System.out.printf("  添加: %d (陣列 %d 的下一個元素)\n", 
                    nextValue, current.arrayIndex);
            }
        }
        
        System.out.println("\n合併結果: " + merged);
        
        // 驗證結果是否排序
        boolean isSorted = true;
        for (int i = 1; i < merged.size(); i++) {
            if (merged.get(i) < merged.get(i - 1)) {
                isSorted = false;
                break;
            }
        }
        
        System.out.println("結果驗證: " + (isSorted ? "✓ 正確排序" : "✗ 排序錯誤"));
        
        System.out.println();
    }
}