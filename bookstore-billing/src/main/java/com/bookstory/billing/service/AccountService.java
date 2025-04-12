package com.bookstory.billing.service;

import com.bookstory.billing.domain.AccountDTO;
import com.bookstory.billing.domain.PaymentDTO;
import reactor.core.publisher.Mono;

public interface AccountService {
    Mono<AccountDTO> getAccountById(Long id);
    Mono<AccountDTO> getAccountByUserId(Long id);
    Mono<AccountDTO> processPayment(Long id, Mono<PaymentDTO>  paymentDTO);
}
