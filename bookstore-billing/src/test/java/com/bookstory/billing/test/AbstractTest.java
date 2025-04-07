package com.bookstory.billing.test;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class AbstractTest {

    @Container // Декларируем объект учитываемым тест-контейнером
    @ServiceConnection // Автоматически назначаем параметры соединения с контейнером
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("store")
            .withUsername("baseadm")
            .withPassword("test")
            .withInitScript("init-script.sql");
}
