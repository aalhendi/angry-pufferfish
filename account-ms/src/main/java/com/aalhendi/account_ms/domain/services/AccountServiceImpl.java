package com.aalhendi.account_ms.domain.services;

import com.aalhendi.account_ms.domain.entities.Account;
import com.aalhendi.account_ms.domain.repositories.AccountRepository;
import com.aalhendi.account_ms.domain.valueobjects.AccountStatus;
import com.aalhendi.account_ms.domain.valueobjects.AccountType;
import com.aalhendi.account_ms.domain.valueobjects.Balance;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of AccountService.
 * Contains business logic for account management operations.
 */
@Service
@Transactional
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Account createAccount(String customerNumber, AccountType accountType) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Account> getAccount(String accountNumber) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    @Transactional(readOnly = true)
    public List<Account> getAccountsByCustomer(String customerNumber) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Account updateAccountStatus(String accountNumber, AccountStatus status) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Account creditAccount(String accountNumber, Balance amount) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Account debitAccount(String accountNumber, Balance amount) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void closeAccount(String accountNumber) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
} 