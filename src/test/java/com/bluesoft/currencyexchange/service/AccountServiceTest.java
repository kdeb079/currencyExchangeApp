package com.bluesoft.currencyexchange.service;

import com.bluesoft.currencyexchange.dto.CreateAccountRequest;
import com.bluesoft.currencyexchange.dto.CurrencyAccountDto;
import com.bluesoft.currencyexchange.entity.Account;
import com.bluesoft.currencyexchange.entity.Symbol;
import com.bluesoft.currencyexchange.dto.validator.AccountRequestValidator;
import com.bluesoft.currencyexchange.exception.AccountNotFoundException;
import com.bluesoft.currencyexchange.repository.AccountRepository;
import com.bluesoft.currencyexchange.dto.AccountDto;
import com.bluesoft.currencyexchange.mapper.AccountMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountMapper accountMapper;

    @Mock
    private AccountRequestValidator validator;

    @InjectMocks
    private AccountService accountService;

    @Test
    void shouldCreateAccountSuccessfullyWhenPlnBalanceIsProvided() {
        CreateAccountRequest request = new CreateAccountRequest(
                "John",
                "Doe",
                List.of(
                        new CurrencyAccountDto(Symbol.PLN, new BigDecimal("1000.00")),
                        new CurrencyAccountDto(Symbol.USD, new BigDecimal("250.00"))
                )
        );

        UUID generatedId = UUID.randomUUID();
        when(accountRepository.save(any())).thenAnswer(invocation -> {
            Account account = invocation.getArgument(0);
            account.setId(generatedId);
            return account;
        });

        UUID accountId = accountService.createAccount(request);

        assertNotNull(accountId);
        assertEquals(generatedId, accountId);
        verify(accountRepository, times(1)).save(any());
        verify(validator, times(1)).validate(request);
    }

    @Test
    void shouldThrowExceptionWhenNoPlnBalanceProvided() {
        CreateAccountRequest request = new CreateAccountRequest(
                "John",
                "Doe",
                List.of(new CurrencyAccountDto(Symbol.USD, new BigDecimal("100.00")))
        );

        doThrow(new IllegalArgumentException("Account must include an initial balance in PLN."))
                .when(validator).validate(request);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            accountService.createAccount(request);
        });

        assertEquals("Account must include an initial balance in PLN.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenBalanceIsNegative() {
        CreateAccountRequest request = new CreateAccountRequest(
                "John",
                "Doe",
                List.of(
                        new CurrencyAccountDto(Symbol.PLN, new BigDecimal("-100.00")),
                        new CurrencyAccountDto(Symbol.USD, new BigDecimal("50.00"))
                )
        );

        doThrow(new IllegalArgumentException("Balance cannot be negative."))
                .when(validator).validate(request);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            accountService.createAccount(request);
        });

        assertEquals("Balance cannot be negative.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenCurrenciesAreDuplicated() {
        CreateAccountRequest request = new CreateAccountRequest(
                "John",
                "Doe",
                List.of(
                        new CurrencyAccountDto(Symbol.PLN, new BigDecimal("100.00")),
                        new CurrencyAccountDto(Symbol.PLN, new BigDecimal("200.00"))
                )
        );

        doThrow(new IllegalArgumentException("Duplicate currency entries found."))
                .when(validator).validate(request);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            accountService.createAccount(request);
        });

        assertEquals("Duplicate currency entries found.", exception.getMessage());
    }

    @Test
    void shouldHandleEmptyCurrenciesList() {
        CreateAccountRequest request = new CreateAccountRequest(
                "John",
                "Doe",
                List.of()
        );

        doThrow(new IllegalArgumentException("At least one currency balance is required."))
                .when(validator).validate(request);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            accountService.createAccount(request);
        });

        assertEquals("At least one currency balance is required.", exception.getMessage());
    }

    @Test
    void shouldGetAccountDetailsSuccessfully() {
        UUID accountId = UUID.randomUUID();
        Account account = new Account(accountId, "John", "Doe", List.of());
        AccountDto expectedDto = new AccountDto("John", "Doe", List.of());

        when(accountRepository.findById(accountId)).thenReturn(java.util.Optional.of(account));
        when(accountMapper.toDto(account)).thenReturn(expectedDto);

        AccountDto accountDto = accountService.getAccountDetails(accountId);

        assertEquals(expectedDto, accountDto);
        verify(accountRepository, times(1)).findById(accountId);
        verify(accountMapper, times(1)).toDto(account);
    }

    @Test
    void shouldThrowExceptionWhenAccountNotFound() {
        UUID accountId = UUID.randomUUID();

        when(accountRepository.findById(accountId)).thenReturn(java.util.Optional.empty());

        AccountNotFoundException exception = assertThrows(AccountNotFoundException.class, () -> {
            accountService.getAccountDetails(accountId);
        });

        assertTrue(exception.getMessage().contains("There is no account with id : '" + accountId + "'"));
    }

}
