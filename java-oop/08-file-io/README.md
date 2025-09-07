# 檔案讀寫與I/O

## 學習目標
- 掌握Java檔案操作基本概念
- 理解不同I/O類別的使用場景
- 學會處理文字檔案和二進制檔案
- 掌握緩衝I/O提升效能的技巧

## 核心概念

### 1. Java I/O體系架構
```
java.io 套件
├── 位元組流 (Byte Streams)
│   ├── InputStream (抽象類別)
│   │   ├── FileInputStream
│   │   ├── BufferedInputStream
│   │   └── DataInputStream
│   └── OutputStream (抽象類別)
│       ├── FileOutputStream
│       ├── BufferedOutputStream
│       └── DataOutputStream
└── 字元流 (Character Streams)
    ├── Reader (抽象類別)
    │   ├── FileReader
    │   ├── BufferedReader
    │   └── InputStreamReader
    └── Writer (抽象類別)
        ├── FileWriter
        ├── BufferedWriter
        └── OutputStreamWriter
```

### 2. 位元組流 vs 字元流
- **位元組流**：處理二進制資料，適用於圖片、音訊、影片等
- **字元流**：處理文字資料，自動處理字元編碼轉換

### 3. 緩衝流的重要性
- 減少實際I/O操作次數
- 大幅提升檔案讀寫效能
- 建議在檔案操作中使用緩衝流

## 實務應用

### 1. 文字檔案操作
```java
// 使用FileReader讀取文字檔案
try (FileReader fr = new FileReader("data.txt");
     BufferedReader br = new BufferedReader(fr)) {
    String line;
    while ((line = br.readLine()) != null) {
        System.out.println(line);
    }
}

// 使用FileWriter寫入文字檔案
try (FileWriter fw = new FileWriter("output.txt");
     BufferedWriter bw = new BufferedWriter(fw)) {
    bw.write("Hello World");
    bw.newLine();
}
```

### 2. 二進制檔案操作
```java
// 使用FileInputStream讀取二進制檔案
try (FileInputStream fis = new FileInputStream("image.jpg");
     BufferedInputStream bis = new BufferedInputStream(fis)) {
    byte[] buffer = new byte[1024];
    int bytesRead;
    while ((bytesRead = bis.read(buffer)) != -1) {
        // 處理讀取的資料
    }
}
```

### 3. 現代檔案操作 (NIO.2)
```java
// 使用Path和Files類別
Path path = Paths.get("data.txt");
List<String> lines = Files.readAllLines(path);
Files.write(path, content.getBytes());
```

## 最佳實務
1. 使用try-with-resources自動關閉資源
2. 優先使用緩衝流提升效能
3. 選擇適當的流類型（位元組流 vs 字元流）
4. 處理檔案不存在、權限不足等例外情況

## 實務練習

### 練習1：日誌系統
建立一個簡單的日誌記錄系統，支援不同等級的日誌。

### 練習2：配置檔案管理
實作配置檔案的讀取和寫入功能。

### 練習3：檔案處理工具
建立檔案複製、移動、刪除等工具方法。

## 編譯與執行
```bash
javac FileIODemo.java
java FileIODemo
```