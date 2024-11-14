package com.bluesoft.currencyexchange.controller;

import com.bluesoft.currencyexchange.entity.Symbol;
import com.bluesoft.currencyexchange.service.CurrencyExchangeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/currency-exchange")
@RequiredArgsConstructor
public class CurrencyExchangeController {

    private final CurrencyExchangeService currencyExchangeService;

    @Operation(summary = "Exchange currency between PLN and USD", description = "Perform a currency exchange between PLN and USD for a given account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Currency exchange successful", content = @Content),
            @ApiResponse(responseCode = "404", description = "Account not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid request or validation errors", content = @Content)
    })
    @PostMapping("/{accountId}/exchange")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void exchangeCurrency(
            @PathVariable @Parameter(description = "ID of the account to perform the exchange on") UUID accountId,
            @RequestParam @Positive @Parameter(description = "Amount to exchange", example = "100") BigDecimal amount,
            @RequestParam @NotNull @Parameter(description = "Currency to exchange from (PLN or USD)") Symbol fromCurrency,
            @RequestParam @NotNull @Parameter(description = "Currency to exchange to (PLN or USD)") Symbol toCurrency) {
        currencyExchangeService.exchangeCurrency(accountId, amount, fromCurrency, toCurrency);
    }

    @Operation(summary = "Get balance", description = "Get current balance of a given currency account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BigDecimal.class))),
            @ApiResponse(responseCode = "404", description = "Account not found", content = @Content)
    })
    @GetMapping("/{accountId}/balance/{symbol}")
    public BigDecimal getPlnBalance(
            @PathVariable @Parameter(description = "ID of the account") UUID accountId, @PathVariable @Parameter(description = "Currency symbol") Symbol symbol) {
        return currencyExchangeService.getBalance(accountId, symbol);
    }

}
