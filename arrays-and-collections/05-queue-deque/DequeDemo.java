import java.util.*;

/**
 * Deque 雙端佇列詳細示範
 * 
 * 這個類別展示了 Deque 的各種特性和操作：
 * - 雙端插入和刪除操作
 * - 作為棧 (Stack) 使用
 * - 作為佇列 (Queue) 使用
 * - ArrayDeque 與 LinkedList 的比較
 * - 實際應用場景
 */
public class DequeDemo {
    
    public static void main(String[] args) {
        System.out.println("=== Deque 雙端佇列詳細示範 ===\n");
        
        // 基本雙端操作
        demonstrateBasicDequeOperations();
        
        // 作為棧使用
        demonstrateDequeAsStack();
        
        // 作為佇列使用
        demonstrateDequeAsQueue();
        
        // ArrayDeque vs LinkedList
        compareDequeImplementations();
        
        // 實際應用範例
        demonstratePracticalApplications();
    }
    
    /**
     * 基本雙端操作示範
     */
    public static void demonstrateBasicDequeOperations() {
        System.out.println("1. Deque 基本雙端操作:\n");
        
        Deque<String> deque = new ArrayDeque<>();
        
        System.out.println("建立空的雙端佇列: " + deque);
        
        // 頭部操作
        System.out.println("\n=== 頭部操作 ===");
        deque.addFirst("頭部-2");
        deque.addFirst("頭部-1");
        System.out.println("addFirst() 兩次後: " + deque);
        
        deque.offerFirst("頭部-0");
        System.out.println("offerFirst() 後: " + deque);
        
        // 尾部操作
        System.out.println("\n=== 尾部操作 ===");
        deque.addLast("尾部-1");
        deque.addLast("尾部-2");
        System.out.println("addLast() 兩次後: " + deque);
        
        deque.offerLast("尾部-3");
        System.out.println("offerLast() 後: " + deque);
        
        // 檢視操作
        System.out.println("\n=== 檢視操作 ===");
        System.out.println("getFirst(): " + deque.getFirst());
        System.out.println("getLast(): " + deque.getLast());
        System.out.println("peekFirst(): " + deque.peekFirst());
        System.out.println("peekLast(): " + deque.peekLast());
        System.out.println("檢視後 deque 不變: " + deque);
        
        // 移除操作
        System.out.println("\n=== 移除操作 ===");
        System.out.println("removeFirst(): " + deque.removeFirst());
        System.out.println("removeLast(): " + deque.removeLast());
        System.out.println("移除後: " + deque);
        
        System.out.println("pollFirst(): " + deque.pollFirst());
        System.out.println("pollLast(): " + deque.pollLast());
        System.out.println("移除後: " + deque);
        
        // 空佇列時的行為差異
        System.out.println("\n=== 空佇列行為 ===");
        deque.clear();
        System.out.println("清空後: " + deque);
        
        System.out.println("peekFirst() 在空佇列: " + deque.peekFirst());
        System.out.println("peekLast() 在空佇列: " + deque.peekLast());
        System.out.println("pollFirst() 在空佇列: " + deque.pollFirst());
        System.out.println("pollLast() 在空佇列: " + deque.pollLast());
        
        try {
            deque.getFirst();
        } catch (NoSuchElementException e) {
            System.out.println("getFirst() 在空佇列拋出異常: " + e.getMessage());
        }
        
        try {
            deque.getLast();
        } catch (NoSuchElementException e) {
            System.out.println("getLast() 在空佇列拋出異常: " + e.getMessage());
        }
        
        System.out.println();
    }
    
    /**
     * Deque 作為棧 (Stack) 使用
     */
    public static void demonstrateDequeAsStack() {
        System.out.println("2. Deque 作為棧 (Stack) 使用:\n");
        
        Deque<Integer> stack = new ArrayDeque<>();
        
        System.out.println("棧操作示範:");
        System.out.println("空棧: " + stack);
        
        // 入棧 (push)
        System.out.println("\n=== 入棧操作 ===");
        for (int i = 1; i <= 5; i++) {
            stack.push(i * 10);
            System.out.println("push(" + (i * 10) + "): " + stack);
        }
        
        // 查看棧頂
        System.out.println("\n棧頂元素 (不移除): " + stack.peek());
        System.out.println("棧內容: " + stack);
        
        // 出棧 (pop)
        System.out.println("\n=== 出棧操作 ===");
        while (!stack.isEmpty()) {
            int popped = stack.pop();
            System.out.println("pop(): " + popped + ", 剩餘: " + stack);
        }
        
        // 與傳統 Stack 類別比較
        System.out.println("\n=== 與傳統 Stack 比較 ===");
        
        // 傳統 Stack (不推薦使用，已過時)
        Stack<String> oldStack = new Stack<>();
        oldStack.push("A");
        oldStack.push("B");
        oldStack.push("C");
        System.out.println("傳統 Stack: " + oldStack);
        System.out.println("棧頂: " + oldStack.peek());
        
        // ArrayDeque 作為棧 (推薦)
        Deque<String> modernStack = new ArrayDeque<>();
        modernStack.push("A");
        modernStack.push("B");
        modernStack.push("C");
        System.out.println("ArrayDeque 棧: " + modernStack);
        System.out.println("棧頂: " + modernStack.peek());
        
        System.out.println("\n推薦使用 ArrayDeque 而非 Stack 類別的原因:");
        System.out.println("1. Stack 繼承自 Vector，有同步開銷");
        System.out.println("2. ArrayDeque 效能更好");
        System.out.println("3. ArrayDeque 提供更一致的介面");
        
        // 實際應用：括號匹配
        demonstrateParenthesesMatching();
        
        System.out.println();
    }
    
    /**
     * 括號匹配示範
     */
    public static void demonstrateParenthesesMatching() {
        System.out.println("\n=== 實際應用：括號匹配 ===");
        
        String[] testCases = {
            "()",
            "()[]{}",
            "([{}])",
            "(((",
            "(()",
            "([)]",
            "{[()()]}"
        };
        
        for (String testCase : testCases) {
            boolean isValid = isValidParentheses(testCase);
            System.out.println("\"" + testCase + "\": " + (isValid ? "有效" : "無效"));
        }
    }
    
    /**
     * 檢查括號是否匹配
     */
    public static boolean isValidParentheses(String s) {
        Deque<Character> stack = new ArrayDeque<>();
        
        for (char c : s.toCharArray()) {
            if (c == '(' || c == '[' || c == '{') {
                stack.push(c);
            } else if (c == ')' || c == ']' || c == '}') {
                if (stack.isEmpty()) return false;
                
                char top = stack.pop();
                if ((c == ')' && top != '(') ||
                    (c == ']' && top != '[') ||
                    (c == '}' && top != '{')) {
                    return false;
                }
            }
        }
        
        return stack.isEmpty();
    }
    
    /**
     * Deque 作為佇列 (Queue) 使用
     */
    public static void demonstrateDequeAsQueue() {
        System.out.println("3. Deque 作為佇列 (Queue) 使用:\n");
        
        Deque<String> queue = new ArrayDeque<>();
        
        System.out.println("佇列操作示範:");
        System.out.println("空佇列: " + queue);
        
        // 入隊 (使用尾部)
        System.out.println("\n=== 入隊操作 ===");
        String[] customers = {"客戶A", "客戶B", "客戶C", "客戶D"};
        for (String customer : customers) {
            queue.offerLast(customer); // 等同於 offer()
            System.out.println("入隊: " + customer + ", 佇列: " + queue);
        }
        
        // 查看隊頭
        System.out.println("\n隊頭客戶: " + queue.peekFirst()); // 等同於 peek()
        System.out.println("佇列狀態: " + queue);
        
        // 出隊 (使用頭部)
        System.out.println("\n=== 出隊操作 ===");
        while (!queue.isEmpty()) {
            String served = queue.pollFirst(); // 等同於 poll()
            System.out.println("服務: " + served + ", 剩餘: " + queue);
        }
        
        // 展示 Deque 的佇列方法對應關係
        System.out.println("\n=== Deque vs Queue 方法對應 ===");
        queue.offer("元素1");        // 等同於 offerLast()
        queue.offerLast("元素2");
        System.out.println("使用 offer() 和 offerLast(): " + queue);
        
        System.out.println("peek(): " + queue.peek());           // 等同於 peekFirst()
        System.out.println("peekFirst(): " + queue.peekFirst());
        
        System.out.println("poll(): " + queue.poll());           // 等同於 pollFirst()
        System.out.println("剩餘: " + queue);
        
        System.out.println();
    }
    
    /**
     * ArrayDeque vs LinkedList 比較
     */
    public static void compareDequeImplementations() {
        System.out.println("4. ArrayDeque vs LinkedList 比較:\n");
        
        // 功能比較
        compareFunctionality();
        
        // 效能比較
        comparePerformance();
        
        // 記憶體使用比較
        compareMemoryUsage();
    }
    
    /**
     * 功能比較
     */
    public static void compareFunctionality() {
        System.out.println("=== 功能比較 ===");
        
        // ArrayDeque
        Deque<String> arrayDeque = new ArrayDeque<>();
        System.out.println("ArrayDeque 特性:");
        System.out.println("- 底層使用動態陣列");
        System.out.println("- 不允許 null 元素");
        System.out.println("- 非執行緒安全");
        System.out.println("- 作為棧和佇列都有優秀效能");
        
        // LinkedList
        Deque<String> linkedDeque = new LinkedList<>();
        System.out.println("\nLinkedList 特性:");
        System.out.println("- 底層使用雙向鏈表");
        System.out.println("- 允許 null 元素");
        System.out.println("- 非執行緒安全");
        System.out.println("- 同時實現 List 介面");
        
        // null 值測試
        System.out.println("\n=== null 值處理 ===");
        
        try {
            arrayDeque.offer(null);
        } catch (NullPointerException e) {
            System.out.println("ArrayDeque 不允許 null: " + e.getMessage());
        }
        
        linkedDeque.offer(null);
        linkedDeque.offer("有效元素");
        System.out.println("LinkedList 允許 null: " + linkedDeque);
        linkedDeque.clear();
        
        System.out.println();
    }
    
    /**
     * 效能比較
     */
    public static void comparePerformance() {
        System.out.println("=== 效能比較 ===");
        
        int testSize = 100000;
        System.out.println("測試 " + String.format("%,d", testSize) + " 次操作:");
        
        // ArrayDeque 效能測試
        long startTime = System.nanoTime();
        Deque<Integer> arrayDeque = new ArrayDeque<>();
        
        // 雙端插入測試
        for (int i = 0; i < testSize / 2; i++) {
            arrayDeque.offerFirst(i);
            arrayDeque.offerLast(i);
        }
        
        // 雙端刪除測試
        while (!arrayDeque.isEmpty()) {
            arrayDeque.pollFirst();
            if (!arrayDeque.isEmpty()) {
                arrayDeque.pollLast();
            }
        }
        
        long arrayDequeTime = System.nanoTime() - startTime;
        
        // LinkedList 效能測試
        startTime = System.nanoTime();
        Deque<Integer> linkedDeque = new LinkedList<>();
        
        // 雙端插入測試
        for (int i = 0; i < testSize / 2; i++) {
            linkedDeque.offerFirst(i);
            linkedDeque.offerLast(i);
        }
        
        // 雙端刪除測試
        while (!linkedDeque.isEmpty()) {
            linkedDeque.pollFirst();
            if (!linkedDeque.isEmpty()) {
                linkedDeque.pollLast();
            }
        }
        
        long linkedDequeTime = System.nanoTime() - startTime;
        
        System.out.printf("ArrayDeque: %.2f ms\n", arrayDequeTime / 1_000_000.0);
        System.out.printf("LinkedList: %.2f ms\n", linkedDequeTime / 1_000_000.0);
        System.out.printf("ArrayDeque 比 LinkedList 快 %.1f%%\n", 
            ((double) linkedDequeTime - arrayDequeTime) / arrayDequeTime * 100);
        
        System.out.println();
    }
    
    /**
     * 記憶體使用比較
     */
    public static void compareMemoryUsage() {
        System.out.println("=== 記憶體使用比較 ===");
        
        System.out.println("ArrayDeque 記憶體特性:");
        System.out.println("- 緊湊的陣列存儲");
        System.out.println("- 無額外的節點指標");
        System.out.println("- 記憶體局部性好");
        
        System.out.println("\nLinkedList 記憶體特性:");
        System.out.println("- 每個節點需要前後指標");
        System.out.println("- 記憶體分散");
        System.out.println("- 額外的物件創建開銷");
        
        // 實際記憶體測試（簡化示範）
        int elementCount = 50000;
        
        Runtime runtime = Runtime.getRuntime();
        System.gc();
        long baseMem = runtime.totalMemory() - runtime.freeMemory();
        
        Deque<Integer> testDeque = new ArrayDeque<>();
        for (int i = 0; i < elementCount; i++) {
            testDeque.offer(i);
        }
        
        System.gc();
        long arrayDequeMem = runtime.totalMemory() - runtime.freeMemory() - baseMem;
        
        System.out.printf("\nArrayDeque (%,d 元素): ~%.2f MB\n", 
            elementCount, arrayDequeMem / (1024.0 * 1024.0));
        
        System.out.println();
    }
    
    /**
     * 實際應用範例
     */
    public static void demonstratePracticalApplications() {
        System.out.println("5. 實際應用範例:\n");
        
        // 應用1: 滑動視窗最大值
        demonstrateSlidingWindowMaximum();
        
        // 應用2: 回文檢查
        demonstratePalindromeCheck();
        
        // 應用3: 瀏覽器歷史記錄
        demonstrateBrowserHistory();
    }
    
    /**
     * 滑動視窗最大值
     */
    public static void demonstrateSlidingWindowMaximum() {
        System.out.println("應用1: 滑動視窗最大值\n");
        
        int[] nums = {1, 3, -1, -3, 5, 3, 6, 7};
        int k = 3;
        
        System.out.println("陣列: " + Arrays.toString(nums));
        System.out.println("視窗大小: " + k);
        
        int[] result = maxSlidingWindow(nums, k);
        System.out.println("滑動視窗最大值: " + Arrays.toString(result));
        
        // 顯示過程
        System.out.println("\n滑動過程:");
        for (int i = 0; i <= nums.length - k; i++) {
            int[] window = Arrays.copyOfRange(nums, i, i + k);
            System.out.printf("視窗 [%d, %d]: %s, 最大值: %d\n", 
                i, i + k - 1, Arrays.toString(window), result[i]);
        }
        
        System.out.println();
    }
    
    /**
     * 滑動視窗最大值演算法實現
     */
    public static int[] maxSlidingWindow(int[] nums, int k) {
        if (nums == null || nums.length == 0) return new int[0];
        
        int[] result = new int[nums.length - k + 1];
        Deque<Integer> deque = new ArrayDeque<>(); // 存儲陣列索引
        
        for (int i = 0; i < nums.length; i++) {
            // 移除超出視窗範圍的元素
            while (!deque.isEmpty() && deque.peekFirst() < i - k + 1) {
                deque.pollFirst();
            }
            
            // 移除所有小於當前元素的索引
            while (!deque.isEmpty() && nums[deque.peekLast()] < nums[i]) {
                deque.pollLast();
            }
            
            deque.offerLast(i);
            
            // 記錄視窗最大值
            if (i >= k - 1) {
                result[i - k + 1] = nums[deque.peekFirst()];
            }
        }
        
        return result;
    }
    
    /**
     * 回文檢查
     */
    public static void demonstratePalindromeCheck() {
        System.out.println("應用2: 回文檢查\n");
        
        String[] testStrings = {
            "racecar",
            "hello",
            "madam",
            "A man a plan a canal Panama",
            "race a car"
        };
        
        for (String str : testStrings) {
            boolean isPalindrome = isPalindrome(str);
            System.out.printf("「%s」 是回文: %s\n", str, isPalindrome);
        }
        
        System.out.println();
    }
    
    /**
     * 回文檢查實現
     */
    public static boolean isPalindrome(String s) {
        // 正規化字串：只保留字母數字，轉為小寫
        String normalized = s.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
        
        if (normalized.length() <= 1) return true;
        
        Deque<Character> deque = new ArrayDeque<>();
        
        // 將字元加入 deque
        for (char c : normalized.toCharArray()) {
            deque.offerLast(c);
        }
        
        // 從兩端比較字元
        while (deque.size() > 1) {
            if (!deque.pollFirst().equals(deque.pollLast())) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * 瀏覽器歷史記錄
     */
    public static void demonstrateBrowserHistory() {
        System.out.println("應用3: 瀏覽器歷史記錄\n");
        
        class BrowserHistory {
            private Deque<String> backHistory = new ArrayDeque<>();
            private Deque<String> forwardHistory = new ArrayDeque<>();
            private String currentPage;
            
            BrowserHistory(String homepage) {
                this.currentPage = homepage;
            }
            
            void visit(String url) {
                if (currentPage != null) {
                    backHistory.offerLast(currentPage);
                }
                currentPage = url;
                forwardHistory.clear(); // 訪問新頁面會清空前進歷史
                System.out.println("訪問: " + url);
                showStatus();
            }
            
            String back(int steps) {
                for (int i = 0; i < steps && !backHistory.isEmpty(); i++) {
                    forwardHistory.offerFirst(currentPage);
                    currentPage = backHistory.pollLast();
                }
                System.out.println("後退 " + steps + " 步到: " + currentPage);
                showStatus();
                return currentPage;
            }
            
            String forward(int steps) {
                for (int i = 0; i < steps && !forwardHistory.isEmpty(); i++) {
                    backHistory.offerLast(currentPage);
                    currentPage = forwardHistory.pollFirst();
                }
                System.out.println("前進 " + steps + " 步到: " + currentPage);
                showStatus();
                return currentPage;
            }
            
            private void showStatus() {
                System.out.println("  當前頁面: " + currentPage);
                System.out.println("  可後退: " + backHistory.size() + " 頁");
                System.out.println("  可前進: " + forwardHistory.size() + " 頁");
                System.out.println();
            }
        }
        
        BrowserHistory browser = new BrowserHistory("google.com");
        browser.showStatus();
        
        // 模擬瀏覽行為
        browser.visit("stackoverflow.com");
        browser.visit("github.com");
        browser.visit("leetcode.com");
        
        browser.back(1);        // 回到 github.com
        browser.back(1);        // 回到 stackoverflow.com
        browser.forward(1);     // 前進到 github.com
        
        browser.visit("youtube.com"); // 訪問新頁面，會清空前進歷史
        browser.forward(1);     // 無法前進
        browser.back(2);        // 回到 stackoverflow.com
        
        System.out.println();
    }
}