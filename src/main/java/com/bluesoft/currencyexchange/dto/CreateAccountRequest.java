package com.bluesoft.currencyexchange.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record CreateAccountRequest(@NotBlank String firstName,
                                   @NotBlank String lastName,
                                   List<CurrencyAccountDto> currencyAccounts
) {}
