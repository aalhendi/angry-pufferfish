package com.aalhendi.customer_ms.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * TODO(aalhendi): add docs
 */
@Repository
public interface JpaCustomerRepository extends JpaRepository<CustomerEntity, Long> {
    
    // TODO(aalhendi): add query methods
    
} 