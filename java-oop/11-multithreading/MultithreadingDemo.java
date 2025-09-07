/**
 * Java 多執行緒基礎示範
 * 展示執行緒建立、管理、同步化和實際應用
 */

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// ================================
// 基本執行緒示範類別
// ================================

/**
 * 簡單的任務類別 - 實作Runnable介面
 */
class SimpleTask implements Runnable {
    private String taskName;
    private int duration;
    
    public SimpleTask(String taskName, int duration) {
        this.taskName = taskName;
        this.duration = duration;
    }
    
    @Override
    public void run() {
        System.out.println("[" + getCurrentTime() + "] " + taskName + " 開始執行 (執行緒: " + 
                         Thread.currentThread().getName() + ")");
        
        try {
            // 模擬工作時間
            Thread.sleep(duration * 1000);
        } catch (InterruptedException e) {
            System.out.println(taskName + " 被中斷");
            Thread.currentThread().interrupt();
        }
        
        System.out.println("[" + getCurrentTime() + "] " + taskName + " 執行完成");
    }
    
    private String getCurrentTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }
}

/**
 * 繼承Thread類別的執行緒
 */
class CustomThread extends Thread {
    private String threadName;
    private int count;
    
    public CustomThread(String threadName, int count) {
        super(threadName);
        this.threadName = threadName;
        this.count = count;
    }
    
    @Override
    public void run() {
        for (int i = 1; i <= count; i++) {
            System.out.println(threadName + " - 計數: " + i);
            try {
                Thread.sleep(500);  // 暫停0.5秒
            } catch (InterruptedException e) {
                System.out.println(threadName + " 被中斷");
                break;
            }
        }
        System.out.println(threadName + " 執行完成");
    }
}

// ================================
// 執行緒安全示範類別
// ================================

/**
 * 非執行緒安全的計數器
 */
class UnsafeCounter {
    private int count = 0;
    
    public void increment() {
        count++;
    }
    
    public int getCount() {
        return count;
    }
}

/**
 * 使用synchronized的執行緒安全計數器
 */
class SafeCounter {
    private int count = 0;
    
    public synchronized void increment() {
        count++;
    }
    
    public synchronized int getCount() {
        return count;
    }
}

/**
 * 使用AtomicInteger的執行緒安全計數器
 */
class AtomicCounter {
    private AtomicInteger count = new AtomicInteger(0);
    
    public void increment() {
        count.incrementAndGet();
    }
    
    public int getCount() {
        return count.get();
    }
}

/**
 * 使用ReentrantLock的執行緒安全計數器
 */
class LockCounter {
    private int count = 0;
    private final ReentrantLock lock = new ReentrantLock();
    
    public void increment() {
        lock.lock();
        try {
            count++;
        } finally {
            lock.unlock();
        }
    }
    
    public int getCount() {
        lock.lock();
        try {
            return count;
        } finally {
            lock.unlock();
        }
    }
}

// ================================
// 生產者消費者模式示範
// ================================

/**
 * 生產者類別
 */
class Producer implements Runnable {
    private BlockingQueue<String> queue;
    private String producerName;
    private int itemCount;
    
    public Producer(BlockingQueue<String> queue, String producerName, int itemCount) {
        this.queue = queue;
        this.producerName = producerName;
        this.itemCount = itemCount;
    }
    
    @Override
    public void run() {
        try {
            for (int i = 1; i <= itemCount; i++) {
                String item = producerName + "-產品-" + i;
                queue.put(item);
                System.out.println("[" + getCurrentTime() + "] " + producerName + " 生產: " + item + 
                                 " (佇列大小: " + queue.size() + ")");
                Thread.sleep(100 + (int)(Math.random() * 200)); // 隨機生產時間
            }
            System.out.println(producerName + " 生產完成");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println(producerName + " 被中斷");
        }
    }
    
    private String getCurrentTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }
}

/**
 * 消費者類別
 */
class Consumer implements Runnable {
    private BlockingQueue<String> queue;
    private String consumerName;
    private volatile boolean running = true;
    
    public Consumer(BlockingQueue<String> queue, String consumerName) {
        this.queue = queue;
        this.consumerName = consumerName;
    }
    
    @Override
    public void run() {
        try {
            while (running || !queue.isEmpty()) {
                String item = queue.poll(1, TimeUnit.SECONDS);
                if (item != null) {
                    System.out.println("[" + getCurrentTime() + "] " + consumerName + " 消費: " + item + 
                                     " (佇列大小: " + queue.size() + ")");
                    Thread.sleep(150 + (int)(Math.random() * 250)); // 隨機消費時間
                }
            }
            System.out.println(consumerName + " 消費完成");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println(consumerName + " 被中斷");
        }
    }
    
    public void stop() {
        running = false;
    }
    
    private String getCurrentTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }
}

// ================================
// 並行計算示範
// ================================

/**
 * 並行計算任務
 */
class CalculationTask implements Callable<Long> {
    private long start;
    private long end;
    
    public CalculationTask(long start, long end) {
        this.start = start;
        this.end = end;
    }
    
    @Override
    public Long call() throws Exception {
        long sum = 0;
        for (long i = start; i <= end; i++) {
            sum += i;
        }
        System.out.println("計算範圍 " + start + "-" + end + " 完成，結果: " + sum + 
                         " (執行緒: " + Thread.currentThread().getName() + ")");
        return sum;
    }
}

/**
 * 下載任務模擬
 */
class DownloadTask implements Runnable {
    private String fileName;
    private int fileSize; // MB
    private CountDownLatch latch;
    
    public DownloadTask(String fileName, int fileSize, CountDownLatch latch) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.latch = latch;
    }
    
    @Override
    public void run() {
        try {
            System.out.println("開始下載: " + fileName + " (" + fileSize + "MB)");
            
            // 模擬下載進度
            for (int progress = 0; progress <= 100; progress += 10) {
                Thread.sleep(fileSize * 10); // 模擬下載時間
                System.out.println(fileName + " 下載進度: " + progress + "%");
            }
            
            System.out.println(fileName + " 下載完成！");
            
        } catch (InterruptedException e) {
            System.out.println(fileName + " 下載被中斷");
            Thread.currentThread().interrupt();
        } finally {
            latch.countDown(); // 通知任務完成
        }
    }
}

// ================================
// 主要示範類別
// ================================

public class MultithreadingDemo {
    
    /**
     * 示範基本執行緒建立和使用
     */
    private static void demonstrateBasicThreads() {
        System.out.println("=== 基本執行緒示範 ===");
        
        // 1. 使用Runnable介面
        System.out.println("1. 使用Runnable介面:");
        Thread thread1 = new Thread(new SimpleTask("任務A", 2));
        Thread thread2 = new Thread(new SimpleTask("任務B", 3));
        
        thread1.start();
        thread2.start();
        
        try {
            thread1.join(); // 等待thread1完成
            thread2.join(); // 等待thread2完成
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // 2. 使用Lambda表達式
        System.out.println("\n2. 使用Lambda表達式:");
        Thread lambdaThread = new Thread(() -> {
            for (int i = 1; i <= 3; i++) {
                System.out.println("Lambda執行緒計數: " + i);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        lambdaThread.start();
        
        // 3. 繼承Thread類別
        System.out.println("\n3. 繼承Thread類別:");
        CustomThread customThread = new CustomThread("自訂執行緒", 3);
        customThread.start();
        
        try {
            lambdaThread.join();
            customThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        System.out.println("基本執行緒示範完成\n");
    }
    
    /**
     * 示範ExecutorService使用
     */
    private static void demonstrateExecutorService() {
        System.out.println("=== ExecutorService示範 ===");
        
        // 1. 固定大小執行緒池
        System.out.println("1. 固定大小執行緒池:");
        ExecutorService fixedPool = Executors.newFixedThreadPool(3);
        
        for (int i = 1; i <= 5; i++) {
            final int taskId = i;
            fixedPool.submit(() -> {
                System.out.println("任務" + taskId + " 開始 (執行緒: " + 
                                 Thread.currentThread().getName() + ")");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                System.out.println("任務" + taskId + " 完成");
            });
        }
        
        fixedPool.shutdown();
        try {
            fixedPool.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // 2. 快取執行緒池
        System.out.println("\n2. 快取執行緒池:");
        ExecutorService cachedPool = Executors.newCachedThreadPool();
        
        for (int i = 1; i <= 3; i++) {
            final int taskId = i;
            cachedPool.submit(new SimpleTask("快取任務" + taskId, 1));
        }
        
        cachedPool.shutdown();
        try {
            cachedPool.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        System.out.println("ExecutorService示範完成\n");
    }
    
    /**
     * 示範執行緒安全
     */
    private static void demonstrateThreadSafety() {
        System.out.println("=== 執行緒安全示範 ===");
        
        int numThreads = 5;
        int incrementsPerThread = 1000;
        
        // 1. 非執行緒安全的計數器
        System.out.println("1. 非執行緒安全的計數器:");
        UnsafeCounter unsafeCounter = new UnsafeCounter();
        testCounter("非安全", unsafeCounter::increment, unsafeCounter::getCount, 
                   numThreads, incrementsPerThread);
        
        // 2. 使用synchronized的安全計數器
        System.out.println("\n2. 使用synchronized的安全計數器:");
        SafeCounter safeCounter = new SafeCounter();
        testCounter("Synchronized", safeCounter::increment, safeCounter::getCount, 
                   numThreads, incrementsPerThread);
        
        // 3. 使用AtomicInteger的計數器
        System.out.println("\n3. 使用AtomicInteger的計數器:");
        AtomicCounter atomicCounter = new AtomicCounter();
        testCounter("Atomic", atomicCounter::increment, atomicCounter::getCount, 
                   numThreads, incrementsPerThread);
        
        // 4. 使用ReentrantLock的計數器
        System.out.println("\n4. 使用ReentrantLock的計數器:");
        LockCounter lockCounter = new LockCounter();
        testCounter("ReentrantLock", lockCounter::increment, lockCounter::getCount, 
                   numThreads, incrementsPerThread);
        
        System.out.println("執行緒安全示範完成\n");
    }
    
    private static void testCounter(String type, Runnable incrementAction, 
                                  java.util.function.Supplier<Integer> getCount,
                                  int numThreads, int incrementsPerThread) {
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < numThreads; i++) {
            executor.submit(() -> {
                for (int j = 0; j < incrementsPerThread; j++) {
                    incrementAction.run();
                }
            });
        }
        
        executor.shutdown();
        try {
            executor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        long endTime = System.currentTimeMillis();
        int expectedCount = numThreads * incrementsPerThread;
        int actualCount = getCount.get();
        
        System.out.println(type + " 計數器結果:");
        System.out.println("  預期值: " + expectedCount);
        System.out.println("  實際值: " + actualCount);
        System.out.println("  正確性: " + (expectedCount == actualCount ? "正確" : "錯誤"));
        System.out.println("  執行時間: " + (endTime - startTime) + "ms");
    }
    
    /**
     * 示範生產者消費者模式
     */
    private static void demonstrateProducerConsumer() {
        System.out.println("=== 生產者消費者模式示範 ===");
        
        BlockingQueue<String> queue = new ArrayBlockingQueue<>(10);
        ExecutorService executor = Executors.newFixedThreadPool(5);
        
        // 建立生產者
        Producer producer1 = new Producer(queue, "生產者A", 5);
        Producer producer2 = new Producer(queue, "生產者B", 3);
        
        // 建立消費者
        Consumer consumer1 = new Consumer(queue, "消費者X");
        Consumer consumer2 = new Consumer(queue, "消費者Y");
        
        // 啟動所有執行緒
        executor.submit(producer1);
        executor.submit(producer2);
        executor.submit(consumer1);
        executor.submit(consumer2);
        
        // 等待生產者完成
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // 停止消費者
        consumer1.stop();
        consumer2.stop();
        
        executor.shutdown();
        try {
            executor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        System.out.println("生產者消費者示範完成\n");
    }
    
    /**
     * 示範並行計算
     */
    private static void demonstrateParallelCalculation() {
        System.out.println("=== 並行計算示範 ===");
        
        long totalRange = 1000000;
        int numTasks = 4;
        long rangePerTask = totalRange / numTasks;
        
        ExecutorService executor = Executors.newFixedThreadPool(numTasks);
        List<Future<Long>> futures = new ArrayList<>();
        
        // 建立並提交計算任務
        for (int i = 0; i < numTasks; i++) {
            long start = i * rangePerTask + 1;
            long end = (i == numTasks - 1) ? totalRange : (i + 1) * rangePerTask;
            
            Future<Long> future = executor.submit(new CalculationTask(start, end));
            futures.add(future);
        }
        
        // 收集結果
        long totalSum = 0;
        try {
            for (Future<Long> future : futures) {
                totalSum += future.get();
            }
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("計算過程中發生錯誤: " + e.getMessage());
        }
        
        executor.shutdown();
        
        // 驗算（使用數學公式）
        long expectedSum = totalRange * (totalRange + 1) / 2;
        
        System.out.println("並行計算結果:");
        System.out.println("  範圍: 1 到 " + totalRange);
        System.out.println("  並行任務數: " + numTasks);
        System.out.println("  計算結果: " + totalSum);
        System.out.println("  預期結果: " + expectedSum);
        System.out.println("  計算正確: " + (totalSum == expectedSum ? "是" : "否"));
        
        System.out.println("並行計算示範完成\n");
    }
    
    /**
     * 示範CountDownLatch使用
     */
    private static void demonstrateCountDownLatch() {
        System.out.println("=== CountDownLatch示範（模擬下載管理器）===");
        
        int numDownloads = 3;
        CountDownLatch latch = new CountDownLatch(numDownloads);
        ExecutorService executor = Executors.newFixedThreadPool(numDownloads);
        
        // 建立下載任務
        DownloadTask[] downloads = {
            new DownloadTask("檔案A.zip", 2, latch),
            new DownloadTask("檔案B.pdf", 1, latch),
            new DownloadTask("檔案C.mp4", 3, latch)
        };
        
        System.out.println("開始所有下載任務...");
        
        // 提交所有下載任務
        for (DownloadTask download : downloads) {
            executor.submit(download);
        }
        
        try {
            // 等待所有下載完成
            latch.await();
            System.out.println("所有下載任務完成！");
        } catch (InterruptedException e) {
            System.out.println("等待過程被中斷");
            Thread.currentThread().interrupt();
        }
        
        executor.shutdown();
        System.out.println("CountDownLatch示範完成\n");
    }
    
    public static void main(String[] args) {
        System.out.println("=== Java 多執行緒基礎示範 ===\n");
        
        demonstrateBasicThreads();
        demonstrateExecutorService();
        demonstrateThreadSafety();
        demonstrateProducerConsumer();
        demonstrateParallelCalculation();
        demonstrateCountDownLatch();
        
        System.out.println("=== 多執行緒示範完成 ===");
    }
}