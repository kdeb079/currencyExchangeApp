package com.bluesoft.currencyexchange.service;

import com.bluesoft.currencyexchange.entity.Account;
import com.bluesoft.currencyexchange.entity.CurrencyAccount;
import com.bluesoft.currencyexchange.entity.Symbol;
import com.bluesoft.currencyexchange.exception.AccountNotFoundException;
import com.bluesoft.currencyexchange.repository.AccountRepository;
import com.bluesoft.currencyexchange.repository.CurrencyAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CurrencyExchangeServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CurrencyAccountRepository currencyAccountRepository;

    @Mock
    private CurrencyRateProvider currencyRateProvider;

    @InjectMocks
    private CurrencyExchangeService currencyExchangeService;

    private UUID accountId;
    private Account account;
    private CurrencyAccount plnAccount;
    private CurrencyAccount usdAccount;

    @BeforeEach
    void setUp() {
        accountId = UUID.randomUUID();
        account = new Account();
        account.setId(accountId);
        account.setFirstName("John");
        account.setLastName("Doe");

        plnAccount = new CurrencyAccount(null, Symbol.PLN, account, new BigDecimal("1000.00"));
        usdAccount = new CurrencyAccount(null, Symbol.USD, account, new BigDecimal("250.00"));

        account.addCurrencyAccount(plnAccount);
        account.addCurrencyAccount(usdAccount);
    }

    @Test
    void shouldExchangePlnToUsdSuccessfully() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(currencyAccountRepository.findBySymbolAndAccount_Id(Symbol.PLN, accountId)).thenReturn(Optional.of(plnAccount));
        when(currencyAccountRepository.findBySymbolAndAccount_Id(Symbol.USD, accountId)).thenReturn(Optional.of(usdAccount));
        when(currencyRateProvider.getUsdToPlnRate()).thenReturn(new BigDecimal("4.00"));

        currencyExchangeService.exchangeCurrency(accountId, new BigDecimal("200.00"), Symbol.PLN, Symbol.USD);

        assertEquals(new BigDecimal("800.00").setScale(2), plnAccount.getBalance());
        assertEquals(new BigDecimal("300.00").setScale(2), usdAccount.getBalance());
        verify(accountRepository, times(1)).save(account);
    }

    @Test
    void shouldExchangeUsdToPlnSuccessfully() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(currencyAccountRepository.findBySymbolAndAccount_Id(Symbol.USD, accountId)).thenReturn(Optional.of(usdAccount));
        when(currencyAccountRepository.findBySymbolAndAccount_Id(Symbol.PLN, accountId)).thenReturn(Optional.of(plnAccount));
        when(currencyRateProvider.getUsdToPlnRate()).thenReturn(new BigDecimal("4.00"));

        currencyExchangeService.exchangeCurrency(accountId, new BigDecimal("50.00"), Symbol.USD, Symbol.PLN);

        assertEquals(new BigDecimal("1200.00").setScale(2), plnAccount.getBalance());
        assertEquals(new BigDecimal("200.00").setScale(2), usdAccount.getBalance());
        verify(accountRepository, times(1)).save(account);
    }

    @Test
    void shouldThrowExceptionWhenAccountNotFound() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        AccountNotFoundException exception = assertThrows(AccountNotFoundException.class, () -> {
            currencyExchangeService.exchangeCurrency(accountId, new BigDecimal("100.00"), Symbol.PLN, Symbol.USD);
        });

        assertTrue(exception.getMessage().contains("There is no account with id : '" + accountId + "'"));
    }

    @Test
    void shouldThrowExceptionWhenCurrencyAccountNotFound() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(currencyAccountRepository.findBySymbolAndAccount_Id(Symbol.PLN, accountId)).thenReturn(Optional.empty());

        AccountNotFoundException exception = assertThrows(AccountNotFoundException.class, () -> {
            currencyExchangeService.exchangeCurrency(accountId, new BigDecimal("100.00"), Symbol.PLN, Symbol.USD);
        });

        assertTrue(exception.getMessage().contains("There is no currency account with accountId : '" + accountId + "' and symbol : 'PLN'"));
    }

    @Test
    void shouldThrowExceptionWhenInsufficientBalance() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(currencyAccountRepository.findBySymbolAndAccount_Id(Symbol.PLN, accountId)).thenReturn(Optional.of(plnAccount));
        when(currencyAccountRepository.findBySymbolAndAccount_Id(Symbol.USD, accountId)).thenReturn(Optional.of(usdAccount));
        when(currencyRateProvider.getUsdToPlnRate()).thenReturn(new BigDecimal("4.00"));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            currencyExchangeService.exchangeCurrency(accountId, new BigDecimal("2000.00"), Symbol.PLN, Symbol.USD);
        });

        assertEquals("Insufficient funds in the account.", exception.getMessage());
    }

    @Test
    void shouldRetrieveCorrectBalance() {
        when(currencyAccountRepository.findBySymbolAndAccount_Id(Symbol.PLN, accountId)).thenReturn(Optional.of(plnAccount));

        BigDecimal balance = currencyExchangeService.getBalance(accountId, Symbol.PLN);

        assertEquals(new BigDecimal("1000.00").setScale(2), balance);
        verify(currencyAccountRepository, times(1)).findBySymbolAndAccount_Id(Symbol.PLN, accountId);
    }

    @Test
    void shouldThrowExceptionWhenRetrievingBalanceForNonExistentAccount() {
        when(currencyAccountRepository.findBySymbolAndAccount_Id(Symbol.PLN, accountId)).thenReturn(Optional.empty());

        AccountNotFoundException exception = assertThrows(AccountNotFoundException.class, () -> {
            currencyExchangeService.getBalance(accountId, Symbol.PLN);
        });

        assertTrue(exception.getMessage().contains("There is no currency account with accountId : '" + accountId + "' and symbol : 'PLN'"));
    }

}
