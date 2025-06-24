package com.aalhendi.customer_ms.infrastructure.grpc;

import com.aalhendi.customer.grpc.*;
import com.aalhendi.customer_ms.domain.entities.Customer;
import com.aalhendi.customer_ms.domain.valueobjects.CustomerStatus;
import com.aalhendi.customer_ms.domain.valueobjects.CustomerType;
import com.aalhendi.customer_ms.domain.services.CustomerServiceImpl;
import com.aalhendi.customer_ms.infrastructure.grpc.AccountServiceClient;
import com.aalhendi.account.grpc.GetAccountSummaryResponse;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.grpc.server.service.GrpcService;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * gRPC service implementation for Customer operations.
 * Handles inter-service communication from account-ms.
 */
@GrpcService
@Service
public class CustomerGrpcService extends CustomerServiceGrpc.CustomerServiceImplBase {

    private static final Logger logger = LoggerFactory.getLogger(CustomerGrpcService.class);
    
    private final CustomerServiceImpl customerService;
    private final AccountServiceClient accountServiceClient;

    public CustomerGrpcService(CustomerServiceImpl customerService, AccountServiceClient accountServiceClient) {
        this.customerService = customerService;
        this.accountServiceClient = accountServiceClient;
    }

    @Override
    public void getCustomer(GetCustomerRequest request, StreamObserver<GetCustomerResponse> responseObserver) {
        logger.debug("gRPC GetCustomer called for customer: {}", request.getCustomerNumber());
        
        try {
            Optional<Customer> customerOpt = customerService.findByCustomerNumber(request.getCustomerNumber());
            
            if (customerOpt.isEmpty()) {
                responseObserver.onError(Status.NOT_FOUND
                    .withDescription("Customer not found: " + request.getCustomerNumber())
                    .asRuntimeException());
                return;
            }
            
            Customer customer = customerOpt.get();
            GetCustomerResponse response = GetCustomerResponse.newBuilder()
                .setCustomerNumber(customer.getCustomerNumber().value())
                .setName(customer.getName().value())
                .setNationalId(customer.getNationalId().value())
                .setCustomerType(customer.getCustomerType().name())
                .setAddress(customer.getAddress().value())
                .setStatus(customer.getStatus().name())
                .setCreatedAt(customer.getCreatedAt().toString())
                .setUpdatedAt(customer.getUpdatedAt().toString())
                .build();
                
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
        } catch (Exception e) {
            logger.error("Error getting customer: {}", request.getCustomerNumber(), e);
            responseObserver.onError(Status.INTERNAL
                .withDescription("Internal error: " + e.getMessage())
                .asRuntimeException());
        }
    }

    @Override
    public void validateCustomer(ValidateCustomerRequest request, StreamObserver<ValidateCustomerResponse> responseObserver) {
        logger.debug("gRPC ValidateCustomer called for customer: {}", request.getCustomerNumber());
        
        try {
            Optional<Customer> customerOpt = customerService.findByCustomerNumber(request.getCustomerNumber());
            
            ValidateCustomerResponse.Builder responseBuilder = ValidateCustomerResponse.newBuilder();
            
            if (customerOpt.isEmpty()) {
                responseBuilder
                    .setIsValid(false)
                    .setIsActive(false)
                    .setErrorMessage("Customer not found: " + request.getCustomerNumber());
            } else {
                Customer customer = customerOpt.get();
                boolean isActive = customer.getStatus() == CustomerStatus.ACTIVE;
                
                responseBuilder
                    .setIsValid(true)
                    .setIsActive(isActive)
                    .setCustomerType(customer.getCustomerType().name())
                    .setStatus(customer.getStatus().name());
                    
                if (!isActive) {
                    responseBuilder.setErrorMessage("Customer is not active. Status: " + customer.getStatus().name());
                }
            }
            
            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();
            
        } catch (Exception e) {
            logger.error("Error validating customer: {}", request.getCustomerNumber(), e);
            responseObserver.onError(Status.INTERNAL
                .withDescription("Internal error: " + e.getMessage())
                .asRuntimeException());
        }
    }

    @Override
    public void checkAccountLimit(CheckAccountLimitRequest request, StreamObserver<CheckAccountLimitResponse> responseObserver) {
        logger.debug("gRPC CheckAccountLimit called for customer: {}, accountType: {}", 
            request.getCustomerNumber(), request.getAccountType());
        
        try {
            Optional<Customer> customerOpt = customerService.findByCustomerNumber(request.getCustomerNumber());
            
            CheckAccountLimitResponse.Builder responseBuilder = CheckAccountLimitResponse.newBuilder();
            
            if (customerOpt.isEmpty()) {
                responseBuilder
                    .setCanCreateAccount(false)
                    .setCurrentAccountCount(0)
                    .setMaxAccountLimit(10)
                    .setAlreadyHasSalaryAccount(false)
                    .setErrorMessage("Customer not found: " + request.getCustomerNumber());
            } else {
                Customer customer = customerOpt.get();
                
                // Get real account data from account service
                int currentActiveAccountCount = 0;  // Only ACTIVE accounts count toward the 10-account limit
                int totalAccountCount = 0;          // All accounts (including closed) for serial numbering
                int maxAccountLimit = 10;
                boolean alreadyHasSalaryAccount = false;
                
                try {
                    GetAccountSummaryResponse accountSummary = accountServiceClient.getAccountSummary(request.getCustomerNumber());
                    currentActiveAccountCount = accountSummary.getActiveAccounts();  // Only active accounts for limit
                    totalAccountCount = accountSummary.getTotalAccounts();           // All accounts for serial numbering
                    alreadyHasSalaryAccount = accountSummary.getHasSalaryAccount();
                } catch (Exception e) {
                    logger.warn("Failed to get account summary for customer {}: {}", request.getCustomerNumber(), e.getMessage());
                    // Continue with default values (0 accounts, no salary account)
                    // TODO(aalhendi): this is a hack. in a real scenario, we would return an error to the client
                }
                
                // Business Rules:
                // 1. Customer must be ACTIVE
                // 2. Active accounts must be < 10 (closed accounts don't count toward limit)
                // 3. Can't have multiple SALARY accounts
                // 4. Total accounts (including closed) must be < 10 for serial numbering (handled in account service)
                boolean canCreate = customer.getStatus() == CustomerStatus.ACTIVE 
                    && currentActiveAccountCount < maxAccountLimit
                    && totalAccountCount < maxAccountLimit  // Also check total for serial availability
                    && (!request.getAccountType().equals("SALARY") || !alreadyHasSalaryAccount);
                
                responseBuilder
                    .setCanCreateAccount(canCreate)
                    .setCurrentAccountCount(currentActiveAccountCount)  // Return active account count for limit checking
                    .setMaxAccountLimit(maxAccountLimit)
                    .setAlreadyHasSalaryAccount(alreadyHasSalaryAccount);
                    
                if (!canCreate) {
                    if (customer.getStatus() != CustomerStatus.ACTIVE) {
                        responseBuilder.setErrorMessage("Customer is not active. Status: " + customer.getStatus().name());
                    } else if (currentActiveAccountCount >= maxAccountLimit) {
                        responseBuilder.setErrorMessage("Customer has reached maximum active account limit of " + maxAccountLimit);
                    } else if (totalAccountCount >= maxAccountLimit) {
                        responseBuilder.setErrorMessage("Customer has reached maximum total account limit (including closed accounts) of " + maxAccountLimit);
                    } else if (request.getAccountType().equals("SALARY") && alreadyHasSalaryAccount) {
                        responseBuilder.setErrorMessage("Customer already has a salary account");
                    }
                }
            }
            
            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();
            
        } catch (Exception e) {
            logger.error("Error checking account limit for customer: {}", request.getCustomerNumber(), e);
            responseObserver.onError(Status.INTERNAL
                .withDescription("Internal error: " + e.getMessage())
                .asRuntimeException());
        }
    }
} 