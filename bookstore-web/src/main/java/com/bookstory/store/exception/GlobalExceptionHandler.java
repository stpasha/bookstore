package com.bookstory.store.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.resource.NoResourceFoundException;
import org.springframework.web.reactive.result.view.Rendering;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler({NoResourceFoundException.class, BillingIsNotAvailableException.class})
    public Mono<Rendering> handleNoResourceFoundException(NoResourceFoundException ex, ServerWebExchange exchange) {
        log.error("Resource not found: {}", ex.getMessage(), ex);
        return Mono.just(Rendering.view("oops")
                .modelAttribute("errorDetails", new ErrorDetails(LocalDateTime.now(), ex.getMessage(),
                        exchange.getRequest().getURI().toString()))
                .status(HttpStatus.NOT_FOUND)
                .build());
    }

    @ExceptionHandler(Exception.class)
    public Mono<Rendering> handleGeneralException(Exception ex, ServerWebExchange exchange) {
        log.error("Error occured {}", ex.getMessage(), ex);
        return Mono.just(Rendering.view("error")
                .modelAttribute("errorDetails", new ErrorDetails(LocalDateTime.now(), ex.getMessage(),
                        exchange.getRequest().getURI().toString()))
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build());
    }

}