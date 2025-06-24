package com.aalhendi.account_ms.web.controllers;

import com.aalhendi.account_ms.domain.entities.Account;
import com.aalhendi.account_ms.domain.exceptions.AccountError;
import com.aalhendi.account_ms.domain.exceptions.BusinessException;
import com.aalhendi.account_ms.domain.services.AccountService;
import com.aalhendi.account_ms.domain.valueobjects.*;
import com.aalhendi.account_ms.web.dtos.*;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests for AccountController REST endpoints.
 */
@WebMvcTest(value = AccountController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration.class
})
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AccountService accountService;

    private Account testAccount;
    private CreateAccountRequest createRequest;
    private TransactionRequest transactionRequest;
    private UpdateAccountStatusRequest statusRequest;

    @BeforeEach
    void setUp() {
        LocalDateTime testTime = LocalDateTime.now();

        testAccount = Account.reconstitute(
                1L,
                new AccountNumber("1234567001"),
                AccountType.SAVING,
                AccountStatus.ACTIVE,
                new Balance("1000.000"),
                testTime,
                testTime
        );

        createRequest = new CreateAccountRequest("1234567", "SAVING");
        transactionRequest = new TransactionRequest(new BigDecimal("100.500"), "Test transaction");
        statusRequest = new UpdateAccountStatusRequest("ACTIVE", "Activation approved");
    }

    @Nested
    @DisplayName("POST /api/accounts - Create Account")
    class CreateAccountTests {

        @Test
        @DisplayName("Should create account successfully with valid data")
        void shouldCreateAccountSuccessfully() throws Exception {
            // Given
            when(accountService.createAccount(anyString(), any(AccountType.class)))
                    .thenReturn(testAccount);

            // When & Then
            mockMvc.perform(post("/api/accounts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.account_number").value("1234567001"))
                    .andExpect(jsonPath("$.customer_number").value("1234567"))
                    .andExpect(jsonPath("$.account_type").value("SAVING"))
                    .andExpect(jsonPath("$.balance").value("1000.000"))
                    .andExpect(jsonPath("$.status").value("ACTIVE"));

            verify(accountService).createAccount("1234567", AccountType.SAVING);
        }

        @Test
        @DisplayName("Should return 400 when customer number is missing")
        void shouldReturn400WhenCustomerNumberIsMissing() throws Exception {
            // Given
            createRequest.setCustomerNumber(null);

            // When & Then
            mockMvc.perform(post("/api/accounts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error_code").value("VALIDATION_ERROR"))
                    .andExpect(jsonPath("$.message").value("Validation failed"))
                    .andExpect(jsonPath("$.details").isArray())
                    .andExpect(jsonPath("$.details", hasItem(containsString("Customer number is required"))));
        }

        @Test
        @DisplayName("Should return 400 when account type is invalid")
        void shouldReturn400WhenAccountTypeIsInvalid() throws Exception {
            // Given
            createRequest.setAccountType("INVALID_TYPE");

            // When & Then
            mockMvc.perform(post("/api/accounts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error_code").value("VALIDATION_ERROR"))
                    .andExpect(jsonPath("$.details", hasItem(containsString("Account type must be SAVING, INVESTMENT, or SALARY"))));
        }

        @Test
        @DisplayName("Should return 409 when salary account already exists")
        void shouldReturn409WhenSalaryAccountAlreadyExists() throws Exception {
            // Given
            CreateAccountRequest salaryRequest = new CreateAccountRequest("1234567", "SALARY");
            when(accountService.createAccount(anyString(), any(AccountType.class)))
                    .thenThrow(new BusinessException(AccountError.SALARY_ACCOUNT_ALREADY_EXISTS, "1234567", "1234567999"));

            // When & Then
            mockMvc.perform(post("/api/accounts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(salaryRequest)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.error_code").value("SALARY_ACCOUNT_ALREADY_EXISTS"))
                    .andExpect(jsonPath("$.message").value("Customer '1234567' already has a salary account '1234567999'"));
        }

        @Test
        @DisplayName("Should return 404 when customer not found")
        void shouldReturn404WhenCustomerNotFound() throws Exception {
            // Given
            when(accountService.createAccount(anyString(), any(AccountType.class)))
                    .thenThrow(new BusinessException(AccountError.CUSTOMER_NOT_FOUND, "1234567"));

            // When & Then
            mockMvc.perform(post("/api/accounts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRequest)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error_code").value("CUSTOMER_NOT_FOUND"))
                    .andExpect(jsonPath("$.message").value("Customer with number '1234567' not found"));
        }

        @Test
        @DisplayName("Should return 409 when account limit exceeded")
        void shouldReturn409WhenAccountLimitExceeded() throws Exception {
            // Given
            when(accountService.createAccount(anyString(), any(AccountType.class)))
                    .thenThrow(new BusinessException(AccountError.ACCOUNT_LIMIT_EXCEEDED, "1234567"));

            // When & Then
            mockMvc.perform(post("/api/accounts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRequest)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.error_code").value("ACCOUNT_LIMIT_EXCEEDED"))
                    .andExpect(jsonPath("$.message").value("Customer '1234567' has reached the maximum limit of 10 accounts"));
        }
    }

    @Nested
    @DisplayName("GET /api/accounts/{accountNumber} - Get Account")
    class GetAccountTests {

        @Test
        @DisplayName("Should return account when found")
        void shouldReturnAccountWhenFound() throws Exception {
            // Given
            when(accountService.getAccount(anyString())).thenReturn(Optional.of(testAccount));

            // When & Then
            mockMvc.perform(get("/api/accounts/1234567001"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.account_number").value("1234567001"))
                    .andExpect(jsonPath("$.customer_number").value("1234567"))
                    .andExpect(jsonPath("$.account_type").value("SAVING"))
                    .andExpect(jsonPath("$.status").value("ACTIVE"));

            verify(accountService).getAccount("1234567001");
        }

        @Test
        @DisplayName("Should return 404 when account not found")
        void shouldReturn404WhenAccountNotFound() throws Exception {
            // Given
            when(accountService.getAccount(anyString())).thenReturn(Optional.empty());

            // When & Then
            mockMvc.perform(get("/api/accounts/1234567001"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error_code").value("ACCOUNT_NOT_FOUND"))
                    .andExpect(jsonPath("$.message").value("Account with number '1234567001' not found"));

            verify(accountService).getAccount("1234567001");
        }

        @Test
        @DisplayName("Should return 400 when account number format is invalid")
        void shouldReturn400WhenAccountNumberFormatIsInvalid() throws Exception {
            // Given
            String invalidAccountNumber = "123";

            // When & Then
            mockMvc.perform(get("/api/accounts/{accountNumber}", invalidAccountNumber))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error_code").value("VALIDATION_ERROR"));
        }
    }

    @Nested
    @DisplayName("POST /api/accounts/{accountNumber}/credit - Credit Account")
    class CreditAccountTests {

        @Test
        @DisplayName("Should credit account successfully")
        void shouldCreditAccountSuccessfully() throws Exception {
            // Given
            Account updatedAccount = Account.reconstitute(
                    1L,
                    new AccountNumber("1234567001"),
                    AccountType.SAVING,
                    AccountStatus.ACTIVE,
                    new Balance("1100.500"), // Original 1000 + 100.500
                    LocalDateTime.now(),
                    LocalDateTime.now()
            );

            when(accountService.creditAccount(anyString(), any(Balance.class)))
                    .thenReturn(updatedAccount);

            // When & Then
            mockMvc.perform(post("/api/accounts/1234567001/credit")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(transactionRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.account_number").value("1234567001"))
                    .andExpect(jsonPath("$.balance").value("1100.500"));

            verify(accountService).creditAccount(eq("1234567001"), any(Balance.class));
        }

        @Test
        @DisplayName("Should return 400 when amount is negative")
        void shouldReturn400WhenAmountIsNegative() throws Exception {
            // Given
            TransactionRequest negativeRequest = new TransactionRequest(new BigDecimal("-50.00"), "Invalid amount");

            // When & Then
            mockMvc.perform(post("/api/accounts/1234567001/credit")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(negativeRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error_code").value("VALIDATION_ERROR"))
                    .andExpect(jsonPath("$.details", hasItem(containsString("Amount must be positive"))));
        }

        @Test
        @DisplayName("Should return 400 when account is not active")
        void shouldReturn400WhenAccountIsNotActive() throws Exception {
            // Given
            when(accountService.creditAccount(anyString(), any(Balance.class)))
                    .thenThrow(new BusinessException(AccountError.ACCOUNT_NOT_ACTIVE, "1234567001", "SUSPENDED"));

            // When & Then
            mockMvc.perform(post("/api/accounts/1234567001/credit")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(transactionRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error_code").value("INVALID_ACCOUNT_STATE"))
                    .andExpect(jsonPath("$.message").value("Account '1234567001' is not active. Current status: SUSPENDED"));
        }
    }

    @Nested
    @DisplayName("POST /api/accounts/{accountNumber}/debit - Debit Account")
    class DebitAccountTests {

        @Test
        @DisplayName("Should debit account successfully")
        void shouldDebitAccountSuccessfully() throws Exception {
            // Given
            Account updatedAccount = Account.reconstitute(
                    1L,
                    new AccountNumber("1234567001"),
                    AccountType.SAVING,
                    AccountStatus.ACTIVE,
                    new Balance("899.500"), // Original 1000 - 100.500
                    LocalDateTime.now(),
                    LocalDateTime.now()
            );

            when(accountService.debitAccount(anyString(), any(Balance.class)))
                    .thenReturn(updatedAccount);

            // When & Then
            mockMvc.perform(post("/api/accounts/1234567001/debit")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(transactionRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.account_number").value("1234567001"))
                    .andExpect(jsonPath("$.balance").value("899.500"));

            verify(accountService).debitAccount(eq("1234567001"), any(Balance.class));
        }

        @Test
        @DisplayName("Should return 400 when insufficient funds")
        void shouldReturn400WhenInsufficientFunds() throws Exception {
            // Given
            TransactionRequest largeRequest = new TransactionRequest(new BigDecimal("2000.00"), "Too much");
            when(accountService.debitAccount(anyString(), any(Balance.class)))
                    .thenThrow(new BusinessException(AccountError.INSUFFICIENT_FUNDS, "1234567001", "1000.000", "2000.00"));

            // When & Then
            mockMvc.perform(post("/api/accounts/1234567001/debit")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(largeRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error_code").value("INSUFFICIENT_FUNDS"))
                    .andExpect(jsonPath("$.message").value("Account '1234567001' has insufficient funds. Current balance: 1000.000, requested: 2000.00"));
        }
    }

    @Nested
    @DisplayName("PUT /api/accounts/{accountNumber}/status - Update Account Status")
    class UpdateAccountStatusTests {

        @Test
        @DisplayName("Should update account status successfully")
        void shouldUpdateAccountStatusSuccessfully() throws Exception {
            // Given
            Account updatedAccount = Account.reconstitute(
                    1L,
                    new AccountNumber("1234567001"),
                    AccountType.SAVING,
                    AccountStatus.SUSPENDED,
                    new Balance("1000.000"),
                    LocalDateTime.now(),
                    LocalDateTime.now()
            );

            when(accountService.updateAccountStatus(anyString(), any(AccountStatus.class)))
                    .thenReturn(updatedAccount);

            UpdateAccountStatusRequest suspendRequest = new UpdateAccountStatusRequest("SUSPENDED", "Risk assessment");

            // When & Then
            mockMvc.perform(put("/api/accounts/1234567001/status")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(suspendRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.account_number").value("1234567001"))
                    .andExpect(jsonPath("$.status").value("SUSPENDED"));

            verify(accountService).updateAccountStatus("1234567001", AccountStatus.SUSPENDED);
        }

        @Test
        @DisplayName("Should return 400 when status is invalid")
        void shouldReturn400WhenStatusIsInvalid() throws Exception {
            // Given
            UpdateAccountStatusRequest invalidRequest = new UpdateAccountStatusRequest("INVALID_STATUS", "Invalid");

            // When & Then
            mockMvc.perform(put("/api/accounts/1234567001/status")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error_code").value("VALIDATION_ERROR"));
        }

        @Test
        @DisplayName("Should return 400 when invalid status transition")
        void shouldReturn400WhenInvalidStatusTransition() throws Exception {
            // Given
            when(accountService.updateAccountStatus(anyString(), any(AccountStatus.class)))
                    .thenThrow(new BusinessException(AccountError.INVALID_STATUS_TRANSITION, "1234567001", "CLOSED", "ACTIVE"));

            // When & Then
            mockMvc.perform(put("/api/accounts/1234567001/status")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(statusRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error_code").value("INVALID_ACCOUNT_STATE"))
                    .andExpect(jsonPath("$.message").value("Account '1234567001' cannot transition from 'CLOSED' to 'ACTIVE'"));
        }
    }

    @Nested
    @DisplayName("DELETE /api/accounts/{accountNumber} - Close Account")
    class CloseAccountTests {

        @Test
        @DisplayName("Should close account successfully")
        void shouldCloseAccountSuccessfully() throws Exception {
            // Given
            doNothing().when(accountService).closeAccount(anyString());

            // When & Then
            mockMvc.perform(delete("/api/accounts/1234567001"))
                    .andExpect(status().isNoContent());

            verify(accountService).closeAccount("1234567001");
        }

        @Test
        @DisplayName("Should return 400 when account has balance")
        void shouldReturn400WhenAccountHasBalance() throws Exception {
            // Given
            doThrow(new BusinessException(AccountError.CANNOT_CLOSE_ACCOUNT_WITH_BALANCE, "1234567001", "1000.000"))
                    .when(accountService).closeAccount(anyString());

            // When & Then
            mockMvc.perform(delete("/api/accounts/1234567001"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error_code").value("CANNOT_CLOSE_ACCOUNT_WITH_BALANCE"))
                    .andExpect(jsonPath("$.message").value("Cannot close account '1234567001' with non-zero balance: 1000.000"));
        }

        @Test
        @DisplayName("Should return 404 when account not found")
        void shouldReturn404WhenAccountNotFound() throws Exception {
            // Given
            doThrow(new BusinessException(AccountError.ACCOUNT_NOT_FOUND, "1234567001"))
                    .when(accountService).closeAccount(anyString());

            // When & Then
            mockMvc.perform(delete("/api/accounts/1234567001"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error_code").value("ACCOUNT_NOT_FOUND"))
                    .andExpect(jsonPath("$.message").value("Account with number '1234567001' not found"));
        }
    }
} 