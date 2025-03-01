package com.bookstory.store.service;

import com.bookstory.store.model.Product;
import com.bookstory.store.persistence.ProductRepository;
import com.bookstory.store.web.dto.ProductDTO;
import com.bookstory.store.web.mapper.ProductMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class DefaultProductService implements ProductService {

    private final ProductRepository repository;

    private final ProductMapper productMapper;

    public DefaultProductService(ProductRepository repository, ProductMapper productMapper) {
        this.repository = repository;
        this.productMapper = productMapper;
    }

    @Override
    public Page<ProductDTO> getAllProducts(String title, Pageable pageable) {
        Page<Product> products;
        if (Objects.isNull(title) || title.isBlank()) {
            products = repository.findAll(pageable);
        } else {
            products = repository.findByNameContainingIgnoreCase(title, pageable);
        }
        return products.map(productMapper::toDto);
    }

    @Override
    public Optional<ProductDTO> getProduct(Long id) {
        ProductDTO productDTO = repository.findById(id)
                .map(productMapper::toDto)
                .orElseGet(() -> ProductDTO.builder().description("Вернитесь на страницу с товарами").title("Выбран некорректный товар").build());
        return Optional.of(productDTO);
    }

    @Override
    public void addProducts(List<ProductDTO> productList) {

    }
}
