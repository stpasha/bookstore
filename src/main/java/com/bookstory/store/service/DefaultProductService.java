package com.bookstory.store.service;

import com.bookstory.store.model.Product;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public class DefaultProductService implements ProductService {

    @Override
    public Pageable getAllProducts() {
        return null;
    }

    @Override
    public Optional<Product> getProduct(Long id) {
        return Optional.empty();
    }

    @Override
    public void addProducts(List<Product> productList) {

    }
}
