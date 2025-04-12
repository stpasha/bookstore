package com.bookstory.billing.test.service;

import com.bookstory.billing.annotation.StoreTestAnnotation;
import com.bookstory.billing.domain.Account;
import com.bookstory.billing.domain.AccountDTO;
import com.bookstory.billing.domain.PaymentDTO;
import com.bookstory.billing.exception.InsufficientBalanceException;
import com.bookstory.billing.mapper.AccountMapper;
import com.bookstory.billing.repository.AccountRepository;
import com.bookstory.billing.service.DefaultAccountService;
import com.bookstory.billing.test.AbstractTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;

@StoreTestAnnotation
public class DefaultServiceTest extends AbstractTest {

    @MockitoBean
    private AccountRepository accountRepository;

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private DefaultAccountService accountService;

    private AccountDTO accountDTO;
    private PaymentDTO paymentDTO;

    @BeforeEach
    void setUp() {
        accountDTO = new AccountDTO(1L, 1L, BigDecimal.valueOf(1000), 1);
        paymentDTO = new PaymentDTO().amount(BigDecimal.valueOf(100)).accountId(1L);
    }

    @Test
    public void getAccountById_ShouldReturnAccount_WhenAccountExists() {

        when(accountRepository.findById(1L)).thenReturn(Mono.just(accountMapper.toEntity(accountDTO)));
        Mono<AccountDTO> result = accountService.getAccountById(1L);
        StepVerifier.create(result)
                .assertNext(account -> {
                    Assertions.assertNotNull(account);
                    Assertions.assertEquals(accountDTO.getId(), account.getId());
                    Assertions.assertEquals(accountDTO.getAmount(), account.getAmount());
                })
                .verifyComplete();

        verify(accountRepository, times(1)).findById(1L);
    }

    @Test
    public void processPayment_ShouldReturnAccount_WhenSufficientBalance() {

        when(accountRepository.findById(1L)).thenReturn(Mono.just(accountMapper.toEntity(accountDTO)));
        when(accountRepository.save(any())).thenReturn(Mono.just(Account.builder().id(1L).amount(BigDecimal.valueOf(900L)).build()));
        Mono<AccountDTO> result = accountService.processPayment(1L, Mono.just(paymentDTO));

        StepVerifier.create(result)
                .assertNext(account -> {
                    Assertions.assertNotNull(account);
                    Assertions.assertEquals(BigDecimal.valueOf(900), account.getAmount());
                })
                .verifyComplete();

        verify(accountRepository, times(1)).findById(1L);
        verify(accountRepository, times(1)).save(any());
    }

    @Test
    public void processPayment_ShouldThrowInsufficientBalanceException_WhenBalanceIsLow() {

        PaymentDTO paymentDTO = new PaymentDTO().amount(BigDecimal.valueOf(2000)).accountId(1L); // Payment greater than balance
        when(accountRepository.findById(1L)).thenReturn(Mono.just(accountMapper.toEntity(accountDTO)));


        StepVerifier.create(accountService.processPayment(1L, Mono.just(paymentDTO)))
                .expectError(InsufficientBalanceException.class)
                .verify();

        verify(accountRepository, times(1)).findById(1L);
        verify(accountRepository, never()).save(any());
    }

    @Test
    public void processPayment_ShouldThrowIllegalArgumentException_WhenPaymentAmountIsNull() {

        PaymentDTO paymentDTO = new PaymentDTO().amount(null).accountId(1L); // Invalid payment amount
        when(accountRepository.findById(1L)).thenReturn(Mono.just(accountMapper.toEntity(accountDTO)));


        StepVerifier.create(accountService.processPayment(1L, Mono.just(paymentDTO)))
                .expectError(IllegalArgumentException.class)
                .verify();

        verify(accountRepository, times(1)).findById(1L);
        verify(accountRepository, never()).save(any());
    }
}
