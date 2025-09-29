package com.tygrus.task_list.domain.exception;

/**
 * Domain層基礎例外類別
 * 
 * 所有Domain相關的業務例外都應該繼承這個類
 * 展示例外層次結構設計
 */
public abstract class DomainException extends RuntimeException {

    protected DomainException(String message) {
        super(message);
    }

    protected DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}