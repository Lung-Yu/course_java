package com.tygrus.task_list.application.exception;

import com.tygrus.task_list.domain.exception.DomainException;

/**
 * 權限拒絕例外
 * 
 * 當用戶沒有足夠權限執行操作時拋出
 * 展示Application層的權限控制
 */
public class PermissionDeniedException extends DomainException {

    private final String userId;
    private final String operation;
    private final String resourceId;

    public PermissionDeniedException(String userId, String operation, String resourceId) {
        super(String.format("User %s does not have permission to %s on resource %s", 
            userId, operation, resourceId));
        this.userId = userId;
        this.operation = operation;
        this.resourceId = resourceId;
    }

    public PermissionDeniedException(String userId, String operation, String resourceId, String message) {
        super(message);
        this.userId = userId;
        this.operation = operation;
        this.resourceId = resourceId;
    }

    public String getUserId() {
        return userId;
    }

    public String getOperation() {
        return operation;
    }

    public String getResourceId() {
        return resourceId;
    }
}