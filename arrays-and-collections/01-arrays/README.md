# Java 陣列基礎

## 學習目標

- 理解陣列的概念和特性
- 掌握陣列的宣告和初始化方法
- 學會操作一維和多維陣列
- 熟練使用 Arrays 工具類
- 了解陣列在記憶體中的存儲方式

## 1. 陣列概念

陣列是相同類型元素的集合，儲存在連續的記憶體空間中。每個元素都有一個索引（index），從 0 開始。

### 陣列特性

- **固定大小**：一旦創建，大小不能改變
- **連續記憶體**：元素在記憶體中連續存放
- **隨機存取**：可透過索引直接存取任何元素 O(1)
- **相同類型**：所有元素必須是相同的數據類型

## 2. 一維陣列

### 陣列宣告

```java
// 方式一：宣告後初始化
int[] numbers;
numbers = new int[5]; // 創建大小為 5 的整數陣列

// 方式二：宣告時初始化
int[] numbers = new int[5];

// 方式三：宣告並賦值
int[] numbers = {1, 2, 3, 4, 5};

// 方式四：完整形式
int[] numbers = new int[]{1, 2, 3, 4, 5};
```

### 陣列操作範例

```java
public class ArrayBasics {
    public static void main(String[] args) {
        // 創建陣列
        int[] scores = new int[5];
        
        // 賦值
        scores[0] = 95;
        scores[1] = 87;
        scores[2] = 92;
        scores[3] = 78;
        scores[4] = 88;
        
        // 存取元素
        System.out.println("第一個分數: " + scores[0]);
        
        // 取得陣列長度
        System.out.println("陣列長度: " + scores.length);
        
        // 遍歷陣列 - 傳統 for 迴圈
        System.out.println("所有分數:");
        for (int i = 0; i < scores.length; i++) {
            System.out.println("scores[" + i + "] = " + scores[i]);
        }
        
        // 遍歷陣列 - 增強式 for 迴圈（for-each）
        System.out.println("使用 for-each:");
        for (int score : scores) {
            System.out.println(score);
        }
        
        // 計算總分和平均分
        int total = 0;
        for (int score : scores) {
            total += score;
        }
        double average = total / (double) scores.length;
        System.out.println("總分: " + total);
        System.out.println("平均分: " + average);
    }
}
```

### 字串陣列範例

```java
public class StringArrayExample {
    public static void main(String[] args) {
        // 創建字串陣列
        String[] names = {"Alice", "Bob", "Charlie", "Diana"};
        
        // 顯示所有名字
        System.out.println("學生名單:");
        for (int i = 0; i < names.length; i++) {
            System.out.println((i + 1) + ". " + names[i]);
        }
        
        // 搜尋特定名字
        String targetName = "Bob";
        boolean found = false;
        for (int i = 0; i < names.length; i++) {
            if (names[i].equals(targetName)) {
                System.out.println(targetName + " 在索引 " + i);
                found = true;
                break;
            }
        }
        
        if (!found) {
            System.out.println(targetName + " 不在名單中");
        }
    }
}
```

## 3. 多維陣列

### 二維陣列

```java
public class TwoDimensionalArray {
    public static void main(String[] args) {
        // 創建二維陣列 - 3行4列
        int[][] matrix = new int[3][4];
        
        // 初始化二維陣列
        int[][] scores = {
            {95, 87, 92, 78},  // 第一個學生的四科成績
            {88, 91, 85, 90},  // 第二個學生的四科成績
            {92, 89, 94, 87}   // 第三個學生的四科成績
        };
        
        // 顯示二維陣列
        String[] subjects = {"數學", "英文", "物理", "化學"};
        System.out.println("學生成績表:");
        System.out.print("學生\\科目\t");
        for (String subject : subjects) {
            System.out.print(subject + "\t");
        }
        System.out.println();
        
        for (int i = 0; i < scores.length; i++) {
            System.out.print("學生" + (i + 1) + "\t");
            for (int j = 0; j < scores[i].length; j++) {
                System.out.print(scores[i][j] + "\t");
            }
            System.out.println();
        }
        
        // 計算每個學生的平均成績
        for (int i = 0; i < scores.length; i++) {
            int total = 0;
            for (int j = 0; j < scores[i].length; j++) {
                total += scores[i][j];
            }
            double average = total / (double) scores[i].length;
            System.out.println("學生" + (i + 1) + " 平均成績: " + average);
        }
        
        // 計算每科的平均成績
        for (int j = 0; j < subjects.length; j++) {
            int total = 0;
            for (int i = 0; i < scores.length; i++) {
                total += scores[i][j];
            }
            double average = total / (double) scores.length;
            System.out.println(subjects[j] + " 平均成績: " + average);
        }
    }
}
```

### 不規則陣列

```java
public class JaggedArray {
    public static void main(String[] args) {
        // 創建不規則陣列
        int[][] jaggedArray = new int[3][];
        jaggedArray[0] = new int[4];  // 第一行有4個元素
        jaggedArray[1] = new int[2];  // 第二行有2個元素
        jaggedArray[2] = new int[3];  // 第三行有3個元素
        
        // 初始化
        int value = 1;
        for (int i = 0; i < jaggedArray.length; i++) {
            for (int j = 0; j < jaggedArray[i].length; j++) {
                jaggedArray[i][j] = value++;
            }
        }
        
        // 顯示不規則陣列
        System.out.println("不規則陣列:");
        for (int i = 0; i < jaggedArray.length; i++) {
            System.out.print("第" + (i + 1) + "行: ");
            for (int j = 0; j < jaggedArray[i].length; j++) {
                System.out.print(jaggedArray[i][j] + " ");
            }
            System.out.println();
        }
    }
}
```

## 4. Arrays 工具類

Java 提供了 `java.util.Arrays` 類，包含許多有用的陣列操作方法。

```java
import java.util.Arrays;

public class ArraysUtilExample {
    public static void main(String[] args) {
        int[] numbers = {5, 2, 8, 1, 9, 3};
        
        // 顯示原始陣列
        System.out.println("原始陣列: " + Arrays.toString(numbers));
        
        // 排序
        Arrays.sort(numbers);
        System.out.println("排序後: " + Arrays.toString(numbers));
        
        // 二元搜尋 (陣列必須已排序)
        int target = 5;
        int index = Arrays.binarySearch(numbers, target);
        System.out.println("數字 " + target + " 在索引: " + index);
        
        // 填充陣列
        int[] filled = new int[5];
        Arrays.fill(filled, 42);
        System.out.println("填充陣列: " + Arrays.toString(filled));
        
        // 部分填充
        Arrays.fill(filled, 1, 4, 99);
        System.out.println("部分填充: " + Arrays.toString(filled));
        
        // 複製陣列
        int[] original = {1, 2, 3, 4, 5};
        int[] copy1 = Arrays.copyOf(original, original.length);
        int[] copy2 = Arrays.copyOfRange(original, 1, 4); // 複製索引 1-3
        
        System.out.println("原陣列: " + Arrays.toString(original));
        System.out.println("完整複製: " + Arrays.toString(copy1));
        System.out.println("部分複製: " + Arrays.toString(copy2));
        
        // 比較陣列
        int[] array1 = {1, 2, 3};
        int[] array2 = {1, 2, 3};
        int[] array3 = {1, 2, 4};
        
        System.out.println("array1 equals array2: " + Arrays.equals(array1, array2));
        System.out.println("array1 equals array3: " + Arrays.equals(array1, array3));
        
        // 多維陣列的字串表示
        int[][] matrix = {{1, 2}, {3, 4}, {5, 6}};
        System.out.println("二維陣列: " + Arrays.deepToString(matrix));
        
        // 多維陣列比較
        int[][] matrix1 = {{1, 2}, {3, 4}};
        int[][] matrix2 = {{1, 2}, {3, 4}};
        System.out.println("二維陣列相等: " + Arrays.deepEquals(matrix1, matrix2));
    }
}
```

## 5. 陣列與記憶體

### 陣列在記憶體中的表示

```java
public class ArrayMemoryExample {
    public static void main(String[] args) {
        // 基本型別陣列
        int[] primitiveArray = {1, 2, 3, 4, 5};
        
        // 物件陣列
        String[] objectArray = {"Hello", "World", "Java"};
        
        // 陣列本身也是物件
        System.out.println("陣列物件位址: " + primitiveArray);
        System.out.println("陣列長度: " + primitiveArray.length);
        
        // 陣列賦值是引用賦值
        int[] anotherRef = primitiveArray;
        anotherRef[0] = 100;
        
        System.out.println("原陣列第一個元素: " + primitiveArray[0]); // 輸出 100
        System.out.println("兩個引用指向同一陣列: " + (primitiveArray == anotherRef));
        
        // 複製陣列以避免引用問題
        int[] realCopy = Arrays.copyOf(primitiveArray, primitiveArray.length);
        realCopy[0] = 200;
        
        System.out.println("原陣列第一個元素: " + primitiveArray[0]); // 還是 100
        System.out.println("複製陣列第一個元素: " + realCopy[0]); // 輸出 200
    }
}
```

## 6. 常見操作範例

### 陣列反轉

```java
public class ArrayReverse {
    public static void reverseArray(int[] arr) {
        int left = 0;
        int right = arr.length - 1;
        
        while (left < right) {
            // 交換元素
            int temp = arr[left];
            arr[left] = arr[right];
            arr[right] = temp;
            
            left++;
            right--;
        }
    }
    
    public static void main(String[] args) {
        int[] numbers = {1, 2, 3, 4, 5, 6};
        
        System.out.println("原陣列: " + Arrays.toString(numbers));
        reverseArray(numbers);
        System.out.println("反轉後: " + Arrays.toString(numbers));
    }
}
```

### 找出最大值和最小值

```java
public class FindMinMax {
    public static int[] findMinMax(int[] arr) {
        if (arr == null || arr.length == 0) {
            throw new IllegalArgumentException("陣列不能為空");
        }
        
        int min = arr[0];
        int max = arr[0];
        
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] < min) {
                min = arr[i];
            }
            if (arr[i] > max) {
                max = arr[i];
            }
        }
        
        return new int[]{min, max};
    }
    
    public static void main(String[] args) {
        int[] numbers = {3, 7, 1, 9, 4, 6, 2};
        int[] result = findMinMax(numbers);
        
        System.out.println("陣列: " + Arrays.toString(numbers));
        System.out.println("最小值: " + result[0]);
        System.out.println("最大值: " + result[1]);
    }
}
```

## 重點整理

### 優點
- **效能優異**：O(1) 隨機存取時間
- **記憶體效率**：連續儲存，無額外開銷
- **簡單易用**：語法簡潔，容易理解

### 限制
- **固定大小**：創建後無法改變大小
- **插入/刪除成本高**：需要移動其他元素
- **類型限制**：只能儲存相同類型的元素

### 最佳實踐
1. **使用有意義的變數名**：讓程式碼更容易理解
2. **避免魔法數字**：使用常數定義陣列大小
3. **檢查邊界**：防止 `ArrayIndexOutOfBoundsException`
4. **考慮使用 Arrays 工具類**：提供許多便利方法
5. **了解引用語義**：注意陣列賦值的引用特性

## 下一步

陣列是理解更複雜數據結構的基礎。接下來我們將學習 Java 集合框架，它提供了更靈活和功能豐富的數據結構選擇。

---

**練習建議**：嘗試實作更多陣列操作，如排序算法、搜尋算法等，這將幫助您更深入地理解陣列的特性和用法。