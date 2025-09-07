# Arrays 和 Collections Framework 練習題

## 練習目標

這些練習題設計為逐步深入，幫助你全面掌握 Java 陣列和集合框架的使用：

1. **基礎操作練習** - 熟悉基本 API 和常用方法
2. **應用場景練習** - 在實際情境中選擇合適的資料結構
3. **演算法實現練習** - 使用集合實現經典演算法
4. **效能優化練習** - 理解不同實現的效能特性
5. **綜合應用練習** - 結合多種集合解決複雜問題

## 練習結構

```
exercises/
├── README.md                    # 本文檔
├── 01-arrays-exercises.md       # 陣列練習題
├── 02-list-exercises.md         # List 練習題
├── 03-set-exercises.md          # Set 練習題
├── 04-map-exercises.md          # Map 練習題
├── 05-queue-deque-exercises.md  # Queue/Deque 練習題
├── 06-comprehensive-exercises.md # 綜合練習題
└── programming-challenges/      # 程式設計挑戰題
    ├── easy/                    # 簡單難度
    ├── medium/                  # 中等難度
    └── hard/                    # 困難難度
```

## 難度分級

### 🟢 簡單 (Easy)
- 基本 API 使用
- 簡單的增刪改查操作
- 直接的資料結構選擇

### 🟡 中等 (Medium)
- 需要組合使用多個方法
- 涉及效能考量的選擇
- 簡單演算法實現

### 🔴 困難 (Hard)
- 複雜的演算法實現
- 多種資料結構組合使用
- 效能優化要求

## 練習指南

### 1. 學習順序建議

1. **陣列基礎** → **List 集合** → **Set 集合** → **Map 集合** → **Queue/Deque** → **綜合應用**

2. 每個主題內部：**基礎操作** → **實際應用** → **演算法實現** → **效能優化**

### 2. 練習方法

- **閱讀題目**：仔細理解需求和約束條件
- **分析問題**：思考最適合的資料結構和演算法
- **編寫代碼**：實現解決方案
- **測試驗證**：使用提供的測試案例驗證
- **優化改進**：分析時間和空間複雜度，嘗試優化

### 3. 提示系統

每道題目都提供三級提示：
- **💡 思路提示**：解題的基本思路
- **🔧 技術提示**：具體的技術實現要點
- **⚡ 優化提示**：效能優化的方向

## 評分標準

### 功能性 (40%)
- ✅ 正確實現基本功能
- ✅ 處理邊界情況
- ✅ 通過所有測試案例

### 程式碼品質 (30%)
- ✅ 程式碼結構清晰
- ✅ 命名規範
- ✅ 適當的註釋

### 效能 (20%)
- ✅ 時間複雜度合理
- ✅ 空間複雜度合理
- ✅ 選擇合適的資料結構

### 創新性 (10%)
- ✅ 使用巧妙的解法
- ✅ 提供多種解決方案
- ✅ 創新的優化思路

## 自我檢測清單

完成每個主題後，請檢查：

### Arrays
- [ ] 能夠熟練進行陣列的基本操作（創建、訪問、修改）
- [ ] 理解一維陣列和多維陣列的使用場景
- [ ] 掌握 Arrays 工具類的常用方法
- [ ] 能夠實現基本的陣列演算法（排序、搜索）

### List
- [ ] 理解 ArrayList 和 LinkedList 的差異和選擇標準
- [ ] 能夠根據需求選擇合適的 List 實現
- [ ] 掌握 List 的增刪改查操作
- [ ] 理解 Iterator 的使用和並發修改問題

### Set
- [ ] 理解不同 Set 實現的特性（HashSet、TreeSet、LinkedHashSet）
- [ ] 能夠使用 Set 解決去重和集合運算問題
- [ ] 掌握自定義物件的 equals 和 hashCode 實現
- [ ] 理解 Set 在演算法中的應用

### Map
- [ ] 理解不同 Map 實現的特性和使用場景
- [ ] 能夠使用 Map 解決映射和索引問題
- [ ] 掌握 Map 的遍歷方法
- [ ] 理解 Map 在資料處理中的重要性

### Queue/Deque
- [ ] 理解佇列和雙端佇列的概念
- [ ] 能夠使用 Queue 實現 BFS 等演算法
- [ ] 掌握 PriorityQueue 的使用
- [ ] 理解佇列在系統設計中的應用

## 學習資源

### 官方文檔
- [Java Collections Framework Guide](https://docs.oracle.com/javase/tutorial/collections/)
- [Java Platform API Specification](https://docs.oracle.com/en/java/javase/17/docs/api/)

### 推薦閱讀
- 《Effective Java》 - Joshua Bloch
- 《Java: The Complete Reference》 - Herbert Schildt
- 《Introduction to Algorithms》 - CLRS

### 線上資源
- [LeetCode Collections Problems](https://leetcode.com/tag/array/)
- [HackerRank Data Structures](https://www.hackerrank.com/domains/data-structures)
- [GeeksforGeeks Java Collections](https://www.geeksforgeeks.org/collections-in-java-2/)

## 開始練習

選擇一個主題開始你的練習之旅：

1. **[陣列練習](01-arrays-exercises.md)** - 掌握陣列基礎
2. **[List 練習](02-list-exercises.md)** - 學習動態陣列和鏈表
3. **[Set 練習](03-set-exercises.md)** - 理解集合和去重
4. **[Map 練習](04-map-exercises.md)** - 掌握映射和索引
5. **[Queue/Deque 練習](05-queue-deque-exercises.md)** - 學習佇列和棧
6. **[綜合練習](06-comprehensive-exercises.md)** - 挑戰複雜問題

記住：**實踐是最好的老師**！開始動手編程吧！ 💪