# Queue/Deque 練習題

## 學習目標

通過這些練習，你將會掌握：
- Queue 和 Deque 介面的使用和特性
- 不同佇列實現的選擇和應用場景
- 佇列在演算法和系統設計中的重要作用
- 生產者-消費者模式和並發程式設計

---

## 🟢 基礎練習 (Easy)

### 練習 1.1：Queue 基本操作和特性

**題目描述：**
創建一個程式，演示不同 Queue 實現的基本操作和特性：
1. LinkedList, ArrayDeque, PriorityQueue 的基本使用
2. 比較不同實現的性能特點
3. 演示 FIFO 和優先順序的區別

**要求：**
- 測試 offer(), poll(), peek() 等操作
- 比較不同實現在空佇列時的行為
- 觀察 PriorityQueue 的排序特性

**測試案例：**
```java
Queue<Integer> linkedQueue = new LinkedList<>();
Queue<Integer> arrayQueue = new ArrayDeque<>();
Queue<Integer> priorityQueue = new PriorityQueue<>();

int[] numbers = {5, 2, 8, 1, 9, 3};
// 測試不同佇列的行為
```

<details>
<summary>💡 思路提示</summary>

```java
public static void demonstrateQueueBehavior() {
    Queue<Integer> linkedQueue = new LinkedList<>();
    Queue<Integer> arrayQueue = new ArrayDeque<>();
    Queue<Integer> priorityQueue = new PriorityQueue<>();
    
    int[] numbers = {5, 2, 8, 1, 9, 3};
    
    // 添加相同的數據到不同佇列
    for (int num : numbers) {
        linkedQueue.offer(num);
        arrayQueue.offer(num);
        priorityQueue.offer(num);
    }
    
    // 比較輸出順序
    System.out.println("LinkedList: ");
    while (!linkedQueue.isEmpty()) {
        System.out.print(linkedQueue.poll() + " ");
    }
    // 輸出: 5 2 8 1 9 3 (FIFO)
    
    System.out.println("\nPriorityQueue: ");
    while (!priorityQueue.isEmpty()) {
        System.out.print(priorityQueue.poll() + " ");
    }
    // 輸出: 1 2 3 5 8 9 (排序)
}
```
</details>

---

### 練習 1.2：使用 Deque 實現棧和佇列

**題目描述：**
使用 Deque 同時實現棧 (Stack) 和佇列 (Queue) 的功能，演示雙端佇列的靈活性。

**要求實現的方法：**
```java
public class StackQueueDemo {
    private Deque<Integer> deque;
    
    public StackQueueDemo() {
        this.deque = new ArrayDeque<>();
    }
    
    // 棧操作 (LIFO)
    public void push(int value) {
        // 實現入棧
    }
    
    public int pop() {
        // 實現出棧
    }
    
    public int peekStack() {
        // 查看棧頂
    }
    
    // 佇列操作 (FIFO)
    public void enqueue(int value) {
        // 實現入隊
    }
    
    public int dequeue() {
        // 實現出隊
    }
    
    public int peekQueue() {
        // 查看隊首
    }
    
    public boolean isEmpty() {
        return deque.isEmpty();
    }
}
```

**測試案例：**
```java
StackQueueDemo demo = new StackQueueDemo();

// 測試棧功能
demo.push(1);
demo.push(2);
demo.push(3);
System.out.println("Stack pop: " + demo.pop());  // 3

// 測試佇列功能  
demo.enqueue(4);
demo.enqueue(5);
System.out.println("Queue dequeue: " + demo.dequeue());  // 4
```

<details>
<summary>🔧 技術提示</summary>

```java
// 棧操作使用 Deque 的一端
public void push(int value) {
    deque.addFirst(value);  // 或 deque.offerFirst(value)
}

public int pop() {
    return deque.removeFirst();  // 或 deque.pollFirst()
}

// 佇列操作使用 Deque 的兩端
public void enqueue(int value) {
    deque.addLast(value);   // 或 deque.offerLast(value)
}

public int dequeue() {
    return deque.removeFirst();  // 或 deque.pollFirst()
}
```
</details>

---

### 練習 1.3：熱土豆問題 (約瑟夫問題)

**題目描述：**
使用 Queue 解決經典的約瑟夫問題：N 個人圍成一圈，從第一個人開始報數，每報到 M 就出局一人，求最後剩餘的人。

**方法簽名：**
```java
public static int josephusProblem(int n, int m)
public static List<Integer> josephusSequence(int n, int m)  // 返回出局順序
```

**測試案例：**
```java
int n = 7, m = 3;
int survivor = josephusProblem(n, m);
System.out.println("最後倖存者: " + survivor);

List<Integer> sequence = josephusSequence(n, m);
System.out.println("出局順序: " + sequence);
```

<details>
<summary>💡 思路提示</summary>

```java
public static int josephusProblem(int n, int m) {
    Queue<Integer> queue = new LinkedList<>();
    
    // 初始化佇列
    for (int i = 1; i <= n; i++) {
        queue.offer(i);
    }
    
    while (queue.size() > 1) {
        // 前 m-1 個人重新入隊
        for (int i = 0; i < m - 1; i++) {
            queue.offer(queue.poll());
        }
        // 第 m 個人出局
        queue.poll();
    }
    
    return queue.poll();
}
```
</details>

---

## 🟡 中等練習 (Medium)

### 練習 2.1：實現滑動視窗最大值

**題目描述：**
給定一個陣列和一個滑動視窗大小，找出每個滑動視窗中的最大值。要求使用 Deque 實現 O(n) 時間複雜度。

**方法簽名：**
```java
public static int[] maxSlidingWindow(int[] nums, int k)
```

**測試案例：**
```java
int[] nums = {1, 3, -1, -3, 5, 3, 6, 7};
int k = 3;
int[] result = maxSlidingWindow(nums, k);
// 結果: [3, 3, 5, 5, 6, 7]
```

**演算法要求：**
- 時間複雜度：O(n)
- 空間複雜度：O(k)
- 使用 Deque 維護可能成為最大值的候選者

<details>
<summary>💡 思路提示</summary>

```java
public static int[] maxSlidingWindow(int[] nums, int k) {
    if (nums == null || nums.length == 0 || k <= 0) return new int[0];
    
    Deque<Integer> deque = new ArrayDeque<>();  // 存儲索引
    int[] result = new int[nums.length - k + 1];
    
    for (int i = 0; i < nums.length; i++) {
        // 移除超出視窗範圍的索引
        while (!deque.isEmpty() && deque.peekFirst() < i - k + 1) {
            deque.pollFirst();
        }
        
        // 移除比當前元素小的索引（因為它們不可能成為最大值）
        while (!deque.isEmpty() && nums[deque.peekLast()] < nums[i]) {
            deque.pollLast();
        }
        
        deque.offerLast(i);
        
        // 當視窗大小達到 k 時，記錄最大值
        if (i >= k - 1) {
            result[i - k + 1] = nums[deque.peekFirst()];
        }
    }
    
    return result;
}
```
</details>

---

### 練習 2.2：實現任務調度器

**題目描述：**
實現一個任務調度器，支援不同優先級的任務和延遲執行。

**要求：**
- 支援立即執行和延遲執行的任務
- 高優先級任務優先執行
- 相同優先級按照加入順序執行

**類別設計：**
```java
class Task {
    private String id;
    private int priority;
    private long executeTime;  // 執行時間（毫秒）
    private Runnable action;
    
    public Task(String id, int priority, long delay, Runnable action) {
        this.id = id;
        this.priority = priority;
        this.executeTime = System.currentTimeMillis() + delay;
        this.action = action;
    }
    
    // getter 方法和 compareTo 實現
}

public class TaskScheduler {
    private PriorityQueue<Task> taskQueue;
    private volatile boolean running;
    
    public TaskScheduler() {
        // 實現建構函數
    }
    
    public void scheduleTask(Task task) {
        // 添加任務
    }
    
    public void start() {
        // 開始調度器
    }
    
    public void stop() {
        // 停止調度器
    }
    
    private void executeTask(Task task) {
        // 執行任務
    }
}
```

**測試案例：**
```java
TaskScheduler scheduler = new TaskScheduler();

scheduler.scheduleTask(new Task("task1", 1, 0, () -> System.out.println("執行任務1")));
scheduler.scheduleTask(new Task("task2", 2, 1000, () -> System.out.println("執行任務2")));
scheduler.scheduleTask(new Task("task3", 1, 500, () -> System.out.println("執行任務3")));

scheduler.start();
```

<details>
<summary>🔧 技術提示</summary>

```java
class Task implements Comparable<Task> {
    @Override
    public int compareTo(Task other) {
        // 首先按執行時間排序
        if (this.executeTime != other.executeTime) {
            return Long.compare(this.executeTime, other.executeTime);
        }
        // 然後按優先級排序（數字越大優先級越高）
        return Integer.compare(other.priority, this.priority);
    }
}
```
</details>

---

### 練習 2.3：實現 LRU 快取（使用 Deque）

**題目描述：**
使用 Deque + HashMap 實現一個 LRU 快取，不使用 LinkedHashMap。

**要求：**
- get 和 put 操作都是 O(1) 時間複雜度
- 達到容量上限時移除最久未使用的項目
- 支援更新現有鍵的值

**類別設計：**
```java
class LRUCache<K, V> {
    private final int capacity;
    private final Deque<Node<K, V>> deque;
    private final Map<K, Node<K, V>> map;
    
    static class Node<K, V> {
        K key;
        V value;
        
        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }
    
    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.deque = new ArrayDeque<>();
        this.map = new HashMap<>();
    }
    
    public V get(K key) {
        // 實現獲取邏輯
    }
    
    public void put(K key, V value) {
        // 實現插入邏輯
    }
    
    private void moveToFront(Node<K, V> node) {
        // 移動節點到前端
    }
}
```

**挑戰：**
如何在 O(1) 時間內從 Deque 中移除任意節點？

<details>
<summary>🔧 技術提示</summary>

由於 ArrayDeque 無法在 O(1) 時間內移除中間節點，這裡需要使用自定義的雙向鏈表或者使用 LinkedHashMap。如果堅持使用 Deque，可以標記節點為已刪除而不是物理移除。
</details>

---

### 練習 2.4：實現生產者-消費者模式

**題目描述：**
使用 BlockingQueue 實現經典的生產者-消費者模式，支援多執行緒併發。

**要求：**
- 支援多個生產者和消費者
- 實現優雅的關閉機制
- 統計生產和消費的數量

**類別設計：**
```java
class ProducerConsumerDemo {
    private final BlockingQueue<String> queue;
    private final AtomicInteger producedCount;
    private final AtomicInteger consumedCount;
    private volatile boolean shutdown;
    
    public ProducerConsumerDemo(int capacity) {
        this.queue = new ArrayBlockingQueue<>(capacity);
        this.producedCount = new AtomicInteger(0);
        this.consumedCount = new AtomicInteger(0);
        this.shutdown = false;
    }
    
    class Producer implements Runnable {
        private final String producerId;
        
        public Producer(String producerId) {
            this.producerId = producerId;
        }
        
        @Override
        public void run() {
            // 實現生產者邏輯
        }
    }
    
    class Consumer implements Runnable {
        private final String consumerId;
        
        public Consumer(String consumerId) {
            this.consumerId = consumerId;
        }
        
        @Override
        public void run() {
            // 實現消費者邏輯
        }
    }
    
    public void shutdown() {
        // 實現優雅關閉
    }
}
```

<details>
<summary>💡 思路提示</summary>

```java
@Override
public void run() {
    while (!shutdown) {
        try {
            String item = "Item-" + producedCount.incrementAndGet();
            queue.put(item);  // 阻塞式插入
            System.out.println(producerId + " produced: " + item);
            Thread.sleep(100);  // 模擬生產時間
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            break;
        }
    }
}
```
</details>

---

## 🔴 困難練習 (Hard)

### 練習 3.1：實現優先級佇列

**題目描述：**
從零開始實現一個優先級佇列，使用二元堆 (Binary Heap) 資料結構。

**要求：**
- 支援自定義比較器
- 實現 offer(), poll(), peek() 操作
- 支援動態擴容
- 時間複雜度：插入和刪除都是 O(log n)

**類別設計：**
```java
public class MyPriorityQueue<E> {
    private Object[] heap;
    private int size;
    private final Comparator<? super E> comparator;
    
    public MyPriorityQueue() {
        this(11, null);
    }
    
    public MyPriorityQueue(Comparator<? super E> comparator) {
        this(11, comparator);
    }
    
    public MyPriorityQueue(int initialCapacity, Comparator<? super E> comparator) {
        this.heap = new Object[initialCapacity];
        this.size = 0;
        this.comparator = comparator;
    }
    
    public boolean offer(E e) {
        // 實現插入邏輯
    }
    
    public E poll() {
        // 實現刪除最小/大元素
    }
    
    public E peek() {
        return size == 0 ? null : (E) heap[0];
    }
    
    private void heapifyUp(int index) {
        // 上浮操作
    }
    
    private void heapifyDown(int index) {
        // 下沉操作
    }
    
    private void grow() {
        // 動態擴容
    }
}
```

<details>
<summary>🔧 技術提示</summary>

```java
private void heapifyUp(int index) {
    while (index > 0) {
        int parentIndex = (index - 1) / 2;
        if (compare((E) heap[index], (E) heap[parentIndex]) >= 0) {
            break;
        }
        swap(index, parentIndex);
        index = parentIndex;
    }
}

private void heapifyDown(int index) {
    while (index < size / 2) {  // 有子節點
        int minChildIndex = 2 * index + 1;
        if (minChildIndex + 1 < size && 
            compare((E) heap[minChildIndex + 1], (E) heap[minChildIndex]) < 0) {
            minChildIndex++;
        }
        
        if (compare((E) heap[index], (E) heap[minChildIndex]) <= 0) {
            break;
        }
        
        swap(index, minChildIndex);
        index = minChildIndex;
    }
}
```
</details>

---

### 練習 3.2：實現 K 路歸併

**題目描述：**
使用優先級佇列實現 K 路歸併演算法，合併 K 個已排序的陣列。

**方法簽名：**
```java
public static List<Integer> mergeKSortedArrays(List<int[]> arrays)
```

**進階要求：**
- 實現外部排序（處理超大檔案）
- 支援自定義比較器
- 優化記憶體使用

**測試案例：**
```java
List<int[]> arrays = Arrays.asList(
    new int[]{1, 4, 5},
    new int[]{1, 3, 4},
    new int[]{2, 6}
);
List<Integer> result = mergeKSortedArrays(arrays);
// 結果: [1, 1, 2, 3, 4, 4, 5, 6]
```

<details>
<summary>💡 思路提示</summary>

```java
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

public static List<Integer> mergeKSortedArrays(List<int[]> arrays) {
    PriorityQueue<ArrayElement> pq = new PriorityQueue<>(
        Comparator.comparingInt(a -> a.value)
    );
    
    // 初始化：將每個陣列的第一個元素加入優先級佇列
    for (int i = 0; i < arrays.size(); i++) {
        if (arrays.get(i).length > 0) {
            pq.offer(new ArrayElement(arrays.get(i)[0], i, 0));
        }
    }
    
    List<Integer> result = new ArrayList<>();
    while (!pq.isEmpty()) {
        ArrayElement current = pq.poll();
        result.add(current.value);
        
        // 如果當前陣列還有下一個元素，加入佇列
        if (current.elementIndex + 1 < arrays.get(current.arrayIndex).length) {
            int nextValue = arrays.get(current.arrayIndex)[current.elementIndex + 1];
            pq.offer(new ArrayElement(nextValue, current.arrayIndex, current.elementIndex + 1));
        }
    }
    
    return result;
}
```
</details>

---

### 練習 3.3：實現事件驅動模擬器

**題目描述：**
實現一個事件驅動的模擬器，用於模擬系統中的事件處理。

**要求：**
- 支援不同類型的事件
- 按時間順序處理事件
- 支援事件之間的依賴關係
- 統計和監控功能

**類別設計：**
```java
abstract class Event {
    protected final long timestamp;
    protected final String eventId;
    
    public Event(long timestamp, String eventId) {
        this.timestamp = timestamp;
        this.eventId = eventId;
    }
    
    public abstract void process(EventSimulator simulator);
    
    // getter 方法和 compareTo 實現
}

class CustomerArrivalEvent extends Event {
    private final String customerId;
    
    @Override
    public void process(EventSimulator simulator) {
        // 處理客戶到達事件
        System.out.println("Customer " + customerId + " arrived at " + timestamp);
        
        // 可能產生新的事件（如服務開始事件）
        long serviceTime = simulator.getServiceTime();
        simulator.scheduleEvent(
            new ServiceStartEvent(timestamp + serviceTime, customerId)
        );
    }
}

public class EventSimulator {
    private final PriorityQueue<Event> eventQueue;
    private long currentTime;
    private final Map<String, Object> statistics;
    
    public void scheduleEvent(Event event) {
        eventQueue.offer(event);
    }
    
    public void run() {
        while (!eventQueue.isEmpty()) {
            Event event = eventQueue.poll();
            currentTime = event.timestamp;
            event.process(this);
        }
    }
}
```

---

## 🧠 進階挑戰

### 挑戰 4.1：實現分散式任務佇列

**題目描述：**
實現一個分散式任務佇列系統，支援多節點間的任務分發和容錯。

**要求：**
- 任務分片和負載均衡
- 失敗重試機制
- 死信佇列處理
- 優先級和延遲任務支援

**基本架構：**
```java
public interface DistributedTaskQueue {
    void submitTask(Task task);
    Task pollTask();
    void completeTask(String taskId);
    void retryTask(String taskId);
    void moveToDeadLetter(String taskId);
    
    void addWorkerNode(String nodeId);
    void removeWorkerNode(String nodeId);
    
    Map<String, Object> getQueueStats();
}
```

---

### 挑戰 4.2：實現流量控制器

**題目描述：**
實現一個基於令牌桶演算法的流量控制器。

**要求：**
- 支援突發流量處理
- 可配置的令牌產生速率
- 多層級流量控制
- 統計和監控介面

**類別設計：**
```java
public class TokenBucketRateLimiter {
    private final long capacity;
    private final long refillRate;
    private long tokens;
    private long lastRefillTime;
    
    public boolean tryConsume(long tokensRequested) {
        // 實現令牌消費邏輯
    }
    
    private void refill() {
        // 實現令牌補充邏輯
    }
}
```

---

## 📊 實戰專案

### 專案：線上遊戲匹配系統

**專案描述：**
實現一個線上遊戲的匹配系統，使用佇列管理等待的玩家。

**功能要求：**

1. **玩家匹配**
   ```java
   class Player {
       String id;
       int skillLevel;
       long waitTime;
       Set<String> preferences;
   }
   
   class MatchmakingQueue {
       PriorityQueue<Player> waitingPlayers;
       
       public void addPlayer(Player player) {
           // 添加玩家到佇列
       }
       
       public List<Player> findMatch(int gameSize) {
           // 找到合適的玩家組合
       }
   }
   ```

2. **技能等級匹配**
   - 基於技能等級的匹配演算法
   - 等待時間補償機制
   - 地理位置考慮

3. **遊戲房間管理**
   ```java
   class GameRoom {
       String roomId;
       List<Player> players;
       GameState state;
       Queue<GameEvent> eventQueue;
   }
   ```

4. **效能監控**
   - 平均等待時間
   - 匹配成功率
   - 玩家滿意度

---

### 專案：分散式日誌處理系統

**專案描述：**
實現一個高效能的分散式日誌收集和處理系統。

**架構組件：**

1. **日誌收集器**
   ```java
   class LogCollector {
       BlockingQueue<LogEntry> buffer;
       
       public void collectLog(LogEntry entry) {
           // 收集日誌項目
       }
   }
   ```

2. **日誌處理器**
   ```java
   class LogProcessor {
       PriorityQueue<LogEntry> processingQueue;
       
       public void processLogs() {
           // 按優先級處理日誌
       }
   }
   ```

3. **日誌聚合器**
   ```java
   class LogAggregator {
       Map<String, Queue<LogEntry>> topicQueues;
       
       public void aggregateByTopic(LogEntry entry) {
           // 按主題聚合日誌
       }
   }
   ```

---

## 📝 練習總結

完成這些練習後，你應該能夠：

1. ✅ **熟練使用 Queue/Deque**
   - 理解 FIFO 和優先級佇列的特性
   - 選擇合適的佇列實現
   - 掌握雙端佇列的靈活性

2. ✅ **掌握併發程式設計**
   - 生產者-消費者模式
   - 阻塞佇列的使用
   - 執行緒安全考慮

3. ✅ **應用演算法技巧**
   - 滑動視窗技術
   - K 路歸併演算法
   - 事件驅動程式設計

4. ✅ **系統設計能力**
   - 任務調度系統
   - 流量控制機制
   - 分散式佇列架構

## 🎯 下一步

完成 Queue/Deque 練習後，可以繼續學習：
- **[綜合練習](06-comprehensive-exercises.md)** - 挑戰複雜的綜合問題
- **[解答與說明](solutions/)** - 查看詳細解答和最佳實踐
- **[效能調優](../performance/)** - 學習集合框架的效能優化

記住：**Queue 是流程控制的核心，並發程式設計的基礎**！ ⚡