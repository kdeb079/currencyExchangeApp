package com.bluesoft.currencyexchange.service;

import com.bluesoft.currencyexchange.dto.CurrencyAccountDto;
import com.bluesoft.currencyexchange.entity.CurrencyAccount;
import com.bluesoft.currencyexchange.dto.validator.AccountRequestValidator;
import com.bluesoft.currencyexchange.exception.AccountNotFoundException;
import com.bluesoft.currencyexchange.repository.AccountRepository;
import com.bluesoft.currencyexchange.dto.AccountDto;
import com.bluesoft.currencyexchange.dto.CreateAccountRequest;
import com.bluesoft.currencyexchange.entity.Account;
import com.bluesoft.currencyexchange.mapper.AccountMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Service for managing accounts, including creation and retrieval of account details.
 */
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final AccountRequestValidator validator;

    /**
     * Creates a new account based on the provided request.
     * Validates the request before creating the account.
     *
     * @param request the request containing account details
     * @return the UUID of the created account
     */
    public UUID createAccount(CreateAccountRequest request) {
        validator.validate(request);

        Account account = new Account(null, request.firstName(), request.lastName(), new ArrayList<>());
        request.currencyAccounts().forEach(currencyAccountDto ->
                account.getCurrencyAccounts().add(createCurrencyAccount(currencyAccountDto, account))
        );
        Account savedAccount = accountRepository.save(account);
        return savedAccount.getId();
    }

    /**
     * Retrieves the details of an account based on its UUID.
     *
     * @param accountId the UUID of the account to retrieve
     * @return the account details as an AccountDto
     * @throws AccountNotFoundException if the account does not exist
     */
    public AccountDto getAccountDetails(UUID accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(String.format("There is no account with id : '%s'.", accountId)));
        return accountMapper.toDto(account);
    }

    /**
     * Creates a new CurrencyAccount entity associated with an Account.
     * The balance is rounded to two decimal places.
     *
     * @param currencyAccountDto the DTO containing currency account details
     * @param account the account to associate with the currency account
     * @return the created CurrencyAccount entity
     */
    public CurrencyAccount createCurrencyAccount(CurrencyAccountDto currencyAccountDto, Account account) {
        return new CurrencyAccount(
                null,
                currencyAccountDto.symbol(),
                account,
                currencyAccountDto.balance().setScale(2, RoundingMode.HALF_UP)
        );
    }
}
