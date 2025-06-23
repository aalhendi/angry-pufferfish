package com.aalhendi.customer_ms.domain.entities;

import com.aalhendi.customer_ms.domain.valueobjects.*;
import com.aalhendi.customer_ms.infrastructure.persistence.CustomerEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests for Customer domain entity.
 */
@DisplayName("Customer")
class CustomerTest {

    private CustomerNumber customerNumber;
    private CustomerName customerName;
    private NationalId nationalId;
    private CustomerType customerType;
    private Address address;
    private CustomerStatus status;
    private LocalDateTime testTime;

    @BeforeEach
    void setUp() {
        customerNumber = new CustomerNumber("1234567");
        customerName = new CustomerName("John Doe");
        nationalId = new NationalId("123456789012");
        customerType = CustomerType.RETAIL;
        address = new Address("OMAR BEN AL KHATTAB STREET, ARRAYA TOWER, FLOOR 01-13, BLOCK 7, SHARQ");
        status = CustomerStatus.ACTIVE;
        testTime = LocalDateTime.now().minusSeconds(5);
    }

    @Test
    @DisplayName("should reconstitute customer from persistence")
    void shouldReconstituteCustomerFromPersistence() {
        // Given
        Long id = 1L;

        // When
        Customer customer = Customer.reconstitute(id, customerNumber, customerName, nationalId,
                customerType, address, status, testTime, testTime);

        // Then
        assertThat(customer.getId()).isEqualTo(id);
        assertThat(customer.getCustomerNumber()).isEqualTo(customerNumber);
        assertThat(customer.getName()).isEqualTo(customerName);
        assertThat(customer.getNationalId()).isEqualTo(nationalId);
        assertThat(customer.getCustomerType()).isEqualTo(customerType);
        assertThat(customer.getAddress()).isEqualTo(address);
        assertThat(customer.getStatus()).isEqualTo(status);
        assertThat(customer.getCreatedAt()).isEqualTo(testTime);
        assertThat(customer.getUpdatedAt()).isEqualTo(testTime);
    }

    @Test
    @DisplayName("should throw exception when reconstituting with null ID")
    void shouldThrowExceptionWhenReconstitutingWithNullId() {
        // When & Then
        assertThatThrownBy(() -> Customer.reconstitute(null, customerNumber, customerName, nationalId,
                customerType, address, status, testTime, testTime))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("ID cannot be null");
    }

    @Test
    @DisplayName("should activate customer")
    void shouldActivateCustomer() {
        // Given
        Customer customer = Customer.reconstitute(1L, customerNumber, customerName, nationalId,
                customerType, address, CustomerStatus.PENDING, testTime, testTime);

        // When
        customer.activate();

        // Then
        assertThat(customer.getStatus()).isEqualTo(CustomerStatus.ACTIVE);
        assertThat(customer.isActive()).isTrue();
        assertThat(customer.getUpdatedAt()).isAfter(testTime);
    }

    @Test
    @DisplayName("should suspend customer")
    void shouldSuspendCustomer() {
        // Given
        Customer customer = Customer.reconstitute(1L, customerNumber, customerName, nationalId,
                customerType, address, CustomerStatus.ACTIVE, testTime, testTime);

        // When
        customer.suspend();

        // Then
        assertThat(customer.getStatus()).isEqualTo(CustomerStatus.SUSPENDED);
        assertThat(customer.isActive()).isFalse();
        assertThat(customer.getUpdatedAt()).isAfter(testTime);
    }

    @Test
    @DisplayName("should freeze customer")
    void shouldFreezeCustomer() {
        // Given
        Customer customer = Customer.reconstitute(1L, customerNumber, customerName, nationalId,
                customerType, address, CustomerStatus.ACTIVE, testTime, testTime);

        // When
        customer.freeze();

        // Then
        assertThat(customer.getStatus()).isEqualTo(CustomerStatus.FROZEN);
        assertThat(customer.isActive()).isFalse();
        assertThat(customer.getUpdatedAt()).isAfter(testTime);
    }

    @Test
    @DisplayName("should close customer")
    void shouldCloseCustomer() {
        // Given
        Customer customer = Customer.reconstitute(1L, customerNumber, customerName, nationalId,
                customerType, address, CustomerStatus.ACTIVE, testTime, testTime);

        // When
        customer.close();

        // Then
        assertThat(customer.getStatus()).isEqualTo(CustomerStatus.CLOSED);
        assertThat(customer.isActive()).isFalse();
        assertThat(customer.getUpdatedAt()).isAfter(testTime);
    }

    @Test
    @DisplayName("should update customer name")
    void shouldUpdateCustomerName() {
        // Given
        Customer customer = Customer.reconstitute(1L, customerNumber, customerName, nationalId,
                customerType, address, CustomerStatus.ACTIVE, testTime, testTime);
        CustomerName newName = new CustomerName("Jane Smith");

        // When
        customer.updateName(newName);

        // Then
        assertThat(customer.getName()).isEqualTo(newName);
        assertThat(customer.getUpdatedAt()).isAfter(testTime);
    }

    @Test
    @DisplayName("should update customer address")
    void shouldUpdateCustomerAddress() {
        // Given
        Customer customer = Customer.reconstitute(1L, customerNumber, customerName, nationalId,
                customerType, address, CustomerStatus.ACTIVE, testTime, testTime);
        Address newAddress = new Address("OMAR BEN AL KHATTAB STREET, AVENUES MALL, FLOOR 01-13, BLOCK 7, SHARQ");

        // When
        customer.updateAddress(newAddress);

        // Then
        assertThat(customer.getAddress()).isEqualTo(newAddress);
        assertThat(customer.getUpdatedAt()).isAfter(testTime);
    }

    @Test
    @DisplayName("should update customer type")
    void shouldUpdateCustomerType() {
        // Given
        Customer customer = Customer.reconstitute(1L, customerNumber, customerName, nationalId,
                customerType, address, CustomerStatus.ACTIVE, testTime, testTime);
        CustomerType newType = CustomerType.CORPORATE;

        // When
        customer.updateCustomerType(newType);

        // Then
        assertThat(customer.getCustomerType()).isEqualTo(newType);
        assertThat(customer.getUpdatedAt()).isAfter(testTime);
    }

    @Test
    @DisplayName("should not allow operations on closed customer")
    void shouldNotAllowOperationsOnClosedCustomer() {
        // Given
        Customer customer = Customer.reconstitute(1L, customerNumber, customerName, nationalId,
                customerType, address, CustomerStatus.CLOSED, testTime, testTime);

        // When & Then
        assertThatThrownBy(() -> customer.updateName(new CustomerName("New Name")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cannot perform operations on a closed customer");
    }

    @Test
    @DisplayName("should not allow activating closed customer")
    void shouldNotAllowActivatingClosedCustomer() {
        // Given
        Customer customer = Customer.reconstitute(1L, customerNumber, customerName, nationalId,
                customerType, address, CustomerStatus.CLOSED, testTime, testTime);

        // When & Then
        assertThatThrownBy(customer::activate)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cannot activate a closed customer");
    }

    @Test
    @DisplayName("should be equal when customer numbers are the same")
    void shouldBeEqualWhenCustomerNumbersAreTheSame() {
        // Given
        Customer customer1 = Customer.reconstitute(1L, customerNumber, customerName, nationalId,
                customerType, address, status, testTime, testTime);
        Customer customer2 = Customer.reconstitute(2L, customerNumber, new CustomerName("Different Name"),
                nationalId, customerType, address, status, testTime, testTime);

        // Then
        assertThat(customer1).isEqualTo(customer2);
        assertThat(customer1.hashCode()).isEqualTo(customer2.hashCode());
    }

    @Test
    @DisplayName("should not be equal when customer numbers are different")
    void shouldNotBeEqualWhenCustomerNumbersAreDifferent() {
        // Given
        Customer customer1 = Customer.reconstitute(1L, customerNumber, customerName, nationalId,
                customerType, address, status, testTime, testTime);
        Customer customer2 = Customer.reconstitute(2L, new CustomerNumber("7654321"), customerName,
                nationalId, customerType, address, status, testTime, testTime);

        // Then
        assertThat(customer1).isNotEqualTo(customer2);
    }

    @Test
    @DisplayName("should have meaningful toString")
    void shouldHaveMeaningfulToString() {
        // Given
        Customer customer = Customer.reconstitute(1L, customerNumber, customerName, nationalId,
                customerType, address, status, testTime, testTime);

        // When
        String toString = customer.toString();

        // Then
        assertThat(toString).contains("Customer[")
                .contains("id=1")
                .contains("customerNumber=")
                .contains("1234567");
    }
} 