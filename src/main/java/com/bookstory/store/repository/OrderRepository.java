package com.bookstory.store.repository;

import com.bookstory.store.model.Order;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface OrderRepository extends R2dbcRepository<Order, Long> {

    @NonNull
    Mono<Order> findById(@NonNull Long id);

    @NonNull
    Flux<Order> findAll();
}
