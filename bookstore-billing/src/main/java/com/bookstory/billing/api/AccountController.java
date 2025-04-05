package com.bookstory.billing.api;

import com.bookstory.billing.domain.AccountDTO;
import com.bookstory.billing.domain.MessageDTO;
import com.bookstory.billing.domain.PaymentDTO;
import com.bookstory.billing.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping
public class AccountController implements AccountApi {

    private final AccountRepository accountRepository;

    @Override
    public Mono<ResponseEntity<MessageDTO>> createAccountPayment(Long id, Mono<PaymentDTO> paymentDTO, ServerWebExchange exchange) {
        log.error("entered overriden createAccountPayment");
        return AccountApi.super.createAccountPayment(id, paymentDTO, exchange);
    }

    @Override
    public Mono<ResponseEntity<AccountDTO>> getAccountById(Long id, ServerWebExchange exchange) {
        log.error("entered overriden getAccountById");
        return AccountApi.super.getAccountById(id, exchange);
    }
}
