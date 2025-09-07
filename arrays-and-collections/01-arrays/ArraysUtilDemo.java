import java.util.Arrays;

/**
 * Arrays 工具類示範
 * 
 * 這個類別展示了 java.util.Arrays 類的常用方法：
 * - 陣列排序
 * - 陣列搜尋
 * - 陣列複製
 * - 陣列填充
 * - 陣列比較
 * - 陣列轉字串
 */
public class ArraysUtilDemo {
    
    public static void main(String[] args) {
        System.out.println("=== Arrays 工具類示範 ===\n");
        
        // 示範排序功能
        demonstrateSorting();
        
        // 示範搜尋功能
        demonstrateSearching();
        
        // 示範複製功能
        demonstrateCopying();
        
        // 示範填充功能
        demonstrateFilling();
        
        // 示範比較功能
        demonstrateComparison();
        
        // 示範字串轉換
        demonstrateStringConversion();
    }
    
    /**
     * 示範陣列排序
     */
    public static void demonstrateSorting() {
        System.out.println("1. 陣列排序:\n");
        
        // 整數陣列排序
        int[] numbers = {64, 34, 25, 12, 22, 11, 90};
        System.out.println("原始數字陣列: " + Arrays.toString(numbers));
        Arrays.sort(numbers);
        System.out.println("排序後: " + Arrays.toString(numbers));
        
        // 字串陣列排序
        String[] names = {"Charlie", "Alice", "Bob", "Diana"};
        System.out.println("\n原始名字陣列: " + Arrays.toString(names));
        Arrays.sort(names);
        System.out.println("排序後: " + Arrays.toString(names));
        
        // 部分排序
        int[] partialSort = {5, 2, 8, 1, 9, 3, 7, 4, 6};
        System.out.println("\n原始陣列: " + Arrays.toString(partialSort));
        Arrays.sort(partialSort, 2, 6); // 只排序索引 2-5
        System.out.println("部分排序 (索引2-5): " + Arrays.toString(partialSort));
        
        System.out.println();
    }
    
    /**
     * 示範陣列搜尋
     */
    public static void demonstrateSearching() {
        System.out.println("2. 陣列搜尋 (二元搜尋):\n");
        
        int[] sortedNumbers = {1, 3, 5, 7, 9, 11, 13, 15, 17, 19};
        System.out.println("已排序陣列: " + Arrays.toString(sortedNumbers));
        
        // 搜尋存在的元素
        int target1 = 7;
        int index1 = Arrays.binarySearch(sortedNumbers, target1);
        System.out.println("搜尋 " + target1 + ": 找到在索引 " + index1);
        
        // 搜尋不存在的元素
        int target2 = 6;
        int index2 = Arrays.binarySearch(sortedNumbers, target2);
        System.out.println("搜尋 " + target2 + ": 結果 " + index2 + " (負數表示不存在)");
        
        // 在指定範圍內搜尋
        int target3 = 9;
        int index3 = Arrays.binarySearch(sortedNumbers, 2, 8, target3);
        System.out.println("在索引2-7範圍搜尋 " + target3 + ": 找到在索引 " + index3);
        
        System.out.println();
    }
    
    /**
     * 示範陣列複製
     */
    public static void demonstrateCopying() {
        System.out.println("3. 陣列複製:\n");
        
        int[] original = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        System.out.println("原始陣列: " + Arrays.toString(original));
        
        // 完整複製
        int[] fullCopy = Arrays.copyOf(original, original.length);
        System.out.println("完整複製: " + Arrays.toString(fullCopy));
        
        // 複製並擴展
        int[] extendedCopy = Arrays.copyOf(original, 15);
        System.out.println("擴展複製: " + Arrays.toString(extendedCopy));
        
        // 複製並縮短
        int[] shortenedCopy = Arrays.copyOf(original, 5);
        System.out.println("縮短複製: " + Arrays.toString(shortenedCopy));
        
        // 範圍複製
        int[] rangeCopy = Arrays.copyOfRange(original, 3, 8);
        System.out.println("範圍複製 (索引3-7): " + Arrays.toString(rangeCopy));
        
        // 驗證複製的獨立性
        original[0] = 999;
        System.out.println("\n修改原陣列後:");
        System.out.println("原始陣列: " + Arrays.toString(original));
        System.out.println("複製陣列: " + Arrays.toString(fullCopy));
        
        System.out.println();
    }
    
    /**
     * 示範陣列填充
     */
    public static void demonstrateFilling() {
        System.out.println("4. 陣列填充:\n");
        
        // 完整填充
        int[] fillArray = new int[8];
        Arrays.fill(fillArray, 42);
        System.out.println("完整填充 42: " + Arrays.toString(fillArray));
        
        // 部分填充
        Arrays.fill(fillArray, 2, 6, 99);
        System.out.println("部分填充 99 (索引2-5): " + Arrays.toString(fillArray));
        
        // 字串陣列填充
        String[] stringArray = new String[5];
        Arrays.fill(stringArray, "Hello");
        System.out.println("字串陣列填充: " + Arrays.toString(stringArray));
        
        // 部分字串填充
        Arrays.fill(stringArray, 1, 4, "World");
        System.out.println("部分字串填充: " + Arrays.toString(stringArray));
        
        System.out.println();
    }
    
    /**
     * 示範陣列比較
     */
    public static void demonstrateComparison() {
        System.out.println("5. 陣列比較:\n");
        
        int[] array1 = {1, 2, 3, 4, 5};
        int[] array2 = {1, 2, 3, 4, 5};
        int[] array3 = {1, 2, 3, 4, 6};
        
        System.out.println("陣列1: " + Arrays.toString(array1));
        System.out.println("陣列2: " + Arrays.toString(array2));
        System.out.println("陣列3: " + Arrays.toString(array3));
        
        System.out.println("陣列1 equals 陣列2: " + Arrays.equals(array1, array2));
        System.out.println("陣列1 equals 陣列3: " + Arrays.equals(array1, array3));
        
        // 多維陣列比較
        int[][] matrix1 = {{1, 2}, {3, 4}};
        int[][] matrix2 = {{1, 2}, {3, 4}};
        int[][] matrix3 = {{1, 2}, {3, 5}};
        
        System.out.println("\n二維陣列比較:");
        System.out.println("矩陣1: " + Arrays.deepToString(matrix1));
        System.out.println("矩陣2: " + Arrays.deepToString(matrix2));
        System.out.println("矩陣3: " + Arrays.deepToString(matrix3));
        
        System.out.println("矩陣1 deepEquals 矩陣2: " + Arrays.deepEquals(matrix1, matrix2));
        System.out.println("矩陣1 deepEquals 矩陣3: " + Arrays.deepEquals(matrix1, matrix3));
        
        System.out.println();
    }
    
    /**
     * 示範陣列轉字串
     */
    public static void demonstrateStringConversion() {
        System.out.println("6. 陣列轉字串:\n");
        
        // 一維陣列
        int[] numbers = {1, 2, 3, 4, 5};
        System.out.println("一維陣列 toString: " + Arrays.toString(numbers));
        
        // 字串陣列
        String[] words = {"Java", "is", "awesome"};
        System.out.println("字串陣列 toString: " + Arrays.toString(words));
        
        // 二維陣列
        int[][] matrix = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}};
        System.out.println("二維陣列 toString: " + Arrays.toString(matrix)); // 不正確的方式
        System.out.println("二維陣列 deepToString: " + Arrays.deepToString(matrix)); // 正確的方式
        
        // 三維陣列
        int[][][] cube = {{{1, 2}, {3, 4}}, {{5, 6}, {7, 8}}};
        System.out.println("三維陣列 deepToString: " + Arrays.deepToString(cube));
        
        System.out.println();
    }
}