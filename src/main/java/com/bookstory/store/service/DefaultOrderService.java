package com.bookstory.store.service;

import com.bookstory.store.model.Order;
import com.bookstory.store.persistence.OrderRepository;
import com.bookstory.store.persistence.ProductRepository;
import com.bookstory.store.web.dto.OrderDTO;
import com.bookstory.store.web.mapper.OrderMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class DefaultOrderService implements OrderService {

    final private OrderRepository orderRepository;
    final private OrderMapper orderMapper;
    final private ProductRepository productRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Optional<OrderDTO> createOrder(OrderDTO order) {
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
        Order savedOrder = orderRepository.save(orderEntity);
        return Optional.of(orderMapper.toDto(savedOrder));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrderDTO> getOrder(Long id) {
        log.info("get order by id {}", id);
        return orderRepository.findById(id).map(orderMapper::toDto);
    }

    @Override
    public List<OrderDTO> getAllOrders() {
        log.info("get all order");
        return orderRepository.findAll().stream().map(orderMapper::toDto).toList();
    }
}
