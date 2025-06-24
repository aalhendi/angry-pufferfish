package com.aalhendi.customer_ms.web.controllers;

import com.aalhendi.customer_ms.domain.entities.Customer;
import com.aalhendi.customer_ms.domain.exceptions.BusinessException;
import com.aalhendi.customer_ms.domain.exceptions.CustomerError;
import com.aalhendi.customer_ms.domain.services.CustomerService;
import com.aalhendi.customer_ms.domain.valueobjects.*;
import com.aalhendi.customer_ms.web.dtos.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests for CustomerController REST endpoints.
 */
@WebMvcTest(value = CustomerController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration.class
})
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CustomerService customerService;

    private Customer testCustomer;
    private CreateCustomerRequest createRequest;
    private UpdateCustomerRequest updateRequest;
    private UpdateCustomerStatusRequest statusRequest;

    @BeforeEach
    void setUp() {
        LocalDateTime testTime = LocalDateTime.now();

        testCustomer = Customer.reconstitute(
                1L,
                new CustomerNumber("1234567"),
                new CustomerName("John Doe"),
                new NationalId("283061284257"),
                CustomerType.RETAIL,
                new Address("OMAR BEN AL KHATTAB STREET, ARRAYA TOWER, FLOOR 01-13, BLOCK 7, SHARQ"),
                CustomerStatus.ACTIVE,
                testTime,
                testTime
        );

        createRequest = new CreateCustomerRequest(
                "John Doe",
                "283061284257",
                "RETAIL",
                "OMAR BEN AL KHATTAB STREET, ARRAYA TOWER, FLOOR 01-13, BLOCK 7, SHARQ"
        );

        updateRequest = new UpdateCustomerRequest(
                "Jane Smith",
                "OMAR BEN AL KHATTAB STREET, AVENUES MALL, FLOOR 01-13, BLOCK 7, SHARQ",
                "CORPORATE"
        );

        statusRequest = new UpdateCustomerStatusRequest(
                "SUSPENDED",
                "Risk assessment required"
        );
    }

    @Nested
    @DisplayName("POST /api/customers - Create Customer")
    class CreateCustomerTests {

        @Test
        @DisplayName("Should create customer successfully with valid data")
        void shouldCreateCustomerSuccessfully() throws Exception {
            // Given
            when(customerService.createCustomer(anyString(), anyString(), any(), anyString()))
                    .thenReturn(testCustomer);

            // When & Then
            mockMvc.perform(post("/api/customers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.customer_number").value("1234567"))
                    .andExpect(jsonPath("$.name").value("John Doe"))
                    .andExpect(jsonPath("$.national_id").value("283061284257"))
                    .andExpect(jsonPath("$.customer_type").value("RETAIL"))
                    .andExpect(jsonPath("$.address").value("OMAR BEN AL KHATTAB STREET, ARRAYA TOWER, FLOOR 01-13, BLOCK 7, SHARQ"))
                    .andExpect(jsonPath("$.status").value("ACTIVE"));

            // verifies that the 'create' has been run once
            verify(customerService).createCustomer("John Doe", "283061284257", CustomerType.RETAIL, "OMAR BEN AL KHATTAB STREET, ARRAYA TOWER, FLOOR 01-13, BLOCK 7, SHARQ");
        }

        @Test
        @DisplayName("Should return 400 when name is missing")
        void shouldReturn400WhenNameIsMissing() throws Exception {
            // Given
            createRequest.setName(null);

            // When & Then
            mockMvc.perform(post("/api/customers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error_code").value("VALIDATION_ERROR"))
                    .andExpect(jsonPath("$.message").value("Validation failed"))
                    .andExpect(jsonPath("$.details").isArray())
                    .andExpect(jsonPath("$.details", hasItem(containsString("Customer name is required"))));
        }

        @Test
        @DisplayName("Should return 400 when national ID format is invalid")
        void shouldReturn400WhenNationalIdFormatIsInvalid() throws Exception {
            // Given
            createRequest.setNationalId("123"); // Invalid format

            // When & Then
            mockMvc.perform(post("/api/customers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error_code").value("VALIDATION_ERROR"))
                    .andExpect(jsonPath("$.details", hasItem(containsString("National ID must be exactly 12 digits"))));
        }

        @Test
        @DisplayName("Should return 400 when customer type is invalid")
        void shouldReturn400WhenCustomerTypeIsInvalid() throws Exception {
            // Given
            createRequest.setCustomerType("INVALID_TYPE");

            // When & Then
            mockMvc.perform(post("/api/customers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error_code").value("VALIDATION_ERROR"))
                    .andExpect(jsonPath("$.details", hasItem(containsString("Customer type must be RETAIL, CORPORATE, or INVESTMENT"))));
        }

        @Test
        @DisplayName("Should return 409 when customer already exists")
        void shouldReturn409WhenCustomerAlreadyExists() throws Exception {
            // Given
            when(customerService.createCustomer(anyString(), anyString(), any(), anyString()))
                    .thenThrow(new BusinessException(CustomerError.CUSTOMER_ALREADY_EXISTS, "283061284257"));

            // When & Then
            mockMvc.perform(post("/api/customers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRequest)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.error_code").value("CUSTOMER_ALREADY_EXISTS"))
                    .andExpect(jsonPath("$.message").value("Customer with national ID '283061284257' already exists"));
        }
    }

    @Nested
    @DisplayName("GET /api/customers/{customerNumber} - Get Customer")
    class GetCustomerTests {

        @Test
        @DisplayName("Should return customer when found")
        void shouldReturnCustomerWhenFound() throws Exception {
            // Given
            when(customerService.findByCustomerNumber(anyString())).thenReturn(Optional.of(testCustomer));

            // When & Then
            mockMvc.perform(get("/api/customers/1234567"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.customer_number").value("1234567"))
                    .andExpect(jsonPath("$.name").value("John Doe"))
                    .andExpect(jsonPath("$.status").value("ACTIVE"));

            verify(customerService).findByCustomerNumber("1234567");
        }

        @Test
        @DisplayName("Should return 404 when customer not found")
        void shouldReturn404WhenCustomerNotFound() throws Exception {
            // Given
            when(customerService.findByCustomerNumber(anyString()))
                    .thenReturn(Optional.empty());

            // When & Then
            mockMvc.perform(get("/api/customers/9999999"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error_code").value("CUSTOMER_NOT_FOUND"))
                    .andExpect(jsonPath("$.message").value("Customer with number '9999999' not found"));
        }

        @Test
        @DisplayName("Should return 400 when customer number format is invalid")
        void shouldReturn400WhenCustomerNumberFormatIsInvalid() throws Exception {
            // Given
            when(customerService.findByCustomerNumber("123"))
                    .thenThrow(new BusinessException(CustomerError.INVALID_CUSTOMER_NUMBER_FORMAT, "123"));

            // When & Then
            mockMvc.perform(get("/api/customers/123")) // Invalid format
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error_code").value("INVALID_CUSTOMER_DATA"))
                    .andExpect(jsonPath("$.message").value("Invalid customer_number '123': must be exactly 7 digits"));
        }
    }

    @Nested
    @DisplayName("PUT /api/customers/{customerNumber} - Update Customer")
    class UpdateCustomerTests {

        @Test
        @DisplayName("Should update customer successfully")
        void shouldUpdateCustomerSuccessfully() throws Exception {
            // Given
            Customer updatedCustomer = Customer.reconstitute(
                    1L,
                    new CustomerNumber("1234567"),
                    new CustomerName("Jane Smith"),
                    new NationalId("307061980328"),
                    CustomerType.CORPORATE,
                    new Address("OMAR BEN AL KHATTAB STREET, AVENUES MALL, FLOOR 01-13, BLOCK 7, SHARQ"),
                    CustomerStatus.ACTIVE,
                    LocalDateTime.now(),
                    LocalDateTime.now()
            );

            when(customerService.updateCustomer(anyString(), anyString(), anyString(), any()))
                    .thenReturn(updatedCustomer);

            // When & Then
            mockMvc.perform(put("/api/customers/1234567")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.customer_number").value("1234567"))
                    .andExpect(jsonPath("$.name").value("Jane Smith"))
                    .andExpect(jsonPath("$.customer_type").value("CORPORATE"))
                    .andExpect(jsonPath("$.address").value("OMAR BEN AL KHATTAB STREET, AVENUES MALL, FLOOR 01-13, BLOCK 7, SHARQ"));

            verify(customerService).updateCustomer("1234567", "Jane Smith", "OMAR BEN AL KHATTAB STREET, AVENUES MALL, FLOOR 01-13, BLOCK 7, SHARQ", CustomerType.CORPORATE);
        }

        @Test
        @DisplayName("Should return 400 when no updates provided")
        void shouldReturn400WhenNoUpdatesProvided() throws Exception {
            // Given
            UpdateCustomerRequest emptyRequest = new UpdateCustomerRequest();

            // When & Then
            mockMvc.perform(put("/api/customers/1234567")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(emptyRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error_code").value("VALIDATION_ERROR"))
                    .andExpect(jsonPath("$.message").value("At least one field must be provided for update"));
        }
    }

    @Nested
    @DisplayName("PUT /api/customers/{customerNumber}/status - Update Status")
    class UpdateCustomerStatusTests {

        @Test
        @DisplayName("Should update customer status successfully")
        void shouldUpdateCustomerStatusSuccessfully() throws Exception {
            // Given
            Customer suspendedCustomer = Customer.reconstitute(
                    1L,
                    new CustomerNumber("1234567"),
                    new CustomerName("John Doe"),
                    new NationalId("284080559193"),
                    CustomerType.RETAIL,
                    new Address("OMAR BEN AL KHATTAB STREET, ARRAYA TOWER, FLOOR 01-13, BLOCK 7, SHARQ"),
                    CustomerStatus.SUSPENDED,
                    LocalDateTime.now(),
                    LocalDateTime.now()
            );

            when(customerService.updateCustomerStatus(anyString(), any()))
                    .thenReturn(suspendedCustomer);

            // When & Then
            mockMvc.perform(put("/api/customers/1234567/status")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(statusRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.customer_number").value("1234567"))
                    .andExpect(jsonPath("$.status").value("SUSPENDED"));

            verify(customerService).updateCustomerStatus("1234567", CustomerStatus.SUSPENDED);
        }

        @Test
        @DisplayName("Should return 400 when status is invalid")
        void shouldReturn400WhenStatusIsInvalid() throws Exception {
            // Given
            statusRequest.setStatus("INVALID_STATUS");

            // When & Then
            mockMvc.perform(put("/api/customers/1234567/status")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(statusRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error_code").value("VALIDATION_ERROR"))
                    .andExpect(jsonPath("$.details", hasItem(containsString("Status must be PENDING, ACTIVE, SUSPENDED, FROZEN, or CLOSED"))));
        }
    }
} 