import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Queue 介面詳細示範
 * 
 * 這個類別展示了 Queue 的各種實現和操作：
 * - LinkedList 作為 Queue 的使用
 * - ArrayDeque 的高效操作
 * - 執行緒安全的 Queue 實現
 * - 效能比較和最佳實踐
 */
public class QueueDemo {
    
    public static void main(String[] args) {
        System.out.println("=== Queue 介面詳細示範 ===\n");
        
        // 基本 Queue 操作
        demonstrateBasicQueueOperations();
        
        // 不同實現類別比較
        compareQueueImplementations();
        
        // 執行緒安全的 Queue
        demonstrateThreadSafeQueues();
        
        // 效能測試
        performanceComparison();
        
        // 實際應用範例
        demonstratePracticalApplications();
    }
    
    /**
     * 基本 Queue 操作示範
     */
    public static void demonstrateBasicQueueOperations() {
        System.out.println("1. Queue 基本操作:\n");
        
        // 使用 LinkedList 實現 Queue
        Queue<String> queue = new LinkedList<>();
        
        System.out.println("建立空佇列: " + queue);
        System.out.println("是否為空: " + queue.isEmpty());
        System.out.println("大小: " + queue.size());
        
        // 添加元素 (offer vs add)
        System.out.println("\n=== 添加操作 ===");
        queue.offer("第一個");
        queue.offer("第二個");
        queue.offer("第三個");
        
        System.out.println("使用 offer() 添加後: " + queue);
        
        // add() 和 offer() 在大多數實現中行為相同
        queue.add("第四個");
        System.out.println("使用 add() 添加後: " + queue);
        
        // 檢視元素 (peek vs element)
        System.out.println("\n=== 檢視操作 ===");
        System.out.println("peek() 查看頭部元素: " + queue.peek());
        System.out.println("element() 查看頭部元素: " + queue.element());
        System.out.println("佇列內容不變: " + queue);
        
        // 移除元素 (poll vs remove)
        System.out.println("\n=== 移除操作 ===");
        System.out.println("poll() 移除頭部元素: " + queue.poll());
        System.out.println("移除後的佇列: " + queue);
        
        System.out.println("remove() 移除頭部元素: " + queue.remove());
        System.out.println("移除後的佇列: " + queue);
        
        // 空佇列的行為差異
        System.out.println("\n=== 空佇列行為差異 ===");
        queue.clear();
        System.out.println("清空佇列: " + queue);
        
        System.out.println("peek() 在空佇列: " + queue.peek()); // 返回 null
        System.out.println("poll() 在空佇列: " + queue.poll()); // 返回 null
        
        try {
            queue.element(); // 拋出異常
        } catch (NoSuchElementException e) {
            System.out.println("element() 在空佇列拋出異常: " + e.getMessage());
        }
        
        try {
            queue.remove(); // 拋出異常
        } catch (NoSuchElementException e) {
            System.out.println("remove() 在空佇列拋出異常: " + e.getMessage());
        }
        
        System.out.println();
    }
    
    /**
     * 不同 Queue 實現類別比較
     */
    public static void compareQueueImplementations() {
        System.out.println("2. Queue 實現類別比較:\n");
        
        // LinkedList 作為 Queue
        demonstrateLinkedListQueue();
        
        // ArrayDeque 作為 Queue
        demonstrateArrayDequeQueue();
        
        // 容量限制的差異
        demonstrateCapacityLimitations();
    }
    
    /**
     * LinkedList 作為 Queue 的示範
     */
    public static void demonstrateLinkedListQueue() {
        System.out.println("=== LinkedList 作為 Queue ===");
        
        Queue<Integer> linkedQueue = new LinkedList<>();
        
        // 添加元素
        for (int i = 1; i <= 5; i++) {
            linkedQueue.offer(i * 10);
        }
        
        System.out.println("LinkedList Queue: " + linkedQueue);
        
        // LinkedList 同時實現了 List 介面
        if (linkedQueue instanceof LinkedList) {
            LinkedList<Integer> list = (LinkedList<Integer>) linkedQueue;
            System.out.println("作為 List 的隨機訪問: 索引 2 的元素 = " + list.get(2));
        }
        
        // 遍歷不會改變佇列
        System.out.println("遍歷佇列內容:");
        for (Integer num : linkedQueue) {
            System.out.print(num + " ");
        }
        System.out.println("\n遍歷後佇列內容不變: " + linkedQueue);
        
        // 處理所有元素
        System.out.println("按順序處理所有元素:");
        while (!linkedQueue.isEmpty()) {
            System.out.print(linkedQueue.poll() + " ");
        }
        System.out.println("\n處理完畢，佇列為空: " + linkedQueue);
        
        System.out.println();
    }
    
    /**
     * ArrayDeque 作為 Queue 的示範
     */
    public static void demonstrateArrayDequeQueue() {
        System.out.println("=== ArrayDeque 作為 Queue ===");
        
        Queue<String> arrayQueue = new ArrayDeque<>();
        
        // 批量添加
        String[] tasks = {"任務A", "任務B", "任務C", "任務D", "任務E"};
        arrayQueue.addAll(Arrays.asList(tasks));
        
        System.out.println("ArrayDeque Queue: " + arrayQueue);
        
        // ArrayDeque 的高效操作
        System.out.println("頭部元素: " + arrayQueue.peek());
        System.out.println("佇列大小: " + arrayQueue.size());
        
        // 條件移除
        System.out.println("\n條件處理:");
        int processed = 0;
        while (!arrayQueue.isEmpty() && processed < 3) {
            String task = arrayQueue.poll();
            System.out.println("處理: " + task);
            processed++;
        }
        
        System.out.println("剩餘任務: " + arrayQueue);
        
        // contains 操作
        System.out.println("是否包含 '任務D': " + arrayQueue.contains("任務D"));
        System.out.println("是否包含 '任務A': " + arrayQueue.contains("任務A"));
        
        System.out.println();
    }
    
    /**
     * 容量限制示範
     */
    public static void demonstrateCapacityLimitations() {
        System.out.println("=== 容量限制示範 ===");
        
        // ArrayBlockingQueue - 有界佇列
        Queue<String> boundedQueue = new ArrayBlockingQueue<>(3);
        
        System.out.println("建立容量為 3 的有界佇列");
        
        // 填滿佇列
        boundedQueue.offer("元素1");
        boundedQueue.offer("元素2");
        boundedQueue.offer("元素3");
        
        System.out.println("填滿後: " + boundedQueue);
        System.out.println("剩餘容量: " + ((ArrayBlockingQueue<String>) boundedQueue).remainingCapacity());
        
        // 嘗試添加更多元素
        boolean added = boundedQueue.offer("元素4");
        System.out.println("嘗試添加第4個元素: " + added);
        System.out.println("佇列內容: " + boundedQueue);
        
        // add() 在容量滿時的行為
        try {
            boundedQueue.add("元素5");
        } catch (IllegalStateException e) {
            System.out.println("add() 在滿容量時拋出異常: " + e.getMessage());
        }
        
        // 對比無界佇列
        Queue<String> unboundedQueue = new LinkedList<>();
        System.out.println("\n無界佇列可以無限添加:");
        for (int i = 1; i <= 1000; i++) {
            unboundedQueue.offer("元素" + i);
        }
        System.out.println("無界佇列大小: " + unboundedQueue.size());
        
        System.out.println();
    }
    
    /**
     * 執行緒安全的 Queue 示範
     */
    public static void demonstrateThreadSafeQueues() {
        System.out.println("3. 執行緒安全的 Queue:\n");
        
        // ConcurrentLinkedQueue
        demonstrateConcurrentLinkedQueue();
        
        // ArrayBlockingQueue
        demonstrateArrayBlockingQueue();
    }
    
    /**
     * ConcurrentLinkedQueue 示範
     */
    public static void demonstrateConcurrentLinkedQueue() {
        System.out.println("=== ConcurrentLinkedQueue ===");
        
        Queue<String> concurrentQueue = new ConcurrentLinkedQueue<>();
        
        System.out.println("ConcurrentLinkedQueue 特性:");
        System.out.println("- 無界佇列");
        System.out.println("- 使用 CAS (Compare-And-Swap) 實現執行緒安全");
        System.out.println("- 非阻塞操作");
        
        // 模擬併發操作
        concurrentQueue.offer("訊息1");
        concurrentQueue.offer("訊息2");
        concurrentQueue.offer("訊息3");
        
        System.out.println("\n初始佇列: " + concurrentQueue);
        
        // 模擬多執行緒環境下的安全操作
        System.out.println("執行緒安全的操作:");
        while (!concurrentQueue.isEmpty()) {
            String message = concurrentQueue.poll();
            if (message != null) {
                System.out.println("安全地處理: " + message);
            }
        }
        
        System.out.println();
    }
    
    /**
     * ArrayBlockingQueue 示範
     */
    public static void demonstrateArrayBlockingQueue() {
        System.out.println("=== ArrayBlockingQueue ===");
        
        ArrayBlockingQueue<Integer> blockingQueue = new ArrayBlockingQueue<>(5);
        
        System.out.println("ArrayBlockingQueue 特性:");
        System.out.println("- 有界佇列");
        System.out.println("- 支援阻塞操作");
        System.out.println("- 適合生產者-消費者模式");
        
        // 填充佇列
        for (int i = 1; i <= 5; i++) {
            blockingQueue.offer(i * 100);
        }
        
        System.out.println("\n佇列內容: " + blockingQueue);
        System.out.println("是否已滿: " + (blockingQueue.remainingCapacity() == 0));
        
        // 嘗試獲取所有元素
        System.out.println("取出所有元素:");
        List<Integer> drained = new ArrayList<>();
        int count = blockingQueue.drainTo(drained);
        
        System.out.println("一次性取出 " + count + " 個元素: " + drained);
        System.out.println("佇列現在為空: " + blockingQueue.isEmpty());
        
        System.out.println();
    }
    
    /**
     * 效能比較
     */
    public static void performanceComparison() {
        System.out.println("4. 效能比較:\n");
        
        int testSize = 100000;
        
        System.out.println("測試 " + String.format("%,d", testSize) + " 次操作的效能:");
        
        // LinkedList 效能測試
        testQueuePerformance("LinkedList", new LinkedList<>(), testSize);
        
        // ArrayDeque 效能測試
        testQueuePerformance("ArrayDeque", new ArrayDeque<>(), testSize);
        
        // ConcurrentLinkedQueue 效能測試
        testQueuePerformance("ConcurrentLinkedQueue", new ConcurrentLinkedQueue<>(), testSize);
        
        System.out.println();
    }
    
    /**
     * 單個 Queue 實現的效能測試
     */
    public static void testQueuePerformance(String name, Queue<Integer> queue, int testSize) {
        // 測試插入效能
        long startTime = System.nanoTime();
        for (int i = 0; i < testSize; i++) {
            queue.offer(i);
        }
        long insertTime = System.nanoTime() - startTime;
        
        // 測試移除效能
        startTime = System.nanoTime();
        while (!queue.isEmpty()) {
            queue.poll();
        }
        long removeTime = System.nanoTime() - startTime;
        
        System.out.printf("%s:\n", name);
        System.out.printf("  插入時間: %.2f ms\n", insertTime / 1_000_000.0);
        System.out.printf("  移除時間: %.2f ms\n", removeTime / 1_000_000.0);
        System.out.printf("  總時間: %.2f ms\n", (insertTime + removeTime) / 1_000_000.0);
        System.out.println();
    }
    
    /**
     * 實際應用範例
     */
    public static void demonstratePracticalApplications() {
        System.out.println("5. 實際應用範例:\n");
        
        // 應用1: 任務佇列
        demonstrateTaskQueue();
        
        // 應用2: 消息緩衝區
        demonstrateMessageBuffer();
        
        // 應用3: 廣度優先搜尋
        demonstrateBFS();
    }
    
    /**
     * 任務佇列範例
     */
    public static void demonstrateTaskQueue() {
        System.out.println("應用1: 任務佇列系統\n");
        
        class Task {
            private final String name;
            private final int duration;
            
            Task(String name, int duration) {
                this.name = name;
                this.duration = duration;
            }
            
            void execute() {
                System.out.println("執行任務: " + name + " (預計 " + duration + " 秒)");
            }
            
            @Override
            public String toString() {
                return name + "(" + duration + "s)";
            }
        }
        
        class TaskProcessor {
            private final Queue<Task> taskQueue = new ArrayDeque<>();
            
            void addTask(Task task) {
                taskQueue.offer(task);
                System.out.println("添加任務: " + task);
            }
            
            void processTasks() {
                System.out.println("\n開始處理任務佇列:");
                while (!taskQueue.isEmpty()) {
                    Task task = taskQueue.poll();
                    task.execute();
                }
                System.out.println("所有任務處理完畢");
            }
            
            void showPendingTasks() {
                System.out.println("待處理任務: " + taskQueue);
            }
        }
        
        TaskProcessor processor = new TaskProcessor();
        
        // 添加任務
        processor.addTask(new Task("資料備份", 30));
        processor.addTask(new Task("系統更新", 60));
        processor.addTask(new Task("日誌清理", 15));
        processor.addTask(new Task("效能監控", 10));
        
        processor.showPendingTasks();
        processor.processTasks();
        
        System.out.println();
    }
    
    /**
     * 消息緩衝區範例
     */
    public static void demonstrateMessageBuffer() {
        System.out.println("應用2: 消息緩衝區\n");
        
        class Message {
            private final String content;
            private final long timestamp;
            
            Message(String content) {
                this.content = content;
                this.timestamp = System.currentTimeMillis();
            }
            
            @Override
            public String toString() {
                return String.format("[%d] %s", timestamp % 100000, content);
            }
        }
        
        class MessageBuffer {
            private final Queue<Message> buffer = new LinkedList<>();
            private final int maxSize;
            
            MessageBuffer(int maxSize) {
                this.maxSize = maxSize;
            }
            
            void addMessage(String content) {
                Message message = new Message(content);
                
                // 如果緩衝區滿了，移除最舊的消息
                if (buffer.size() >= maxSize) {
                    Message removed = buffer.poll();
                    System.out.println("緩衝區已滿，移除舊消息: " + removed);
                }
                
                buffer.offer(message);
                System.out.println("添加消息: " + message);
            }
            
            List<Message> getAllMessages() {
                return new ArrayList<>(buffer);
            }
            
            void processMessages() {
                System.out.println("\n處理緩衝區中的所有消息:");
                while (!buffer.isEmpty()) {
                    Message message = buffer.poll();
                    System.out.println("處理: " + message);
                }
            }
        }
        
        MessageBuffer messageBuffer = new MessageBuffer(3);
        
        // 添加消息
        messageBuffer.addMessage("系統啟動");
        messageBuffer.addMessage("用戶登入");
        messageBuffer.addMessage("檔案上傳");
        messageBuffer.addMessage("資料處理"); // 這個會觸發舊消息移除
        messageBuffer.addMessage("任務完成"); // 這個也會觸發舊消息移除
        
        System.out.println("\n目前緩衝區內容:");
        messageBuffer.getAllMessages().forEach(System.out::println);
        
        messageBuffer.processMessages();
        
        System.out.println();
    }
    
    /**
     * 廣度優先搜尋範例
     */
    public static void demonstrateBFS() {
        System.out.println("應用3: 廣度優先搜尋 (BFS)\n");
        
        class TreeNode {
            int val;
            TreeNode left;
            TreeNode right;
            
            TreeNode(int val) {
                this.val = val;
            }
            
            @Override
            public String toString() {
                return String.valueOf(val);
            }
        }
        
        class BinaryTree {
            TreeNode root;
            
            void levelOrderTraversal() {
                if (root == null) return;
                
                Queue<TreeNode> queue = new LinkedList<>();
                queue.offer(root);
                
                System.out.println("層序遍歷結果:");
                while (!queue.isEmpty()) {
                    int levelSize = queue.size();
                    System.out.print("第" + getCurrentLevel(queue) + "層: ");
                    
                    for (int i = 0; i < levelSize; i++) {
                        TreeNode node = queue.poll();
                        System.out.print(node.val + " ");
                        
                        if (node.left != null) queue.offer(node.left);
                        if (node.right != null) queue.offer(node.right);
                    }
                    System.out.println();
                }
            }
            
            private int getCurrentLevel(Queue<TreeNode> queue) {
                // 簡化的層級計算，實際應用中可能需要更複雜的邏輯
                return queue.isEmpty() ? 0 : 1;
            }
            
            TreeNode search(int target) {
                if (root == null) return null;
                
                Queue<TreeNode> queue = new LinkedList<>();
                queue.offer(root);
                
                System.out.println("BFS 搜尋 " + target + ":");
                while (!queue.isEmpty()) {
                    TreeNode node = queue.poll();
                    System.out.println("檢查節點: " + node.val);
                    
                    if (node.val == target) {
                        System.out.println("找到目標節點: " + target);
                        return node;
                    }
                    
                    if (node.left != null) {
                        queue.offer(node.left);
                        System.out.println("  左子節點 " + node.left.val + " 加入佇列");
                    }
                    if (node.right != null) {
                        queue.offer(node.right);
                        System.out.println("  右子節點 " + node.right.val + " 加入佇列");
                    }
                }
                
                System.out.println("未找到目標節點: " + target);
                return null;
            }
        }
        
        // 建立測試樹
        //       1
        //      / \
        //     2   3
        //    / \   \
        //   4   5   6
        BinaryTree tree = new BinaryTree();
        tree.root = new TreeNode(1);
        tree.root.left = new TreeNode(2);
        tree.root.right = new TreeNode(3);
        tree.root.left.left = new TreeNode(4);
        tree.root.left.right = new TreeNode(5);
        tree.root.right.right = new TreeNode(6);
        
        System.out.println("二元樹結構:");
        System.out.println("       1");
        System.out.println("      / \\");
        System.out.println("     2   3");
        System.out.println("    / \\   \\");
        System.out.println("   4   5   6");
        System.out.println();
        
        tree.levelOrderTraversal();
        
        System.out.println();
        tree.search(5);
        
        System.out.println();
    }
}