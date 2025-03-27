package com.bookstory.store.service;

import com.bookstory.store.web.dto.OrderDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OrderService {
    Mono<OrderDTO> createOrder(Mono<OrderDTO> order);

    Mono<OrderDTO> getOrder(Long id);

    Flux<OrderDTO> getAllOrders();
}
