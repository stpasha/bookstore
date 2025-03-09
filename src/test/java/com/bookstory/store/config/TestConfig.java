package com.bookstory.store.config;

import com.bookstory.store.util.TestDataFactory;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import javax.sql.DataSource;

@TestConfiguration
@Slf4j
public class TestConfig {

    @Bean
    public Faker faker() {
        return new Faker();
    }

    @Bean
    public TestDataFactory testDataFactory(Faker faker) {
        return new TestDataFactory(faker);
    }

    private static final PostgreSQLContainer<?> POSTGRES_CONTAINER = new PostgreSQLContainer<>(DockerImageName.parse("postgres:16"))
            .withDatabaseName("testdb")
            .withUsername("baseadm")
            .withPassword("testpass")
            .withInitScript("init-script.sql");

    static {
        POSTGRES_CONTAINER.start();
        log.info("co {}", POSTGRES_CONTAINER);
    }


    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl(POSTGRES_CONTAINER.getJdbcUrl());
        dataSource.setUsername(POSTGRES_CONTAINER.getUsername());
        dataSource.setPassword(POSTGRES_CONTAINER.getPassword());
        return dataSource;
    }
}

