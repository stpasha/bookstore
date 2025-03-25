package com.bookstory.store.web;

import com.bookstory.store.service.OrderService;
import com.bookstory.store.web.dto.CartDTO;
import com.bookstory.store.web.dto.OrderDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import reactor.core.publisher.Mono;

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
    public Mono<String> listOrders(Model model) {
        return orderService.getAllOrders().collectList().map(allOrders -> {
            log.info("list orders");
            model.addAttribute("orders", allOrders);
            return "orders";
        });
    }

    @GetMapping("/{id}")
    public Mono<String> getOrder(Model model, @PathVariable("id") Long id) {
        return orderService.getOrder(id).map(orderDTO -> {
            model.addAttribute("order", orderDTO);
            log.info("found order: {}", orderDTO);
            return "order";
        }).onErrorResume((e) -> {
            log.warn("No such order id {} error {} ", id, e.getMessage());
            return Mono.empty();
        });
    }

    @PostMapping
    public Mono<String> createOrder(@SessionAttribute("cart") CartDTO cartDTO,
                                    @RequestParam(value = "comment", defaultValue = "")
                                    String comment,
                              Model model,  SessionStatus sessionStatus) {
        if (cartDTO.getItems().isEmpty()) {
            return Mono.just("redirect:/products");
        }

        return Mono.fromSupplier(() -> OrderDTO.builder()
                        .comment(comment)
                        .items(cartDTO.getItems().values().stream().toList())
                        .build())
                .flatMap(orderDTO -> orderService.createOrder(Mono.just(orderDTO)))
                .doOnNext(createdOrder -> {
                    log.info("Created order: {}", createdOrder);
                    model.addAttribute("order", createdOrder);
                    model.addAttribute("newOrder", true);
                    sessionStatus.setComplete();
                })
                .thenReturn("order")
                .onErrorResume(e -> {
                    model.addAttribute("error", "Order is not created");
                    log.error("Order is not created: {}", e.getMessage());
                    sessionStatus.setComplete();
                    return Mono.just("error");
                });
    }
}
