package com.aalhendi.customer_ms.infrastructure.persistence;

import com.aalhendi.customer_ms.domain.entities.Customer;
import com.aalhendi.customer_ms.domain.entities.NewCustomer;
import com.aalhendi.customer_ms.domain.repositories.CustomerRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JPA implementation of the CustomerRepository domain interface.
 * Bridges domain layer with persistence layer.
 */
@Repository
public class CustomerRepositoryImpl implements CustomerRepository {

    private final JpaCustomerRepository jpaRepository;

    public CustomerRepositoryImpl(JpaCustomerRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Customer save(NewCustomer newCustomer) {
        CustomerEntity entity = CustomerEntity.fromDomain(newCustomer);
        CustomerEntity savedEntity = jpaRepository.save(entity);
        return savedEntity.toDomain();
    }

    @Override
    public Customer save(Customer customer) {
        CustomerEntity entity = CustomerEntity.fromDomain(customer);
        CustomerEntity savedEntity = jpaRepository.save(entity);
        return savedEntity.toDomain();
    }

    @Override
    public Optional<Customer> findByCustomerNumber(String customerNumber) {
        return jpaRepository.findByCustomerNumber(customerNumber)
                .map(CustomerEntity::toDomain);
    }

    @Override
    public Optional<Customer> findByNationalId(String nationalId) {
        return jpaRepository.findByNationalId(nationalId)
                .map(CustomerEntity::toDomain);
    }

    @Override
    public List<Customer> findByNameContaining(String name) {
        return jpaRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(CustomerEntity::toDomain)
                .toList();
    }

    @Override
    public boolean existsByCustomerNumber(String customerNumber) {
        return jpaRepository.existsByCustomerNumber(customerNumber);
    }

    @Override
    public boolean existsByNationalId(String nationalId) {
        return jpaRepository.existsByNationalId(nationalId);
    }

    @Override
    public void deleteByCustomerNumber(String customerNumber) {
        jpaRepository.findByCustomerNumber(customerNumber)
                .ifPresent(jpaRepository::delete);
    }
} 