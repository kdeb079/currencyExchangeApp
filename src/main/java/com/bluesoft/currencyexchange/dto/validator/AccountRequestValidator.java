package com.bluesoft.currencyexchange.dto.validator;

import com.bluesoft.currencyexchange.dto.CreateAccountRequest;
import com.bluesoft.currencyexchange.dto.CurrencyAccountDto;
import com.bluesoft.currencyexchange.entity.Symbol;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class AccountRequestValidator {

    public void validate(CreateAccountRequest request) {
        checkAtLeastOneCurrencyBalance(request);
        checkContainsPlnBalance(request);
        checkNoNegativeBalances(request);
        checkNoDuplicateCurrencies(request);
    }

    private void checkAtLeastOneCurrencyBalance(CreateAccountRequest request) {
        if (request.currencyAccounts().isEmpty()) {
            throw new IllegalArgumentException("At least one currency balance is required.");
        }
    }

    private void checkContainsPlnBalance(CreateAccountRequest request) {
        boolean hasPlnBalance = request.currencyAccounts().stream()
                .anyMatch(account -> account.symbol() == Symbol.PLN);
        if (!hasPlnBalance) {
            throw new IllegalArgumentException("Account must include an initial balance in PLN.");
        }
    }

    private void checkNoNegativeBalances(CreateAccountRequest request) {
        if (request.currencyAccounts().stream()
                .anyMatch(account -> account.balance().compareTo(BigDecimal.ZERO) < 0)) {
            throw new IllegalArgumentException("Balance cannot be negative.");
        }
    }

    private void checkNoDuplicateCurrencies(CreateAccountRequest request) {
        Set<Symbol> symbols = request.currencyAccounts().stream()
                .map(CurrencyAccountDto::symbol)
                .collect(Collectors.toSet());
        if (symbols.size() != request.currencyAccounts().size()) {
            throw new IllegalArgumentException("Duplicate currency entries found.");
        }
    }
}
