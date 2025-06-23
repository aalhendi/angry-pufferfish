package com.aalhendi.customer_ms.domain.entities;

import com.aalhendi.customer_ms.domain.valueobjects.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests for a NewCustomer domain entity.
 */
@DisplayName("NewCustomer")
class NewCustomerTest {

    private CustomerNumber customerNumber;
    private CustomerName customerName;
    private NationalId nationalId;
    private CustomerType customerType;
    private Address address;

    @BeforeEach
    void setUp() {
        customerNumber = new CustomerNumber("1234567");
        customerName = new CustomerName("John Doe");
        nationalId = new NationalId("123456789012");
        customerType = CustomerType.RETAIL;
        address = new Address("OMAR BEN AL KHATTAB STREET, ARRAYA TOWER, FLOOR 01-13, BLOCK 7, SHARQ");
    }

    @Test
    @DisplayName("should create new customer with default values")
    void shouldCreateNewCustomerWithDefaultValues() {
        // When
        NewCustomer newCustomer = NewCustomer.create(customerNumber, customerName, nationalId,
                customerType, address);

        // Then
        assertThat(newCustomer.getCustomerNumber()).isEqualTo(customerNumber);
        assertThat(newCustomer.getName()).isEqualTo(customerName);
        assertThat(newCustomer.getNationalId()).isEqualTo(nationalId);
        assertThat(newCustomer.getCustomerType()).isEqualTo(customerType);
        assertThat(newCustomer.getAddress()).isEqualTo(address);
        assertThat(newCustomer.getStatus()).isEqualTo(CustomerStatus.PENDING);
        assertThat(newCustomer.getCreatedAt()).isNotNull();
        assertThat(newCustomer.getUpdatedAt()).isNotNull();
        assertThat(newCustomer.isActive()).isFalse(); // PENDING is not active
    }

    @Test
    @DisplayName("should activate new customer")
    void shouldActivateNewCustomer() {
        // Given
        NewCustomer newCustomer = NewCustomer.create(customerNumber, customerName, nationalId,
                customerType, address);

        // When
        newCustomer.activate();

        // Then
        assertThat(newCustomer.getStatus()).isEqualTo(CustomerStatus.ACTIVE);
        assertThat(newCustomer.isActive()).isTrue();
    }

    @Test
    @DisplayName("should update customer name")
    void shouldUpdateCustomerName() {
        // Given
        NewCustomer newCustomer = NewCustomer.create(customerNumber, customerName, nationalId,
                customerType, address);
        CustomerName newName = new CustomerName("Jane Smith");

        // When
        newCustomer.activate(); // Need to activate first to allow operations
        newCustomer.updateName(newName);

        // Then
        assertThat(newCustomer.getName()).isEqualTo(newName);
    }

    @Test
    @DisplayName("should update customer address")
    void shouldUpdateCustomerAddress() {
        // Given
        NewCustomer newCustomer = NewCustomer.create(customerNumber, customerName, nationalId,
                customerType, address);
        Address newAddress = new Address("OMAR BEN AL KHATTAB STREET, AVENUES MALL, FLOOR 01-13, BLOCK 7, SHARQ");

        // When
        newCustomer.activate(); // Need to activate first to allow operations
        newCustomer.updateAddress(newAddress);

        // Then
        assertThat(newCustomer.getAddress()).isEqualTo(newAddress);
    }

    @Test
    @DisplayName("should update customer type")
    void shouldUpdateCustomerType() {
        // Given
        NewCustomer newCustomer = NewCustomer.create(customerNumber, customerName, nationalId,
                customerType, address);
        CustomerType newType = CustomerType.CORPORATE;

        // When
        newCustomer.activate(); // Need to activate first to allow operations
        newCustomer.updateCustomerType(newType);

        // Then
        assertThat(newCustomer.getCustomerType()).isEqualTo(newType);
    }

    @Test
    @DisplayName("should allow activation from pending status")
    void shouldAllowActivationFromPendingStatus() {
        // Given
        NewCustomer newCustomer = NewCustomer.create(customerNumber, customerName, nationalId,
                customerType, address);

        // When & Then
        assertThatCode(newCustomer::activate)
                .doesNotThrowAnyException();
        
        assertThat(newCustomer.getStatus()).isEqualTo(CustomerStatus.ACTIVE);
        assertThat(newCustomer.isActive()).isTrue();
    }

    @Test
    @DisplayName("should be equal when customer numbers are the same")
    void shouldBeEqualWhenCustomerNumbersAreTheSame() {
        // Given
        NewCustomer newCustomer1 = NewCustomer.create(customerNumber, customerName, nationalId,
                customerType, address);
        NewCustomer newCustomer2 = NewCustomer.create(customerNumber, new CustomerName("Different Name"),
                nationalId, customerType, address);

        // Then
        assertThat(newCustomer1).isEqualTo(newCustomer2);
        assertThat(newCustomer1.hashCode()).isEqualTo(newCustomer2.hashCode());
    }

    @Test
    @DisplayName("should not be equal when customer numbers are different")
    void shouldNotBeEqualWhenCustomerNumbersAreDifferent() {
        // Given
        NewCustomer newCustomer1 = NewCustomer.create(customerNumber, customerName, nationalId,
                customerType, address);
        NewCustomer newCustomer2 = NewCustomer.create(new CustomerNumber("7654321"), customerName,
                nationalId, customerType, address);

        // Then
        assertThat(newCustomer1).isNotEqualTo(newCustomer2);
    }

    @Test
    @DisplayName("should have meaningful toString")
    void shouldHaveMeaningfulToString() {
        // Given
        NewCustomer newCustomer = NewCustomer.create(customerNumber, customerName, nationalId,
                customerType, address);

        // When
        String toString = newCustomer.toString();

        // Then
        assertThat(toString).contains("Customer[")
                .contains("customerNumber=")
                .contains("1234567");
    }
} 