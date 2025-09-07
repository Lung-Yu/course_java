# 陣列練習題解答

## 🟢 基礎練習解答

### 練習 1.1：陣列基本操作

**完整解答：**

```java
public class ArrayBasicsAnswer {
    
    /**
     * 陣列基本操作演示
     * 時間複雜度: O(n) - 需要遍歷陣列
     * 空間複雜度: O(1) - 只使用固定額外空間
     */
    public static void demonstrateArrayOperations() {
        // 1. 創建和初始化陣列
        int[] numbers = {5, 2, 8, 1, 9, 3};
        System.out.println("原始陣列: " + Arrays.toString(numbers));
        
        // 2. 訪問元素（注意邊界檢查）
        System.out.println("第一個元素: " + numbers[0]);
        System.out.println("最後一個元素: " + numbers[numbers.length - 1]);
        
        // 3. 修改元素
        numbers[0] = 10;
        System.out.println("修改後: " + Arrays.toString(numbers));
        
        // 4. 計算統計資訊
        int sum = calculateSum(numbers);
        double average = (double) sum / numbers.length;
        int max = findMax(numbers);
        int min = findMin(numbers);
        
        System.out.println("總和: " + sum);
        System.out.println("平均值: " + average);
        System.out.println("最大值: " + max);
        System.out.println("最小值: " + min);
    }
    
    /**
     * 計算陣列總和
     * @param arr 輸入陣列
     * @return 陣列元素的總和
     */
    public static int calculateSum(int[] arr) {
        if (arr == null) {
            throw new IllegalArgumentException("陣列不能為 null");
        }
        
        int sum = 0;
        for (int num : arr) {
            sum += num;
        }
        return sum;
    }
    
    /**
     * 找到陣列中的最大值
     * @param arr 輸入陣列
     * @return 最大值
     */
    public static int findMax(int[] arr) {
        if (arr == null || arr.length == 0) {
            throw new IllegalArgumentException("陣列不能為空");
        }
        
        int max = arr[0];
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] > max) {
                max = arr[i];
            }
        }
        return max;
    }
    
    /**
     * 找到陣列中的最小值
     * @param arr 輸入陣列
     * @return 最小值
     */
    public static int findMin(int[] arr) {
        if (arr == null || arr.length == 0) {
            throw new IllegalArgumentException("陣列不能為空");
        }
        
        int min = arr[0];
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] < min) {
                min = arr[i];
            }
        }
        return min;
    }
    
    public static void main(String[] args) {
        demonstrateArrayOperations();
    }
}
```

**關鍵概念：**
- **邊界檢查**：防止陣列越界訪問
- **防禦性程式設計**：檢查 null 輸入
- **增強型 for 迴圈**：更簡潔的陣列遍歷方式

---

### 練習 1.2：陣列排序和搜尋

**完整解答：**

```java
public class ArraySortSearchAnswer {
    
    /**
     * 冒泡排序實現
     * 時間複雜度: O(n²)
     * 空間複雜度: O(1)
     * 
     * 穩定排序：相等元素的相對位置不變
     */
    public static void bubbleSort(int[] arr) {
        if (arr == null || arr.length <= 1) {
            return;
        }
        
        int n = arr.length;
        boolean swapped;
        
        for (int i = 0; i < n - 1; i++) {
            swapped = false;
            
            // 每次內層迴圈將最大元素"冒泡"到末尾
            for (int j = 0; j < n - 1 - i; j++) {
                if (arr[j] > arr[j + 1]) {
                    swap(arr, j, j + 1);
                    swapped = true;
                }
            }
            
            // 如果沒有交換，陣列已排序
            if (!swapped) {
                break;
            }
        }
    }
    
    /**
     * 線性搜尋實現
     * 時間複雜度: O(n)
     * 空間複雜度: O(1)
     */
    public static int linearSearch(int[] arr, int target) {
        if (arr == null) {
            return -1;
        }
        
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == target) {
                return i;
            }
        }
        return -1;
    }
    
    /**
     * 二分搜尋實現（要求陣列已排序）
     * 時間複雜度: O(log n)
     * 空間複雜度: O(1)
     */
    public static int binarySearch(int[] arr, int target) {
        if (arr == null) {
            return -1;
        }
        
        int left = 0;
        int right = arr.length - 1;
        
        while (left <= right) {
            // 防止整數溢位的中點計算
            int mid = left + (right - left) / 2;
            
            if (arr[mid] == target) {
                return mid;
            } else if (arr[mid] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        
        return -1;
    }
    
    /**
     * 工具方法：交換陣列中兩個元素
     */
    private static void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
    
    /**
     * 測試方法
     */
    public static void main(String[] args) {
        int[] testArray = {64, 34, 25, 12, 22, 11, 90};
        System.out.println("原始陣列: " + Arrays.toString(testArray));
        
        // 測試冒泡排序
        int[] sortArray = testArray.clone();
        bubbleSort(sortArray);
        System.out.println("排序後: " + Arrays.toString(sortArray));
        
        // 測試線性搜尋
        int target = 25;
        int linearResult = linearSearch(testArray, target);
        System.out.println("線性搜尋 " + target + ": 索引 " + linearResult);
        
        // 測試二分搜尋（需要已排序陣列）
        int binaryResult = binarySearch(sortArray, target);
        System.out.println("二分搜尋 " + target + ": 索引 " + binaryResult);
    }
}
```

**演算法分析：**

| 演算法 | 時間複雜度 | 空間複雜度 | 穩定性 | 適用場景 |
|--------|------------|------------|--------|----------|
| 冒泡排序 | O(n²) | O(1) | 穩定 | 小型資料集、教學 |
| 線性搜尋 | O(n) | O(1) | - | 未排序陣列 |
| 二分搜尋 | O(log n) | O(1) | - | 已排序陣列 |

---

## 🟡 中等練習解答

### 練習 2.1：陣列重新排列

**完整解答：**

```java
public class ArrayRearrangeAnswer {
    
    /**
     * 將偶數移到前面，奇數移到後面
     * 時間複雜度: O(n)
     * 空間複雜度: O(1)
     * 
     * 使用雙指標技術保持原地操作
     */
    public static void segregateEvenOdd(int[] arr) {
        if (arr == null || arr.length <= 1) {
            return;
        }
        
        int left = 0;
        int right = arr.length - 1;
        
        while (left < right) {
            // 從左邊找奇數
            while (left < right && arr[left] % 2 == 0) {
                left++;
            }
            
            // 從右邊找偶數
            while (left < right && arr[right] % 2 == 1) {
                right--;
            }
            
            // 交換奇數和偶數
            if (left < right) {
                swap(arr, left, right);
                left++;
                right--;
            }
        }
    }
    
    /**
     * 移動所有零到陣列末尾
     * 時間複雜度: O(n)
     * 空間複雜度: O(1)
     * 
     * 保持非零元素的相對順序
     */
    public static void moveZerosToEnd(int[] arr) {
        if (arr == null || arr.length <= 1) {
            return;
        }
        
        int writeIndex = 0;
        
        // 第一次遍歷：將所有非零元素移到前面
        for (int readIndex = 0; readIndex < arr.length; readIndex++) {
            if (arr[readIndex] != 0) {
                arr[writeIndex] = arr[readIndex];
                writeIndex++;
            }
        }
        
        // 第二次遍歷：將剩餘位置填充為零
        while (writeIndex < arr.length) {
            arr[writeIndex] = 0;
            writeIndex++;
        }
    }
    
    /**
     * 旋轉陣列 k 位
     * 時間複雜度: O(n)
     * 空間複雜度: O(1)
     * 
     * 使用三次反轉的巧妙方法
     */
    public static void rotateArray(int[] arr, int k) {
        if (arr == null || arr.length <= 1) {
            return;
        }
        
        int n = arr.length;
        k = k % n;  // 處理 k 大於陣列長度的情況
        
        // 1. 反轉整個陣列
        reverse(arr, 0, n - 1);
        
        // 2. 反轉前 k 個元素
        reverse(arr, 0, k - 1);
        
        // 3. 反轉剩餘元素
        reverse(arr, k, n - 1);
    }
    
    /**
     * 反轉陣列指定範圍的元素
     */
    private static void reverse(int[] arr, int start, int end) {
        while (start < end) {
            swap(arr, start, end);
            start++;
            end--;
        }
    }
    
    private static void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
    
    /**
     * 測試方法
     */
    public static void main(String[] args) {
        // 測試偶奇數分離
        int[] test1 = {1, 2, 3, 4, 5, 6, 7, 8};
        System.out.println("分離前: " + Arrays.toString(test1));
        segregateEvenOdd(test1);
        System.out.println("分離後: " + Arrays.toString(test1));
        
        // 測試移動零
        int[] test2 = {0, 1, 0, 3, 12};
        System.out.println("移動零前: " + Arrays.toString(test2));
        moveZerosToEnd(test2);
        System.out.println("移動零後: " + Arrays.toString(test2));
        
        // 測試陣列旋轉
        int[] test3 = {1, 2, 3, 4, 5, 6, 7};
        System.out.println("旋轉前: " + Arrays.toString(test3));
        rotateArray(test3, 3);
        System.out.println("旋轉 3 位後: " + Arrays.toString(test3));
    }
}
```

**核心技巧：**
- **雙指標技術**：用於原地重新排列
- **三次反轉法**：巧妙實現陣列旋轉
- **讀寫指標分離**：保持元素相對順序

---

### 練習 2.2：最大子陣列和（Kadane's 演算法）

**完整解答：**

```java
public class MaxSubarrayAnswer {
    
    /**
     * Kadane's 演算法找最大子陣列和
     * 時間複雜度: O(n)
     * 空間複雜度: O(1)
     * 
     * 核心思想：局部最優解推導全局最優解
     */
    public static int maxSubarraySum(int[] nums) {
        if (nums == null || nums.length == 0) {
            throw new IllegalArgumentException("陣列不能為空");
        }
        
        int maxSum = nums[0];     // 全局最大和
        int currentSum = nums[0]; // 當前子陣列和
        
        for (int i = 1; i < nums.length; i++) {
            // 關鍵決策：是否將當前元素加入已有子陣列
            currentSum = Math.max(nums[i], currentSum + nums[i]);
            
            // 更新全局最大和
            maxSum = Math.max(maxSum, currentSum);
        }
        
        return maxSum;
    }
    
    /**
     * 返回最大子陣列的起始和結束索引
     * 時間複雜度: O(n)
     * 空間複雜度: O(1)
     */
    public static class SubarrayResult {
        public final int sum;
        public final int start;
        public final int end;
        
        public SubarrayResult(int sum, int start, int end) {
            this.sum = sum;
            this.start = start;
            this.end = end;
        }
    }
    
    public static SubarrayResult maxSubarrayWithIndices(int[] nums) {
        if (nums == null || nums.length == 0) {
            throw new IllegalArgumentException("陣列不能為空");
        }
        
        int maxSum = nums[0];
        int currentSum = nums[0];
        int start = 0, end = 0, tempStart = 0;
        
        for (int i = 1; i < nums.length; i++) {
            if (currentSum < 0) {
                currentSum = nums[i];
                tempStart = i;
            } else {
                currentSum += nums[i];
            }
            
            if (currentSum > maxSum) {
                maxSum = currentSum;
                start = tempStart;
                end = i;
            }
        }
        
        return new SubarrayResult(maxSum, start, end);
    }
    
    /**
     * 處理全為負數的情況
     */
    public static int maxSubarraySumAllNegative(int[] nums) {
        if (nums == null || nums.length == 0) {
            throw new IllegalArgumentException("陣列不能為空");
        }
        
        // 如果所有數都是負數，返回最大的單個元素
        int maxElement = nums[0];
        for (int i = 1; i < nums.length; i++) {
            maxElement = Math.max(maxElement, nums[i]);
        }
        
        return maxElement;
    }
    
    /**
     * 測試方法
     */
    public static void main(String[] args) {
        // 測試案例1：混合正負數
        int[] test1 = {-2, 1, -3, 4, -1, 2, 1, -5, 4};
        int result1 = maxSubarraySum(test1);
        System.out.println("測試1 - 最大子陣列和: " + result1); // 期望: 6
        
        SubarrayResult detailed1 = maxSubarrayWithIndices(test1);
        System.out.printf("子陣列範圍: [%d, %d], 和: %d%n", 
                         detailed1.start, detailed1.end, detailed1.sum);
        
        // 測試案例2：全為負數
        int[] test2 = {-3, -2, -5, -1};
        int result2 = maxSubarraySum(test2);
        System.out.println("測試2 - 最大子陣列和: " + result2); // 期望: -1
        
        // 測試案例3：單一元素
        int[] test3 = {5};
        int result3 = maxSubarraySum(test3);
        System.out.println("測試3 - 最大子陣列和: " + result3); // 期望: 5
    }
}
```

**演算法洞察：**
- **動態規劃思想**：每步做最優決策
- **狀態轉移**：`dp[i] = max(nums[i], dp[i-1] + nums[i])`
- **空間優化**：只需要記住前一個狀態

---

## 🔴 困難練習解答

### 練習 3.1：接雨水問題

**完整解答：**

```java
public class RainWaterAnswer {
    
    /**
     * 接雨水問題 - 雙指標解法
     * 時間複雜度: O(n)
     * 空間複雜度: O(1)
     * 
     * 核心思想：兩端向中間收縮，較低的一端決定水位
     */
    public static int trapRainWater(int[] height) {
        if (height == null || height.length <= 2) {
            return 0;
        }
        
        int left = 0;
        int right = height.length - 1;
        int leftMax = 0;
        int rightMax = 0;
        int totalWater = 0;
        
        while (left < right) {
            if (height[left] < height[right]) {
                // 左邊較低，處理左邊
                if (height[left] >= leftMax) {
                    leftMax = height[left];
                } else {
                    totalWater += leftMax - height[left];
                }
                left++;
            } else {
                // 右邊較低，處理右邊
                if (height[right] >= rightMax) {
                    rightMax = height[right];
                } else {
                    totalWater += rightMax - height[right];
                }
                right--;
            }
        }
        
        return totalWater;
    }
    
    /**
     * 接雨水問題 - 動態規劃解法
     * 時間複雜度: O(n)
     * 空間複雜度: O(n)
     * 
     * 思路：預計算每個位置左右的最大高度
     */
    public static int trapRainWaterDP(int[] height) {
        if (height == null || height.length <= 2) {
            return 0;
        }
        
        int n = height.length;
        int[] leftMax = new int[n];
        int[] rightMax = new int[n];
        
        // 計算每個位置左邊的最大高度
        leftMax[0] = height[0];
        for (int i = 1; i < n; i++) {
            leftMax[i] = Math.max(leftMax[i - 1], height[i]);
        }
        
        // 計算每個位置右邊的最大高度
        rightMax[n - 1] = height[n - 1];
        for (int i = n - 2; i >= 0; i--) {
            rightMax[i] = Math.max(rightMax[i + 1], height[i]);
        }
        
        // 計算總雨水量
        int totalWater = 0;
        for (int i = 0; i < n; i++) {
            int waterLevel = Math.min(leftMax[i], rightMax[i]);
            totalWater += Math.max(0, waterLevel - height[i]);
        }
        
        return totalWater;
    }
    
    /**
     * 接雨水問題 - 單調棧解法
     * 時間複雜度: O(n)
     * 空間複雜度: O(n)
     * 
     * 思路：維護一個遞減的單調棧
     */
    public static int trapRainWaterStack(int[] height) {
        if (height == null || height.length <= 2) {
            return 0;
        }
        
        Stack<Integer> stack = new Stack<>();
        int totalWater = 0;
        
        for (int i = 0; i < height.length; i++) {
            while (!stack.isEmpty() && height[i] > height[stack.peek()]) {
                int bottom = stack.pop();
                
                if (stack.isEmpty()) {
                    break;
                }
                
                int distance = i - stack.peek() - 1;
                int boundedHeight = Math.min(height[i], height[stack.peek()]) - height[bottom];
                totalWater += distance * boundedHeight;
            }
            
            stack.push(i);
        }
        
        return totalWater;
    }
    
    /**
     * 測試方法
     */
    public static void main(String[] args) {
        int[] test1 = {0, 1, 0, 2, 1, 0, 1, 3, 2, 1, 2, 1};
        System.out.println("高度陣列: " + Arrays.toString(test1));
        
        int result1 = trapRainWater(test1);
        int result2 = trapRainWaterDP(test1);
        int result3 = trapRainWaterStack(test1);
        
        System.out.println("雙指標解法: " + result1);
        System.out.println("動態規劃解法: " + result2);
        System.out.println("單調棧解法: " + result3);
        
        // 驗證三種解法結果一致
        assert result1 == result2 && result2 == result3 : "三種解法結果應該相同";
        System.out.println("所有解法驗證通過！");
    }
}
```

**三種解法比較：**

| 解法 | 時間複雜度 | 空間複雜度 | 優點 | 缺點 |
|------|------------|------------|------|------|
| 雙指標 | O(n) | O(1) | 空間效率高 | 理解難度較大 |
| 動態規劃 | O(n) | O(n) | 思路清晰 | 需要額外空間 |
| 單調棧 | O(n) | O(n) | 通用性強 | 實現複雜 |

---

## 📈 效能分析和最佳化建議

### 1. 記憶體使用分析

```java
public class MemoryAnalysis {
    
    /**
     * 分析不同陣列操作的記憶體使用
     */
    public static void analyzeMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        
        // 測試大陣列的記憶體使用
        runtime.gc(); // 建議垃圾回收
        long beforeMemory = runtime.totalMemory() - runtime.freeMemory();
        
        int[] largeArray = new int[1_000_000];
        Arrays.fill(largeArray, 42);
        
        long afterMemory = runtime.totalMemory() - runtime.freeMemory();
        long usedMemory = afterMemory - beforeMemory;
        
        System.out.printf("創建百萬整數陣列使用記憶體: %d bytes%n", usedMemory);
        System.out.printf("每個整數約使用: %.2f bytes%n", (double) usedMemory / largeArray.length);
    }
}
```

### 2. 效能基準測試

```java
public class BenchmarkTest {
    
    /**
     * 比較不同排序演算法的效能
     */
    @Test
    public void benchmarkSortAlgorithms() {
        int[] sizes = {1000, 10000, 100000};
        
        for (int size : sizes) {
            int[] randomArray = generateRandomArray(size);
            
            // 測試Arrays.sort()
            int[] array1 = randomArray.clone();
            long startTime = System.nanoTime();
            Arrays.sort(array1);
            long javaSort = System.nanoTime() - startTime;
            
            // 測試自實現冒泡排序
            int[] array2 = randomArray.clone();
            startTime = System.nanoTime();
            bubbleSort(array2);
            long bubbleTime = System.nanoTime() - startTime;
            
            System.out.printf("Size: %d, Java Sort: %d ns, Bubble Sort: %d ns%n",
                             size, javaSort, bubbleTime);
        }
    }
    
    private int[] generateRandomArray(int size) {
        Random random = new Random(42); // 固定種子確保可重現
        return random.ints(size, 0, 1000).toArray();
    }
}
```

### 3. 最佳化建議

**空間優化：**
```java
// ✅ 好：原地操作
public static void reverseArray(int[] arr) {
    int left = 0, right = arr.length - 1;
    while (left < right) {
        swap(arr, left++, right--);
    }
}

// ❌ 不好：創建新陣列
public static int[] reverseArrayNew(int[] arr) {
    int[] result = new int[arr.length];
    for (int i = 0; i < arr.length; i++) {
        result[i] = arr[arr.length - 1 - i];
    }
    return result;
}
```

**時間優化：**
```java
// ✅ 好：減少比較次數
public static boolean isPalindrome(int[] arr) {
    int left = 0, right = arr.length - 1;
    while (left < right) {
        if (arr[left] != arr[right]) {
            return false;
        }
        left++;
        right--;
    }
    return true;
}

// ❌ 不好：全陣列比較
public static boolean isPalindromeNaive(int[] arr) {
    for (int i = 0; i < arr.length; i++) {
        if (arr[i] != arr[arr.length - 1 - i]) {
            return false;
        }
    }
    return true;
}
```

---

## 🎯 重點回顧

### 核心概念掌握

1. **陣列特性**
   - 固定大小、連續記憶體、索引訪問 O(1)
   - 插入/刪除操作需要移動元素 O(n)

2. **重要演算法**
   - Kadane's 演算法：線性時間求最大子陣列和
   - 雙指標技術：空間效率的陣列重排
   - 三次反轉法：巧妙的陣列旋轉

3. **設計原則**
   - 邊界檢查：防止陣列越界
   - 防禦性程式設計：處理異常輸入
   - 空間時間權衡：選擇合適的演算法

### 常見錯誤避免

1. **忘記邊界檢查**
2. **整數溢位**（如計算中點時）
3. **忽略特殊情況**（空陣列、單元素等）
4. **不考慮穩定性要求**

記住：**陣列是所有資料結構的基礎，熟練掌握陣列操作對後續學習至關重要！** 💪