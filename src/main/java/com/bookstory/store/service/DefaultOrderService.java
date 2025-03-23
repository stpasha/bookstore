package com.bookstory.store.service;

import com.bookstory.store.model.Order;
import com.bookstory.store.repository.OrderRepository;
import com.bookstory.store.repository.ProductRepository;
import com.bookstory.store.web.dto.OrderDTO;
import com.bookstory.store.web.mapper.OrderMapper;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@AllArgsConstructor
@Validated
public class DefaultOrderService implements OrderService {

    final private OrderRepository orderRepository;
    final private OrderMapper orderMapper;
    final private ProductRepository productRepository;

    @Override
    @Transactional
    public Mono<OrderDTO> createOrder(@Valid OrderDTO order) {
        log.info("create order {}", order);
        Order orderEntity = orderMapper.toEntity(order);
        if (orderEntity.getItems() != null) {
            orderEntity.getItems().forEach(item -> {
                item.setOrder(orderEntity);
                item.setProduct(productRepository.findById(item.getProduct().getId())
                        .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + item.getProduct().getId())));
            });
        }
        orderEntity.getItems().forEach(item -> {
            Long quantity = item.getQuantity();
            Long availableQuatity = item.getProduct().getQuantityAvailable();
            if (quantity != null && availableQuatity != null) {
                item.getProduct().setQuantityAvailable(Math.subtractExact(availableQuatity, quantity));
            }
        });
        return orderRepository.save(orderEntity).map(orderMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<OrderDTO> getOrder(Long id) {
        log.info("get order by id {}", id);
        return orderRepository.findById(id).map(orderMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<OrderDTO> getAllOrders() {
        log.info("get all order");
        return orderRepository.findAll().map(orderMapper::toDto);
    }
}
