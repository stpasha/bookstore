package com.bookstory.store.config;

import com.bookstory.store.api.AccountControllerApi;
import com.bookstory.store.client.ApiClient;
import com.bookstory.store.util.PageableProductKeyGenerator;
import com.bookstory.store.web.dto.ProductDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;

import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Configuration
@EnableCaching
public class ApplicationConfig {

    @Value("${billing.base-url}")
    private String billingBaseUrl;

    @Bean
    public PageableProductKeyGenerator pageableProductKeyGenerator() {
        return new PageableProductKeyGenerator();
    }

    /**
     * С помощью бинов {@link RedisCacheManagerBuilderCustomizer} мы можем добавлять
     * кастомизации, которые будут использованы при построении бина {@link RedisCacheManager}.
     * В кастомайзерах можно добавлять общие конфигурации для всех кешей и отдельные по имени кеша.
     * Можно объявлять несколько кастомайзеров, они привязываются списком в автоконфигурации по созданию {@link RedisCacheManager}.
     * В примере ниже добавлен кастомайзер, добавляющий в билдер конфигурацию кеша `products`.
     */
    @Bean
    public RedisCacheManagerBuilderCustomizer productsCacheCustomizer() {
        return builder -> builder.withCacheConfiguration(
                "products",                                         // Имя кеша
                RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.of(10, ChronoUnit.MINUTES))  // TTL
                        .serializeValuesWith(                          // Сериализация JSON
                                RedisSerializationContext.SerializationPair.fromSerializer(
                                        new Jackson2JsonRedisSerializer<>(ProductDTO.class)
                                )
                        )
        );
    }

    @Bean
    public AccountControllerApi accountControllerApi(ApiClient billingOAuth2ApiClient) {
        return new AccountControllerApi(billingOAuth2ApiClient);
    }

    @Bean
    public ApiClient billingOAuth2ApiClient(WebClient billingWebClient) {
        ApiClient apiClient = new ApiClient(billingWebClient);
        apiClient.setBasePath(billingBaseUrl);
        return apiClient;
    }

}
