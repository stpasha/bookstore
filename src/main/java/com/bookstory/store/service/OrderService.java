package com.bookstory.store.service;

import com.bookstory.store.model.Order;

import java.util.List;
import java.util.Optional;

public interface OrderService {
    Optional<Order> createOrder(Order order);
    Optional<Order> getOrder(Long id);
    List<Order> getAllOrders();
}
