package com.bluesoft.currencyexchange.dto;

import java.util.List;

public record AccountDto(String firstName, String lastName, List<CurrencyAccountDto> currencyAccounts) {}
