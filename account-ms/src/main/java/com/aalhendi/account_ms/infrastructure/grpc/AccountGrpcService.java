package com.aalhendi.account_ms.infrastructure.grpc;

import com.aalhendi.account.grpc.*;
import com.aalhendi.account_ms.domain.entities.Account;
import com.aalhendi.account_ms.domain.services.AccountServiceImpl;
import com.aalhendi.account_ms.domain.repositories.AccountRepository;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.grpc.server.service.GrpcService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * gRPC service implementation for Account operations.
 * Handles inter-service communication from customer-ms.
 */
@GrpcService
@Service
public class AccountGrpcService extends AccountServiceGrpc.AccountServiceImplBase {

    private static final Logger logger = LoggerFactory.getLogger(AccountGrpcService.class);
    
    private final AccountRepository accountRepository;

    public AccountGrpcService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public void getAccountsByCustomer(GetAccountsByCustomerRequest request, 
                                     StreamObserver<GetAccountsByCustomerResponse> responseObserver) {
        logger.debug("gRPC GetAccountsByCustomer called for customer: {}", request.getCustomerNumber());
        
        try {
            List<Account> accounts = accountRepository.findByCustomerNumber(request.getCustomerNumber());
            
            GetAccountsByCustomerResponse.Builder responseBuilder = GetAccountsByCustomerResponse.newBuilder();
            
            for (Account account : accounts) {
                AccountInfo accountInfo = AccountInfo.newBuilder()
                    .setAccountNumber(account.getAccountNumber().value())
                    .setCustomerNumber(account.getAccountNumber().customerNumber())
                    .setAccountType(account.getAccountType().name())
                    .setStatus(account.getStatus().name())
                    .setBalance(account.getBalance().value().toString())
                    .setCreatedAt(account.getCreatedAt().toString())
                    .setUpdatedAt(account.getUpdatedAt().toString())
                    .build();
                    
                responseBuilder.addAccounts(accountInfo);
            }
            
            responseBuilder.setTotalCount(accounts.size());
            
            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();
            
        } catch (Exception e) {
            logger.error("Error getting accounts for customer: {}", request.getCustomerNumber(), e);
            responseObserver.onError(Status.INTERNAL
                .withDescription("Internal error: " + e.getMessage())
                .asRuntimeException());
        }
    }

    @Override
    public void hasActiveAccounts(HasActiveAccountsRequest request, 
                                 StreamObserver<HasActiveAccountsResponse> responseObserver) {
        logger.debug("gRPC HasActiveAccounts called for customer: {}", request.getCustomerNumber());
        
        try {
            List<Account> accounts = accountRepository.findByCustomerNumber(request.getCustomerNumber());
            
            List<Account> activeAccounts = accounts.stream()
                .filter(account -> account.getStatus().isActive())
                .toList();
                
            HasActiveAccountsResponse.Builder responseBuilder = HasActiveAccountsResponse.newBuilder()
                .setHasActiveAccounts(!activeAccounts.isEmpty())
                .setActiveAccountCount(activeAccounts.size());
                
            for (Account account : activeAccounts) {
                responseBuilder.addActiveAccountNumbers(account.getAccountNumber().value());
            }
            
            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();
            
        } catch (Exception e) {
            logger.error("Error checking active accounts for customer: {}", request.getCustomerNumber(), e);
            responseObserver.onError(Status.INTERNAL
                .withDescription("Internal error: " + e.getMessage())
                .asRuntimeException());
        }
    }

    @Override
    public void getAccountSummary(GetAccountSummaryRequest request, 
                                 StreamObserver<GetAccountSummaryResponse> responseObserver) {
        logger.debug("gRPC GetAccountSummary called for customer: {}", request.getCustomerNumber());
        
        try {
            List<Account> accounts = accountRepository.findByCustomerNumber(request.getCustomerNumber());
            
            int totalAccounts = accounts.size();
            int activeAccounts = (int) accounts.stream()
                .filter(account -> account.getStatus().isActive())
                .count();
                
            boolean hasSalaryAccount = accounts.stream()
                .anyMatch(account -> account.getAccountType().name().equals("SALARY"));
                
            // Calculate total balance (sum of all account balances)
            var totalBalance = accounts.stream()
                .map(account -> account.getBalance().value())
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
            
            GetAccountSummaryResponse.Builder responseBuilder = GetAccountSummaryResponse.newBuilder()
                .setCustomerNumber(request.getCustomerNumber())
                .setTotalAccounts(totalAccounts)
                .setActiveAccounts(activeAccounts)
                .setTotalBalance(totalBalance.toString())
                .setHasSalaryAccount(hasSalaryAccount);
                
            for (Account account : accounts) {
                AccountInfo accountInfo = AccountInfo.newBuilder()
                    .setAccountNumber(account.getAccountNumber().value())
                    .setCustomerNumber(account.getAccountNumber().customerNumber())
                    .setAccountType(account.getAccountType().name())
                    .setStatus(account.getStatus().name())
                    .setBalance(account.getBalance().value().toString())
                    .setCreatedAt(account.getCreatedAt().toString())
                    .setUpdatedAt(account.getUpdatedAt().toString())
                    .build();
                    
                responseBuilder.addAccountDetails(accountInfo);
            }
            
            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();
            
        } catch (Exception e) {
            logger.error("Error getting account summary for customer: {}", request.getCustomerNumber(), e);
            responseObserver.onError(Status.INTERNAL
                .withDescription("Internal error: " + e.getMessage())
                .asRuntimeException());
        }
    }
} 