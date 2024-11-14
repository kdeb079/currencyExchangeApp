package com.bluesoft.currencyexchange.controller;

import com.bluesoft.currencyexchange.dto.AccountDto;
import com.bluesoft.currencyexchange.dto.CreateAccountRequest;
import com.bluesoft.currencyexchange.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @Operation(summary = "Create a new account", description = "Creates a new account with initial PLN and USD balances")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Account created successfully", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = UUID.class))
            }),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content),
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UUID createAccount(
            @Valid @RequestBody CreateAccountRequest request) {
        return accountService.createAccount(request);
    }

    @Operation(summary = "Get account details", description = "Fetches account details including PLN and USD balances by account ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account details retrieved successfully", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = AccountDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "Account not found", content = @Content),
    })
    @GetMapping("/{accountId}")
    public AccountDto getAccountDetails(
            @PathVariable UUID accountId) {
        return accountService.getAccountDetails(accountId);
    }
}
