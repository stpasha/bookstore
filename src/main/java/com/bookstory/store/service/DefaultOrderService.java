package com.bookstory.store.service;

import com.bookstory.store.model.Order;
import com.bookstory.store.repository.OrderRepository;
import com.bookstory.store.web.dto.ItemDTO;
import com.bookstory.store.web.dto.OrderDTO;
import com.bookstory.store.web.mapper.ItemMapper;
import com.bookstory.store.web.mapper.OrderMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Slf4j
@Service
@AllArgsConstructor
@Validated
public class DefaultOrderService implements OrderService {

    final private OrderRepository orderRepository;
    final private OrderMapper orderMapper;
    final private ItemMapper itemMapper;
    final private ItemService itemService;
    final private ProductService productService;

    @Override
    @Transactional
    public Mono<OrderDTO> createOrder(Mono<OrderDTO> order) {
        log.info("create order {}", order);

        return order.flatMap(orderDTO -> {
            return Flux.fromIterable(orderDTO.getItems())
                    .flatMap(itemDTO -> productService.getProduct(itemDTO.getProductId())
                            .map(productDTO -> productDTO.getPrice().multiply(BigDecimal.valueOf(itemDTO.getQuantity())))
                    )
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .flatMap(totalSum -> {
                        orderDTO.setTotal(totalSum);
                        Order orderEntity = orderMapper.toEntity(orderDTO);

                        return orderRepository.save(orderEntity)
                                .flatMap(savedOrder -> {
                                    Flux<ItemDTO> itemDTOs = Flux.fromIterable(orderDTO.getItems())
                                            .map(itemDTO -> {
                                                itemDTO.setOrderId(savedOrder.getId());
                                                return itemDTO;
                                            });

                                    return itemService.createItems(itemDTOs)
                                            .collectList()
                                            .map(savedItems -> {
                                                savedOrder.setItems(savedItems.stream()
                                                        .map(itemMapper::toEntity)
                                                        .toList());
                                                return orderMapper.toDto(savedOrder);
                                            });
                                });
                    });
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<OrderDTO> getOrder(Long id) {
        log.info("get order by id {}", id);
        return orderRepository.findById(id)
                .map(orderMapper::toDto)
                .flatMap(orderDTO -> itemService.getItemsByOrderId(Mono.just(orderDTO))
                        .collectList()
                        .doOnNext(orderDTO::setItems)
                        .thenReturn(orderDTO));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<OrderDTO> getAllOrders() {
        log.info("get all order");
        return orderRepository.findAll().map(orderMapper::toDto);
    }
}
