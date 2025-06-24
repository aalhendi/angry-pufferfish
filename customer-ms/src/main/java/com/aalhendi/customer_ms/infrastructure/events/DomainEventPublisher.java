package com.aalhendi.customer_ms.infrastructure.events;

import com.aalhendi.customer_ms.domain.events.DomainEvent;

/**
 * Interface for publishing domain events to external systems.
 * This abstraction allows us to switch between different messaging systems.
 * TODO(aalhendi): /should/ be in common lib
 */
public interface DomainEventPublisher {
    
    /**
     * Publishes a domain event to the appropriate topic/queue.
     * 
     * @param event The domain event to publish
     * @throws EventPublishingException if the event cannot be published
     */
    void publish(DomainEvent event);
    
    /**
     * Publishes a domain event to a specific topic.
     * 
     * @param topic The topic to publish to
     * @param event The domain event to publish
     * @throws EventPublishingException if the event cannot be published
     */
    void publish(String topic, DomainEvent event);
} 