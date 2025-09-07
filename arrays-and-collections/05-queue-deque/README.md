# Queue 與 Deque - 佇列與雙端佇列

## 學習目標

通過本節的學習，你將會掌握：

1. **Queue 介面的基本概念和特性**
2. **常用 Queue 實現類別的使用**
3. **Deque 雙端佇列的操作**
4. **PriorityQueue 優先佇列的應用**
5. **實際應用場景和最佳實踐**

## 內容概覽

```
05-queue-deque/
├── README.md              # 本文檔
├── QueueDemo.java         # Queue 基本操作示範
├── DequeDemo.java         # Deque 雙端佇列示範
├── PriorityQueueDemo.java # 優先佇列示範
└── QueueApplications.java # 實際應用範例
```

## 1. Queue 介面概述

Queue（佇列）是一種遵循 **FIFO（First In First Out）** 原則的集合類型。

### 1.1 基本特性

- **先進先出**：最先添加的元素最先被移除
- **有序性**：元素按添加順序排列
- **兩端操作**：一端添加（尾部），另一端移除（頭部）

### 1.2 核心方法

```java
// 添加操作
boolean add(E e)     // 添加元素，失敗時拋出異常
boolean offer(E e)   // 添加元素，失敗時返回 false

// 移除操作
E remove()           // 移除並返回頭部元素，空時拋出異常
E poll()             // 移除並返回頭部元素，空時返回 null

// 檢查操作
E element()          // 返回頭部元素但不移除，空時拋出異常
E peek()             // 返回頭部元素但不移除，空時返回 null
```

### 1.3 常用實現類別

| 實現類別 | 底層結構 | 執行緒安全 | 特點 |
|---------|---------|-----------|------|
| `LinkedList` | 雙向鏈表 | ❌ | 實現了 List 和 Deque 介面 |
| `ArrayDeque` | 動態陣列 | ❌ | 效能優秀的雙端佇列 |
| `PriorityQueue` | 堆積 | ❌ | 優先佇列，自動排序 |
| `ConcurrentLinkedQueue` | CAS 鏈表 | ✅ | 無界執行緒安全佇列 |
| `ArrayBlockingQueue` | 環形陣列 | ✅ | 有界阻塞佇列 |

## 2. Deque 介面概述

Deque（Double Ended Queue）是雙端佇列，支援在兩端進行插入和刪除操作。

### 2.1 基本特性

- **雙端操作**：可以在頭部和尾部進行插入、刪除
- **靈活性**：可以作為棧（Stack）或佇列（Queue）使用
- **繼承關係**：Deque 繼承了 Queue 介面

### 2.2 核心方法

```java
// 頭部操作
void addFirst(E e)     // 頭部添加，失敗時拋出異常
boolean offerFirst(E e) // 頭部添加，失敗時返回 false
E removeFirst()        // 移除頭部元素，空時拋出異常
E pollFirst()          // 移除頭部元素，空時返回 null
E getFirst()           // 獲取頭部元素，空時拋出異常
E peekFirst()          // 獲取頭部元素，空時返回 null

// 尾部操作
void addLast(E e)      // 尾部添加，失敗時拋出異常
boolean offerLast(E e) // 尾部添加，失敗時返回 false
E removeLast()         // 移除尾部元素，空時拋出異常
E pollLast()           // 移除尾部元素，空時返回 null
E getLast()            // 獲取尾部元素，空時拋出異常
E peekLast()           // 獲取尾部元素，空時返回 null

// 棧操作 (Stack-like)
void push(E e)         // 壓棧（相當於 addFirst）
E pop()               // 出棧（相當於 removeFirst）
```

## 3. PriorityQueue 優先佇列

PriorityQueue 是基於堆積實現的優先佇列，元素按照優先級順序處理。

### 3.1 基本特性

- **堆積結構**：使用二元堆積維護元素順序
- **自然排序**：預設使用元素的自然順序
- **自定義比較器**：可以提供 Comparator 自定義排序
- **無界**：容量會自動擴展

### 3.2 時間複雜度

| 操作 | 時間複雜度 | 說明 |
|------|-----------|------|
| `offer(e)` | O(log n) | 插入元素 |
| `poll()` | O(log n) | 移除並返回最小元素 |
| `peek()` | O(1) | 檢視最小元素 |
| `remove(o)` | O(n) | 移除指定元素 |
| `contains(o)` | O(n) | 檢查是否包含元素 |

## 4. 效能比較

### 4.1 基本操作效能

| 實現類別 | 插入 | 刪除 | 查找 | 空間複雜度 |
|---------|------|------|------|-----------|
| `LinkedList` | O(1) | O(1) | O(n) | O(n) |
| `ArrayDeque` | O(1)* | O(1) | O(n) | O(n) |
| `PriorityQueue` | O(log n) | O(log n) | O(n) | O(n) |

*平攤時間複雜度，偶爾需要 O(n) 進行陣列擴容

### 4.2 記憶體使用

- **LinkedList**：每個節點需要額外的指標空間
- **ArrayDeque**：緊湊的陣列存儲，記憶體效率高
- **PriorityQueue**：陣列存儲，無額外指標開銷

## 5. 選擇指南

### 5.1 使用場景選擇

| 場景 | 推薦實現 | 理由 |
|------|---------|------|
| 簡單佇列操作 | `ArrayDeque` | 效能最佳，記憶體高效 |
| 需要隨機訪問 | `LinkedList` | 實現了 List 介面 |
| 優先級處理 | `PriorityQueue` | 自動維護優先順序 |
| 執行緒安全 | `ConcurrentLinkedQueue` | 無鎖並發佇列 |
| 生產者-消費者 | `ArrayBlockingQueue` | 阻塞佇列，支援等待 |

### 5.2 最佳實踐

```java
// ✅ 推薦：使用介面類型聲明
Queue<String> queue = new ArrayDeque<>();
Deque<Integer> deque = new LinkedList<>();

// ❌ 不推薦：使用具體類型聲明
ArrayDeque<String> queue = new ArrayDeque<>();

// ✅ 推薦：適當的異常處理
if (!queue.isEmpty()) {
    String element = queue.poll();
    // 處理元素
}

// ✅ 推薦：使用增強 for 迴圈遍歷
for (String item : queue) {
    System.out.println(item);
}
```

## 6. 實際應用範例

### 6.1 廣度優先搜尋 (BFS)

```java
public void bfs(TreeNode root) {
    if (root == null) return;
    
    Queue<TreeNode> queue = new LinkedList<>();
    queue.offer(root);
    
    while (!queue.isEmpty()) {
        TreeNode node = queue.poll();
        System.out.println(node.val);
        
        if (node.left != null) queue.offer(node.left);
        if (node.right != null) queue.offer(node.right);
    }
}
```

### 6.2 任務調度系統

```java
public class TaskScheduler {
    private PriorityQueue<Task> taskQueue = new PriorityQueue<>(
        Comparator.comparing(Task::getPriority).reversed()
    );
    
    public void addTask(Task task) {
        taskQueue.offer(task);
    }
    
    public Task getNextTask() {
        return taskQueue.poll();
    }
}
```

### 6.3 滑動視窗最大值

```java
public int[] maxSlidingWindow(int[] nums, int k) {
    Deque<Integer> deque = new ArrayDeque<>();
    int[] result = new int[nums.length - k + 1];
    
    for (int i = 0; i < nums.length; i++) {
        // 移除超出視窗的元素
        while (!deque.isEmpty() && deque.peekFirst() < i - k + 1) {
            deque.pollFirst();
        }
        
        // 移除較小的元素
        while (!deque.isEmpty() && nums[deque.peekLast()] < nums[i]) {
            deque.pollLast();
        }
        
        deque.offerLast(i);
        
        if (i >= k - 1) {
            result[i - k + 1] = nums[deque.peekFirst()];
        }
    }
    
    return result;
}
```

## 7. 常見陷阱

### 7.1 Null 值處理

```java
// ❌ 錯誤：大多數 Queue 實現不允許 null
queue.offer(null); // 拋出 NullPointerException

// ✅ 正確：檢查 null 值
if (value != null) {
    queue.offer(value);
}
```

### 7.2 PriorityQueue 的遍歷順序

```java
PriorityQueue<Integer> pq = new PriorityQueue<>();
pq.addAll(Arrays.asList(3, 1, 4, 1, 5));

// ❌ 錯誤假設：遍歷順序是排序的
for (Integer num : pq) {
    System.out.println(num); // 輸出順序不是排序的！
}

// ✅ 正確：使用 poll() 獲取排序順序
while (!pq.isEmpty()) {
    System.out.println(pq.poll()); // 1, 1, 3, 4, 5
}
```

### 7.3 併發修改

```java
// ❌ 錯誤：遍歷時修改
for (String item : queue) {
    if (shouldRemove(item)) {
        queue.remove(item); // ConcurrentModificationException
    }
}

// ✅ 正確：使用迭代器
Iterator<String> it = queue.iterator();
while (it.hasNext()) {
    String item = it.next();
    if (shouldRemove(item)) {
        it.remove();
    }
}
```

## 8. Java 8+ 特性

### 8.1 Stream API 支援

```java
Queue<String> queue = new ArrayDeque<>(Arrays.asList("a", "b", "c"));

// 過濾和收集
List<String> filtered = queue.stream()
    .filter(s -> s.compareTo("b") > 0)
    .collect(Collectors.toList());

// 統計
long count = queue.stream()
    .filter(s -> s.length() > 1)
    .count();
```

### 8.2 方法引用

```java
// 使用方法引用創建比較器
PriorityQueue<String> pq = new PriorityQueue<>(String::compareTo);

// 自定義物件排序
PriorityQueue<Person> personQueue = new PriorityQueue<>(
    Comparator.comparing(Person::getAge)
             .thenComparing(Person::getName)
);
```

## 總結

Queue 和 Deque 是處理序列化操作的重要工具：

1. **Queue** 適合 FIFO 場景，如任務排程、BFS 等
2. **Deque** 提供雙端操作，既可作佇列也可作棧使用
3. **PriorityQueue** 用於需要優先級處理的場景
4. 選擇合適的實現類別可以顯著影響效能
5. 注意 null 值處理和併發修改問題

下一節我們將通過具體的程式碼範例來深入學習這些佇列的使用方法。