package com.tygrus.task_list.infrastructure.cache;

import com.tygrus.task_list.application.dto.StatisticsReport;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 統計結果快取管理器
 * 
 * 提供記憶體內快取機制，用於提升統計查詢效能
 * 支援 TTL (Time To Live) 和記憶體管理
 */
@Component
public class StatisticsCache {
    
    /**
     * 快取項目內部類別
     */
    private static class CacheEntry {
        private final StatisticsReport report;
        private final LocalDateTime createdAt;
        private final long ttlMinutes;
        private volatile LocalDateTime lastAccessed;
        private final AtomicLong accessCount = new AtomicLong(0);
        
        public CacheEntry(StatisticsReport report, long ttlMinutes) {
            this.report = report;
            this.createdAt = LocalDateTime.now();
            this.ttlMinutes = ttlMinutes;
            this.lastAccessed = LocalDateTime.now();
        }
        
        public boolean isExpired() {
            return LocalDateTime.now().isAfter(createdAt.plusMinutes(ttlMinutes));
        }
        
        public StatisticsReport getReport() {
            lastAccessed = LocalDateTime.now();
            accessCount.incrementAndGet();
            return report;
        }
        
        public LocalDateTime getLastAccessed() {
            return lastAccessed;
        }
        
        public long getAccessCount() {
            return accessCount.get();
        }
        
        public LocalDateTime getCreatedAt() {
            return createdAt;
        }
        
        public long getTtlMinutes() {
            return ttlMinutes;
        }
    }
    
    private final ConcurrentMap<String, CacheEntry> cache = new ConcurrentHashMap<>();
    private final long defaultTtlMinutes;
    private final int maxCacheSize;
    private final AtomicLong hitCount = new AtomicLong(0);
    private final AtomicLong missCount = new AtomicLong(0);
    
    /**
     * 使用預設設定建立快取
     * TTL: 15分鐘，最大容量: 100
     */
    public StatisticsCache() {
        this(15, 100);
    }
    
    /**
     * 建立自訂設定的快取
     * 
     * @param defaultTtlMinutes 預設存活時間（分鐘）
     * @param maxCacheSize 最大快取項目數
     */
    public StatisticsCache(long defaultTtlMinutes, int maxCacheSize) {
        this.defaultTtlMinutes = defaultTtlMinutes;
        this.maxCacheSize = maxCacheSize;
    }
    
    /**
     * 獲取快取的統計報告
     * 
     * @param key 快取鍵
     * @return 快取的統計報告，如果不存在或已過期則返回空
     */
    public Optional<StatisticsReport> get(String key) {
        CacheEntry entry = cache.get(key);
        
        if (entry == null) {
            missCount.incrementAndGet();
            return Optional.empty();
        }
        
        if (entry.isExpired()) {
            cache.remove(key);
            missCount.incrementAndGet();
            return Optional.empty();
        }
        
        hitCount.incrementAndGet();
        return Optional.of(entry.getReport());
    }
    
    /**
     * 使用預設 TTL 存儲統計報告
     */
    public void put(String key, StatisticsReport report) {
        put(key, report, defaultTtlMinutes);
    }
    
    /**
     * 使用自訂 TTL 存儲統計報告
     */
    public void put(String key, StatisticsReport report, long ttlMinutes) {
        // 檢查快取容量，如果超過最大值則清理
        if (cache.size() >= maxCacheSize) {
            evictLeastRecentlyUsed();
        }
        
        CacheEntry entry = new CacheEntry(report, ttlMinutes);
        cache.put(key, entry);
    }
    
    /**
     * 移除特定鍵的快取項目
     */
    public boolean remove(String key) {
        return cache.remove(key) != null;
    }
    
    /**
     * 清空所有快取
     */
    public void clear() {
        cache.clear();
        hitCount.set(0);
        missCount.set(0);
    }
    
    /**
     * 清理過期的快取項目
     */
    public int cleanupExpired() {
        int cleanedCount = 0;
        
        for (var iterator = cache.entrySet().iterator(); iterator.hasNext();) {
            var entry = iterator.next();
            if (entry.getValue().isExpired()) {
                iterator.remove();
                cleanedCount++;
            }
        }
        
        return cleanedCount;
    }
    
    /**
     * 驅逐最近最少使用的項目（LRU策略）
     */
    private void evictLeastRecentlyUsed() {
        if (cache.isEmpty()) {
            return;
        }
        
        String oldestKey = null;
        LocalDateTime oldestTime = LocalDateTime.now();
        
        for (var entry : cache.entrySet()) {
            LocalDateTime lastAccessed = entry.getValue().getLastAccessed();
            if (lastAccessed.isBefore(oldestTime)) {
                oldestTime = lastAccessed;
                oldestKey = entry.getKey();
            }
        }
        
        if (oldestKey != null) {
            cache.remove(oldestKey);
        }
    }
    
    /**
     * 檢查快取中是否包含指定鍵
     */
    public boolean containsKey(String key) {
        CacheEntry entry = cache.get(key);
        return entry != null && !entry.isExpired();
    }
    
    /**
     * 獲取快取統計資訊
     */
    public CacheStats getStats() {
        long totalRequests = hitCount.get() + missCount.get();
        double hitRate = totalRequests > 0 ? (double) hitCount.get() / totalRequests : 0.0;
        
        return new CacheStats(
            cache.size(),
            hitCount.get(),
            missCount.get(),
            hitRate,
            maxCacheSize
        );
    }
    
    /**
     * 快取統計資訊類別
     */
    public static class CacheStats {
        private final int currentSize;
        private final long hitCount;
        private final long missCount;
        private final double hitRate;
        private final int maxSize;
        
        public CacheStats(int currentSize, long hitCount, long missCount, double hitRate, int maxSize) {
            this.currentSize = currentSize;
            this.hitCount = hitCount;
            this.missCount = missCount;
            this.hitRate = hitRate;
            this.maxSize = maxSize;
        }
        
        public int getCurrentSize() { return currentSize; }
        public long getHitCount() { return hitCount; }
        public long getMissCount() { return missCount; }
        public double getHitRate() { return hitRate; }
        public int getMaxSize() { return maxSize; }
        
        @Override
        public String toString() {
            return String.format(
                "CacheStats{size=%d/%d, hits=%d, misses=%d, hitRate=%.2f%%}",
                currentSize, maxSize, hitCount, missCount, hitRate * 100
            );
        }
    }
    
    /**
     * 獲取所有快取鍵的詳細資訊（用於調試）
     */
    public String getCacheInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== 統計快取資訊 ===\n");
        sb.append(getStats()).append("\n");
        sb.append("快取項目詳情:\n");
        
        cache.forEach((key, entry) -> {
            sb.append(String.format(
                "  %s: 創建時間=%s, 存取次數=%d, TTL=%d分鐘, 已過期=%s\n",
                key,
                entry.getCreatedAt().toString().substring(0, 19),
                entry.getAccessCount(),
                entry.getTtlMinutes(),
                entry.isExpired()
            ));
        });
        
        return sb.toString();
    }
}