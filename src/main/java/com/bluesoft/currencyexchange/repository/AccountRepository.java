package com.bluesoft.currencyexchange.repository;

import com.bluesoft.currencyexchange.entity.Account;
import com.bluesoft.currencyexchange.entity.CurrencyAccount;
import com.bluesoft.currencyexchange.entity.Symbol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {

    Optional<Account> findByIdAndCurrencyAccounts_Symbol(UUID id, Symbol symbol);
}