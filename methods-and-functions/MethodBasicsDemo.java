/**
 * 方法基礎演示
 * 
 * 本檔案演示：
 * 1. 基本方法定義和調用
 * 2. 參數傳遞
 * 3. 回傳值使用
 * 4. 方法的實際應用
 * 
 * @author Java Course
 * @version 1.0
 */

import java.util.Scanner;

public class MethodBasicsDemo {
    
    // === 基本方法範例 ===
    
    /**
     * 簡單的問候方法
     */
    public static void greet() {
        System.out.println("歡迎學習 Java 方法！");
        System.out.println("方法讓程式更模組化、更易維護");
    }
    
    /**
     * 帶參數的問候方法
     * @param name 使用者姓名
     */
    public static void greetUser(String name) {
        System.out.println("Hello, " + name + "!");
        System.out.println("很高興見到你！");
    }
    
    /**
     * 計算兩個數的和
     * @param a 第一個數
     * @param b 第二個數
     * @return 兩數之和
     */
    public static int add(int a, int b) {
        int sum = a + b;
        System.out.println("計算過程：" + a + " + " + b + " = " + sum);
        return sum;
    }
    
    /**
     * 檢查數字是否為質數
     * @param number 要檢查的數字
     * @return true 如果是質數，否則 false
     */
    public static boolean isPrime(int number) {
        if (number <= 1) {
            return false;
        }
        
        if (number == 2) {
            return true;
        }
        
        if (number % 2 == 0) {
            return false;
        }
        
        // 只需要檢查到平方根
        for (int i = 3; i * i <= number; i += 2) {
            if (number % i == 0) {
                return false;
            }
        }
        
        return true;
    }
    
    // === 實用工具方法 ===
    
    /**
     * 計算陣列的平均值
     * @param numbers 數字陣列
     * @return 平均值，如果陣列為空則返回 0
     */
    public static double calculateAverage(int[] numbers) {
        if (numbers == null || numbers.length == 0) {
            System.out.println("警告：陣列為空，返回 0");
            return 0.0;
        }
        
        int sum = 0;
        for (int number : numbers) {
            sum += number;
        }
        
        double average = (double) sum / numbers.length;
        System.out.printf("陣列元素總和：%d，個數：%d，平均值：%.2f%n", 
                         sum, numbers.length, average);
        return average;
    }
    
    /**
     * 在陣列中尋找最大值
     * @param numbers 數字陣列
     * @return 最大值
     * @throws IllegalArgumentException 如果陣列為空或 null
     */
    public static int findMax(int[] numbers) {
        if (numbers == null || numbers.length == 0) {
            throw new IllegalArgumentException("陣列不能為空");
        }
        
        int max = numbers[0];
        int maxIndex = 0;
        
        for (int i = 1; i < numbers.length; i++) {
            if (numbers[i] > max) {
                max = numbers[i];
                maxIndex = i;
            }
        }
        
        System.out.println("最大值 " + max + " 位於索引 " + maxIndex);
        return max;
    }
    
    /**
     * 格式化顯示陣列
     * @param array 要顯示的陣列
     * @param title 標題
     */
    public static void printArray(int[] array, String title) {
        System.out.println("\n" + title + ":");
        if (array == null) {
            System.out.println("  null");
            return;
        }
        
        if (array.length == 0) {
            System.out.println("  []");
            return;
        }
        
        System.out.print("  [");
        for (int i = 0; i < array.length; i++) {
            System.out.print(array[i]);
            if (i < array.length - 1) {
                System.out.print(", ");
            }
        }
        System.out.println("]");
    }
    
    // === 字串處理方法 ===
    
    /**
     * 計算字串中指定字符的出現次數
     * @param text 源字串
     * @param target 目標字符
     * @return 出現次數
     */
    public static int countCharacter(String text, char target) {
        if (text == null) {
            return 0;
        }
        
        int count = 0;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == target) {
                count++;
            }
        }
        
        System.out.printf("字串 \"%s\" 中字符 '%c' 出現了 %d 次%n", 
                         text, target, count);
        return count;
    }
    
    /**
     * 反轉字串
     * @param text 原字串
     * @return 反轉後的字串
     */
    public static String reverseString(String text) {
        if (text == null) {
            return null;
        }
        
        StringBuilder reversed = new StringBuilder();
        for (int i = text.length() - 1; i >= 0; i--) {
            reversed.append(text.charAt(i));
        }
        
        String result = reversed.toString();
        System.out.println("\"" + text + "\" 反轉後為 \"" + result + "\"");
        return result;
    }
    
    // === 數學計算方法 ===
    
    /**
     * 計算 x 的 n 次方
     * @param base 底數
     * @param exponent 指數
     * @return base^exponent
     */
    public static long power(int base, int exponent) {
        if (exponent < 0) {
            throw new IllegalArgumentException("指數不能為負數");
        }
        
        if (exponent == 0) {
            return 1;
        }
        
        long result = 1;
        for (int i = 0; i < exponent; i++) {
            result *= base;
        }
        
        System.out.printf("%d^%d = %d%n", base, exponent, result);
        return result;
    }
    
    /**
     * 計算最大公約數 (GCD)
     * @param a 第一個數
     * @param b 第二個數
     * @return 最大公約數
     */
    public static int gcd(int a, int b) {
        // 確保都是正數
        a = Math.abs(a);
        b = Math.abs(b);
        
        System.out.printf("計算 %d 和 %d 的最大公約數：%n", a, b);
        
        // 歐幾里得演算法
        while (b != 0) {
            System.out.printf("  %d = %d × %d + %d%n", a, a/b, b, a%b);
            int temp = b;
            b = a % b;
            a = temp;
        }
        
        System.out.printf("最大公約數：%d%n", a);
        return a;
    }
    
    // === 互動式示例 ===
    
    /**
     * 簡單的計算器功能
     */
    public static void simpleCalculator() {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("\n=== 簡單計算器 ===");
            System.out.print("請輸入第一個數字：");
            double num1 = scanner.nextDouble();
            
            System.out.print("請輸入運算符 (+, -, *, /)：");
            char operator = scanner.next().charAt(0);
            
            System.out.print("請輸入第二個數字：");
            double num2 = scanner.nextDouble();
            
            double result = calculate(num1, operator, num2);
            System.out.printf("%.2f %c %.2f = %.2f%n", num1, operator, num2, result);
        }
    }
    
    /**
     * 執行計算
     * @param num1 第一個數
     * @param operator 運算符
     * @param num2 第二個數
     * @return 計算結果
     */
    private static double calculate(double num1, char operator, double num2) {
        switch (operator) {
            case '+':
                return num1 + num2;
            case '-':
                return num1 - num2;
            case '*':
                return num1 * num2;
            case '/':
                if (num2 == 0) {
                    throw new ArithmeticException("除數不能為零");
                }
                return num1 / num2;
            default:
                throw new IllegalArgumentException("不支援的運算符：" + operator);
        }
    }
    
    // === 主方法：演示所有功能 ===
    
    public static void main(String[] args) {
        System.out.println("=========================================");
        System.out.println("           Java 方法基礎演示            ");
        System.out.println("=========================================");
        
        // 1. 基本方法調用
        System.out.println("\n1. 基本方法調用");
        greet();
        greetUser("程式設計愛好者");
        
        // 2. 有回傳值的方法
        System.out.println("\n2. 有回傳值的方法");
        int sum = add(25, 17);
        System.out.println("儲存的結果：" + sum);
        
        // 3. 質數檢查
        System.out.println("\n3. 質數檢查");
        int[] testNumbers = {2, 3, 4, 17, 25, 29};
        for (int num : testNumbers) {
            boolean prime = isPrime(num);
            System.out.println(num + (prime ? " 是質數" : " 不是質數"));
        }
        
        // 4. 陣列處理
        System.out.println("\n4. 陣列處理");
        int[] numbers = {12, 45, 3, 89, 67, 23, 91, 15};
        printArray(numbers, "測試陣列");
        
        double average = calculateAverage(numbers);
        System.out.printf("平均值：%.2f%n", average);
        
        try {
            int max = findMax(numbers);
            System.out.println("最大值：" + max);
        } catch (IllegalArgumentException e) {
            System.out.println("錯誤：" + e.getMessage());
        }
        
        // 5. 字串處理
        System.out.println("\n5. 字串處理");
        String testString = "Hello Java Programming";
        countCharacter(testString, 'a');
        countCharacter(testString, 'l');
        reverseString(testString);
        
        // 6. 數學計算
        System.out.println("\n6. 數學計算");
        power(2, 10);
        power(3, 4);
        gcd(48, 18);
        gcd(100, 75);
        
        // 7. 測試空陣列處理
        System.out.println("\n7. 邊界情況測試");
        int[] emptyArray = {};
        printArray(emptyArray, "空陣列");
        calculateAverage(emptyArray);
        
        try {
            findMax(emptyArray);
        } catch (IllegalArgumentException e) {
            System.out.println("捕獲異常：" + e.getMessage());
        }
        
        // 8. 互動式計算器（可選）
        System.out.println("\n8. 互動式功能（輸入示例）");
        System.out.println("計算器功能可用，但在此演示中跳過互動部分");
        // 如果需要測試互動功能，請取消註解下一行
        // simpleCalculator();
        
        System.out.println("\n=========================================");
        System.out.println("           演示結束，感謝學習！          ");
        System.out.println("=========================================");
    }
}