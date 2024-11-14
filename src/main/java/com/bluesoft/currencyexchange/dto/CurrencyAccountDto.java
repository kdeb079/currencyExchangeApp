package com.bluesoft.currencyexchange.dto;

import com.bluesoft.currencyexchange.entity.Symbol;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record CurrencyAccountDto (
                                  @NotNull Symbol symbol,
                                  @PositiveOrZero BigDecimal balance
) {}
