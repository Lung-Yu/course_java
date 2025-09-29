package com.tygrus.task_list.domain.observer;

import java.util.concurrent.CompletableFuture;

/**
 * Observer Pattern - Observable 介面
 * 
 * 定義被觀察者的行為，可以註冊、移除和通知觀察者
 * 支援同步和異步通知機制
 * 
 * @param <T> 通知事件的類型
 */
public interface Observable<T> {
    
    /**
     * 註冊觀察者
     * 
     * @param observer 要註冊的觀察者
     */
    void addObserver(Observer<T> observer);
    
    /**
     * 移除觀察者
     * 
     * @param observer 要移除的觀察者
     */
    void removeObserver(Observer<T> observer);
    
    /**
     * 根據ID移除觀察者
     * 
     * @param observerId 觀察者ID
     */
    void removeObserver(String observerId);
    
    /**
     * 同步通知所有註冊的觀察者
     * 
     * @param event 要通知的事件
     */
    void notifyObservers(T event);
    
    /**
     * 異步通知所有觀察者
     * 
     * @param event 要發送的事件
     * @return CompletableFuture 表示異步操作的結果
     */
    CompletableFuture<Void> notifyObserversAsync(T event);
    
    /**
     * 獲取當前註冊的觀察者數量
     * 
     * @return 觀察者數量
     */
    int getObserverCount();
    
    /**
     * 檢查是否有觀察者
     * 
     * @return 如果有觀察者返回true，否則返回false
     */
    boolean hasObservers();
    
    /**
     * 清除所有觀察者
     */
    void clearObservers();
}