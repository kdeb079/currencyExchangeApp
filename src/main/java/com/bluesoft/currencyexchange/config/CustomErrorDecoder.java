package com.bluesoft.currencyexchange.config;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
public class CustomErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        HttpStatus status = HttpStatus.valueOf(response.status());

        return switch (status) {
            case SERVICE_UNAVAILABLE -> {
                Optional<String> retryAfter = response.headers().getOrDefault("Retry-After", null)
                        .stream().findFirst();
                yield retryAfter.map(s -> new RuntimeException("Service unavailable. Retry after " + s + " seconds for method: " + methodKey)).orElseGet(() -> new RuntimeException("Service unavailable. Retry later for method: " + methodKey));
            }
            case NOT_FOUND -> new RuntimeException("Resource not found: " + methodKey);
            case BAD_REQUEST -> new RuntimeException("Bad request for method: " + methodKey);
            case UNAUTHORIZED -> new RuntimeException("Unauthorized access for method: " + methodKey);
            case FORBIDDEN -> new RuntimeException("Forbidden access for method: " + methodKey);
            case INTERNAL_SERVER_ERROR -> new RuntimeException("Internal server error for method: " + methodKey);
            default -> defaultDecoder.decode(methodKey, response);
        };
    }
}
