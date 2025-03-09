package com.bookstory.store.tests.persistence;

import com.bookstory.store.annotations.StoreTestAnnotation;
import com.bookstory.store.model.Product;
import com.bookstory.store.persistence.ProductRepository;
import com.bookstory.store.util.TestDataFactory;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@StoreTestAnnotation
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TestDataFactory testDataFactory;

    @Test
    @Transactional
    public void createProduct() {
        Product product = testDataFactory.createProduct();
        productRepository.save(product);

        assertNotNull(product.getId());
        assertFalse(product.getTitle().isBlank());
        assertTrue(product.getPrice().compareTo(BigDecimal.ZERO) >= 0, "Price should be >= 0");
    }

    @Test
    @Transactional
    public void getProduct() {
        Product product = productRepository.save(testDataFactory.createProduct());

        Optional<Product> foundProduct = productRepository.findById(product.getId());
        assertTrue(foundProduct.isPresent(), "Product should be found");
        assertEquals(product.getId(), foundProduct.get().getId(), "ID should match");
    }

    @Test
    @Transactional
    public void findByTitleContainingIgnoreCase() {
        productRepository.save(Product.builder()
                .title("gRPC: Up and Running. Building Cloud Native Applications with Go and Java for Docker and Kubernetes")
                .description("""
                                With this practical guide, you’ll learn how this high-performance interprocess 
                                communication protocol is capable of connecting polyglot services in microservices
                                architecture
                                """)
                .price(BigDecimal.valueOf(2345.99))
                .quantityAvailable(10L)
                .build());

        productRepository.save(Product.builder()
                .title("Practical gRPC")
                .description("Build highly-connected systems with a framework that can run on any platform")
                .price(BigDecimal.valueOf(2339.99))
                .quantityAvailable(5L)
                .build());

        Page<Product> result = productRepository.findByTitleContainingIgnoreCase("gRPC", PageRequest.of(0, 10));
        assertEquals(2, result.getTotalElements(), "Should find 2 products containing 'gRPC' in the title");
    }

    @Test
    @Transactional
    public void testPagination() {
        List<Product> products = testDataFactory.createProducts(15);
        productRepository.saveAll(products);

        Page<Product> page = productRepository.findAll(PageRequest.of(0, 5));

        assertEquals(5, page.getSize(), "Page should contain 5 products");
        assertEquals(39, page.getTotalElements(), "Total elements should be 39");
        assertEquals(8, page.getTotalPages(), "Total pages should be 8");
    }
}
