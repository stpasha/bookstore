package com.bookstory.store.tests.web;

import com.bookstory.store.annotations.StoreTestAnnotation;
import com.bookstory.store.service.OrderService;
import com.bookstory.store.util.TestDataFactory;
import com.bookstory.store.web.dto.OrderDTO;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@StoreTestAnnotation
@AutoConfigureMockMvc
class OrderControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderService orderService;

    @Autowired
    private TestDataFactory testDataFactory;

    @Autowired
    private EntityManager entityManager;

    @Test
    void shouldReturnOrdersList() throws Exception {
        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(view().name("orders"))
                .andExpect(model().attributeExists("orders"));
    }

    @Test
    void shouldReturnOrderDetails() throws Exception {
        OrderDTO order = orderService.createOrder(testDataFactory.createOrderDTO()).orElseThrow();
        mockMvc.perform(get("/orders/{id}", order.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("order"))
                .andExpect(model().attributeExists("order"));
    }

    @Test
    void shouldReturnErrorWhenOrderNotFound() throws Exception {
        mockMvc.perform(get("/orders/{id}", 9999L))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("error"));
    }

}
