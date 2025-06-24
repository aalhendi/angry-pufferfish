package com.aalhendi.account_ms.domain.events;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.Objects;

/**
 * Abstract base class for domain events providing common functionality.
 */
public abstract class AbstractDomainEvent implements DomainEvent {
    
    @JsonProperty("aggregate_id")
    private final String aggregateId;
    
    @JsonProperty("event_type")
    private final String eventType;
    
    @JsonProperty("occurred_at")
    private final Instant occurredAt;
    
    @JsonProperty("aggregate_version")
    private final Long aggregateVersion;
    
    protected AbstractDomainEvent(String aggregateId, String eventType, Long aggregateVersion) {
        this.aggregateId = Objects.requireNonNull(aggregateId, "Aggregate ID cannot be null");
        this.eventType = Objects.requireNonNull(eventType, "Event type cannot be null");
        this.aggregateVersion = Objects.requireNonNull(aggregateVersion, "Aggregate version cannot be null");
        this.occurredAt = Instant.now();
    }
    
    @Override
    public String getAggregateId() {
        return aggregateId;
    }
    
    @Override
    public String getEventType() {
        return eventType;
    }
    
    @Override
    public Instant getOccurredAt() {
        return occurredAt;
    }
    
    @Override
    public Long getAggregateVersion() {
        return aggregateVersion;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (Objects.isNull(o) || getClass() != o.getClass()) return false;
        AbstractDomainEvent that = (AbstractDomainEvent) o;
        return Objects.equals(aggregateId, that.aggregateId) &&
               Objects.equals(eventType, that.eventType) &&
               Objects.equals(aggregateVersion, that.aggregateVersion);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(aggregateId, eventType, aggregateVersion);
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" +
               "aggregateId='" + aggregateId +
               ", eventType='" + eventType +
               ", occurredAt=" + occurredAt +
               ", aggregateVersion=" + aggregateVersion +
               ']';
    }
} 