package com.bookstory.store.repository;

import com.bookstory.store.model.Product;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ProductRepository extends R2dbcRepository<Product, Long> {
    Mono<Long> countByTitleContainingIgnoreCase(String title);
    Flux<Product> findByTitleContainingIgnoreCase(String title, int limit, long offset);
    Flux<Product> findAllBy(int limit, long offset);
}
