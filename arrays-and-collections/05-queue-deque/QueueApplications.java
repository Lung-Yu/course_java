import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Queue 和 Deque 綜合應用範例
 * 
 * 這個類別展示了佇列在實際系統中的綜合應用：
 * - 生產者-消費者模式
 * - 事件處理系統
 * - 圖的遍歷演算法
 * - 緩衝區管理
 * - 工作流引擎
 */
public class QueueApplications {
    
    public static void main(String[] args) {
        System.out.println("=== Queue 與 Deque 綜合應用範例 ===\n");
        
        // 應用1: 生產者-消費者模式
        demonstrateProducerConsumer();
        
        // 應用2: 事件處理系統
        demonstrateEventProcessing();
        
        // 應用3: 圖的 BFS 和 DFS
        demonstrateGraphTraversal();
        
        // 應用4: 輪詢調度器
        demonstrateRoundRobinScheduler();
        
        // 應用5: 表達式求值
        demonstrateExpressionEvaluation();
    }
    
    /**
     * 生產者-消費者模式示範
     */
    public static void demonstrateProducerConsumer() {
        System.out.println("1. 生產者-消費者模式:\n");
        
        class Message {
            private final int id;
            private final String content;
            
            Message(int id, String content) {
                this.id = id;
                this.content = content;
            }
            
            @Override
            public String toString() {
                return String.format("Message{id=%d, content='%s'}", id, content);
            }
        }
        
        class Producer {
            private final BlockingQueue<Message> queue;
            private final String name;
            private int messageCounter = 0;
            
            Producer(String name, BlockingQueue<Message> queue) {
                this.name = name;
                this.queue = queue;
            }
            
            void produce(int count) {
                System.out.println(name + " 開始生產 " + count + " 條消息");
                
                for (int i = 0; i < count; i++) {
                    try {
                        Message message = new Message(
                            ++messageCounter, 
                            name + " 的消息 #" + messageCounter
                        );
                        
                        queue.put(message); // 阻塞直到有空間
                        System.out.println("  " + name + " 生產: " + message);
                        
                        Thread.sleep(100); // 模擬生產時間
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
                
                System.out.println(name + " 生產完成\n");
            }
        }
        
        class Consumer {
            private final BlockingQueue<Message> queue;
            private final String name;
            
            Consumer(String name, BlockingQueue<Message> queue) {
                this.name = name;
                this.queue = queue;
            }
            
            void consume(int count) {
                System.out.println(name + " 開始消費 " + count + " 條消息");
                
                for (int i = 0; i < count; i++) {
                    try {
                        Message message = queue.take(); // 阻塞直到有消息
                        System.out.println("  " + name + " 消費: " + message);
                        
                        // 模擬處理時間
                        Thread.sleep(150);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
                
                System.out.println(name + " 消費完成\n");
            }
        }
        
        // 建立有界阻塞佇列
        BlockingQueue<Message> messageQueue = new ArrayBlockingQueue<>(5);
        
        Producer producer1 = new Producer("生產者A", messageQueue);
        Producer producer2 = new Producer("生產者B", messageQueue);
        Consumer consumer1 = new Consumer("消費者1", messageQueue);
        Consumer consumer2 = new Consumer("消費者2", messageQueue);
        
        // 模擬並發環境
        System.out.println("佇列容量: " + ((ArrayBlockingQueue<Message>) messageQueue).remainingCapacity() + 5);
        
        // 順序執行模擬（實際應用中會是多執行緒）
        producer1.produce(3);
        consumer1.consume(2);
        producer2.produce(4);
        consumer2.consume(3);
        consumer1.consume(2);
        
        System.out.println("最終佇列大小: " + messageQueue.size());
        System.out.println();
    }
    
    /**
     * 事件處理系統示範
     */
    public static void demonstrateEventProcessing() {
        System.out.println("2. 事件處理系統:\n");
        
        enum EventType {
            USER_LOGIN, USER_LOGOUT, FILE_UPLOAD, FILE_DOWNLOAD, SYSTEM_ERROR
        }
        
        class Event {
            private final EventType type;
            private final String data;
            private final long timestamp;
            private final int priority;
            
            Event(EventType type, String data, int priority) {
                this.type = type;
                this.data = data;
                this.priority = priority;
                this.timestamp = System.currentTimeMillis();
            }
            
            @Override
            public String toString() {
                return String.format("Event{type=%s, data='%s', priority=%d}", 
                    type, data, priority);
            }
            
            public EventType getType() { return type; }
            public int getPriority() { return priority; }
            public long getTimestamp() { return timestamp; }
        }
        
        class EventProcessor {
            private final PriorityQueue<Event> eventQueue = new PriorityQueue<>(
                Comparator.comparing((Event e) -> e.getPriority())
                         .thenComparing(Event::getTimestamp)
            );
            
            void addEvent(Event event) {
                eventQueue.offer(event);
                System.out.println("添加事件: " + event);
            }
            
            void processEvents() {
                System.out.println("\n按優先級處理事件:");
                
                while (!eventQueue.isEmpty()) {
                    Event event = eventQueue.poll();
                    processEvent(event);
                }
                
                System.out.println("所有事件處理完成");
            }
            
            private void processEvent(Event event) {
                System.out.println("處理: " + event);
                
                switch (event.getType()) {
                    case USER_LOGIN:
                        System.out.println("  -> 記錄用戶登入，更新在線狀態");
                        break;
                    case USER_LOGOUT:
                        System.out.println("  -> 清理用戶會話，記錄登出時間");
                        break;
                    case FILE_UPLOAD:
                        System.out.println("  -> 掃描病毒，生成縮圖，更新索引");
                        break;
                    case FILE_DOWNLOAD:
                        System.out.println("  -> 記錄下載日誌，更新統計");
                        break;
                    case SYSTEM_ERROR:
                        System.out.println("  -> 發送告警，記錄錯誤日誌");
                        break;
                }
            }
            
            void showPendingEvents() {
                System.out.println("待處理事件 (" + eventQueue.size() + " 個):");
                eventQueue.forEach(event -> System.out.println("  " + event));
            }
        }
        
        EventProcessor processor = new EventProcessor();
        
        // 添加不同優先級的事件
        processor.addEvent(new Event(EventType.FILE_UPLOAD, "用戶A上傳圖片", 3));
        processor.addEvent(new Event(EventType.SYSTEM_ERROR, "資料庫連接失敗", 1));
        processor.addEvent(new Event(EventType.USER_LOGIN, "用戶B登入", 2));
        processor.addEvent(new Event(EventType.FILE_DOWNLOAD, "用戶C下載文件", 4));
        processor.addEvent(new Event(EventType.USER_LOGOUT, "用戶A登出", 2));
        processor.addEvent(new Event(EventType.SYSTEM_ERROR, "磁碟空間不足", 1));
        
        processor.showPendingEvents();
        processor.processEvents();
        
        System.out.println();
    }
    
    /**
     * 圖的遍歷演算法示範
     */
    public static void demonstrateGraphTraversal() {
        System.out.println("3. 圖的遍歷演算法:\n");
        
        class Graph {
            private final Map<Integer, List<Integer>> adjacencyList = new HashMap<>();
            
            void addEdge(int from, int to) {
                adjacencyList.computeIfAbsent(from, k -> new ArrayList<>()).add(to);
                adjacencyList.computeIfAbsent(to, k -> new ArrayList<>()).add(from);
            }
            
            void bfs(int start) {
                System.out.println("廣度優先搜尋 (BFS) 從節點 " + start + " 開始:");
                
                Set<Integer> visited = new HashSet<>();
                Queue<Integer> queue = new LinkedList<>();
                
                queue.offer(start);
                visited.add(start);
                
                while (!queue.isEmpty()) {
                    int current = queue.poll();
                    System.out.print(current + " ");
                    
                    List<Integer> neighbors = adjacencyList.getOrDefault(current, new ArrayList<>());
                    for (int neighbor : neighbors) {
                        if (!visited.contains(neighbor)) {
                            visited.add(neighbor);
                            queue.offer(neighbor);
                        }
                    }
                }
                System.out.println();
            }
            
            void dfs(int start) {
                System.out.println("深度優先搜尋 (DFS) 從節點 " + start + " 開始:");
                
                Set<Integer> visited = new HashSet<>();
                Deque<Integer> stack = new ArrayDeque<>();
                
                stack.push(start);
                
                while (!stack.isEmpty()) {
                    int current = stack.pop();
                    
                    if (!visited.contains(current)) {
                        visited.add(current);
                        System.out.print(current + " ");
                        
                        List<Integer> neighbors = adjacencyList.getOrDefault(current, new ArrayList<>());
                        // 逆序添加以保持較小數字優先訪問
                        for (int i = neighbors.size() - 1; i >= 0; i--) {
                            int neighbor = neighbors.get(i);
                            if (!visited.contains(neighbor)) {
                                stack.push(neighbor);
                            }
                        }
                    }
                }
                System.out.println();
            }
            
            void findShortestPath(int start, int end) {
                System.out.println("BFS 尋找從 " + start + " 到 " + end + " 的最短路徑:");
                
                if (start == end) {
                    System.out.println("路徑: " + start);
                    return;
                }
                
                Set<Integer> visited = new HashSet<>();
                Queue<List<Integer>> queue = new LinkedList<>();
                
                queue.offer(Arrays.asList(start));
                visited.add(start);
                
                while (!queue.isEmpty()) {
                    List<Integer> path = queue.poll();
                    int current = path.get(path.size() - 1);
                    
                    List<Integer> neighbors = adjacencyList.getOrDefault(current, new ArrayList<>());
                    for (int neighbor : neighbors) {
                        if (neighbor == end) {
                            List<Integer> finalPath = new ArrayList<>(path);
                            finalPath.add(neighbor);
                            System.out.println("最短路徑: " + finalPath);
                            System.out.println("路徑長度: " + (finalPath.size() - 1));
                            return;
                        }
                        
                        if (!visited.contains(neighbor)) {
                            visited.add(neighbor);
                            List<Integer> newPath = new ArrayList<>(path);
                            newPath.add(neighbor);
                            queue.offer(newPath);
                        }
                    }
                }
                
                System.out.println("無法從 " + start + " 到達 " + end);
            }
            
            void printGraph() {
                System.out.println("圖結構:");
                adjacencyList.forEach((node, neighbors) -> 
                    System.out.println("  " + node + " -> " + neighbors));
            }
        }
        
        // 建立測試圖
        //     1 --- 2
        //     |     |
        //     |     |
        //     3 --- 4 --- 5
        //           |
        //           6
        Graph graph = new Graph();
        graph.addEdge(1, 2);
        graph.addEdge(1, 3);
        graph.addEdge(2, 4);
        graph.addEdge(3, 4);
        graph.addEdge(4, 5);
        graph.addEdge(4, 6);
        
        graph.printGraph();
        System.out.println();
        
        graph.bfs(1);
        graph.dfs(1);
        System.out.println();
        
        graph.findShortestPath(1, 5);
        graph.findShortestPath(1, 6);
        
        System.out.println();
    }
    
    /**
     * 輪詢調度器示範
     */
    public static void demonstrateRoundRobinScheduler() {
        System.out.println("4. 輪詢調度器:\n");
        
        class Process {
            private final String name;
            private int remainingTime;
            private final int originalTime;
            
            Process(String name, int burstTime) {
                this.name = name;
                this.remainingTime = burstTime;
                this.originalTime = burstTime;
            }
            
            boolean isCompleted() {
                return remainingTime <= 0;
            }
            
            int execute(int timeSlice) {
                int executedTime = Math.min(timeSlice, remainingTime);
                remainingTime -= executedTime;
                return executedTime;
            }
            
            @Override
            public String toString() {
                return String.format("%s(剩餘:%d/%d)", name, remainingTime, originalTime);
            }
            
            public String getName() { return name; }
        }
        
        class RoundRobinScheduler {
            private final Queue<Process> processQueue = new LinkedList<>();
            private final int timeSlice;
            private int currentTime = 0;
            
            RoundRobinScheduler(int timeSlice) {
                this.timeSlice = timeSlice;
            }
            
            void addProcess(Process process) {
                processQueue.offer(process);
                System.out.println("添加程序: " + process);
            }
            
            void run() {
                System.out.println("\n開始輪詢調度 (時間片: " + timeSlice + "):");
                System.out.println("時間 | 執行程序 | 執行時間 | 程序狀態");
                System.out.println("-----|----------|----------|----------");
                
                while (!processQueue.isEmpty()) {
                    Process current = processQueue.poll();
                    
                    int executedTime = current.execute(timeSlice);
                    currentTime += executedTime;
                    
                    System.out.printf("%4d | %-8s | %8d | %s\n", 
                        currentTime, current.getName(), executedTime, 
                        current.isCompleted() ? "完成" : "繼續");
                    
                    // 如果程序未完成，重新加入佇列
                    if (!current.isCompleted()) {
                        processQueue.offer(current);
                    }
                    
                    // 顯示當前佇列狀態
                    if (!processQueue.isEmpty()) {
                        System.out.println("     | 佇列: " + processQueue);
                    }
                }
                
                System.out.println("\n所有程序執行完成，總時間: " + currentTime);
            }
        }
        
        RoundRobinScheduler scheduler = new RoundRobinScheduler(3);
        
        // 添加程序
        scheduler.addProcess(new Process("P1", 8));
        scheduler.addProcess(new Process("P2", 4));
        scheduler.addProcess(new Process("P3", 9));
        scheduler.addProcess(new Process("P4", 5));
        
        scheduler.run();
        
        System.out.println();
    }
    
    /**
     * 表達式求值示範（使用兩個棧）
     */
    public static void demonstrateExpressionEvaluation() {
        System.out.println("5. 表達式求值（中綴轉後綴）:\n");
        
        class ExpressionEvaluator {
            
            // 運算子優先級
            private int precedence(char operator) {
                switch (operator) {
                    case '+':
                    case '-':
                        return 1;
                    case '*':
                    case '/':
                        return 2;
                    case '^':
                        return 3;
                    default:
                        return 0;
                }
            }
            
            // 中綴轉後綴
            String infixToPostfix(String infix) {
                StringBuilder result = new StringBuilder();
                Deque<Character> stack = new ArrayDeque<>();
                
                System.out.println("中綴轉後綴過程:");
                System.out.println("輸入: " + infix);
                System.out.println("步驟 | 字符 | 棧狀態     | 輸出");
                System.out.println("-----|------|------------|----------");
                
                int step = 1;
                for (char c : infix.toCharArray()) {
                    if (Character.isDigit(c)) {
                        result.append(c).append(' ');
                    } else if (c == '(') {
                        stack.push(c);
                    } else if (c == ')') {
                        while (!stack.isEmpty() && stack.peek() != '(') {
                            result.append(stack.pop()).append(' ');
                        }
                        if (!stack.isEmpty()) {
                            stack.pop(); // 移除 '('
                        }
                    } else if (c != ' ') { // 運算子
                        while (!stack.isEmpty() && stack.peek() != '(' &&
                               precedence(c) <= precedence(stack.peek())) {
                            result.append(stack.pop()).append(' ');
                        }
                        stack.push(c);
                    }
                    
                    System.out.printf("%4d | %4c | %-10s | %s\n", 
                        step++, c, stack.toString(), result.toString().trim());
                }
                
                // 清空棧中剩餘運算子
                while (!stack.isEmpty()) {
                    result.append(stack.pop()).append(' ');
                    System.out.printf("%4d | %4s | %-10s | %s\n", 
                        step++, "", stack.toString(), result.toString().trim());
                }
                
                return result.toString().trim();
            }
            
            // 後綴表達式求值
            int evaluatePostfix(String postfix) {
                Deque<Integer> stack = new ArrayDeque<>();
                String[] tokens = postfix.split(" ");
                
                System.out.println("\n後綴表達式求值過程:");
                System.out.println("後綴: " + postfix);
                System.out.println("步驟 | Token | 棧狀態");
                System.out.println("-----|-------|----------");
                
                int step = 1;
                for (String token : tokens) {
                    if (Character.isDigit(token.charAt(0))) {
                        stack.push(Integer.parseInt(token));
                    } else {
                        int operand2 = stack.pop();
                        int operand1 = stack.pop();
                        int result = 0;
                        
                        switch (token.charAt(0)) {
                            case '+': result = operand1 + operand2; break;
                            case '-': result = operand1 - operand2; break;
                            case '*': result = operand1 * operand2; break;
                            case '/': result = operand1 / operand2; break;
                            case '^': result = (int) Math.pow(operand1, operand2); break;
                        }
                        
                        stack.push(result);
                    }
                    
                    System.out.printf("%4d | %5s | %s\n", 
                        step++, token, stack.toString());
                }
                
                return stack.pop();
            }
            
            // 完整計算過程
            int evaluate(String expression) {
                System.out.println("計算表達式: " + expression);
                String postfix = infixToPostfix(expression);
                System.out.println("後綴表達式: " + postfix);
                int result = evaluatePostfix(postfix);
                System.out.println("計算結果: " + result);
                return result;
            }
        }
        
        ExpressionEvaluator evaluator = new ExpressionEvaluator();
        
        // 測試不同的表達式
        String[] expressions = {
            "3 + 4 * 2",
            "(3 + 4) * 2",
            "2 ^ 3 + 1",
            "((3 + 4) * 2 - 1) / 3"
        };
        
        for (String expr : expressions) {
            evaluator.evaluate(expr);
            System.out.println();
        }
        
        System.out.println();
    }
}