package com.aalhendi.account_ms.infrastructure.events;

/**
 * Exception thrown when domain events cannot be published.
 */
public class EventPublishingException extends RuntimeException {
    
    public EventPublishingException(String message) {
        super(message);
    }
    
    public EventPublishingException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public EventPublishingException(Throwable cause) {
        super(cause);
    }
} 