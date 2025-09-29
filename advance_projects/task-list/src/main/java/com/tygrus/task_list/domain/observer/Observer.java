package com.tygrus.task_list.domain.observer;

/**
 * Observer Pattern - Observer 介面
 * 
 * 定義觀察者的更新方法，當被觀察的物件狀態改變時會被通知
 * 
 * @param <T> 通知事件的類型
 */
public interface Observer<T> {
    
    /**
     * 當被觀察者狀態改變時被調用
     * 
     * @param event 事件物件，包含變更的詳細資訊
     */
    void update(T event);
    
    /**
     * 獲取觀察者的唯一識別符
     * 用於在Observable中管理觀察者
     * 
     * @return 觀察者識別符
     */
    String getObserverId();
    
    /**
     * 檢查觀察者是否對特定類型的事件感興趣
     * 
     * @param eventType 事件類型
     * @return true如果感興趣，false否則
     */
    default boolean isInterestedIn(Class<?> eventType) {
        return true; // 預設對所有事件感興趣
    }
}