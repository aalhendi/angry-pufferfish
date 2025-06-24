package com.aalhendi.account_ms.infrastructure.persistence;

import com.aalhendi.account_ms.domain.entities.Account;
import com.aalhendi.account_ms.domain.entities.NewAccount;
import com.aalhendi.account_ms.domain.repositories.AccountRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JPA implementation of the AccountRepository domain interface.
 * Bridges domain layer with persistence layer.
 */
@Repository
public class AccountRepositoryImpl implements AccountRepository {

    private final JpaAccountRepository jpaRepository;

    public AccountRepositoryImpl(JpaAccountRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Account save(NewAccount newAccount) {
        AccountEntity entity = AccountEntity.fromDomain(newAccount);
        AccountEntity savedEntity = jpaRepository.save(entity);
        return savedEntity.toDomain();
    }

    @Override
    public Account save(Account account) {
        AccountEntity entity = AccountEntity.fromDomain(account);
        AccountEntity savedEntity = jpaRepository.save(entity);
        return savedEntity.toDomain();
    }

    @Override
    public Optional<Account> findByAccountNumber(String accountNumber) {
        return jpaRepository.findByAccountNumber(accountNumber)
                .map(AccountEntity::toDomain);
    }

    @Override
    public List<Account> findByCustomerNumber(String customerNumber) {
        return jpaRepository.findByCustomerNumber(customerNumber)
                .stream()
                .map(AccountEntity::toDomain)
                .toList();
    }

    @Override
    public long countByCustomerNumber(String customerNumber) {
        return jpaRepository.countByCustomerNumber(customerNumber);
    }

    @Override
    public boolean existsByAccountNumber(String accountNumber) {
        return jpaRepository.existsByAccountNumber(accountNumber);
    }

    @Override
    public void deleteByAccountNumber(String accountNumber) {
        jpaRepository.findByAccountNumber(accountNumber)
                .ifPresent(jpaRepository::delete);
    }

    @Override
    public List<String> findExistingSerialNumbers(String customerNumber) {
        return jpaRepository.findExistingSerialNumbers(customerNumber);
    }
} 