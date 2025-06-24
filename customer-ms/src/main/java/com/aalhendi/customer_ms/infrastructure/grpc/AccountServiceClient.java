package com.aalhendi.customer_ms.infrastructure.grpc;

import com.aalhendi.account.grpc.*;
import io.grpc.StatusRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * gRPC client for calling Account service from Customer service.
 * Handles inter-service communication to account-ms.
 */
@Service
public class AccountServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(AccountServiceClient.class);
    
    private final AccountServiceGrpc.AccountServiceBlockingStub accountServiceStub;

    public AccountServiceClient(AccountServiceGrpc.AccountServiceBlockingStub accountServiceStub) {
        this.accountServiceStub = accountServiceStub;
    }

    /**
     * Gets account summary for a customer.
     * 
     * @param customerNumber the customer number
     * @return account summary response
     * @throws AccountServiceException if service error
     */
    public GetAccountSummaryResponse getAccountSummary(String customerNumber) {
        logger.debug("Calling account service to get account summary for customer: {}", customerNumber);
        
        try {
            GetAccountSummaryRequest request = GetAccountSummaryRequest.newBuilder()
                .setCustomerNumber(customerNumber)
                .build();
                
            return accountServiceStub.getAccountSummary(request);
            
        } catch (StatusRuntimeException e) {
            logger.error("gRPC error getting account summary for customer {}: {}", customerNumber, e.getStatus());
            throw new AccountServiceException("Failed to get account summary for customer: " + customerNumber, e);
        } catch (Exception e) {
            logger.error("Unexpected error getting account summary for customer {}: {}", customerNumber, e.getMessage(), e);
            throw new AccountServiceException("Unexpected error getting account summary for customer: " + customerNumber, e);
        }
    }

    /**
     * Checks if customer has active accounts.
     * 
     * @param customerNumber the customer number
     * @return active accounts response
     * @throws AccountServiceException if service error
     */
    public HasActiveAccountsResponse hasActiveAccounts(String customerNumber) {
        logger.debug("Calling account service to check active accounts for customer: {}", customerNumber);
        
        try {
            HasActiveAccountsRequest request = HasActiveAccountsRequest.newBuilder()
                .setCustomerNumber(customerNumber)
                .build();
                
            return accountServiceStub.hasActiveAccounts(request);
            
        } catch (StatusRuntimeException e) {
            logger.error("gRPC error checking active accounts for customer {}: {}", customerNumber, e.getStatus());
            throw new AccountServiceException("Failed to check active accounts for customer: " + customerNumber, e);
        } catch (Exception e) {
            logger.error("Unexpected error checking active accounts for customer {}: {}", customerNumber, e.getMessage(), e);
            throw new AccountServiceException("Unexpected error checking active accounts for customer: " + customerNumber, e);
        }
    }

    /**
     * Gets all accounts for a customer.
     * 
     * @param customerNumber the customer number
     * @return accounts response
     * @throws AccountServiceException if service error
     */
    public GetAccountsByCustomerResponse getAccountsByCustomer(String customerNumber) {
        logger.debug("Calling account service to get accounts for customer: {}", customerNumber);
        
        try {
            GetAccountsByCustomerRequest request = GetAccountsByCustomerRequest.newBuilder()
                .setCustomerNumber(customerNumber)
                .build();
                
            return accountServiceStub.getAccountsByCustomer(request);
            
        } catch (StatusRuntimeException e) {
            logger.error("gRPC error getting accounts for customer {}: {}", customerNumber, e.getStatus());
            throw new AccountServiceException("Failed to get accounts for customer: " + customerNumber, e);
        } catch (Exception e) {
            logger.error("Unexpected error getting accounts for customer {}: {}", customerNumber, e.getMessage(), e);
            throw new AccountServiceException("Unexpected error getting accounts for customer: " + customerNumber, e);
        }
    }

    /**
     * Custom exception for account service communication errors.
     */
    public static class AccountServiceException extends RuntimeException {
        public AccountServiceException(String message, Throwable cause) {
            super(message, cause);
        }
    }
} 