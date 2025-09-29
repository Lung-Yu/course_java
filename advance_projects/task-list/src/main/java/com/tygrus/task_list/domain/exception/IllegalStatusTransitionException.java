package com.tygrus.task_list.domain.exception;

/**
 * 非法狀態轉換例外
 * 
 * 當嘗試進行不允許的任務狀態轉換時拋出
 * 展示域驅動設計中的業務規則例外處理
 */
public class IllegalStatusTransitionException extends DomainException {

    private final String fromStatus;
    private final String toStatus;

    public IllegalStatusTransitionException(String fromStatus, String toStatus) {
        super(String.format("Invalid status transition from %s to %s", fromStatus, toStatus));
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
    }

    public IllegalStatusTransitionException(String fromStatus, String toStatus, String message) {
        super(message);
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
    }

    public String getFromStatus() {
        return fromStatus;
    }

    public String getToStatus() {
        return toStatus;
    }
}