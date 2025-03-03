package com.bookstory.store.web;

import com.bookstory.store.service.OrderService;
import com.bookstory.store.web.dto.CartDTO;
import com.bookstory.store.web.dto.OrderDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/orders")
@Slf4j
public class OrderController {

    final private OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public String listOrders(Model model) {
        log.info("list orders");
        model.addAttribute("orders", orderService.getAllOrders());
        return "orders";
    }

    @GetMapping("/{id}")
    public String getOrder(Model model, @PathVariable("id") Long id) {
        return orderService.getOrder(id).map(orderDTO -> {
            model.addAttribute("order", orderDTO);
            log.info("found order: {}", orderDTO);
            return "order";
        }).orElseGet(() -> {
            model.addAttribute("error", "Order is not found");
            log.error("no order found by id {}", id);
            return "error";
        });
    }

    @PostMapping
    public String createOrder(@SessionAttribute("cart") CartDTO cart, Model model) {
        OrderDTO order = new OrderDTO();
        order.setItems(cart.getItems().values().stream().toList());
        order.setComment("order");
        return orderService.createOrder(order).map(orderDTO -> {
            log.info("created order: {}", orderDTO);
            model.addAttribute("order", orderDTO);
            model.addAttribute("newOrder", true);
            return "order";
        }).orElseGet(() -> {
            model.addAttribute("error", "Order is not created");
            log.error("Order is not created {}", order);
            return "error";
        });
    }
}
