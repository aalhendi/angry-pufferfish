package com.aalhendi.customer_ms.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * JPA repository interface for CustomerEntity.
 * Provides standard CRUD operations plus custom query methods
 * for customer-specific business operations.
 */
@Repository
public interface JpaCustomerRepository extends JpaRepository<CustomerEntity, Long> {
    
    /**
     * Find a customer by customer number.
     */
    Optional<CustomerEntity> findByCustomerNumber(String customerNumber);
    
    /**
     * Find a customer by national ID.
     */
    Optional<CustomerEntity> findByNationalId(String nationalId);
    
    /**
     * Find customers by status.
     */
    List<CustomerEntity> findByStatus(Integer status);
    
    /**
     * Find customers by customer type.
     */
    List<CustomerEntity> findByCustomerType(String customerType);
    
    /**
     * Check if the customer number already exists.
     */
    boolean existsByCustomerNumber(String customerNumber);
    
    /**
     * Check if the national ID already exists.
     */
    boolean existsByNationalId(String nationalId);
    
    /**
     * Count customers by type.
     */
    long countByCustomerType(String customerType);
    
    /**
     * Find customers created after a specific date.
     */
    List<CustomerEntity> findByCreatedAtAfter(LocalDateTime date);
    
    /**
     * Find customers created between dates.
     */
    List<CustomerEntity> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find active customers (status = 1).
     */
    @Query("SELECT c FROM CustomerEntity c WHERE c.status = 1")
    List<CustomerEntity> findActiveCustomers();
    
    /**
     * Find customers by partial name (case-insensitive).
     */
    @Query("SELECT c FROM CustomerEntity c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<CustomerEntity> findByNameContainingIgnoreCase(@Param("name") String name);
    
    /**
     * Find customers by address containing specific text.
     * TODO(aalhendi): do we want lowercase on app level, db level, both? or neither? do we even want to support address?
     */
    @Query("SELECT c FROM CustomerEntity c WHERE LOWER(c.address) LIKE LOWER(CONCAT('%', :addressPart, '%'))")
    List<CustomerEntity> findByAddressContaining(@Param("addressPart") String addressPart);
    
} 