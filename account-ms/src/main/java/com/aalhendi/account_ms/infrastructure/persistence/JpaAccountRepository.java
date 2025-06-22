package com.aalhendi.account_ms.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * TODO(aalhendi): add docs
 */
@Repository
public interface JpaAccountRepository extends JpaRepository<AccountEntity, Long> {
    
    // TODO(aalhendi): add query methods
    
} 