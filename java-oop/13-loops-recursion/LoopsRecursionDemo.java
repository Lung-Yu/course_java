/**
 * Java 迴圈設計與遞迴程式設計示範
 * 展示迴圈優化、遞迴演算法、效能比較和除錯技巧
 */

import java.util.*;

// ================================
// 迴圈設計模式示範
// ================================

/**
 * 迴圈設計模式和最佳實務
 */
class LoopPatterns {
    
    /**
     * 基本迴圈模式示範
     */
    public static void demonstrateBasicLoops() {
        System.out.println("=== 基本迴圈模式示範 ===");
        
        // 1. 計數迴圈
        System.out.println("1. 計數迴圈（for loop）:");
        for (int i = 1; i <= 5; i++) {
            System.out.print(i + " ");
        }
        System.out.println();
        
        // 2. 條件迴圈
        System.out.println("\n2. 條件迴圈（while loop）:");
        int count = 1;
        while (count <= 5) {
            System.out.print(count + " ");
            count++;
        }
        System.out.println();
        
        // 3. 後測試迴圈
        System.out.println("\n3. 後測試迴圈（do-while loop）:");
        int num = 1;
        do {
            System.out.print(num + " ");
            num++;
        } while (num <= 5);
        System.out.println();
        
        // 4. 增強型 for 迴圈
        System.out.println("\n4. 增強型 for 迴圈（enhanced for loop）:");
        int[] array = {1, 2, 3, 4, 5};
        for (int value : array) {
            System.out.print(value + " ");
        }
        System.out.println("\n");
    }
    
    /**
     * 進階迴圈模式示範
     */
    public static void demonstrateAdvancedLoops() {
        System.out.println("=== 進階迴圈模式示範 ===");
        
        // 1. 巢狀迴圈
        System.out.println("1. 巢狀迴圈（九九乘法表）:");
        for (int i = 1; i <= 3; i++) {
            for (int j = 1; j <= 3; j++) {
                System.out.printf("%d×%d=%d ", i, j, i*j);
            }
            System.out.println();
        }
        
        // 2. 迴圈控制語句
        System.out.println("\n2. 迴圈控制語句示範:");
        for (int i = 1; i <= 10; i++) {
            if (i == 3) continue;    // 跳過3
            if (i == 8) break;       // 在8處中斷
            System.out.print(i + " ");
        }
        System.out.println();
        
        // 3. 標籤迴圈控制
        System.out.println("\n3. 標籤迴圈控制:");
        outer: for (int i = 1; i <= 3; i++) {
            for (int j = 1; j <= 3; j++) {
                if (i == 2 && j == 2) {
                    System.out.println("中斷外層迴圈");
                    break outer;
                }
                System.out.printf("(%d,%d) ", i, j);
            }
        }
        System.out.println("\n");
    }
    
    /**
     * 迴圈效能最佳化示範
     */
    public static void demonstrateLoopOptimization() {
        System.out.println("=== 迴圈效能最佳化示範 ===");
        
        List<Integer> numbers = new ArrayList<>();
        for (int i = 0; i < 100000; i++) {
            numbers.add(i);
        }
        
        // 1. 避免重複計算
        System.out.println("1. 避免重複計算:");
        
        // 不好的做法
        long startTime = System.nanoTime();
        int sum1 = 0;
        for (int i = 0; i < numbers.size(); i++) {  // 每次都調用size()
            sum1 += numbers.get(i);
        }
        long endTime = System.nanoTime();
        System.out.println("不佳做法耗時: " + (endTime - startTime) / 1000000.0 + " ms");
        
        // 好的做法
        startTime = System.nanoTime();
        int sum2 = 0;
        int size = numbers.size();  // 預先計算size
        for (int i = 0; i < size; i++) {
            sum2 += numbers.get(i);
        }
        endTime = System.nanoTime();
        System.out.println("優化做法耗時: " + (endTime - startTime) / 1000000.0 + " ms");
        
        // 2. 使用增強型for迴圈
        System.out.println("\n2. 增強型for迴圈效能:");
        startTime = System.nanoTime();
        int sum3 = 0;
        for (Integer num : numbers) {
            sum3 += num;
        }
        endTime = System.nanoTime();
        System.out.println("增強型for迴圈耗時: " + (endTime - startTime) / 1000000.0 + " ms");
        
        // 3. Stream API
        System.out.println("\n3. Stream API效能:");
        startTime = System.nanoTime();
        int sum4 = numbers.stream().mapToInt(Integer::intValue).sum();
        endTime = System.nanoTime();
        System.out.println("Stream API耗時: " + (endTime - startTime) / 1000000.0 + " ms");
        
        System.out.println("結果驗證: " + (sum1 == sum2 && sum2 == sum3 && sum3 == sum4));
        System.out.println();
    }
}

// ================================
// 遞迴演算法示範
// ================================

/**
 * 經典遞迴演算法實作
 */
class RecursiveAlgorithms {
    
    /**
     * 階乘計算 - 基本遞迴
     */
    public static long factorial(int n) {
        // 基本情況
        if (n <= 1) {
            return 1;
        }
        // 遞迴關係
        return n * factorial(n - 1);
    }
    
    /**
     * 階乘計算 - 尾遞迴優化版本
     */
    public static long factorialTailRecursive(int n) {
        return factorialTailHelper(n, 1);
    }
    
    private static long factorialTailHelper(int n, long accumulator) {
        if (n <= 1) {
            return accumulator;
        }
        return factorialTailHelper(n - 1, n * accumulator);
    }
    
    /**
     * 費波納契數列 - 基本遞迴（效能較差）
     */
    public static long fibonacciBasic(int n) {
        if (n <= 1) {
            return n;
        }
        return fibonacciBasic(n - 1) + fibonacciBasic(n - 2);
    }
    
    /**
     * 費波納契數列 - 記憶化優化
     */
    private static Map<Integer, Long> fibonacciMemo = new HashMap<>();
    
    public static long fibonacciMemoized(int n) {
        if (n <= 1) {
            return n;
        }
        
        if (fibonacciMemo.containsKey(n)) {
            return fibonacciMemo.get(n);
        }
        
        long result = fibonacciMemoized(n - 1) + fibonacciMemoized(n - 2);
        fibonacciMemo.put(n, result);
        return result;
    }
    
    /**
     * 河內塔問題
     */
    public static void hanoi(int n, char source, char destination, char auxiliary) {
        if (n == 1) {
            System.out.println("移動圓盤 1 從 " + source + " 到 " + destination);
            return;
        }
        
        // 先將上面 n-1 個圓盤移動到輔助桿
        hanoi(n - 1, source, auxiliary, destination);
        
        // 移動最大的圓盤到目標桿
        System.out.println("移動圓盤 " + n + " 從 " + source + " 到 " + destination);
        
        // 將 n-1 個圓盤從輔助桿移動到目標桿
        hanoi(n - 1, auxiliary, destination, source);
    }
    
    /**
     * 計算河內塔步數
     */
    public static int hanoiSteps(int n) {
        if (n == 1) {
            return 1;
        }
        return 2 * hanoiSteps(n - 1) + 1;
    }
    
    /**
     * 二元搜尋 - 遞迴實作
     */
    public static int binarySearchRecursive(int[] array, int target, int left, int right) {
        if (left > right) {
            return -1;  // 未找到
        }
        
        int mid = left + (right - left) / 2;
        
        if (array[mid] == target) {
            return mid;
        } else if (array[mid] > target) {
            return binarySearchRecursive(array, target, left, mid - 1);
        } else {
            return binarySearchRecursive(array, target, mid + 1, right);
        }
    }
    
    /**
     * 快速排序 - 分治演算法
     */
    public static void quickSort(int[] array, int low, int high) {
        if (low < high) {
            int pivotIndex = partition(array, low, high);
            
            // 遞迴排序基準點左右兩部分
            quickSort(array, low, pivotIndex - 1);
            quickSort(array, pivotIndex + 1, high);
        }
    }
    
    private static int partition(int[] array, int low, int high) {
        int pivot = array[high];
        int i = (low - 1);
        
        for (int j = low; j < high; j++) {
            if (array[j] <= pivot) {
                i++;
                // 交換元素
                int temp = array[i];
                array[i] = array[j];
                array[j] = temp;
            }
        }
        
        // 交換基準點
        int temp = array[i + 1];
        array[i + 1] = array[high];
        array[high] = temp;
        
        return i + 1;
    }
}

// ================================
// 樹狀結構遞迴操作
// ================================

/**
 * 二元樹節點
 */
class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;
    
    TreeNode(int val) {
        this.val = val;
    }
    
    TreeNode(int val, TreeNode left, TreeNode right) {
        this.val = val;
        this.left = left;
        this.right = right;
    }
}

/**
 * 二元樹遞迴操作
 */
class BinaryTreeOperations {
    
    /**
     * 前序遍歷（根-左-右）
     */
    public static void preorderTraversal(TreeNode root) {
        if (root == null) {
            return;
        }
        
        System.out.print(root.val + " ");
        preorderTraversal(root.left);
        preorderTraversal(root.right);
    }
    
    /**
     * 中序遍歷（左-根-右）
     */
    public static void inorderTraversal(TreeNode root) {
        if (root == null) {
            return;
        }
        
        inorderTraversal(root.left);
        System.out.print(root.val + " ");
        inorderTraversal(root.right);
    }
    
    /**
     * 後序遍歷（左-右-根）
     */
    public static void postorderTraversal(TreeNode root) {
        if (root == null) {
            return;
        }
        
        postorderTraversal(root.left);
        postorderTraversal(root.right);
        System.out.print(root.val + " ");
    }
    
    /**
     * 計算樹的高度
     */
    public static int maxDepth(TreeNode root) {
        if (root == null) {
            return 0;
        }
        
        int leftDepth = maxDepth(root.left);
        int rightDepth = maxDepth(root.right);
        
        return Math.max(leftDepth, rightDepth) + 1;
    }
    
    /**
     * 計算樹的節點總數
     */
    public static int countNodes(TreeNode root) {
        if (root == null) {
            return 0;
        }
        
        return 1 + countNodes(root.left) + countNodes(root.right);
    }
    
    /**
     * 搜尋指定值
     */
    public static boolean searchValue(TreeNode root, int target) {
        if (root == null) {
            return false;
        }
        
        if (root.val == target) {
            return true;
        }
        
        return searchValue(root.left, target) || searchValue(root.right, target);
    }
}

// ================================
// 回溯法示範
// ================================

/**
 * 回溯法演算法示範
 */
class BacktrackingAlgorithms {
    
    /**
     * N皇后問題
     */
    public static List<List<String>> solveNQueens(int n) {
        List<List<String>> solutions = new ArrayList<>();
        int[] queens = new int[n];  // queens[i] 表示第i行皇后的列位置
        
        solveNQueensBacktrack(solutions, queens, 0, n);
        return solutions;
    }
    
    private static void solveNQueensBacktrack(List<List<String>> solutions, 
                                            int[] queens, int row, int n) {
        if (row == n) {
            // 找到一個解
            solutions.add(generateBoard(queens, n));
            return;
        }
        
        for (int col = 0; col < n; col++) {
            if (isValidPosition(queens, row, col)) {
                queens[row] = col;
                solveNQueensBacktrack(solutions, queens, row + 1, n);
                // 回溯：不需要顯式重置，因為下次迴圈會覆蓋
            }
        }
    }
    
    private static boolean isValidPosition(int[] queens, int row, int col) {
        for (int i = 0; i < row; i++) {
            // 檢查列衝突
            if (queens[i] == col) {
                return false;
            }
            
            // 檢查對角線衝突
            if (Math.abs(queens[i] - col) == Math.abs(i - row)) {
                return false;
            }
        }
        return true;
    }
    
    private static List<String> generateBoard(int[] queens, int n) {
        List<String> board = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            StringBuilder row = new StringBuilder();
            for (int j = 0; j < n; j++) {
                if (queens[i] == j) {
                    row.append("Q");
                } else {
                    row.append(".");
                }
            }
            board.add(row.toString());
        }
        return board;
    }
    
    /**
     * 組合生成
     */
    public static List<List<Integer>> generateCombinations(int n, int k) {
        List<List<Integer>> result = new ArrayList<>();
        List<Integer> current = new ArrayList<>();
        
        generateCombinationsBacktrack(result, current, 1, n, k);
        return result;
    }
    
    private static void generateCombinationsBacktrack(List<List<Integer>> result,
                                                    List<Integer> current,
                                                    int start, int n, int k) {
        if (current.size() == k) {
            result.add(new ArrayList<>(current));
            return;
        }
        
        for (int i = start; i <= n; i++) {
            current.add(i);
            generateCombinationsBacktrack(result, current, i + 1, n, k);
            current.remove(current.size() - 1);  // 回溯
        }
    }
}

// ================================
// 效能比較和除錯工具
// ================================

/**
 * 迭代 vs 遞迴效能比較
 */
class PerformanceComparison {
    
    /**
     * 階乘計算 - 迭代版本
     */
    public static long factorialIterative(int n) {
        long result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }
    
    /**
     * 費波納契數列 - 迭代版本
     */
    public static long fibonacciIterative(int n) {
        if (n <= 1) return n;
        
        long prev = 0, curr = 1;
        for (int i = 2; i <= n; i++) {
            long next = prev + curr;
            prev = curr;
            curr = next;
        }
        return curr;
    }
    
    /**
     * 效能測試方法
     */
    public static void performanceTest() {
        System.out.println("=== 迭代 vs 遞迴效能比較 ===");
        
        // 階乘效能測試
        System.out.println("1. 階乘計算效能測試 (n=20):");
        
        long startTime = System.nanoTime();
        long result1 = factorialIterative(20);
        long endTime = System.nanoTime();
        System.out.println("迭代版本: " + result1 + ", 耗時: " + (endTime - startTime) + " ns");
        
        startTime = System.nanoTime();
        long result2 = RecursiveAlgorithms.factorial(20);
        endTime = System.nanoTime();
        System.out.println("遞迴版本: " + result2 + ", 耗時: " + (endTime - startTime) + " ns");
        
        // 費波納契效能測試
        System.out.println("\n2. 費波納契數列效能測試 (n=40):");
        
        startTime = System.nanoTime();
        long fib1 = fibonacciIterative(40);
        endTime = System.nanoTime();
        System.out.println("迭代版本: " + fib1 + ", 耗時: " + (endTime - startTime) / 1000000.0 + " ms");
        
        startTime = System.nanoTime();
        long fib2 = RecursiveAlgorithms.fibonacciMemoized(40);
        endTime = System.nanoTime();
        System.out.println("記憶化遞迴: " + fib2 + ", 耗時: " + (endTime - startTime) / 1000000.0 + " ms");
        
        // 注意：不測試基本遞迴版本，因為會很慢
        System.out.println("基本遞迴版本太慢，此處略過測試");
        
        System.out.println();
    }
}

/**
 * 遞迴除錯工具
 */
class RecursionDebugger {
    private static int recursionDepth = 0;
    private static final int MAX_DEPTH = 1000;
    
    /**
     * 帶除錯的階乘計算
     */
    public static long factorialWithDebug(int n) {
        recursionDepth++;
        
        // 檢查遞迴深度
        if (recursionDepth > MAX_DEPTH) {
            throw new StackOverflowError("遞迴深度超過限制: " + MAX_DEPTH);
        }
        
        // 除錯輸出
        String indent = "  ".repeat(recursionDepth - 1);
        System.out.println(indent + "factorial(" + n + ") - 深度: " + recursionDepth);
        
        // 基本情況
        if (n <= 1) {
            System.out.println(indent + "基本情況: 回傳 1");
            recursionDepth--;
            return 1;
        }
        
        // 遞迴呼叫
        long result = n * factorialWithDebug(n - 1);
        System.out.println(indent + "factorial(" + n + ") = " + n + " * " + 
                         (result / n) + " = " + result);
        
        recursionDepth--;
        return result;
    }
    
    /**
     * 重置除錯器
     */
    public static void resetDebugger() {
        recursionDepth = 0;
    }
}

// ================================
// 主要示範類別
// ================================

public class LoopsRecursionDemo {
    
    /**
     * 建立示範二元樹
     */
    private static TreeNode createSampleTree() {
        // 建立樹結構：
        //       1
        //      / \
        //     2   3
        //    / \
        //   4   5
        
        TreeNode root = new TreeNode(1);
        root.left = new TreeNode(2);
        root.right = new TreeNode(3);
        root.left.left = new TreeNode(4);
        root.left.right = new TreeNode(5);
        
        return root;
    }
    
    /**
     * 示範基本遞迴演算法
     */
    private static void demonstrateBasicRecursion() {
        System.out.println("=== 基本遞迴演算法示範 ===");
        
        // 階乘計算
        System.out.println("1. 階乘計算:");
        for (int i = 0; i <= 6; i++) {
            System.out.println(i + "! = " + RecursiveAlgorithms.factorial(i));
        }
        
        // 費波納契數列
        System.out.println("\n2. 費波納契數列 (前10項):");
        for (int i = 0; i < 10; i++) {
            System.out.print(RecursiveAlgorithms.fibonacciMemoized(i) + " ");
        }
        System.out.println();
        
        // 河內塔
        System.out.println("\n3. 河內塔問題 (3個圓盤):");
        RecursiveAlgorithms.hanoi(3, 'A', 'C', 'B');
        System.out.println("總步數: " + RecursiveAlgorithms.hanoiSteps(3));
        
        // 二元搜尋
        System.out.println("\n4. 二元搜尋:");
        int[] sortedArray = {1, 3, 5, 7, 9, 11, 13, 15, 17, 19};
        int target = 7;
        int index = RecursiveAlgorithms.binarySearchRecursive(sortedArray, target, 0, sortedArray.length - 1);
        System.out.println("在陣列中搜尋 " + target + ", 索引: " + index);
        
        System.out.println();
    }
    
    /**
     * 示範樹狀結構操作
     */
    private static void demonstrateTreeOperations() {
        System.out.println("=== 樹狀結構遞迴操作示範 ===");
        
        TreeNode root = createSampleTree();
        
        System.out.println("樹結構:");
        System.out.println("    1");
        System.out.println("   / \\");
        System.out.println("  2   3");
        System.out.println(" / \\");
        System.out.println("4   5");
        
        System.out.print("\n前序遍歷: ");
        BinaryTreeOperations.preorderTraversal(root);
        System.out.println();
        
        System.out.print("中序遍歷: ");
        BinaryTreeOperations.inorderTraversal(root);
        System.out.println();
        
        System.out.print("後序遍歷: ");
        BinaryTreeOperations.postorderTraversal(root);
        System.out.println();
        
        System.out.println("樹的高度: " + BinaryTreeOperations.maxDepth(root));
        System.out.println("節點總數: " + BinaryTreeOperations.countNodes(root));
        System.out.println("搜尋值5: " + BinaryTreeOperations.searchValue(root, 5));
        System.out.println("搜尋值6: " + BinaryTreeOperations.searchValue(root, 6));
        
        System.out.println();
    }
    
    /**
     * 示範回溯法
     */
    private static void demonstrateBacktracking() {
        System.out.println("=== 回溯法示範 ===");
        
        // N皇后問題
        System.out.println("1. 4皇后問題解:");
        List<List<String>> solutions = BacktrackingAlgorithms.solveNQueens(4);
        for (int i = 0; i < solutions.size(); i++) {
            System.out.println("解 " + (i + 1) + ":");
            for (String row : solutions.get(i)) {
                System.out.println("  " + row);
            }
            System.out.println();
        }
        System.out.println("總共找到 " + solutions.size() + " 個解");
        
        // 組合生成
        System.out.println("\n2. 從5個數字中選3個的組合:");
        List<List<Integer>> combinations = BacktrackingAlgorithms.generateCombinations(5, 3);
        for (List<Integer> combination : combinations) {
            System.out.println("  " + combination);
        }
        System.out.println("總共 " + combinations.size() + " 種組合");
        
        System.out.println();
    }
    
    /**
     * 示範排序演算法
     */
    private static void demonstrateSorting() {
        System.out.println("=== 分治演算法示範（快速排序）===");
        
        int[] array = {64, 34, 25, 12, 22, 11, 90, 88, 76, 50, 42};
        System.out.println("原始陣列: " + Arrays.toString(array));
        
        RecursiveAlgorithms.quickSort(array, 0, array.length - 1);
        System.out.println("排序後: " + Arrays.toString(array));
        
        System.out.println();
    }
    
    /**
     * 示範遞迴除錯
     */
    private static void demonstrateRecursionDebugging() {
        System.out.println("=== 遞迴除錯示範 ===");
        
        System.out.println("計算 factorial(4) 的遞迴過程:");
        RecursionDebugger.resetDebugger();
        long result = RecursionDebugger.factorialWithDebug(4);
        System.out.println("最終結果: " + result);
        
        System.out.println();
    }
    
    public static void main(String[] args) {
        System.out.println("=== Java 迴圈設計與遞迴程式設計示範 ===\n");
        
        // 迴圈設計示範
        LoopPatterns.demonstrateBasicLoops();
        LoopPatterns.demonstrateAdvancedLoops();
        LoopPatterns.demonstrateLoopOptimization();
        
        // 遞迴演算法示範
        demonstrateBasicRecursion();
        demonstrateTreeOperations();
        demonstrateBacktracking();
        demonstrateSorting();
        
        // 效能比較
        PerformanceComparison.performanceTest();
        
        // 除錯示範
        demonstrateRecursionDebugging();
        
        System.out.println("=== 迴圈與遞迴示範完成 ===");
        System.out.println("\n重要提醒:");
        System.out.println("1. 選擇迭代或遞迴要考慮問題性質、效能需求和程式碼可讀性");
        System.out.println("2. 遞迴要注意基本情況、收斂性和堆疊溢位問題");
        System.out.println("3. 可使用記憶化和尾遞迴等技術優化遞迴效能");
        System.out.println("4. 回溯法是解決組合最佳化問題的重要技術");
    }
}