package com.bluesoft.currencyexchange.service;

import com.bluesoft.currencyexchange.entity.Account;
import com.bluesoft.currencyexchange.entity.CurrencyAccount;
import com.bluesoft.currencyexchange.entity.Symbol;
import com.bluesoft.currencyexchange.exception.AccountNotFoundException;
import com.bluesoft.currencyexchange.exception.InvalidCurrencyExchangeException;
import com.bluesoft.currencyexchange.repository.AccountRepository;
import com.bluesoft.currencyexchange.repository.CurrencyAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

/**
 * Service for handling currency exchange operations between different currency accounts.
 */
@Service
@RequiredArgsConstructor
public class CurrencyExchangeService {

    private final AccountRepository accountRepository;
    private final CurrencyAccountRepository currencyAccountRepository;
    private final CurrencyRateProvider currencyRateProvider;

    /**
     * Exchanges a specified amount from one currency to another within an account.
     *
     * @param accountId the UUID of the account
     * @param amount the amount to exchange
     * @param fromCurrency the currency to exchange from
     * @param toCurrency the currency to exchange to
     * @throws AccountNotFoundException if the account or currency accounts do not exist
     * @throws IllegalArgumentException if there are insufficient funds in the fromCurrency account
     */
    @Transactional
    public void exchangeCurrency(UUID accountId, BigDecimal amount, Symbol fromCurrency, Symbol toCurrency) {
        if (fromCurrency.equals(toCurrency)) {
            throw new InvalidCurrencyExchangeException("Cannot exchange the same currency. Please select different currencies.");
        }
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("There is no account with id : '" + accountId + "'."));

        BigDecimal rate = currencyRateProvider.getUsdToPlnRate();
        processTransfer(accountId, amount, fromCurrency, toCurrency, rate);
        accountRepository.save(account);
    }

    /**
     * Retrieves the balance of a specific currency within an account.
     *
     * @param accountId the UUID of the account
     * @param symbol the currency symbol
     * @return the balance of the specified currency
     * @throws AccountNotFoundException if the currency account does not exist
     */
    @Transactional(readOnly = true)
    public BigDecimal getBalance(UUID accountId, Symbol symbol) {
        return currencyAccountRepository.findBySymbolAndAccount_Id(symbol, accountId)
                .orElseThrow(() -> new AccountNotFoundException("There is no currency account with accountId : '" + accountId + "' and symbol : '" + symbol + "'"))
                .getBalance();
    }

    /**
     * Internal method to process the currency transfer between accounts.
     *
     * @param accountId the UUID of the account
     * @param amount the amount to transfer
     * @param fromCurrency the currency to transfer from
     * @param toCurrency the currency to transfer to
     * @param rate the exchange rate between the currencies
     * @throws AccountNotFoundException if the currency accounts do not exist
     * @throws IllegalArgumentException if there are insufficient funds in the fromCurrency account
     */
    private void processTransfer(UUID accountId, BigDecimal amount, Symbol fromCurrency, Symbol toCurrency, BigDecimal rate) {
        CurrencyAccount fromAccount = currencyAccountRepository.findBySymbolAndAccount_Id(fromCurrency, accountId)
                .orElseThrow(() -> new AccountNotFoundException(String.format("There is no currency account with accountId : '%s' and symbol : '%s'", accountId, fromCurrency)));
        CurrencyAccount toAccount = currencyAccountRepository.findBySymbolAndAccount_Id(toCurrency, accountId)
                .orElseThrow(() -> new AccountNotFoundException(String.format("There is no currency account with accountId : '%s' and symbol : '%s'", accountId, toCurrency)));

        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient funds in the account.");
        }

        rate = fromCurrency == Symbol.PLN ? rate : BigDecimal.ONE.divide(rate, 10, RoundingMode.HALF_UP);
        fromAccount.exchange(amount, rate, toAccount);
    }
}
