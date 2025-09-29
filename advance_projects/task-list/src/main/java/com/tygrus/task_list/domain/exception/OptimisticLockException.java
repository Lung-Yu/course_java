package com.tygrus.task_list.domain.exception;

/**
 * 樂觀鎖例外
 * 
 * 當併發修改導致版本衝突時拋出
 * 展示併發控制的例外處理
 */
public class OptimisticLockException extends DomainException {

    public OptimisticLockException(String message) {
        super(message);
    }

    public OptimisticLockException(String message, Throwable cause) {
        super(message, cause);
    }
}