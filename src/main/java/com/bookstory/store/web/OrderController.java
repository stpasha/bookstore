package com.bookstory.store.web;

import com.bookstory.store.service.OrderService;
import com.bookstory.store.web.dto.OrderDTO;
import com.bookstory.store.web.mapper.OrderMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/orders")
public class OrderController {

    private OrderService orderService;
    private OrderMapper orderMapper;

    public OrderController(OrderService orderService, OrderMapper orderMapper) {
        this.orderService = orderService;
        this.orderMapper = orderMapper;
    }

    @GetMapping
    public String listOrders(Model model) {
        model.addAttribute("orders", orderMapper.toDtoList(orderService.getAllOrders()));
        return "orders";
    }

    @PostMapping
    public String createOrder(@ModelAttribute OrderDTO order) {

    }
}
