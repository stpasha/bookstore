package com.bookstory.store.tests.persistence;

import com.bookstory.store.annotations.StoreTestAnnotation;
import com.bookstory.store.model.Order;
import com.bookstory.store.persistence.OrderRepository;
import com.bookstory.store.util.TestDataFactory;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertAll;

@StoreTestAnnotation
public class OrderRepositoryTest {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    TestDataFactory testDataFactory;

    @Test
    @Transactional
    public void createOrder() {
        Order order = testDataFactory.createOrder();
        orderRepository.save(order);
        Assertions.assertNotNull(order.getId());
        assertAll("Check order saved",
                () -> Assertions.assertNotNull(order.getCreatedAt()),
                () -> Assertions.assertNotNull(order.getUpdatedAt()),
                () -> Assertions.assertNotNull(order.getComment()),
                () -> Assertions.assertNotNull(order.getItems())
        );
    }

    @Test
    @Transactional
    public void getAllOrders_Empty() {
        List<Order> orders = orderRepository.findAll();
        Assertions.assertTrue(orders.isEmpty());
    }

    @Test
    @Transactional
    public void getAllOrders() {
        int orderCount = 10;
        List<Order> orders = testDataFactory.createOrders(orderCount);
        orderRepository.saveAll(orders);
        assertEquals(orderCount, orderRepository.findAll().size());
    }

    @Test
    @Transactional
    public void getOrder() {
        Order order = testDataFactory.createOrder();
        Order dbOrder = orderRepository.save(order);
        Optional<Order> foundOrder = orderRepository.findById(dbOrder.getId());
        assertAll("Check order saved",
                () -> Assertions.assertTrue(foundOrder
                        .map(found -> dbOrder.getCreatedAt().equals(found.getCreatedAt())).orElse(false)),
                () -> Assertions.assertTrue(foundOrder
                        .map(found -> dbOrder.getUpdatedAt().equals(found.getUpdatedAt())).orElse(false)),
                () -> Assertions.assertTrue(foundOrder
                        .map(found -> dbOrder.getId().equals(found.getId())).orElse(false)),
                () -> Assertions.assertTrue(foundOrder
                        .map(found -> dbOrder.getComment().equals(found.getComment())).orElse(false))
        );
    }

    @Test
    @Transactional
    public void updateOrder() {
        Order order = testDataFactory.createOrder();
        orderRepository.save(order);

        order.setComment("Updated comment");
        orderRepository.save(order);

        Optional<Order> updatedOrder = orderRepository.findById(order.getId());
        Assertions.assertTrue(updatedOrder.isPresent());
        Assertions.assertEquals("Updated comment", updatedOrder.get().getComment());
    }


}
