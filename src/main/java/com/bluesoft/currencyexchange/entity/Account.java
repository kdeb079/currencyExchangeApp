package com.bluesoft.currencyexchange.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UuidGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @OneToMany(
            mappedBy = "account",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<CurrencyAccount> currencyAccounts = new ArrayList<>();

    public void addCurrencyAccount(CurrencyAccount currencyAccount) {
        currencyAccounts.add(currencyAccount);
        currencyAccount.setAccount(this);
    }

    public void removeCurrencyAccount(CurrencyAccount currencyAccount) {
        currencyAccounts.remove(currencyAccount);
        currencyAccount.setAccount(null);
    }
}
