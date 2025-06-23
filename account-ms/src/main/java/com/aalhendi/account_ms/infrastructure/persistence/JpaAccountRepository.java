package com.aalhendi.account_ms.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JPA repository for AccountEntity.
 */
@Repository
public interface JpaAccountRepository extends JpaRepository<AccountEntity, Long> {

    /**
     * Find all accounts belonging to a specific customer.
     */
    @Query("SELECT a FROM AccountEntity a WHERE SUBSTRING(a.accountNumber, 1, 7) = :customerNumber")
    List<AccountEntity> findByCustomerNumber(@Param("customerNumber") String customerNumber);

    /**
     * Count accounts for a specific customer.
     */
    @Query("SELECT COUNT(a) FROM AccountEntity a WHERE SUBSTRING(a.accountNumber, 1, 7) = :customerNumber")
    long countByCustomerNumber(@Param("customerNumber") String customerNumber);

    /**
     * Find an account by its account number.
     */
    Optional<AccountEntity> findByAccountNumber(String accountNumber);

    /**
     * Check if an account exists by account number.
     */
    boolean existsByAccountNumber(String accountNumber);

    /**
     * Find accounts by customer number and status.
     */
    @Query("SELECT a FROM AccountEntity a WHERE SUBSTRING(a.accountNumber, 1, 7) = :customerNumber AND a.status = :status")
    List<AccountEntity> findByCustomerNumberAndStatus(@Param("customerNumber") String customerNumber, @Param("status") Integer status);

    /**
     * Find existing serial numbers (last 3 digits) for a customer.
     */
    @Query("SELECT SUBSTRING(a.accountNumber, 8, 3) FROM AccountEntity a WHERE SUBSTRING(a.accountNumber, 1, 7) = :customerNumber")
    List<String> findExistingSerialNumbers(@Param("customerNumber") String customerNumber);

    /**
     * Find active accounts for a specific customer.
     */
    @Query("SELECT a FROM AccountEntity a WHERE SUBSTRING(a.accountNumber, 1, 7) = :customerNumber AND a.status = 1")
    List<AccountEntity> findActiveAccountsByCustomerNumber(@Param("customerNumber") String customerNumber);
} 