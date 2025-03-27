package com.bookstory.store.util;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class ObjectValidator {

    private final Validator validator;

    public <T> void validate(T object) {
        if (object == null) {
            throw new IllegalArgumentException("Validation error: object must not be null");
        }

        Set<ConstraintViolation<T>> violations = validator.validate(object);
        handleViolations(violations);
    }

    public <T> void validate(List<T> objects) {
        if (objects == null || objects.isEmpty()) {
            throw new IllegalArgumentException("Validation error: list must not be null or empty");
        }

        for (T object : objects) {
            validate(object);
        }
    }

    private <T> void handleViolations(Set<ConstraintViolation<T>> violations) {
        if (!violations.isEmpty()) {
            String errorMessages = violations.stream()
                    .map(v -> v.getPropertyPath() + " " + v.getMessage())
                    .collect(Collectors.joining("; "));
            log.error("Validation failed: {}", errorMessages);
            throw new IllegalArgumentException("Validation error: " + errorMessages);
        }
    }

    public <T> Flux<T> validate(Flux<T> flux) {
        return flux.switchIfEmpty(Flux.error(new IllegalArgumentException("Validation error: Flux must not be empty")))
                .flatMap(object -> Mono.defer(() -> {
                    Set<ConstraintViolation<T>> violations = validator.validate(object);
                    if (!violations.isEmpty()) {
                        String errorMessages = violations.stream()
                                .map(v -> v.getPropertyPath() + " " + v.getMessage())
                                .collect(Collectors.joining("; "));
                        log.error("Validation failed: {}", errorMessages);
                        return Mono.error(new IllegalArgumentException("Validation error: " + errorMessages));
                    }
                    return Mono.just(object);
                }));
    }
}

