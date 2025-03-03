package com.bookstory.store.service;

import com.bookstory.store.model.Order;
import com.bookstory.store.persistence.ItemRepository;
import com.bookstory.store.persistence.OrderRepository;
import com.bookstory.store.persistence.ProductRepository;
import com.bookstory.store.web.dto.OrderDTO;
import com.bookstory.store.web.mapper.OrderMapper;
import org.springframework.transaction.annotation.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class DefaultOrderService implements OrderService {

    final private OrderRepository orderRepository;
    final private OrderMapper orderMapper;
    final private ItemRepository itemRepository;
    final private ProductRepository productRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Optional<OrderDTO> createOrder(OrderDTO order) {
        log.info("create order {}", order);
        Order orderEntity = orderMapper.toEntity(order);
        if (orderEntity.getItems() != null) {
            orderEntity.getItems().forEach(item -> {
                item.setOrder(orderEntity);
                item.setProduct(productRepository.findById(item.getProduct().getId()).get());
            });
        }
        Order savedOrder = orderRepository.save(orderEntity);
        orderEntity.getItems().forEach(item -> {
            itemRepository.save(item);
        });
        return Optional.of(orderMapper.toDto(savedOrder));
    }

    @Override
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
