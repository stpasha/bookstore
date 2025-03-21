package com.bookstory.store.service;

import com.bookstory.store.web.dto.OrderDTO;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

public interface OrderService {
    Optional<OrderDTO> createOrder(@Valid OrderDTO order);
    Optional<OrderDTO> getOrder(Long id);
    List<OrderDTO> getAllOrders();
}
