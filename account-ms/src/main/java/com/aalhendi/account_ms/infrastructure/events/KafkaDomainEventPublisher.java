package com.aalhendi.account_ms.infrastructure.events;

import com.aalhendi.account_ms.domain.events.DomainEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * Kafka implementation of the domain event publisher.
 * Publishes domain events to Kafka topics for consumption by other services.
 */
@Component
public class KafkaDomainEventPublisher implements DomainEventPublisher {
    
    private static final Logger logger = LoggerFactory.getLogger(KafkaDomainEventPublisher.class);
    
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    
    public KafkaDomainEventPublisher(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }
    
    @Override
    public void publish(DomainEvent event) {
        String topic = getTopicForEvent(event);
        publish(topic, event);
    }
    
    @Override
    public void publish(String topic, DomainEvent event) {
        try {
            String eventJson = objectMapper.writeValueAsString(event);
            String key = event.getAggregateId();
            
            logger.info("Publishing event {} to topic {} with key {}", 
                       event.getEventType(), topic, key);

            kafkaTemplate
                    .send(topic, key, eventJson)
                    .whenComplete((result, throwable) -> {
                        if (throwable != null) {
                            logger.error("Failed to publish event {} to topic {}: {}",
                                    event.getEventType(), topic, throwable.getMessage(), throwable);
                            throw new EventPublishingException("Failed to publish event", throwable);
                        } else {
                            logger.debug("Successfully published event {} to topic {} at offset {}",
                                    event.getEventType(), topic, result.getRecordMetadata().offset());
                        }
                    });
            
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize event {}: {}", event.getEventType(), e.getMessage(), e);
            throw new EventPublishingException("Failed to serialize event", e);
        }
    }
    
    /**
     * Determines the appropriate Kafka topic for the given event.
     * TODO(aalhendi): this is a hack. should be an enum
     */
    private String getTopicForEvent(DomainEvent event) {
        return switch (event.getEventType()) {
            case "AccountCreated" -> "account.events.created";
            case "AccountUpdated" -> "account.events.updated";
            case "AccountClosed" -> "account.events.closed";
            default -> "account.events.general";
        };
    }
} 