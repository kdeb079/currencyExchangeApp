package com.bluesoft.currencyexchange.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Symbol symbol;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @Positive
    private BigDecimal balance;

    public void exchange(BigDecimal currencyAmount, BigDecimal exchangeRate, CurrencyAccount to) {
        this.balance = getBalance().subtract(currencyAmount).setScale(2, RoundingMode.HALF_UP);
        BigDecimal exchangedCurrencyAmount = currencyAmount.divide(exchangeRate, 10, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP);
        to.setBalance(to.getBalance().add(exchangedCurrencyAmount).setScale(2, RoundingMode.HALF_UP));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CurrencyAccount)) return false;
        CurrencyAccount that = (CurrencyAccount) o;
        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
