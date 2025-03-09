package com.bookstory.store.tests.persistence;

import com.bookstory.store.annotations.StoreTestAnnotation;
import com.bookstory.store.model.Item;
import com.bookstory.store.model.Order;
import com.bookstory.store.model.Product;
import com.bookstory.store.persistence.ItemRepository;
import com.bookstory.store.persistence.OrderRepository;
import com.bookstory.store.persistence.ProductRepository;
import com.bookstory.store.util.TestDataFactory;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@StoreTestAnnotation
public class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TestDataFactory testDataFactory;

    @Test
    @Transactional
    public void createItem() {
        Order order = orderRepository.save(testDataFactory.createOrder());
        Product product = productRepository.save(testDataFactory.createProduct());

        Item item = testDataFactory.createItem(order, product);
        itemRepository.save(item);

        assertNotNull(item.getId(), "Item ID should be generated");
        assertEquals(order.getId(), item.getOrder().getId(), "Item should be linked to the correct order");
        assertEquals(product.getId(), item.getProduct().getId(), "Item should be linked to the correct product");
        assertTrue(item.getQuantity() > 0, "Quantity greater than 0");
    }

    @Test
    @Transactional
    public void getItem() {
        Order order = orderRepository.save(testDataFactory.createOrder());
        Product product = productRepository.save(testDataFactory.createProduct());

        Item item = itemRepository.save(testDataFactory.createItem(order, product));

        Optional<Item> foundItem = itemRepository.findById(item.getId());
        assertTrue(foundItem.isPresent(), "Item should be found");
        assertEquals(item.getId(), foundItem.get().getId(), "IDs should match");
        assertEquals(order.getId(), foundItem.get().getOrder().getId(), "Order IDs should match");
        assertEquals(product.getId(), foundItem.get().getProduct().getId(), "Product IDs should match");
    }


    @Test
    @Transactional
    public void findAllItems() {
        Order order = orderRepository.save(testDataFactory.createOrder());
        Product product = productRepository.save(testDataFactory.createProduct());

        List<Item> items = testDataFactory.createItems(5, List.of(order), List.of(product));
        itemRepository.saveAll(items);

        List<Item> retrievedItems = itemRepository.findAll();
        assertEquals(5, retrievedItems.size(), "Should get 5 items");
    }

}
