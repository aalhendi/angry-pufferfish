package com.aalhendi.account_ms.infrastructure.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * Kafka consumer for customer domain events.
 * Handles customer events that are relevant to the account service.
 */
@Component
public class CustomerEventConsumer {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomerEventConsumer.class);
    
    private final ObjectMapper objectMapper;
    
    public CustomerEventConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    
    /**
     * Handles customer created events.
     * Currently just log the event for audit purposes.
     */
    @KafkaListener(topics = "customer.events.created", groupId = "account-service")
    public void handleCustomerCreated(@Payload String eventJson,
                                     @Header(KafkaHeaders.RECEIVED_KEY) String key,
                                     @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                     @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                                     @Header(KafkaHeaders.OFFSET) long offset) {
        
        logger.info("Received customer created event from topic {} partition {} offset {} with key {}", 
                   topic, partition, offset, key);
        
        try {
            // Parse the event (for now just log it)
            var eventData = objectMapper.readTree(eventJson);
            String customerNumber = eventData.get("customer_number").asText();
            String customerType = eventData.get("customer_type").asText();
            
            logger.info("Customer {} of type {} was created", customerNumber, customerType);
            
        } catch (Exception e) {
            logger.error("Failed to process customer created event: {}", e.getMessage(), e);
            // TODO(aalhendi): in a real system, we would send to DLQ here
        }
    }
    
    /**
     * Handles customer status changed events.
     * This is important for account service to know if customers become inactive.
     */
    @KafkaListener(topics = "customer.events.status-changed", groupId = "account-service")
    public void handleCustomerStatusChanged(@Payload String eventJson,
                                          @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        
        logger.info("Received customer status changed event with key {}", key);
        
        try {
            var eventData = objectMapper.readTree(eventJson);
            String customerNumber = eventData.get("customer_number").asText();
            String newStatus = eventData.get("new_status").asText();
            
            logger.info("Customer {} status changed to {}", customerNumber, newStatus);
            
        } catch (Exception e) {
            logger.error("Failed to process customer status changed event: {}", e.getMessage(), e);
        }
    }

    /**
     * Handles customer updated events.
     */
    @KafkaListener(topics = "customer.events.updated")
    public void handleCustomerUpdated(@Payload String eventJson) {
        logger.info("Received customer updated event: {}", eventJson);
        
        try {
            logger.info("Successfully processed customer updated event");
        } catch (Exception e) {
            logger.error("Error processing customer updated event: {}", eventJson, e);
        }
    }
} 