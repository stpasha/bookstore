package com.bookstory.store.tests;

import liquibase.command.CommandScope;
import liquibase.exception.CommandExecutionException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class AbstractTest {
    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("store")
            .withUsername("baseadm")
            .withPassword("test")
            .withInitScript("init-script.sql");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.r2dbc.url", () ->
                String.format("r2dbc:pool:postgresql://%s:%d/store",
                        postgres.getHost(), postgres.getMappedPort(5432)));
        registry.add("spring.r2dbc.username", postgres::getUsername);
        registry.add("spring.r2dbc.password", postgres::getPassword);
        applyMigrations(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
    }

    static void applyMigrations(String jdbcUrl, String username, String password) {
        try {
            new CommandScope("update")
                    .addArgumentValue("changelogFile", "db/changelog/db.changelog-master.xml")
                    .addArgumentValue("url", jdbcUrl + "&currentSchema=storedata")
                    .addArgumentValue("username", username)
                    .addArgumentValue("password", password)
                    .addArgumentValue("contexts", "!exclude-db-create")
                    .execute();
        } catch (CommandExecutionException e) {
            throw new RuntimeException("Failed to execute Liquibase migrations", e);
        }
    }
}
