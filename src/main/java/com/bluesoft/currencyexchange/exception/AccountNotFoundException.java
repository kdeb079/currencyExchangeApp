package com.bluesoft.currencyexchange.exception;

import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

public class AccountNotFoundException extends ResponseStatusException {
    public AccountNotFoundException(String message) {
        super(NOT_FOUND, message);
    }
}
