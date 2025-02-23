package com.bookstory.store.service;

import com.bookstory.store.model.Product;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    Pageable getAllProducts();
    Optional<Product> getProduct(Long id);
    void addProducts(List<Product> productList);
}
