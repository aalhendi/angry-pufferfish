package com.aalhendi.account_ms.infrastructure.grpc;

import com.aalhendi.customer.grpc.*;
import io.grpc.StatusRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * gRPC client for calling Customer service from Account service.
 * Handles inter-service communication to customer-ms.
 * 
 * TODO(aalhendi): impl proper gRPC client config with service discovery, as opposed to hardcoded values
 */
@Service
public class CustomerServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(CustomerServiceClient.class);
    
    // TODO(aalhendi): inject the actual gRPC stub with spring autowiring when Spring gRPC client is configured
    // should look like: @GrpcClient("customer-service")
    private final CustomerServiceGrpc.CustomerServiceBlockingStub customerServiceStub;

    public CustomerServiceClient(CustomerServiceGrpc.CustomerServiceBlockingStub customerServiceStub) {
        this.customerServiceStub = customerServiceStub;
    }

    /**
     * Gets customer details by customer number.
     * 
     * @param customerNumber the customer number
     * @return customer response
     * @throws CustomerServiceException if a customer is not found or service error
     */
    public GetCustomerResponse getCustomer(String customerNumber) {
        logger.debug("Calling customer service to get customer: {}", customerNumber);
        
        try {
            GetCustomerRequest request = GetCustomerRequest.newBuilder()
                .setCustomerNumber(customerNumber)
                .build();
                
            return customerServiceStub.getCustomer(request);
            
        } catch (StatusRuntimeException e) {
            logger.error("gRPC error getting customer {}: {}", customerNumber, e.getStatus());
            throw new CustomerServiceException("Failed to get customer: " + customerNumber, e);
        } catch (Exception e) {
            logger.error("Unexpected error getting customer {}: {}", customerNumber, e.getMessage(), e);
            throw new CustomerServiceException("Unexpected error getting customer: " + customerNumber, e);
        }
    }

    /**
     * Validates if customer exists and is active.
     * 
     * @param customerNumber the customer number
     * @return validation response
     * @throws CustomerServiceException if service error
     */
    public ValidateCustomerResponse validateCustomer(String customerNumber) {
        logger.debug("Calling customer service to validate customer: {}", customerNumber);
        
        try {
            ValidateCustomerRequest request = ValidateCustomerRequest.newBuilder()
                .setCustomerNumber(customerNumber)
                .build();
                
            return customerServiceStub.validateCustomer(request);
            
        } catch (StatusRuntimeException e) {
            logger.error("gRPC error validating customer {}: {}", customerNumber, e.getStatus());
            throw new CustomerServiceException("Failed to validate customer: " + customerNumber, e);
        } catch (Exception e) {
            logger.error("Unexpected error validating customer {}: {}", customerNumber, e.getMessage(), e);
            throw new CustomerServiceException("Unexpected error validating customer: " + customerNumber, e);
        }
    }

    /**
     * Checks if customer can create additional accounts.
     * 
     * @param customerNumber the customer number
     * @param accountType the account type to create
     * @return account limit check response
     * @throws CustomerServiceException if service error
     */
    public CheckAccountLimitResponse checkAccountLimit(String customerNumber, String accountType) {
        logger.debug("Calling customer service to check account limit for customer: {}, accountType: {}", 
            customerNumber, accountType);
        
        try {
            CheckAccountLimitRequest request = CheckAccountLimitRequest.newBuilder()
                .setCustomerNumber(customerNumber)
                .setAccountType(accountType)
                .build();
                
            return customerServiceStub.checkAccountLimit(request);
            
        } catch (StatusRuntimeException e) {
            logger.error("gRPC error checking account limit for customer {}: {}", customerNumber, e.getStatus());
            throw new CustomerServiceException("Failed to check account limit for customer: " + customerNumber, e);
        } catch (Exception e) {
            logger.error("Unexpected error checking account limit for customer {}: {}", customerNumber, e.getMessage(), e);
            throw new CustomerServiceException("Unexpected error checking account limit for customer: " + customerNumber, e);
        }
    }

    /**
     * Custom exception for customer service communication errors.
     */
    public static class CustomerServiceException extends RuntimeException {
        public CustomerServiceException(String message, Throwable cause) {
            super(message, cause);
        }
    }
} 