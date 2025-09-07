import java.util.Arrays;

/**
 * 陣列基礎操作示範
 * 
 * 這個類別展示了 Java 陣列的基本操作，包括：
 * - 陣列的宣告和初始化
 * - 基本的陣列操作
 * - 陣列遍歷的不同方法
 * - 常見的陣列運算
 */
public class ArrayBasicsDemo {
    
    public static void main(String[] args) {
        System.out.println("=== Java 陣列基礎操作示範 ===\n");
        
        // 示範不同的陣列宣告方法
        demonstrateArrayDeclaration();
        
        // 示範陣列的基本操作
        demonstrateBasicOperations();
        
        // 示範陣列遍歷
        demonstrateArrayTraversal();
        
        // 示範陣列運算
        demonstrateArrayCalculations();
    }
    
    /**
     * 示範陣列宣告的不同方式
     */
    public static void demonstrateArrayDeclaration() {
        System.out.println("1. 陣列宣告方式:\n");
        
        // 方式一：先宣告，後初始化
        int[] method1;
        method1 = new int[5];
        System.out.println("方式一 - 先宣告後初始化: " + Arrays.toString(method1));
        
        // 方式二：宣告時指定大小
        int[] method2 = new int[5];
        System.out.println("方式二 - 宣告時指定大小: " + Arrays.toString(method2));
        
        // 方式三：宣告並賦值
        int[] method3 = {1, 2, 3, 4, 5};
        System.out.println("方式三 - 宣告並賦值: " + Arrays.toString(method3));
        
        // 方式四：完整形式
        int[] method4 = new int[]{10, 20, 30, 40, 50};
        System.out.println("方式四 - 完整形式: " + Arrays.toString(method4));
        
        System.out.println();
    }
    
    /**
     * 示範陣列的基本操作
     */
    public static void demonstrateBasicOperations() {
        System.out.println("2. 陣列基本操作:\n");
        
        // 創建學生分數陣列
        int[] scores = new int[5];
        
        // 賦值操作
        scores[0] = 95;
        scores[1] = 87;
        scores[2] = 92;
        scores[3] = 78;
        scores[4] = 88;
        
        System.out.println("學生分數: " + Arrays.toString(scores));
        
        // 存取操作
        System.out.println("第一個學生分數: " + scores[0]);
        System.out.println("最後一個學生分數: " + scores[scores.length - 1]);
        
        // 陣列長度
        System.out.println("班級人數: " + scores.length);
        
        // 修改操作
        scores[2] = 95; // 修改第三個學生的分數
        System.out.println("修改後分數: " + Arrays.toString(scores));
        
        System.out.println();
    }
    
    /**
     * 示範陣列遍歷的不同方法
     */
    public static void demonstrateArrayTraversal() {
        System.out.println("3. 陣列遍歷方法:\n");
        
        String[] subjects = {"數學", "英文", "物理", "化學", "生物"};
        
        // 方法一：傳統 for 迴圈
        System.out.println("方法一 - 傳統 for 迴圈:");
        for (int i = 0; i < subjects.length; i++) {
            System.out.println("  科目 " + (i + 1) + ": " + subjects[i]);
        }
        
        // 方法二：增強式 for 迴圈 (for-each)
        System.out.println("\n方法二 - 增強式 for 迴圈:");
        int index = 1;
        for (String subject : subjects) {
            System.out.println("  科目 " + index++ + ": " + subject);
        }
        
        // 方法三：while 迴圈
        System.out.println("\n方法三 - while 迴圈:");
        int i = 0;
        while (i < subjects.length) {
            System.out.println("  科目 " + (i + 1) + ": " + subjects[i]);
            i++;
        }
        
        System.out.println();
    }
    
    /**
     * 示範陣列運算
     */
    public static void demonstrateArrayCalculations() {
        System.out.println("4. 陣列運算示範:\n");
        
        double[] prices = {25.5, 30.0, 15.8, 42.3, 18.9};
        System.out.println("商品價格: " + Arrays.toString(prices));
        
        // 計算總額
        double total = 0;
        for (double price : prices) {
            total += price;
        }
        System.out.println("總金額: $" + String.format("%.2f", total));
        
        // 計算平均值
        double average = total / prices.length;
        System.out.println("平均價格: $" + String.format("%.2f", average));
        
        // 找出最高價和最低價
        double max = prices[0];
        double min = prices[0];
        
        for (int i = 1; i < prices.length; i++) {
            if (prices[i] > max) {
                max = prices[i];
            }
            if (prices[i] < min) {
                min = prices[i];
            }
        }
        
        System.out.println("最高價: $" + String.format("%.2f", max));
        System.out.println("最低價: $" + String.format("%.2f", min));
        
        // 計算高於平均價格的商品數量
        int aboveAverage = 0;
        for (double price : prices) {
            if (price > average) {
                aboveAverage++;
            }
        }
        
        System.out.println("高於平均價格的商品數量: " + aboveAverage);
        System.out.println();
    }
}