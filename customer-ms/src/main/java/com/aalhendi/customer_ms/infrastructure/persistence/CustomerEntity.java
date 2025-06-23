package com.aalhendi.customer_ms.infrastructure.persistence;

import com.aalhendi.customer_ms.domain.entities.Customer;
import com.aalhendi.customer_ms.domain.entities.NewCustomer;
import com.aalhendi.customer_ms.domain.valueobjects.*;
import jakarta.persistence.*;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.annotations.SoftDeleteType;
import org.springframework.data.domain.Persistable;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * JPA entity for Customer persistence.
 */
@Entity
@Table(name = "customer")
@SoftDelete(strategy = SoftDeleteType.DELETED, columnName = "is_deleted", converter = StateConverter.class)

public class CustomerEntity implements Persistable<Long> {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private Long id;
    
    @Column(name = "customer_number", unique = true, nullable = false, length = 7)
    private String customerNumber;
    
    @Column(name = "name", nullable = false, length = 255)
    private String name;
    
    @Column(name = "national_id", unique = true, nullable = false, length = 12)
    private String nationalId;
    
    @Column(name = "customer_type", nullable = false, length = 20)
    private String customerType;
    
    @Column(name = "address", nullable = false, length = 500)
    private String address;
    
    @Column(name = "status", nullable = false)
    private Integer status;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    /**
     * Default constructor for JPA.
     */
    public CustomerEntity() {
    }
    
    /**
     * Constructor with all fields.
     */
    public CustomerEntity(Long id, String customerNumber, String name, String nationalId,
                         String customerType, String address, Integer status,
                         LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.customerNumber = customerNumber;
        this.name = name;
        this.nationalId = nationalId;
        this.customerType = customerType;
        this.address = address;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getCustomerNumber() {
        return customerNumber;
    }
    
    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getNationalId() {
        return nationalId;
    }
    
    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }
    
    public String getCustomerType() {
        return customerType;
    }
    
    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public Integer getStatus() {
        return status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Checks if the customer is active (status = 1).
     */
    public boolean isActive() {
        return status != null && status == 1;
    }
    
    /**
     * Validates a national ID format.
     * TODO(aalhendi): Ideally, we'd centralize this logic somewhere. Entity and domain use the same logic.
     */
    public boolean isValidNationalIdFormat() {
        if (Objects.isNull(nationalId)) {
            return false;
        }
        
        // Check length - should be 12 characters based on test data
        if (nationalId.length() != 12) {
            return false;
        }
        
        // Check if all characters are digits
        if (!nationalId.matches("\\d{12}")) {
            return false;
        }
        
        // Basic format validation - the first digit should be 1-4 (nationality code)
        char firstDigit = nationalId.charAt(0);
        if (firstDigit < '1' || firstDigit > '4') {
            return false;
        }
        
        return true;
    }
    
    /**
     * Updates the updatedAt timestamp to the current time.
     */
    public void touch() {
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Converts this JPA entity to a domain Customer object.
     */
    public Customer toDomain() {
        return Customer.reconstitute(
            this.id,
            new CustomerNumber(this.customerNumber),
            new CustomerName(this.name),
            new NationalId(this.nationalId),
            CustomerType.valueOf(this.customerType),
            new Address(this.address),
            CustomerStatus.fromCode(this.status),
            this.createdAt,
            this.updatedAt
        );
    }
    
    /**
     * Creates a JPA entity from a domain Customer object.
     */
    public static CustomerEntity fromDomain(Customer domainCustomer) {
        CustomerEntity entity = new CustomerEntity();
        entity.setId(domainCustomer.getId());
        entity.setCustomerNumber(domainCustomer.getCustomerNumber().value());
        entity.setName(domainCustomer.getName().value());
        entity.setNationalId(domainCustomer.getNationalId().value());
        entity.setCustomerType(domainCustomer.getCustomerType().name());
        entity.setAddress(domainCustomer.getAddress().value());
        entity.setStatus(domainCustomer.getStatus().getCode());
        entity.setCreatedAt(domainCustomer.getCreatedAt());
        entity.setUpdatedAt(domainCustomer.getUpdatedAt());
        return entity;
    }
    
    /**
     * Creates JPA entity from a domain NewCustomer object.
     */
    public static CustomerEntity fromDomain(NewCustomer domainCustomer) {
        CustomerEntity entity = new CustomerEntity();
        // NewCustomer doesn't have an ID since it's not persisted yet
        entity.setCustomerNumber(domainCustomer.getCustomerNumber().value());
        entity.setName(domainCustomer.getName().value());
        entity.setNationalId(domainCustomer.getNationalId().value());
        entity.setCustomerType(domainCustomer.getCustomerType().name());
        entity.setAddress(domainCustomer.getAddress().value());
        entity.setStatus(domainCustomer.getStatus().getCode());
        entity.setCreatedAt(domainCustomer.getCreatedAt());
        entity.setUpdatedAt(domainCustomer.getUpdatedAt());
        return entity;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (Objects.isNull(o) || getClass() != o.getClass()) return false;
        CustomerEntity that = (CustomerEntity) o;
        return Objects.equals(customerNumber, that.customerNumber);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(customerNumber);
    }
    
    @Override
    public String toString() {
        return "CustomerEntity{" +
                "id=" + id +
                ", customerNumber='" + customerNumber + '\'' +
                ", name='" + name + '\'' +
                ", nationalId='" + nationalId + '\'' +
                ", customerType='" + customerType + '\'' +
                ", address='" + address + '\'' +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    @Override
    public boolean isNew() {
        return id == null;
    }
} 