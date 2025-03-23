package com.bookstory.store.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.reactive.resource.NoResourceFoundException;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(NoResourceFoundException.class)
    public Mono<Rendering> handleNoResourceFoundException(NoResourceFoundException ex, WebRequest request) {
        log.error("Resource not found: {}", ex.getMessage(), ex);
        return Mono.just(Rendering.view("oops")
                .modelAttribute("errorDetails", new ErrorDetails(LocalDateTime.now(), ex.getMessage(), request.getDescription(false)))
                .build());
    }

    @ExceptionHandler(Exception.class)
    public Mono<Rendering> handleGeneralException(Exception ex, WebRequest request) {
        log.error("Error occured {}", ex.getMessage(), ex);
        return Mono.just(Rendering.view("error")
                .modelAttribute("errorDetails", new ErrorDetails(LocalDateTime.now(), ex.getMessage(), request.getDescription(false)))
                .build());
    }

}