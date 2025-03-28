package com.bookstory.store.tests.persistence;

import com.bookstory.store.annotations.StoreTestAnnotation;
import com.bookstory.store.model.Item;
import com.bookstory.store.model.Order;
import com.bookstory.store.model.Product;
import com.bookstory.store.repository.ItemRepository;
import com.bookstory.store.repository.OrderRepository;
import com.bookstory.store.repository.ProductRepository;
import com.bookstory.store.tests.AbstractTest;
import com.bookstory.store.util.TestDataFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.AbstractMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@StoreTestAnnotation
@Slf4j
public class RepositoryTest extends AbstractTest {


    @Nested
    class ItemRepositoryTest {

        @Autowired
        private ItemRepository itemRepository;

        @Autowired
        private OrderRepository orderRepository;

        @Autowired
        private ProductRepository productRepository;

        @Autowired
        private TestDataFactory testDataFactory;

        @BeforeEach
        public void setUp() {
            Mono<Void> cleanUp = orderRepository.deleteAll().then(itemRepository.deleteAll());
            StepVerifier.create(cleanUp)
                    .verifyComplete();
        }

        @Test
        public void createItem() {
            Order order = testDataFactory.createOrder();
            Mono<Item> itemMono = orderRepository.save(order)
                    .flatMap(dbOrder ->
                            productRepository.findById(1L)
                                    .flatMap(product -> itemRepository.save(testDataFactory.createItem(dbOrder, product)))
                    );

            StepVerifier.create(itemMono)
                    .assertNext(item -> {
                        assertNotNull(item.getId(), "Item ID should be generated");
                        assertNotNull(item.getOrderId(), "Item should have an order ID");
                        assertTrue(item.getQuantity() > 0, "Quantity should be greater than 0");
                    })
                    .verifyComplete();
            ;
        }


        @Test
        public void getItem() {
            Order order = testDataFactory.createOrder();
            Mono<Order> orderMono = orderRepository.save(order);
            Mono<Product> productMono = productRepository.findById(1L);

            Mono<Item> itemMono = Mono.zip(orderMono, productMono)
                    .flatMap(tuple -> itemRepository.save(testDataFactory.createItem(tuple.getT1(), tuple.getT2())));

            StepVerifier.create(itemMono.flatMap(item -> itemRepository.findById(item.getId())))
                    .assertNext(foundItem -> {
                        assertNotNull(foundItem, "Item should be found");
                        assertNotNull(foundItem.getId(), "Item ID should not be null");
                        assertNotNull(foundItem.getOrderId(), "Order should not be null");
                        assertNotNull(foundItem.getProductId(), "Product should not be null");
                    })
                    .verifyComplete();
        }


        @Test
        public void findAllItems() {
            Mono<Order> orderMono = orderRepository.save(testDataFactory.createOrder());

            Flux<Long> ids = Flux.just(1L, 2L, 3L, 4L, 5L);

            Flux<Product> productFlux = ids.flatMap(productRepository::findById);

            Flux<Item> itemFlux = orderMono.flatMapMany(order ->
                    productFlux.flatMap(product ->
                            itemRepository.save(testDataFactory.createItem(order, product))
                    )
            );

            StepVerifier.create(itemFlux.thenMany(itemRepository.findAll().collectList()))
                    .assertNext(foundItems -> assertEquals(5, foundItems.size(), "Should get 5 items"))
                    .verifyComplete();
        }
    }


    @Nested
    class OrderRepositoryTest {
        @Autowired
        private OrderRepository orderRepository;

        @Autowired
        TestDataFactory testDataFactory;

        @BeforeEach
        public void setUp() {
            StepVerifier.create(orderRepository.deleteAll())
                    .verifyComplete();
        }

        @Test
        public void createOrder() {
            Order order = testDataFactory.createOrder();
            Mono<Order> orderMono = orderRepository.save(order);
            StepVerifier.create(orderMono).assertNext(orderRes -> {
                assertAll("Check order saved",
                        () -> assertNotNull(orderRes.getCreatedAt()),
                        () -> assertNotNull(orderRes.getUpdatedAt()),
                        () -> assertNotNull(orderRes.getComment()),
                        () -> assertNotNull(orderRes.getItems())
                );
            }).verifyComplete();
            StepVerifier.create(orderRepository.deleteAll()).verifyComplete();
        }


        @Test
        public void getAllOrders_Empty() {
            StepVerifier.create(orderRepository.findAll())
                    .expectNextCount(0)
                    .verifyComplete();
        }

        @Test
        public void getAllOrders() {
            int orderCount = 10;
            List<Order> orders = testDataFactory.createOrders(orderCount);
            StepVerifier.create(orderRepository.saveAll(Flux.fromIterable(orders))).expectNextCount(orders.size()).verifyComplete();
        }

        @Test
        public void getOrder() {
            Order order = testDataFactory.createOrder();

            Mono<AbstractMap.SimpleEntry<Order, Order>> orderMono = orderRepository.save(order)
                    .flatMap(savedOrder -> orderRepository.findById(savedOrder.getId())
                            .map(foundOrder -> new AbstractMap.SimpleEntry<>(savedOrder, foundOrder)));

            StepVerifier.create(orderMono)
                    .assertNext(entry -> {
                        Order dbOrder = entry.getKey();
                        Order foundOrder = entry.getValue();

                        assertAll("Check order saved",
                                () -> assertEquals(dbOrder.getCreatedAt().truncatedTo(ChronoUnit.MILLIS), foundOrder.getCreatedAt().truncatedTo(ChronoUnit.MILLIS), "CreatedAt should match"),
                                () -> assertEquals(dbOrder.getUpdatedAt().truncatedTo(ChronoUnit.MILLIS), foundOrder.getUpdatedAt().truncatedTo(ChronoUnit.MILLIS), "UpdatedAt should match"),
                                () -> assertEquals(dbOrder.getId(), foundOrder.getId(), "ID should match"),
                                () -> assertEquals(dbOrder.getComment(), foundOrder.getComment(), "Comment should match")
                        );
                    })
                    .verifyComplete();
        }

    }


    @Nested
    class ProductRepositoryTest {

        @Autowired
        private ProductRepository productRepository;

        @Autowired
        private TestDataFactory testDataFactory;

        @Test
        public void createProduct() {
            Product product = testDataFactory.createProduct();
            StepVerifier.create(productRepository.save(product)).assertNext(savedProd -> {
                assertNotNull(savedProd.getId());
                assertFalse(savedProd.getTitle().isBlank());
                assertTrue(savedProd.getPrice().compareTo(BigDecimal.ZERO) >= 0, "Price should be >= 0");
            }).verifyComplete();

        }

        @Test
        public void getProduct() {
            Mono<Product> foundProduct = productRepository.findById(1L);
            StepVerifier.create(foundProduct).assertNext(savedProd -> {
                assertNotNull(savedProd, "Product should be found");
                assertEquals(1L, savedProd.getId(), "ID should match");
            }).verifyComplete();
        }

        @Test
        public void findByTitleContainingIgnoreCase() {
            Flux<Product> result = productRepository.findByTitleContainingIgnoreCase("Java", PageRequest.of(0, 10));
            StepVerifier.create(result).expectNextCount(2).verifyComplete();
        }

        @Test
        public void testPagination() {
            Pageable pageable = PageRequest.of(0, 5);

            Mono<Long> totalCountMono = productRepository.count();
            Flux<Product> productFlux = productRepository.findAllBy(pageable);

            StepVerifier.create(Mono.zip(productFlux.collectList(), totalCountMono))
                    .assertNext(tuple -> {
                        List<Product> products = tuple.getT1();
                        long totalCount = tuple.getT2();
                        int totalPages = (int) Math.ceil((double) totalCount / pageable.getPageSize());

                        assertEquals(5, products.size(), "Page should contain 5 products");
                        assertTrue(totalPages > 0, "Total pages should be greater than 0");
                    })
                    .verifyComplete();
        }
    }
}
