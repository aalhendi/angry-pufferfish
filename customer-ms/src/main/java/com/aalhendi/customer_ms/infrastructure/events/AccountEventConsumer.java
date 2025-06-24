package com.aalhendi.customer_ms.infrastructure.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * Kafka consumer for account domain events.
 * Handles account events that are relevant to the customer service.
 */
@Component
public class AccountEventConsumer {
    
    private static final Logger logger = LoggerFactory.getLogger(AccountEventConsumer.class);
    
    private final ObjectMapper objectMapper;
    
    public AccountEventConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    
    /**
     * Handles account created events.
     * Updates customer's account count and tracks account relationships.
     */
    @KafkaListener(topics = "account.events.created", groupId = "customer-service")
    public void handleAccountCreated(@Payload String eventJson,
                                   @Header(KafkaHeaders.RECEIVED_KEY) String key,
                                   @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                   @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                                   @Header(KafkaHeaders.OFFSET) long offset) {
        
        logger.info("Received account created event from topic {} partition {} offset {} with key {}", 
                   topic, partition, offset, key);
        
        try {
            var eventData = objectMapper.readTree(eventJson);
            String accountNumber = eventData.get("account_number").asText();
            String customerNumber = eventData.get("customer_number").asText();
            String accountType = eventData.get("account_type").asText();
            
            logger.info("Account {} of type {} created for customer {}", 
                       accountNumber, accountType, customerNumber);

            // NOTE(aalhendi): this is a demo, so we just log.
            // in a real-scenario, we update account counts, trigger notifications or update local caches if any...
            
        } catch (Exception e) {
            logger.error("Failed to process account created event: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Handles account closed events.
     * Updates customer's account counts when accounts are closed.
     */
    @KafkaListener(topics = "account.events.closed", groupId = "customer-service")
    public void handleAccountClosed(@Payload String eventJson,
                                  @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        
        logger.info("Received account closed event with key {}", key);
        
        try {
            var eventData = objectMapper.readTree(eventJson);
            String accountNumber = eventData.get("account_number").asText();
            String customerNumber = eventData.get("customer_number").asText();
            
            logger.info("Account {} closed for customer {}", accountNumber, customerNumber);

            // NOTE(aalhendi): this is a demo, so we just log.
            // in a real-scenario, we update account counts, trigger notifications or update local caches if any...
            
        } catch (Exception e) {
            logger.error("Failed to process account closed event: {}", e.getMessage(), e);
        }
    }

    /**
     * Handles account status changed events.
     */
    @KafkaListener(topics = "account.events.status-changed")
    public void handleAccountStatusChanged(@Payload String eventJson) {
        logger.info("Received account status changed event: {}", eventJson);
        
        try {         
            logger.info("Successfully processed account status changed event");
        } catch (Exception e) {
            logger.error("Error processing account status changed event: {}", eventJson, e);
        }
    }

    /**
     * Handles account transaction events.
     */
    @KafkaListener(topics = "account.events.transaction")
    public void handleAccountTransaction(@Payload String eventJson) {
        logger.info("Received account transaction event: {}", eventJson);
        
        try {          
            logger.info("Successfully processed account transaction event");
        } catch (Exception e) {
            logger.error("Error processing account transaction event: {}", eventJson, e);
        }
    }
} 