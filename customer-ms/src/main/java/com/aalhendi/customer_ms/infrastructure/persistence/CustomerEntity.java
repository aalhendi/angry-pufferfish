package com.aalhendi.customer_ms.infrastructure.persistence;

import jakarta.persistence.*;

/**
 * TODO(aalhendi): add docs
 */
@Entity
@Table(name = "customer")
public class CustomerEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private Long id;
    
    // TODO(aalhendi): add fields
    
    /**
     * TODO(aalhendi): add docs
     */
    public CustomerEntity() {
    }
    
    // TODO(aalhendi): add methods, getters and setters
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
} 