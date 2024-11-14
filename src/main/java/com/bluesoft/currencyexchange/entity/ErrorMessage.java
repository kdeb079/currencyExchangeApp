package com.bluesoft.currencyexchange.entity;

import org.springframework.http.HttpStatusCode;

public record ErrorMessage(HttpStatusCode status, String message) {
}
