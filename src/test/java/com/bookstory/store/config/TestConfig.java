package com.bookstory.store.config;

import com.bookstory.store.util.TestDataFactory;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

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

}

