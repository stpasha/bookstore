package com.bookstory.store.service;

import com.bookstory.store.web.dto.NewProductDTO;
import com.bookstory.store.web.dto.ProductDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.stream.Stream;

public interface ProductService {
    Page<ProductDTO> getAllProducts(String title, Pageable pageable);

    Optional<ProductDTO> getProduct(Long id);

    void addProducts(Stream<NewProductDTO> productList);
}
