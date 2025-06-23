package com.aalhendi.customer_ms.domain.valueobjects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests for CustomerNumber value object.
 * 
 * Tests validation, formatting, and business rules for customer numbers.
 */
@DisplayName("CustomerNumber")
class CustomerNumberTest {

    @Test
    @DisplayName("should create valid customer number")
    void shouldCreateValidCustomerNumber() {
        // Given
        String validCustomerNumber = "1234567";

        // When
        CustomerNumber customerNumber = new CustomerNumber(validCustomerNumber);

        // Then
        assertThat(customerNumber.value()).isEqualTo(validCustomerNumber);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("should throw exception for null or empty customer number")
    void shouldThrowExceptionForNullOrEmptyCustomerNumber(String invalidCustomerNumber) {
        // When & Then
        assertThatThrownBy(() -> new CustomerNumber(invalidCustomerNumber))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Customer number cannot be null");
    }

    @ParameterizedTest
    @ValueSource(strings = {"123456", "12345678", "1", "123456789012345"})
    @DisplayName("should throw exception for incorrect length")
    void shouldThrowExceptionForIncorrectLength(String invalidLength) {
        // When & Then
        assertThatThrownBy(() -> new CustomerNumber(invalidLength))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Customer number must be exactly 7 digits");
    }

    @ParameterizedTest
    @ValueSource(strings = {"abcdefg", "123456a", "12345@#", "1 23456", "123-456"})
    @DisplayName("should throw exception for non-digit characters")
    void shouldThrowExceptionForNonDigitCharacters(String invalidFormat) {
        // When & Then
        assertThatThrownBy(() -> new CustomerNumber(invalidFormat))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Customer number must contain only digits");
    }

    @Test
    @DisplayName("should be equal when values are the same")
    void shouldBeEqualWhenValuesAreTheSame() {
        // Given
        CustomerNumber customerNumber1 = new CustomerNumber("1234567");
        CustomerNumber customerNumber2 = new CustomerNumber("1234567");

        // Then
        assertThat(customerNumber1).isEqualTo(customerNumber2);
        assertThat(customerNumber1.hashCode()).isEqualTo(customerNumber2.hashCode());
    }

    @Test
    @DisplayName("should not be equal when values are different")
    void shouldNotBeEqualWhenValuesAreDifferent() {
        // Given
        CustomerNumber customerNumber1 = new CustomerNumber("1234567");
        CustomerNumber customerNumber2 = new CustomerNumber("7654321");

        // Then
        assertThat(customerNumber1).isNotEqualTo(customerNumber2);
    }

    @Test
    @DisplayName("should have meaningful toString")
    void shouldHaveMeaningfulToString() {
        // Given
        CustomerNumber customerNumber = new CustomerNumber("1234567");

        // When
        String toString = customerNumber.toString();

        // Then
        assertThat(toString).contains("1234567");
        assertThat(toString).contains("CustomerNumber");
    }
} 