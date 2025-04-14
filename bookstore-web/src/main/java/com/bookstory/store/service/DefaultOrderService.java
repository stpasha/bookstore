package com.bookstory.store.service;

import com.bookstory.store.api.AccountControllerApi;
import com.bookstory.store.domain.PaymentDTO;
import com.bookstory.store.model.Order;
import com.bookstory.store.repository.OrderRepository;
import com.bookstory.store.util.ObjectValidator;
import com.bookstory.store.web.dto.ItemDTO;
import com.bookstory.store.web.dto.OrderDTO;
import com.bookstory.store.web.mapper.ItemMapper;
import com.bookstory.store.web.mapper.OrderMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.resource.NoResourceFoundException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Slf4j
@Service
@AllArgsConstructor
public class DefaultOrderService implements OrderService {

    final private OrderRepository orderRepository;
    final private OrderMapper orderMapper;
    final private ItemMapper itemMapper;
    final private ItemService itemService;
    final private ProductService productService;
    final private ObjectValidator objectValidator;
    final private AccountControllerApi accountControllerApi;


    @Override
    @Transactional
    @Secured("USER")
    public Mono<OrderDTO> createOrder(Mono<OrderDTO> order) {
        return order.flatMap(orderDTO -> {
            log.info("Creating order: {}", orderDTO);
            objectValidator.validate(orderDTO);
            if (orderDTO.getItems() == null || orderDTO.getItems().isEmpty()) {
                return Mono.error(new IllegalArgumentException("Order must contain at least one item."));
            }
            return Flux.fromIterable(orderDTO.getItems())
                    .flatMap(itemDTO -> productService.getProduct(itemDTO.getProductId())
                            .switchIfEmpty(Mono.error(new IllegalArgumentException("Product not found: " + itemDTO.getProductId())))
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

                                    return itemService.createItems(itemDTOs).flatMap(itemDTO -> productService.updateProductQuantity(Mono.just(itemDTO)).thenReturn(itemDTO))
                                            .collectList()
                                            .map(savedItems -> {
                                                savedOrder.setItems(savedItems.stream()
                                                        .map(itemMapper::toEntity)
                                                        .toList());
                                                return orderMapper.toDto(savedOrder);
                                            }).flatMap(finalOrder -> accountControllerApi.getAccountByUserId(finalOrder.getUserId())
                                                    .flatMap(accountDTO -> {
                                                        return accountControllerApi.createAccountPayment(
                                                                        accountDTO.getId(), new PaymentDTO().accountId(accountDTO.getId()).amount(totalSum));
                                                    }).thenReturn(finalOrder)
                                                    .onErrorResume(ex -> {
                                                        log.error("Failed to create payment for order {}: {}", finalOrder.getId(), ex.getMessage());
                                                        return Mono.error(new RuntimeException("Failed to create payment: " + ex.getMessage()));
                                                    })
                                            );
                                });
                    });
        });
    }

    @Override
    @Secured("USER")
    public Mono<OrderDTO> getOrder(Long id) {
        log.info("get order by id {}", id);
        return orderRepository.findById(id)
                .map(orderMapper::toDto)
                .flatMap(orderDTO -> itemService.getItemsByOrderId(Mono.just(orderDTO))
                        .collectList()
                        .doOnNext(orderDTO::setItems)
                        .thenReturn(orderDTO))
                .switchIfEmpty(Mono.error(new NoResourceFoundException("Order with id " + id + " not found")));
    }

    @Override
    @Secured("USER")
    public Flux<OrderDTO> getAllOrders() {
        log.info("get all order");
        return orderRepository.findAll().map(orderMapper::toDto);
    }
}
