package com.bookstory.store.service;

import com.bookstory.store.model.Order;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DefaultOrderService implements OrderService {
    @Override
    public Optional<Order> createOrder(Order order) {
        return Optional.empty();
    }

    @Override
    public Optional<Order> getOrder(Long id) {
        return Optional.empty();
    }

    @Override
    public List<Order> getAllOrders() {
        return List.of();
    }
}
