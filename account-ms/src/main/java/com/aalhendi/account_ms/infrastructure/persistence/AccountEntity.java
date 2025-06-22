package com.aalhendi.account_ms.infrastructure.persistence;

import jakarta.persistence.*;

/**
 * TODO(aalhendi): add docs
 */
@Entity
@Table(name = "account")
public class AccountEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long id;
    
    // TODO(aalhendi): add fields
    
    /**
     * TODO(aalhendi): add docs
     */
    public AccountEntity() {
    }
    
    // TODO(aalhendi): add methods, getters and setters
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
} 