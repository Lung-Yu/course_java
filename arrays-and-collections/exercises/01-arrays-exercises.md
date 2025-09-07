# 陣列練習題

## 學習目標

通過這些練習，你將會掌握：
- 陣列的基本操作和使用方法
- 一維陣列和多維陣列的處理
- Arrays 工具類的使用
- 基本的陣列演算法實現

---

## 🟢 基礎練習 (Easy)

### 練習 1.1：陣列基本操作

**題目描述：**
創建一個程式，實現以下陣列基本操作：
1. 創建一個整數陣列，包含 10 個元素
2. 初始化陣列為 1 到 10 的數字
3. 印出陣列中所有元素
4. 計算陣列中所有元素的總和
5. 找出陣列中的最大值和最小值

**要求：**
- 使用 for 迴圈進行陣列遍歷
- 提供清楚的輸出格式

**預期輸出：**
```
陣列內容: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
總和: 55
最大值: 10
最小值: 1
```

<details>
<summary>💡 思路提示</summary>

1. 使用 `new int[10]` 創建陣列
2. 使用 for 迴圈初始化和遍歷
3. 使用變數記錄總和、最大值、最小值
</details>

<details>
<summary>🔧 技術提示</summary>

```java
int[] array = new int[10];
for (int i = 0; i < array.length; i++) {
    array[i] = i + 1;
}
```
</details>

---

### 練習 1.2：陣列搜尋

**題目描述：**
實現一個方法，在整數陣列中搜尋指定的元素，返回該元素第一次出現的索引。如果元素不存在，返回 -1。

**方法簽名：**
```java
public static int linearSearch(int[] array, int target)
```

**測試案例：**
```java
int[] array = {5, 2, 8, 1, 9, 3};
System.out.println(linearSearch(array, 8));  // 輸出: 2
System.out.println(linearSearch(array, 7));  // 輸出: -1
```

<details>
<summary>💡 思路提示</summary>

使用線性搜尋演算法，逐一檢查陣列中的每個元素。
</details>

---

### 練習 1.3：陣列反轉

**題目描述：**
實現一個方法，將陣列中的元素順序反轉（原地反轉，不使用額外空間）。

**方法簽名：**
```java
public static void reverseArray(int[] array)
```

**測試案例：**
```java
int[] array = {1, 2, 3, 4, 5};
reverseArray(array);
System.out.println(Arrays.toString(array));  // 輸出: [5, 4, 3, 2, 1]
```

<details>
<summary>💡 思路提示</summary>

使用雙指標技術，一個指向開頭，一個指向結尾，交換元素並向中間移動。
</details>

---

## 🟡 中等練習 (Medium)

### 練習 2.1：陣列去重

**題目描述：**
實現一個方法，移除排序陣列中的重複元素，返回新長度。要求原地修改陣列，不使用額外空間。

**方法簽名：**
```java
public static int removeDuplicates(int[] nums)
```

**說明：**
- 輸入陣列已排序
- 修改陣列使前 n 個元素為去重後的結果
- 返回去重後的長度 n

**測試案例：**
```java
int[] nums = {1, 1, 2, 2, 2, 3, 4, 4, 5};
int newLength = removeDuplicates(nums);
System.out.println("新長度: " + newLength);  // 輸出: 5
// nums 的前 5 個元素應該是 [1, 2, 3, 4, 5]
```

<details>
<summary>💡 思路提示</summary>

使用雙指標技術：
- 一個指標（慢指標）指向下一個不重複元素的位置
- 一個指標（快指標）遍歷陣列
</details>

<details>
<summary>🔧 技術提示</summary>

```java
if (nums.length == 0) return 0;
int slow = 0;
for (int fast = 1; fast < nums.length; fast++) {
    if (nums[fast] != nums[slow]) {
        // 找到不重複元素
    }
}
```
</details>

---

### 練習 2.2：旋轉陣列

**題目描述：**
實現一個方法，將陣列向右旋轉 k 個位置。

**方法簽名：**
```java
public static void rotateArray(int[] nums, int k)
```

**測試案例：**
```java
int[] nums = {1, 2, 3, 4, 5, 6, 7};
rotateArray(nums, 3);
System.out.println(Arrays.toString(nums));  // 輸出: [5, 6, 7, 1, 2, 3, 4]
```

<details>
<summary>💡 思路提示</summary>

有多種解法：
1. 使用額外陣列
2. 逐個旋轉（多次反轉）
3. 三次反轉法
</details>

<details>
<summary>⚡ 優化提示</summary>

三次反轉法最優雅：
1. 反轉整個陣列
2. 反轉前 k 個元素
3. 反轉後 n-k 個元素
</details>

---

### 練習 2.3：合併兩個排序陣列

**題目描述：**
實現一個方法，合併兩個已排序的陣列成一個新的排序陣列。

**方法簽名：**
```java
public static int[] mergeSortedArrays(int[] nums1, int[] nums2)
```

**測試案例：**
```java
int[] nums1 = {1, 3, 5, 7};
int[] nums2 = {2, 4, 6, 8, 9};
int[] result = mergeSortedArrays(nums1, nums2);
System.out.println(Arrays.toString(result));  // 輸出: [1, 2, 3, 4, 5, 6, 7, 8, 9]
```

<details>
<summary>💡 思路提示</summary>

使用雙指標技術，類似合併排序的合併步驟。
</details>

---

## 🔴 困難練習 (Hard)

### 練習 3.1：找出陣列中的重複數字

**題目描述：**
給定一個包含 n+1 個整數的陣列，其中每個整數都在 1 到 n 的範圍內。根據鴿籠原理，至少存在一個重複的數字。找出任意一個重複的數字。

**要求：**
- 不能修改陣列
- 只能使用常數級額外空間
- 時間複雜度小於 O(n²)

**方法簽名：**
```java
public static int findDuplicate(int[] nums)
```

**測試案例：**
```java
int[] nums = {1, 3, 4, 2, 2};
System.out.println(findDuplicate(nums));  // 輸出: 2
```

<details>
<summary>💡 思路提示</summary>

這是一個經典的「Floyd 判環演算法」問題。把陣列看作鏈表，值作為下一個節點的索引。
</details>

<details>
<summary>🔧 技術提示</summary>

使用快慢指標：
```java
// 第一階段：找到環中的相遇點
int slow = nums[0], fast = nums[0];
do {
    slow = nums[slow];
    fast = nums[nums[fast]];
} while (slow != fast);

// 第二階段：找到環的入口
```
</details>

---

### 練習 3.2：最大子陣列和

**題目描述：**
實現 Kadane 演算法，找出一個整數陣列中具有最大和的連續子陣列。

**方法簽名：**
```java
public static int maxSubArray(int[] nums)
```

**測試案例：**
```java
int[] nums = {-2, 1, -3, 4, -1, 2, 1, -5, 4};
System.out.println(maxSubArray(nums));  // 輸出: 6 (子陣列 [4, -1, 2, 1])
```

<details>
<summary>💡 思路提示</summary>

Kadane 演算法的核心思想：
- 對於每個位置，決定是否將當前元素加入之前的子陣列
- 如果之前的和為負，則重新開始
</details>

---

### 練習 3.3：二維陣列螺旋遍歷

**題目描述：**
給定一個 m × n 的二維陣列，按螺旋順序返回陣列中的所有元素。

**方法簽名：**
```java
public static List<Integer> spiralOrder(int[][] matrix)
```

**測試案例：**
```java
int[][] matrix = {
    {1, 2, 3},
    {4, 5, 6},
    {7, 8, 9}
};
System.out.println(spiralOrder(matrix));  // 輸出: [1, 2, 3, 6, 9, 8, 7, 4, 5]
```

<details>
<summary>💡 思路提示</summary>

維護四個邊界：上、下、左、右，按順序遍歷並更新邊界。
</details>

<details>
<summary>🔧 技術提示</summary>

```java
int top = 0, bottom = matrix.length - 1;
int left = 0, right = matrix[0].length - 1;

while (top <= bottom && left <= right) {
    // 向右遍歷上邊界
    // 向下遍歷右邊界
    // 向左遍歷下邊界
    // 向上遍歷左邊界
}
```
</details>

---

## 🧠 進階挑戰

### 挑戰 4.1：實現動態陣列

**題目描述：**
實現一個類似 ArrayList 的動態陣列類，支援基本操作。

**要求實現的方法：**
```java
public class DynamicArray<T> {
    public void add(T element)              // 添加元素
    public T get(int index)                 // 獲取元素
    public void set(int index, T element)   // 設置元素
    public void remove(int index)           // 移除元素
    public int size()                       // 獲取大小
    public boolean isEmpty()                // 是否為空
}
```

**要求：**
- 自動擴容（當容量不足時）
- 支援泛型
- 處理邊界情況（索引越界等）

---

### 挑戰 4.2：實現多維陣列的矩陣運算

**題目描述：**
實現一個矩陣類，支援基本的矩陣運算。

**要求實現的操作：**
- 矩陣加法
- 矩陣乘法
- 矩陣轉置
- 矩陣行列式計算（2×2 和 3×3）

**方法簽名：**
```java
public class Matrix {
    public Matrix add(Matrix other)
    public Matrix multiply(Matrix other)
    public Matrix transpose()
    public double determinant()  // 假設矩陣為方陣
}
```

---

## 📝 練習總結

完成這些練習後，你應該能夠：

1. ✅ **熟練使用陣列基本操作**
   - 創建、初始化、遍歷陣列
   - 搜尋、排序、反轉等基本演算法

2. ✅ **掌握陣列的高級技巧**
   - 雙指標技術
   - 原地修改演算法
   - 時間和空間複雜度優化

3. ✅ **理解多維陣列的使用**
   - 二維陣列的遍歷和操作
   - 矩陣相關演算法

4. ✅ **具備解決實際問題的能力**
   - 選擇合適的陣列演算法
   - 處理邊界情況
   - 優化效能

## 🎯 下一步

完成陣列練習後，可以繼續學習：
- **[List 練習](02-list-exercises.md)** - 學習動態陣列和鏈表
- **[綜合練習](06-comprehensive-exercises.md)** - 挑戰更複雜的問題

記住：**多練習，多思考，多優化**！ 💪