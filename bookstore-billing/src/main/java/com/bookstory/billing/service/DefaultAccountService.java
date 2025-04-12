package com.bookstory.billing.service;

import com.bookstory.billing.domain.Account;
import com.bookstory.billing.domain.AccountDTO;
import com.bookstory.billing.domain.PaymentDTO;
import com.bookstory.billing.exception.InsufficientBalanceException;
import com.bookstory.billing.mapper.AccountMapper;
import com.bookstory.billing.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Service
public class DefaultAccountService implements AccountService {

    private final AccountRepository accountRepository;

    private final AccountMapper accountMapper;

    public Mono<AccountDTO> getAccountById(Long id) {
        return accountRepository.findById(id).map(accountMapper::toDto);
    }
    @Transactional
    public Mono<AccountDTO> processPayment(Long id, Mono<PaymentDTO> dtoMono) {
        return dtoMono.flatMap(paymentDTO ->
                accountRepository.findById(id).flatMap(account -> {
                    if (paymentDTO.getAmount() == null) {
                        return Mono.error(new IllegalArgumentException("Payment amount must be provided"));
                    }

                    if (account.getAmount().compareTo(paymentDTO.getAmount()) < 0) {
                        log.warn("Insufficient balance id {}: balance {}, payment {}",
                                account.getId(), account.getAmount(), paymentDTO.getAmount());
                        return Mono.error(new InsufficientBalanceException(String.format(
                                "Insufficient balance id %s balance %s payment %s",
                                account.getId(), account.getAmount(), paymentDTO.getAmount())));
                    }

                    account.setAmount(account.getAmount().subtract(paymentDTO.getAmount()));
                    return accountRepository.save(account).map(accountMapper::toDto);
                })
        );
    }

    public Mono<AccountDTO> getAccountByUserId(Long id) {
        return accountRepository.findOne(Example.of(Account.builder().userId(id).build())).map(accountMapper::toDto);
    }

}
