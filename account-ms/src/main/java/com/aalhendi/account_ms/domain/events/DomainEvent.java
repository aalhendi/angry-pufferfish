package com.aalhendi.account_ms.domain.events;

import java.time.Instant;

/**
 * Base interface for all domain events.
 * Represents something that happened in the domain that other parts of the system might be interested in.
 */
public interface DomainEvent {
    
    /**
     * Gets the unique identifier of the aggregate that generated this event.
     */
    String getAggregateId();
    
    /**
     * Gets the type of the event.
     */
    String getEventType();
    
    /**
     * Gets the timestamp when the event occurred.
     */
    Instant getOccurredAt();
    
    /**
     * Gets the version of the aggregate when this event was generated.
     */
    Long getAggregateVersion();
} 