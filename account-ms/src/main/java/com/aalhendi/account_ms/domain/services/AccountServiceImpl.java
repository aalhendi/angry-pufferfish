package com.aalhendi.account_ms.domain.services;

import com.aalhendi.account_ms.domain.entities.Account;
import com.aalhendi.account_ms.domain.repositories.AccountRepository;
import com.aalhendi.account_ms.domain.valueobjects.AccountStatus;
import com.aalhendi.account_ms.domain.valueobjects.AccountType;
import com.aalhendi.account_ms.domain.valueobjects.Balance;
import com.aalhendi.account_ms.domain.valueobjects.AccountNumber;
import com.aalhendi.account_ms.domain.entities.NewAccount;
import com.aalhendi.account_ms.domain.exceptions.AccountError;
import com.aalhendi.account_ms.domain.exceptions.BusinessException;
import com.aalhendi.account_ms.infrastructure.grpc.CustomerServiceClient;
import com.aalhendi.customer.grpc.ValidateCustomerResponse;
import com.aalhendi.customer.grpc.CheckAccountLimitResponse;
import com.aalhendi.account_ms.domain.events.AccountCreatedEvent;
import com.aalhendi.account_ms.domain.events.AccountStatusChangedEvent;
import com.aalhendi.account_ms.domain.events.AccountTransactionEvent;
import com.aalhendi.account_ms.domain.events.AccountClosedEvent;
import com.aalhendi.account_ms.infrastructure.events.DomainEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Objects;

/**
 * Implementation of AccountService.
 * Contains business logic for account management operations.
 */
@Service
@Transactional
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final CustomerServiceClient customerServiceClient;
    private final DomainEventPublisher eventPublisher;

    public AccountServiceImpl(AccountRepository accountRepository, CustomerServiceClient customerServiceClient, DomainEventPublisher eventPublisher) {
        this.accountRepository = accountRepository;
        this.customerServiceClient = customerServiceClient;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Account createAccount(String customerNumber, AccountType accountType) {
        ValidateCustomerResponse validation = customerServiceClient.validateCustomer(customerNumber);
        
        if (!validation.getIsValid()) {
            throw new BusinessException(AccountError.CUSTOMER_NOT_FOUND, customerNumber);
        }
        
        if (!validation.getIsActive()) {
            throw new BusinessException(AccountError.ACCOUNT_NOT_ACTIVE, customerNumber, validation.getStatus());
        }
        
        CheckAccountLimitResponse limitCheck = customerServiceClient.checkAccountLimit(customerNumber, accountType.toString());
        
        if (!limitCheck.getCanCreateAccount()) {
            if (limitCheck.getAlreadyHasSalaryAccount() && accountType == AccountType.SALARY) {
                throw new BusinessException(AccountError.SALARY_ACCOUNT_ALREADY_EXISTS, customerNumber, "existing-account");
            } else if (limitCheck.getCurrentAccountCount() >= limitCheck.getMaxAccountLimit()) {
                throw new BusinessException(AccountError.ACCOUNT_LIMIT_EXCEEDED, customerNumber);
            }
        }
        
        String accountNumberValue = generateAccountNumber(customerNumber);
        AccountNumber accountNumber = new AccountNumber(accountNumberValue);
        
        NewAccount newAccount = NewAccount.create(
            accountNumber,
            accountType
        );
        
        Account savedAccount = accountRepository.save(newAccount);
        
        AccountCreatedEvent event = new AccountCreatedEvent(
            accountNumberValue,
            savedAccount.getAccountNumber().customerNumber(), // TODO(aalhendi): we /could/ get it from account number
            accountType,
            savedAccount.getBalance().value(),
            savedAccount.getStatus().name(),
            1L // Initial version
        );
        
        eventPublisher.publish(event);
        
        return savedAccount;
    }

    /**
     * Generates a sequential account number for a customer.
     * Format: CCCCCCCNNN (7-digit customer + 3-digit serial from 001-010)
     * 
     * Business Rules:
     * - Serial numbers are sequential (001, 002, 003, etc.)
     * - All accounts (including closed) count for serial assignment
     * - Maximum 10 accounts per customer (serial 001-010)
     * - If all serials are used, throws exception
     */
    private String generateAccountNumber(String customerNumber) {
        // Get existing serial numbers for this customer (includes closed accounts)
        List<String> existingSerials = accountRepository.findExistingSerialNumbers(customerNumber);
        
        // Find the next available serial number (001-010)
        // TODO(aalhendi): this is not the best way to do this, but it's a start
        for (int serial = 1; serial <= 10; serial++) {
            String serialStr = String.format("%03d", serial);
            if (!existingSerials.contains(serialStr)) {
                return customerNumber + serialStr;
            }
        }
        
        // If we reach here, all 10 serial numbers are used
        throw new BusinessException(AccountError.ACCOUNT_LIMIT_EXCEEDED, customerNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Account> getAccount(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Account> getAccountsByCustomer(String customerNumber) {
        return accountRepository.findByCustomerNumber(customerNumber);
    }

    @Override
    public Account updateAccountStatus(String accountNumber, AccountStatus status) {
        if (Objects.isNull(accountNumber) || accountNumber.trim().isEmpty()) {
            throw new BusinessException(
                AccountError.MISSING_REQUIRED_FIELD, "account_number"
            );
        }
        
        Account account = getAccount(accountNumber)
                .orElseThrow(() -> new BusinessException(
                    AccountError.ACCOUNT_NOT_FOUND, accountNumber
                ));
        
        AccountStatus previousStatus = account.getStatus();
        
        switch (status) {
            case ACTIVE -> account.activate();
            case SUSPENDED -> account.suspend();
            case CLOSED -> {
                if (!account.getBalance().isEqualTo(Balance.ZERO)) {
                    throw new BusinessException(
                        AccountError.CANNOT_CLOSE_ACCOUNT_WITH_BALANCE, 
                        accountNumber, account.getBalance().value()
                    );
                }
                account.close();
                
                // Publish account closed event
                AccountClosedEvent closedEvent = new AccountClosedEvent(
                    account.getAccountNumber().value(),
                    account.getAccountNumber().customerNumber(),
                    account.getAccountType(),
                    "Account closed via status update",
                    1L
                );
                
                eventPublisher.publish(closedEvent);
            }
        }
        
        Account updatedAccount = accountRepository.save(account);
        
        // Publish status changed event (unless it was closed, as we already published the closed event)
        if (status != AccountStatus.CLOSED) {
            AccountStatusChangedEvent event = new AccountStatusChangedEvent(
                updatedAccount.getAccountNumber().value(),
                updatedAccount.getAccountNumber().customerNumber(),
                previousStatus.name(),
                updatedAccount.getStatus().name(),
                "Status updated via API",
                1L
            );
            
            eventPublisher.publish(event);
        }
        
        return updatedAccount;
    }

    @Override
    public Account creditAccount(String accountNumber, Balance amount) {
        if (Objects.isNull(accountNumber) || accountNumber.trim().isEmpty()) {
            throw new BusinessException(
                AccountError.MISSING_REQUIRED_FIELD, "account_number"
            );
        }
        
        if (Objects.isNull(amount)) {
            throw new BusinessException(
                AccountError.MISSING_REQUIRED_FIELD, "amount"
            );
        }
        
        if (amount.value().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(
                AccountError.NEGATIVE_AMOUNT, amount.value()
            );
        }
        
        Account account = getAccount(accountNumber)
                .orElseThrow(() -> new BusinessException(
                    AccountError.ACCOUNT_NOT_FOUND, accountNumber
                ));
        
        if (!account.isActive()) {
            throw new BusinessException(
                AccountError.ACCOUNT_NOT_ACTIVE, accountNumber, account.getStatus().name()
            );
        }
        
        BigDecimal previousBalance = account.getBalance().value();
        account.credit(amount);
        Account updatedAccount = accountRepository.save(account);
        
        // Publish transaction event
        AccountTransactionEvent event = new AccountTransactionEvent(
            updatedAccount.getAccountNumber().value(),
            updatedAccount.getAccountNumber().customerNumber(),
            "CREDIT",
            amount.value(),
            previousBalance,
            updatedAccount.getBalance().value(),
            "Credit transaction via API",
            1L
        );
        
        eventPublisher.publish(event);
        
        return updatedAccount;
    }

    @Override
    public Account debitAccount(String accountNumber, Balance amount) {
        if (Objects.isNull(accountNumber) || accountNumber.trim().isEmpty()) {
            throw new BusinessException(
                AccountError.MISSING_REQUIRED_FIELD, "account_number"
            );
        }
        
        if (Objects.isNull(amount)) {
            throw new BusinessException(
                AccountError.MISSING_REQUIRED_FIELD, "amount"
            );
        }
        
        if (amount.value().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(
                AccountError.NEGATIVE_AMOUNT, amount.value()
            );
        }
        
        Account account = getAccount(accountNumber)
                .orElseThrow(() -> new BusinessException(
                    AccountError.ACCOUNT_NOT_FOUND, accountNumber
                ));
        
        if (!account.isActive()) {
            throw new BusinessException(
                AccountError.ACCOUNT_NOT_ACTIVE, accountNumber, account.getStatus().name()
            );
        }
        
        if (account.getBalance().isLessThan(amount)) {
            throw new BusinessException(
                AccountError.INSUFFICIENT_FUNDS, accountNumber, 
                account.getBalance().value(), amount.value()
            );
        }
        
        BigDecimal previousBalance = account.getBalance().value();
        account.debit(amount);
        Account updatedAccount = accountRepository.save(account);
        
        // Publish transaction event
        AccountTransactionEvent event = new AccountTransactionEvent(
            updatedAccount.getAccountNumber().value(),
            updatedAccount.getAccountNumber().customerNumber(),
            "DEBIT",
            amount.value(),
            previousBalance,
            updatedAccount.getBalance().value(),
            "Debit transaction via API",
            1L
        );
        
        eventPublisher.publish(event);
        
        return updatedAccount;
    }

    @Override
    public void closeAccount(String accountNumber) {
        if (Objects.isNull(accountNumber) || accountNumber.trim().isEmpty()) {
            throw new BusinessException(
                AccountError.MISSING_REQUIRED_FIELD, "account_number"
            );
        }
        
        Account account = getAccount(accountNumber)
                .orElseThrow(() -> new BusinessException(
                    AccountError.ACCOUNT_NOT_FOUND, accountNumber
                ));
        
        if (!account.getBalance().isEqualTo(Balance.ZERO)) {
            throw new BusinessException(
                AccountError.CANNOT_CLOSE_ACCOUNT_WITH_BALANCE, 
                accountNumber, account.getBalance().value()
            );
        }
        
        account.close();
        accountRepository.save(account);
        
        // Publish account closed event
        AccountClosedEvent event = new AccountClosedEvent(
            account.getAccountNumber().value(),
            account.getAccountNumber().customerNumber(),
            account.getAccountType(),
            "Account closed via API",
            1L
        );
        
        eventPublisher.publish(event);
    }
} 