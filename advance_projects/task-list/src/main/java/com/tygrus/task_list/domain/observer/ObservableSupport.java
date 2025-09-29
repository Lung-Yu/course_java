package com.tygrus.task_list.domain.observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Observable 介面的通用實作
 * 提供執行緒安全的觀察者管理和通知機制
 * 
 * @param <T> 通知事件的類型
 */
public class ObservableSupport<T> implements Observable<T> {
    
    private static final Logger logger = LoggerFactory.getLogger(ObservableSupport.class);
    
    private final Map<String, Observer<T>> observers = new ConcurrentHashMap<>();
    private final Executor asyncExecutor;
    
    /**
     * 建構子 - 使用預設的 ForkJoinPool
     */
    public ObservableSupport() {
        this(ForkJoinPool.commonPool());
    }
    
    /**
     * 建構子 - 使用自定義的執行器
     * 
     * @param executor 用於異步通知的執行器
     */
    public ObservableSupport(Executor executor) {
        this.asyncExecutor = executor;
    }
    
    @Override
    public void addObserver(Observer<T> observer) {
        if (observer == null) {
            throw new IllegalArgumentException("Observer cannot be null");
        }
        
        String observerId = observer.getObserverId();
        if (observerId == null || observerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Observer ID cannot be null or empty");
        }
        
        observers.put(observerId, observer);
        logger.debug("Observer added: {}", observerId);
    }
    
    @Override
    public void removeObserver(Observer<T> observer) {
        if (observer != null) {
            removeObserver(observer.getObserverId());
        }
    }
    
    @Override
    public void removeObserver(String observerId) {
        if (observerId != null) {
            Observer<T> removed = observers.remove(observerId);
            if (removed != null) {
                logger.debug("Observer removed: {}", observerId);
            }
        }
    }
    
    @Override
    public void notifyObservers(T event) {
        if (event == null) {
            logger.warn("Attempted to notify observers with null event");
            return;
        }
        
        List<Observer<T>> interestedObservers = getInterestedObservers(event);
        
        for (Observer<T> observer : interestedObservers) {
            try {
                observer.update(event);
                logger.debug("Observer {} notified successfully", observer.getObserverId());
            } catch (Exception e) {
                logger.error("Error notifying observer {}: {}", 
                    observer.getObserverId(), e.getMessage(), e);
            }
        }
        
        logger.debug("Notified {} observers for event type: {}", 
            interestedObservers.size(), event.getClass().getSimpleName());
    }
    
    @Override
    public CompletableFuture<Void> notifyObserversAsync(T event) {
        if (event == null) {
            return CompletableFuture.completedFuture(null);
        }
        
        List<Observer<T>> interestedObservers = getInterestedObservers(event);
        
        if (interestedObservers.isEmpty()) {
            return CompletableFuture.completedFuture(null);
        }
        
        // 為每個觀察者創建異步任務
        CompletableFuture<?>[] futures = interestedObservers.stream()
            .map(observer -> CompletableFuture.runAsync(() -> {
                try {
                    observer.update(event);
                    logger.debug("Observer {} notified asynchronously", observer.getObserverId());
                } catch (Exception e) {
                    logger.error("Error notifying observer {} asynchronously: {}", 
                        observer.getObserverId(), e.getMessage(), e);
                }
            }, asyncExecutor))
            .toArray(CompletableFuture[]::new);
        
        return CompletableFuture.allOf(futures)
            .whenComplete((result, throwable) -> {
                if (throwable != null) {
                    logger.error("Error in async notification completion: {}", throwable.getMessage(), throwable);
                } else {
                    logger.debug("Async notification completed for {} observers", interestedObservers.size());
                }
            });
    }
    
    @Override
    public int getObserverCount() {
        return observers.size();
    }
    
    @Override
    public boolean hasObservers() {
        return !observers.isEmpty();
    }
    
    @Override
    public void clearObservers() {
        int count = observers.size();
        observers.clear();
        logger.debug("Cleared {} observers", count);
    }
    
    /**
     * 獲取對特定事件感興趣的觀察者清單
     * 
     * @param event 事件物件
     * @return 感興趣的觀察者清單
     */
    private List<Observer<T>> getInterestedObservers(T event) {
        return observers.values().stream()
            .filter(observer -> observer.isInterestedIn(event.getClass()))
            .collect(Collectors.toList());
    }
    
    /**
     * 取得所有已註冊的觀察者ID
     * 
     * @return 觀察者ID集合
     */
    public java.util.Set<String> getObserverIds() {
        return new java.util.HashSet<>(observers.keySet());
    }
}