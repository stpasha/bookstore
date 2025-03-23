package com.bookstory.store.tests.persistence;

import com.bookstory.store.annotations.StoreTestAnnotation;
import com.bookstory.store.repository.ItemRepository;
import com.bookstory.store.repository.OrderRepository;
import com.bookstory.store.repository.ProductRepository;
import com.bookstory.store.util.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.springframework.beans.factory.annotation.Autowired;

@StoreTestAnnotation
public class RepositoryTest {
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
            orderRepository.deleteAll();
            itemRepository.deleteAll();
        }

//        @Test
//        @Transactional
//        public void createItem() {
//            Order order = orderRepository.save(testDataFactory.createOrder());
//            Product product = productRepository.save(testDataFactory.createProduct());
//
//            Item item = testDataFactory.createItem(order, product);
//            itemRepository.save(item);
//
//            assertNotNull(item.getId(), "Item ID should be generated");
//            assertEquals(order.getId(), item.getOrder().getId(), "Item should be linked to the correct order");
//            assertEquals(product.getId(), item.getProduct().getId(), "Item should be linked to the correct product");
//            assertTrue(item.getQuantity() > 0, "Quantity greater than 0");
//        }
//
//        @Test
//        @Transactional
//        public void getItem() {
//            Order order = orderRepository.save(testDataFactory.createOrder());
//            Product product = productRepository.save(testDataFactory.createProduct());
//
//            Item item = itemRepository.save(testDataFactory.createItem(order, product));
//
//            Optional<Item> foundItem = itemRepository.findById(item.getId());
//            assertTrue(foundItem.isPresent(), "Item should be found");
//            assertEquals(item.getId(), foundItem.get().getId(), "IDs should match");
//            assertEquals(order.getId(), foundItem.get().getOrder().getId(), "Order IDs should match");
//            assertEquals(product.getId(), foundItem.get().getProduct().getId(), "Product IDs should match");
//        }
//
//
//        @Test
//        @Transactional
//        public void findAllItems() {
//            Order order = orderRepository.save(testDataFactory.createOrder());
//            Product product = productRepository.save(testDataFactory.createProduct());
//
//            List<Item> items = testDataFactory.createItems(5, List.of(order), List.of(product));
//            itemRepository.saveAll(items);
//
//            List<Item> retrievedItems = itemRepository.findAll();
//            assertEquals(5, retrievedItems.size(), "Should get 5 items");
//        }
//
//    }
//
//    @Nested
//    class OrderRepositoryTest {
//        @Autowired
//        private OrderRepository orderRepository;
//
//        @Autowired
//        TestDataFactory testDataFactory;
//
//        @BeforeEach
//        public void setUp() {
//            orderRepository.deleteAll();
//        }
//
//        @Test
//        @Transactional
//        public void createOrder() {
//            Order order = testDataFactory.createOrder();
//            orderRepository.save(order);
//            Assertions.assertNotNull(order.getId());
//            assertAll("Check order saved",
//                    () -> Assertions.assertNotNull(order.getCreatedAt()),
//                    () -> Assertions.assertNotNull(order.getUpdatedAt()),
//                    () -> Assertions.assertNotNull(order.getComment()),
//                    () -> Assertions.assertNotNull(order.getItems())
//            );
//        }
//
//        @Test
//        @Transactional
//        public void getAllOrders_Empty() {
//            List<Order> orders = orderRepository.findAll();
//            Assertions.assertTrue(orders.isEmpty());
//        }
//
//        @Test
//        @Transactional
//        public void getAllOrders() {
//            int orderCount = 10;
//            List<Order> orders = testDataFactory.createOrders(orderCount);
//            orderRepository.saveAll(orders);
//            Assert.assertEquals(orderCount, orderRepository.findAll().size());
//        }
//
//        @Test
//        @Transactional
//        public void getOrder() {
//            Order order = testDataFactory.createOrder();
//            Order dbOrder = orderRepository.save(order);
//            Optional<Order> foundOrder = orderRepository.findById(dbOrder.getId());
//            assertAll("Check order saved",
//                    () -> Assertions.assertTrue(foundOrder
//                            .map(found -> dbOrder.getCreatedAt().equals(found.getCreatedAt())).orElse(false)),
//                    () -> Assertions.assertTrue(foundOrder
//                            .map(found -> dbOrder.getUpdatedAt().equals(found.getUpdatedAt())).orElse(false)),
//                    () -> Assertions.assertTrue(foundOrder
//                            .map(found -> dbOrder.getId().equals(found.getId())).orElse(false)),
//                    () -> Assertions.assertTrue(foundOrder
//                            .map(found -> dbOrder.getComment().equals(found.getComment())).orElse(false))
//            );
//        }
//
//        @Test
//        @Transactional
//        public void updateOrder() {
//            Order order = testDataFactory.createOrder();
//            orderRepository.save(order);
//
//            order.setComment("Updated comment");
//            orderRepository.save(order);
//
//            Optional<Order> updatedOrder = orderRepository.findById(order.getId());
//            Assertions.assertTrue(updatedOrder.isPresent());
//            Assertions.assertEquals("Updated comment", updatedOrder.get().getComment());
//        }
//
//
//    }
//
//    @Nested
//    class ProductRepositoryTest {
//
//        @Autowired
//        private ProductRepository productRepository;
//
//        @Autowired
//        private TestDataFactory testDataFactory;
//
//        @Test
//        @Transactional
//        public void createProduct() {
//            Product product = testDataFactory.createProduct();
//            productRepository.save(product);
//
//            assertNotNull(product.getId());
//            assertFalse(product.getTitle().isBlank());
//            assertTrue(product.getPrice().compareTo(BigDecimal.ZERO) >= 0, "Price should be >= 0");
//        }
//
//        @Test
//        @Transactional
//        public void getProduct() {
//            Product product = productRepository.save(testDataFactory.createProduct());
//
//            Optional<Product> foundProduct = productRepository.findById(product.getId());
//            assertTrue(foundProduct.isPresent(), "Product should be found");
//            assertEquals(product.getId(), foundProduct.get().getId(), "ID should match");
//        }
//
//        @Test
//        @Transactional
//        public void findByTitleContainingIgnoreCase() {
//            productRepository.save(Product.builder()
//                    .title("gRPC: Up and Running. Building Cloud Native Applications with Go and Java for Docker and Kubernetes")
//                    .description("""
//                                With this practical guide, you’ll learn how this high-performance interprocess
//                                communication protocol is capable of connecting polyglot services in microservices
//                                architecture
//                                """)
//                    .price(BigDecimal.valueOf(2345.99))
//                    .quantityAvailable(10L)
//                    .build());
//
//            productRepository.save(Product.builder()
//                    .title("Practical gRPC")
//                    .description("Build highly-connected systems with a framework that can run on any platform")
//                    .price(BigDecimal.valueOf(2339.99))
//                    .quantityAvailable(5L)
//                    .build());
//
//            Page<Product> result = productRepository.findByTitleContainingIgnoreCase("gRPC", PageRequest.of(0, 10));
//            assertEquals(2, result.getTotalElements(), "Should find 2 products containing 'gRPC' in the title");
//        }
//
//        @Test
//        @Transactional
//        public void testPagination() {
//            List<Product> products = testDataFactory.createProducts(15);
//            productRepository.saveAll(products);
//
//            Page<Product> page = productRepository.findAll(PageRequest.of(0, 5));
//
//            assertEquals(5, page.getSize(), "Page should contain 5 products");
//            assertEquals(39, page.getTotalElements(), "Total elements should be 39");
//            assertEquals(8, page.getTotalPages(), "Total pages should be 8");
//        }
    }
}
