package com.bookstory.billing.api;

import com.bookstory.billing.domain.AccountDTO;
import com.bookstory.billing.domain.MessageDTO;
import com.bookstory.billing.domain.PaymentDTO;
import com.bookstory.billing.exception.InsufficientBalanceException;
import com.bookstory.billing.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping
public class AccountController implements AccountApi {

    private final AccountService accountService;

    @Override
    public Mono<ResponseEntity<MessageDTO>> createAccountPayment(Long id, @Valid Mono<PaymentDTO> paymentDTO, ServerWebExchange exchange) {
        log.error("entered overriden createAccountPayment");
        return accountService.processPayment(id, paymentDTO)
                .thenReturn(ResponseEntity.ok(new MessageDTO().message("Payment successful")))
                .onErrorResume(InsufficientBalanceException.class, ex ->
                        Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(new MessageDTO().message(ex.getMessage())))
                );
    }

    @Override
    public Mono<ResponseEntity<AccountDTO>> getAccountById(Long id, ServerWebExchange exchange) {
        return accountService.getAccountById(id)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found")));
    }

    public Mono<ResponseEntity<AccountDTO>> getAccountByUserId(Long userId, ServerWebExchange exchange) {
        return accountService.getAccountByUserId(userId)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found")));

    }
}
