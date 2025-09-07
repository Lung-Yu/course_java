import java.util.*;

/**
 * LinkedList 詳細示範
 * 
 * 這個類別展示了 LinkedList 的各種特性和操作：
 * - 作為 List 的基本操作
 * - 作為 Deque 的雙端操作
 * - 作為 Queue 的佇列操作
 * - 作為 Stack 的堆疊操作
 * - 效能特點展示
 */
public class LinkedListDemo {
    
    public static void main(String[] args) {
        System.out.println("=== LinkedList 詳細示範 ===\n");
        
        // List 基本操作
        demonstrateListOperations();
        
        // Deque 雙端操作
        demonstrateDequeOperations();
        
        // Queue 佇列操作
        demonstrateQueueOperations();
        
        // Stack 堆疊操作
        demonstrateStackOperations();
        
        // 迭代器操作
        demonstrateIteratorOperations();
        
        // 效能特點
        demonstratePerformanceCharacteristics();
    }
    
    /**
     * List 基本操作
     */
    public static void demonstrateListOperations() {
        System.out.println("1. LinkedList 作為 List 的操作:\n");
        
        LinkedList<String> cities = new LinkedList<>();
        
        // 基本添加操作
        cities.add("台北");
        cities.add("台中");
        cities.add("高雄");
        System.out.println("初始城市列表: " + cities);
        
        // 指定位置插入
        cities.add(1, "桃園");
        System.out.println("插入桃園後: " + cities);
        
        // 存取操作
        String firstCity = cities.get(0);
        String lastCity = cities.get(cities.size() - 1);
        System.out.println("第一個城市: " + firstCity);
        System.out.println("最後一個城市: " + lastCity);
        
        // 修改操作
        cities.set(2, "新竹");
        System.out.println("修改後: " + cities);
        
        // 查找操作
        int index = cities.indexOf("台中");
        boolean contains = cities.contains("台北");
        System.out.println("台中的索引: " + index);
        System.out.println("是否包含台北: " + contains);
        
        // 移除操作
        cities.remove("桃園");           // 按值移除
        String removed = cities.remove(1); // 按索引移除
        System.out.println("移除的城市: " + removed);
        System.out.println("移除後: " + cities);
        
        System.out.println();
    }
    
    /**
     * Deque 雙端操作
     */
    public static void demonstrateDequeOperations() {
        System.out.println("2. LinkedList 作為 Deque 的操作:\n");
        
        LinkedList<Integer> deque = new LinkedList<>();
        
        // 在兩端添加元素
        deque.addFirst(1);        // 在開頭添加
        deque.addLast(2);         // 在結尾添加
        deque.addFirst(0);        // 在開頭添加
        deque.addLast(3);         // 在結尾添加
        
        System.out.println("雙端添加後: " + deque);
        
        // 檢查兩端元素 (不移除)
        Integer first = deque.peekFirst();
        Integer last = deque.peekLast();
        System.out.println("首元素 (不移除): " + first);
        System.out.println("尾元素 (不移除): " + last);
        System.out.println("檢查後: " + deque);
        
        // 獲取兩端元素 (拋出異常如果為空)
        Integer getFirst = deque.getFirst();
        Integer getLast = deque.getLast();
        System.out.println("首元素 (getFirst): " + getFirst);
        System.out.println("尾元素 (getLast): " + getLast);
        
        // 移除兩端元素
        Integer removedFirst = deque.removeFirst();
        Integer removedLast = deque.removeLast();
        System.out.println("移除的首元素: " + removedFirst);
        System.out.println("移除的尾元素: " + removedLast);
        System.out.println("移除後: " + deque);
        
        // 安全的移除操作 (返回 null 如果為空)
        deque.clear();
        Integer polledFirst = deque.pollFirst();
        Integer polledLast = deque.pollLast();
        System.out.println("空 deque pollFirst: " + polledFirst);
        System.out.println("空 deque pollLast: " + polledLast);
        
        System.out.println();
    }
    
    /**
     * Queue 佇列操作
     */
    public static void demonstrateQueueOperations() {
        System.out.println("3. LinkedList 作為 Queue 的操作:\n");
        
        Queue<String> queue = new LinkedList<>();
        
        // 入隊操作
        queue.offer("第一個顧客");
        queue.offer("第二個顧客");
        queue.offer("第三個顧客");
        queue.offer("第四個顧客");
        
        System.out.println("銀行排隊隊列: " + queue);
        System.out.println("隊列大小: " + queue.size());
        
        // 檢查隊首元素 (不移除)
        String nextCustomer = queue.peek();
        System.out.println("下一個顧客: " + nextCustomer);
        System.out.println("檢查後隊列: " + queue);
        
        // 出隊操作
        System.out.println("\n開始服務顧客:");
        while (!queue.isEmpty()) {
            String customer = queue.poll();
            System.out.println("正在服務: " + customer);
            System.out.println("剩餘等待: " + queue);
        }
        
        // 空隊列操作
        String emptyPeek = queue.peek();
        String emptyPoll = queue.poll();
        System.out.println("\n空隊列 peek: " + emptyPeek);
        System.out.println("空隊列 poll: " + emptyPoll);
        
        // element() 和 remove() 會拋出異常
        try {
            queue.element(); // 會拋出 NoSuchElementException
        } catch (NoSuchElementException e) {
            System.out.println("空隊列 element() 拋出異常: " + e.getClass().getSimpleName());
        }
        
        System.out.println();
    }
    
    /**
     * Stack 堆疊操作
     */
    public static void demonstrateStackOperations() {
        System.out.println("4. LinkedList 作為 Stack 的操作:\n");
        
        LinkedList<String> stack = new LinkedList<>();
        
        // 入棧操作
        stack.push("底部書籍");
        stack.push("中間書籍");
        stack.push("頂部書籍");
        
        System.out.println("書籍堆疊: " + stack);
        
        // 檢查棧頂元素 (不移除)
        String topBook = stack.peek();
        System.out.println("棧頂書籍: " + topBook);
        
        // 出棧操作
        System.out.println("\n取出書籍:");
        while (!stack.isEmpty()) {
            String book = stack.pop();
            System.out.println("取出: " + book);
            System.out.println("剩餘: " + stack);
        }
        
        // 空棧操作
        try {
            stack.pop(); // 會拋出 NoSuchElementException
        } catch (NoSuchElementException e) {
            System.out.println("\n空棧 pop() 拋出異常: " + e.getClass().getSimpleName());
        }
        
        // 使用 pollFirst() 作為安全的 pop()
        String safePop = stack.pollFirst();
        System.out.println("安全的空棧操作: " + safePop);
        
        System.out.println();
    }
    
    /**
     * 迭代器操作
     */
    public static void demonstrateIteratorOperations() {
        System.out.println("5. LinkedList 迭代器操作:\n");
        
        LinkedList<Integer> numbers = new LinkedList<>();
        for (int i = 1; i <= 5; i++) {
            numbers.add(i);
        }
        
        System.out.println("原始列表: " + numbers);
        
        // ListIterator 正向遍歷
        System.out.println("\n正向遍歷:");
        ListIterator<Integer> listIterator = numbers.listIterator();
        while (listIterator.hasNext()) {
            int index = listIterator.nextIndex();
            Integer value = listIterator.next();
            System.out.println("索引 " + index + ": " + value);
        }
        
        // ListIterator 反向遍歷
        System.out.println("\n反向遍歷:");
        while (listIterator.hasPrevious()) {
            int index = listIterator.previousIndex();
            Integer value = listIterator.previous();
            System.out.println("索引 " + index + ": " + value);
        }
        
        // 使用迭代器修改列表
        System.out.println("\n使用迭代器修改:");
        listIterator = numbers.listIterator();
        while (listIterator.hasNext()) {
            Integer value = listIterator.next();
            if (value % 2 == 0) {
                listIterator.set(value * 10); // 偶數乘以 10
            }
        }
        System.out.println("修改後: " + numbers);
        
        // 使用迭代器添加元素
        System.out.println("\n使用迭代器添加:");
        listIterator = numbers.listIterator();
        while (listIterator.hasNext()) {
            Integer value = listIterator.next();
            if (value > 10) {
                listIterator.add(999); // 在大於 10 的元素後添加 999
            }
        }
        System.out.println("添加後: " + numbers);
        
        // 使用迭代器移除元素
        System.out.println("\n使用迭代器移除:");
        Iterator<Integer> iterator = numbers.iterator();
        while (iterator.hasNext()) {
            Integer value = iterator.next();
            if (value == 999) {
                iterator.remove(); // 移除 999
            }
        }
        System.out.println("移除後: " + numbers);
        
        System.out.println();
    }
    
    /**
     * 效能特點展示
     */
    public static void demonstratePerformanceCharacteristics() {
        System.out.println("6. LinkedList 效能特點:\n");
        
        LinkedList<Integer> linkedList = new LinkedList<>();
        
        // 首尾操作效能測試
        System.out.println("首尾操作效能測試:");
        
        long startTime = System.nanoTime();
        for (int i = 0; i < 100000; i++) {
            linkedList.addFirst(i);
        }
        long firstTime = System.nanoTime() - startTime;
        
        startTime = System.nanoTime();
        for (int i = 0; i < 100000; i++) {
            linkedList.addLast(i);
        }
        long lastTime = System.nanoTime() - startTime;
        
        System.out.printf("addFirst 10萬次: %.2f ms\n", firstTime / 1_000_000.0);
        System.out.printf("addLast 10萬次: %.2f ms\n", lastTime / 1_000_000.0);
        
        // 隨機存取效能警告
        System.out.println("\n隨機存取效能警告:");
        System.out.println("LinkedList 大小: " + linkedList.size());
        
        startTime = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            linkedList.get(linkedList.size() / 2); // 存取中間元素
        }
        long randomAccessTime = System.nanoTime() - startTime;
        
        System.out.printf("隨機存取中間元素1000次: %.2f ms\n", randomAccessTime / 1_000_000.0);
        System.out.println("注意：LinkedList 的隨機存取效能較差，應避免頻繁使用 get(index)");
        
        // 順序遍歷效能
        startTime = System.nanoTime();
        int count = 0;
        for (@SuppressWarnings("unused") Integer value : linkedList) {
            count++;
        }
        long iterationTime = System.nanoTime() - startTime;
        
        System.out.printf("使用增強式for迴圈遍歷: %.2f ms\n", iterationTime / 1_000_000.0);
        System.out.println("遍歷元素數量: " + count);
        System.out.println("建議：使用迭代器或增強式for迴圈進行遍歷");
        
        System.out.println();
    }
}