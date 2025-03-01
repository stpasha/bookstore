package com.bookstory.store.service;

import com.bookstory.store.model.Product;
import com.bookstory.store.persistence.ProductRepository;
import com.bookstory.store.web.dto.ProductDTO;
import com.bookstory.store.web.mapper.ProductMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class DefaultProductService implements ProductService {

    private final ProductRepository repository;

    private final ProductMapper productMapper;

    @Override
    public Page<ProductDTO> getAllProducts(String title, Pageable pageable) {
        Page<Product> products;
        if (Objects.isNull(title) || title.isBlank()) {
            log.info("get all products no filter {}", pageable);
            products = repository.findAll(pageable);
        } else {
            log.info("get all products filter {} page {}", title, pageable);
            products = repository.findByNameContainingIgnoreCase(title, pageable);
        }
        return products.map(productMapper::toDto);
    }

    @Override
    public Optional<ProductDTO> getProduct(Long id) {
        log.info("get product id {}", id);
        return repository.findById(id)
                .map(productMapper::toDto);
    }

    @Override
    public void addProducts(List<ProductDTO> productList) {
        //TODO implement file loading
    }
}
