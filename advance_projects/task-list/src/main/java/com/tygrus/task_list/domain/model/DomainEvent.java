package com.tygrus.task_list.domain.model;

import java.time.LocalDateTime;

/**
 * Domain Event基礎介面
 */
public interface DomainEvent {
    LocalDateTime getOccurredOn();
    String getEventType();
}