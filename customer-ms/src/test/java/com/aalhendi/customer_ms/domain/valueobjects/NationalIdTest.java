package com.aalhendi.customer_ms.domain.valueobjects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests for NationalId value object.
 */
@DisplayName("NationalId")
class NationalIdTest {

    @Test
    @DisplayName("should create valid national ID")
    void shouldCreateValidNationalId() {
        // Given
        String validNationalId = "123456789012";

        // When
        NationalId nationalId = new NationalId(validNationalId);

        // Then
        assertThat(nationalId.value()).isEqualTo(validNationalId);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("should throw exception for null or empty national ID")
    void shouldThrowExceptionForNullOrEmptyNationalId(String invalidNationalId) {
        // When & Then
        assertThatThrownBy(() -> new NationalId(invalidNationalId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("National ID cannot be null");
    }

    @ParameterizedTest
    @ValueSource(strings = {"12345678901", "1234567890123", "1", "12345678901234567890"})
    @DisplayName("should throw exception for incorrect length")
    void shouldThrowExceptionForIncorrectLength(String invalidLength) {
        // When & Then
        assertThatThrownBy(() -> new NationalId(invalidLength))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("National ID must be exactly 12 digits");
    }

    @ParameterizedTest
    @ValueSource(strings = {"abcdefghijkl", "12345678901a", "12345678@#$%", "123 456 7890", "123-456-7890"})
    @DisplayName("should throw exception for non-digit characters")
    void shouldThrowExceptionForNonDigitCharacters(String invalidFormat) {
        // When & Then
        assertThatThrownBy(() -> new NationalId(invalidFormat))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("National ID must contain only digits");
    }

    @ParameterizedTest
    @ValueSource(strings = {"012345678901", "512345678901", "612345678901", "912345678901"})
    @DisplayName("should throw exception for invalid nationality code")
    void shouldThrowExceptionForInvalidNationalityCode(String invalidNationalityCode) {
        // When & Then
        assertThatThrownBy(() -> new NationalId(invalidNationalityCode))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("National ID must start with nationality code (1-4)");
    }

    @ParameterizedTest
    @ValueSource(strings = {"123456789012", "223456789012", "323456789012", "423456789012"})
    @DisplayName("should accept valid nationality codes")
    void shouldAcceptValidNationalityCodes(String validNationalId) {
        // When & Then
        assertThatCode(() -> new NationalId(validNationalId))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("should extract nationality code correctly")
    void shouldExtractNationalityCodeCorrectly() {
        // Given
        NationalId nationalId = new NationalId("123456789012");

        // When
        String nationalityCode = nationalId.nationalityCode();

        // Then
        assertThat(nationalityCode).isEqualTo("1");
    }

    @Test
    @DisplayName("should extract identification number correctly")
    void shouldExtractIdentificationNumberCorrectly() {
        // Given
        NationalId nationalId = new NationalId("123456789012");

        // When
        String identificationNumber = nationalId.identificationNumber();

        // Then
        assertThat(identificationNumber).isEqualTo("23456789012");
    }

    @Test
    @DisplayName("should be equal when values are the same")
    void shouldBeEqualWhenValuesAreTheSame() {
        // Given
        NationalId nationalId1 = new NationalId("123456789012");
        NationalId nationalId2 = new NationalId("123456789012");

        // Then
        assertThat(nationalId1).isEqualTo(nationalId2);
        assertThat(nationalId1.hashCode()).isEqualTo(nationalId2.hashCode());
    }

    @Test
    @DisplayName("should not be equal when values are different")
    void shouldNotBeEqualWhenValuesAreDifferent() {
        // Given
        NationalId nationalId1 = new NationalId("123456789012");
        NationalId nationalId2 = new NationalId("210987654321");

        // Then
        assertThat(nationalId1).isNotEqualTo(nationalId2);
    }

    @Test
    @DisplayName("should have meaningful toString")
    void shouldHaveMeaningfulToString() {
        // Given
        NationalId nationalId = new NationalId("123456789012");

        // When
        String toString = nationalId.toString();

        // Then
        assertThat(toString).contains("123456789012");
        assertThat(toString).contains("NationalId");
    }
} 