# 多執行緒基礎 (Multithreading Basics)

## 學習目標
- 理解多執行緒程式設計的基本概念
- 掌握Thread類別和Runnable介面的使用
- 學會使用ExecutorService管理執行緒池
- 理解同步化機制和執行緒安全
- 掌握執行緒通信的基本方法

## 核心概念

### 1. 多執行緒基礎概念
- **程序 (Process)**：作業系統中執行的程式實例
- **執行緒 (Thread)**：程序中的輕量級執行單位
- **並發 (Concurrency)**：多個任務交替執行
- **並行 (Parallelism)**：多個任務同時執行

### 2. Java執行緒模型
```
主執行緒 (Main Thread)
├── 用戶執行緒 (User Threads)
└── 守護執行緒 (Daemon Threads)
```

### 3. 執行緒生命週期
```
NEW -> RUNNABLE -> BLOCKED/WAITING/TIMED_WAITING -> TERMINATED
```

### 4. 建立執行緒的方式

#### 方式1：繼承Thread類別
```java
class MyThread extends Thread {
    @Override
    public void run() {
        // 執行緒執行的程式碼
    }
}
```

#### 方式2：實作Runnable介面（推薦）
```java
class MyTask implements Runnable {
    @Override
    public void run() {
        // 執行緒執行的程式碼
    }
}
```

#### 方式3：使用Lambda表達式
```java
Thread thread = new Thread(() -> {
    // 執行緒執行的程式碼
});
```

### 5. 執行緒同步化

#### synchronized 關鍵字
```java
// 同步化方法
public synchronized void method() {
    // 同步化程式碼
}

// 同步化程式碼區塊
synchronized(this) {
    // 同步化程式碼
}
```

#### 其他同步化工具
- `Lock` 和 `ReentrantLock`
- `Semaphore`
- `CountDownLatch`
- `CyclicBarrier`

### 6. ExecutorService
現代Java推薦使用ExecutorService管理執行緒：
```java
ExecutorService executor = Executors.newFixedThreadPool(5);
executor.submit(() -> {
    // 任務程式碼
});
executor.shutdown();
```

## 實務應用

### 1. 基本執行緒使用
```java
// 使用Runnable
Runnable task = () -> {
    System.out.println("執行緒執行中: " + Thread.currentThread().getName());
};
Thread thread = new Thread(task);
thread.start();
```

### 2. 執行緒池管理
```java
ExecutorService executor = Executors.newCachedThreadPool();
for (int i = 0; i < 10; i++) {
    executor.submit(() -> {
        // 並行任務
    });
}
```

### 3. 生產者消費者模式
```java
BlockingQueue<String> queue = new ArrayBlockingQueue<>(10);

// 生產者
executor.submit(() -> {
    queue.put("data");
});

// 消費者
executor.submit(() -> {
    String data = queue.take();
});
```

## 最佳實務
1. 優先使用ExecutorService而非直接建立Thread
2. 適當設定執行緒池大小
3. 注意執行緒安全問題
4. 正確關閉ExecutorService
5. 避免死鎖情況

## 實務練習

### 練習1：下載管理器
建立多執行緒檔案下載管理器。

### 練習2：並行計算
實作並行數值計算任務。

### 練習3：執行緒安全的計數器
設計執行緒安全的共享計數器。

## 編譯與執行
```bash
javac MultithreadingDemo.java
java MultithreadingDemo
```