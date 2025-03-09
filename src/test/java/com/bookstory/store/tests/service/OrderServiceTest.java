package com.bookstory.store.tests.service;

import com.bookstory.store.annotations.StoreTestAnnotation;
import com.bookstory.store.model.Order;
import com.bookstory.store.persistence.OrderRepository;
import com.bookstory.store.persistence.ProductRepository;
import com.bookstory.store.service.OrderService;
import com.bookstory.store.util.TestDataFactory;
import com.bookstory.store.web.dto.OrderDTO;
import com.bookstory.store.web.mapper.OrderMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@StoreTestAnnotation
public class OrderServiceTest {

    @Autowired
    private TestDataFactory testDataFactory;

    @Autowired
    private OrderService orderService;

    @MockitoBean
    private OrderRepository orderRepository;

    @MockitoBean
    private ProductRepository productRepository;

    @Autowired
    OrderMapper orderMapper;

    @Test
    public void createOrder() {
        OrderDTO orderDTO = testDataFactory.createOrderDTO();
        Order order = orderMapper.toEntity(orderDTO);

        order.getItems().forEach(item ->
                when(productRepository.findById(item.getProduct().getId()))
                        .thenReturn(Optional.of(item.getProduct()))
        );

        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Optional<OrderDTO> outOrderDTO = orderService.createOrder(orderDTO);

        assertTrue(outOrderDTO.isPresent(), "Order should be created");
        OrderDTO result = outOrderDTO.get();

        assertEquals(order.getId(), result.getId());
        assertEquals(order.getCreatedAt(), result.getCreatedAt());
        assertEquals(order.getUpdatedAt(), result.getUpdatedAt());
        assertEquals(order.getComment(), result.getComment());

        verify(orderRepository, times(1)).save(any(Order.class));
        verify(productRepository, times(order.getItems().size())).findById(anyLong());
    }

    @Test
    public void createOrderThrowsWhenProductNotFound() {
        OrderDTO orderDTO = testDataFactory.createOrderDTO();

        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(orderDTO));

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    public void getOrder() {
        OrderDTO orderDTO = testDataFactory.createOrderDTO();
        Order order = orderMapper.toEntity(orderDTO);

        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        Optional<OrderDTO> outOrderDTO = orderService.getOrder(orderDTO.getId());

        assertTrue(outOrderDTO.isPresent());
        OrderDTO result = outOrderDTO.get();

        assertEquals(orderDTO.getId(), result.getId());
        assertEquals(orderDTO.getCreatedAt(), result.getCreatedAt());
        assertEquals(orderDTO.getUpdatedAt(), result.getUpdatedAt());
        assertEquals(orderDTO.getComment(), result.getComment());

        verify(orderRepository, times(1)).findById(order.getId());
    }

    @Test
    public void getOrders() {
        List<OrderDTO> orderDTOs = testDataFactory.createOrderDTOs(5);
        List<Order> orders = orderDTOs.stream().map(orderMapper::toEntity).toList();

        when(orderRepository.findAll()).thenReturn(orders);

        List<OrderDTO> outOrderDTOs = orderService.getAllOrders();

        assertEquals(orderDTOs.size(), outOrderDTOs.size());

        verify(orderRepository, times(1)).findAll();
    }
}
