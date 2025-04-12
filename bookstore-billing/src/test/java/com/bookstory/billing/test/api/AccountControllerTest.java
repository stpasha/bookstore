package com.bookstory.billing.test.api;

import com.bookstory.billing.api.AccountController;
import com.bookstory.billing.domain.AccountDTO;
import com.bookstory.billing.domain.MessageDTO;
import com.bookstory.billing.domain.PaymentDTO;
import com.bookstory.billing.exception.InsufficientBalanceException;
import com.bookstory.billing.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@WebFluxTest(controllers = AccountController.class)
class AccountControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private AccountService accountService;

    private final Long accountId = 1L;
    private final Long userId = 1L;

    private PaymentDTO validPayment;
    private AccountDTO accountDTO;

    @BeforeEach
    void setUp() {
        validPayment = new PaymentDTO(accountId, new BigDecimal("10.00"));
        accountDTO = new AccountDTO(accountId, userId, new BigDecimal("20.00"), 1);
    }

    @Test
    void createAccountPaymentSuccessful() {
        Mockito.when(accountService.processPayment(eq(accountId), any(Mono.class)))
                .thenReturn(Mono.empty());

        webTestClient.post()
                .uri("/account/{id}/payment", accountId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(validPayment)
                .exchange()
                .expectStatus().isOk()
                .expectBody(MessageDTO.class)
                .value(response -> {
                    assert response.getMessage().equals("Payment successful");
                });
    }

    @Test
    void createAccountPaymentInsufficientBalance() {
        Mockito.when(accountService.processPayment(eq(accountId), any(Mono.class)))
                .thenReturn(Mono.error(new InsufficientBalanceException("Недостаточно средств")));

        webTestClient.post()
                .uri("/account/{id}/payment", accountId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(validPayment)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(MessageDTO.class)
                .value(response -> {
                    assert response.getMessage().equals("Недостаточно средств");
                });
    }

    @Test
    void getAccountById() {
        Mockito.when(accountService.getAccountById(accountId))
                .thenReturn(Mono.just(accountDTO));

        webTestClient.get()
                .uri("/account/{id}", accountId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AccountDTO.class)
                .value(response -> {
                    assert response.getId().equals(accountDTO.getId());
                    assert response.getAmount().equals(accountDTO.getAmount());
                    assert response.getVersion().equals(accountDTO.getVersion());
                });
    }

    @Test
    void getAccountByIdNotFound() {
        Mockito.when(accountService.getAccountById(accountId))
                .thenReturn(Mono.empty());

        webTestClient.get()
                .uri("/account/{id}", accountId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody();
    }
}
