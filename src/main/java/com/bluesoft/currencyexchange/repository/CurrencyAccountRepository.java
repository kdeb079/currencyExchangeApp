package com.bluesoft.currencyexchange.repository;

import com.bluesoft.currencyexchange.entity.CurrencyAccount;
import com.bluesoft.currencyexchange.entity.Symbol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CurrencyAccountRepository extends JpaRepository<CurrencyAccount, Long> {
    Optional<CurrencyAccount> findBySymbolAndAccount_Id(Symbol symbol, UUID id);

}
