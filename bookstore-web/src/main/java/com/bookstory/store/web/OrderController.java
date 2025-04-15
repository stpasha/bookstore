package com.bookstory.store.web;

import com.bookstory.store.exception.ErrorDetails;
import com.bookstory.store.service.OrderService;
import com.bookstory.store.web.dto.CartDTO;
import com.bookstory.store.web.dto.OrderDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.reactive.result.view.Rendering;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@RequestMapping("/orders")
@Slf4j
@SessionAttributes("cart")
public class OrderController {

    final private OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public  Mono<Rendering> listOrders() {
        return orderService.getAllOrders().collectList().map(allOrders -> {
            log.info("list orders");
            return Rendering.view("orders").modelAttribute("orders", allOrders).build();
        });
    }

    @GetMapping("/{id}")
    public Mono<Rendering> getOrder(@PathVariable("id") Long id) {
        return orderService.getOrder(id).map(orderDTO -> {
            log.info("found order: {}", orderDTO);
            return Rendering.view("order")
                    .modelAttribute("order", orderDTO).build();
        });
    }

    @PostMapping
    public Mono<Rendering> createOrder(@SessionAttribute("cart") CartDTO cartDTO,
                                       ServerWebExchange exchange,
                                       SessionStatus sessionStatus) {
        if (cartDTO.getItems().isEmpty()) {
            return Mono.just(Rendering.redirectTo("/products").build());
        }

        return exchange.getFormData()
                .map(formData -> Optional.ofNullable(formData.getFirst("comment")).orElse(""))
                .flatMap(comment -> {
                    OrderDTO orderDTO = OrderDTO.builder()
                            .comment(comment)
                            .userId(cartDTO.getAccountDTO().getUserId())
                            .items(cartDTO.getItems().values().stream().toList())
                            .build();

                    return getRenderingOrder(sessionStatus, orderDTO);
                })
                .onErrorResume(e -> {
                    log.error("Order is not created: {}", e.getMessage());
                    return Mono.just(Rendering.view("error")
                            .modelAttribute("errorDetails", new ErrorDetails(LocalDateTime.now(), e.getMessage(),
                                    exchange.getRequest().getURI().toString())).build());
                });
    }

    private Mono<Rendering> getRenderingOrder(SessionStatus sessionStatus, OrderDTO orderDTO) {
        return orderService.createOrder(Mono.just(orderDTO))
                .flatMap(createdOrder -> {
                    sessionStatus.setComplete();
                    return Mono.just(Rendering.view("order")
                            .modelAttribute("order", createdOrder)
                            .modelAttribute("newOrder", true)
                            .build());
                });
    }
}
