package com.bookstory.billing.test.repository;

import com.bookstory.billing.annotation.StoreTestAnnotation;
import com.bookstory.billing.domain.Account;
import com.bookstory.billing.repository.AccountRepository;
import com.bookstory.billing.test.AbstractTest;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.*;

@StoreTestAnnotation
@Slf4j
class AccountRepositoryTest extends AbstractTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private Faker faker;

    @BeforeEach
    void setUp() {
        StepVerifier.create(accountRepository.deleteAll())
                .verifyComplete();
    }

    @Test
    void createAccount() {
        BigDecimal amount = BigDecimal.valueOf(faker.number().randomDouble(2, 10, 10000));

        Account account = Account.builder()
                .amount(amount)
                .build();

        StepVerifier.create(accountRepository.save(account))
                .assertNext(saved -> {
                    assertNotNull(saved.getId(), "Account ID must be generated");
                    assertEquals(amount, saved.getAmount(), "Amount should match");
                    assertNotNull(saved.getVersion(), "Version must be initialized");
                })
                .verifyComplete();
    }

    @Test
    void findByIdAccount() {
        BigDecimal amount = BigDecimal.valueOf(faker.number().randomDouble(2, 10, 5000)).setScale(2, RoundingMode.HALF_UP);

        Account account = Account.builder()
                .amount(amount)
                .build();

        Mono<Account> accountMono = accountRepository.save(account)
                .flatMap(saved -> accountRepository.findById(saved.getId()));

        StepVerifier.create(accountMono)
                .assertNext(found -> {
                    assertNotNull(found.getId(), "Found account should have ID");
                    assertEquals(amount, found.getAmount(), "Amount should match");
                })
                .verifyComplete();
    }

    @Test
    void updateAccount() {
        BigDecimal initial = BigDecimal.valueOf(faker.number().randomDouble(2, 100, 500));
        BigDecimal delta = BigDecimal.TEN;

        Account account = Account.builder()
                .amount(initial)
                .build();

        Mono<Account> updatedMono = accountRepository.save(account)
                .map(saved -> {
                    saved.setAmount(saved.getAmount().add(delta));
                    return saved;
                })
                .flatMap(accountRepository::save);

        StepVerifier.create(updatedMono)
                .assertNext(updated -> assertEquals(initial.add(delta), updated.getAmount(), "Updated amount must be correct"))
                .verifyComplete();
    }
}
