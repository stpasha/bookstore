package com.bookstory.store.service;

import com.bookstory.store.model.Product;
import com.bookstory.store.persistence.ProductRepository;
import com.bookstory.store.web.dto.NewProductDTO;
import com.bookstory.store.web.dto.ProductDTO;
import com.bookstory.store.web.mapper.NewProductMapper;
import com.bookstory.store.web.mapper.ProductMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
@Slf4j
@Validated
public class DefaultProductService implements ProductService {

    private final ProductRepository repository;

    private final ProductMapper productMapper;

    private final NewProductMapper newProductMapper;

    private final ImageService imageService;

    @Override
    public Page<ProductDTO> getAllProducts(String title, Pageable pageable) {
        Page<Product> products;
        if (Objects.isNull(title) || title.isBlank()) {
            log.info("get all products no filter {}", pageable);
            products = repository.findAll(pageable);
        } else {
            log.info("get all products filter {} page {}", title, pageable);
            products = repository.findByTitleContainingIgnoreCase(title, pageable);
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
    @Transactional(rollbackFor = Exception.class)
    public void addProducts(List<@Valid NewProductDTO> productList) {
        List<Product> productEntities = productList.stream()
                .map(newProductDTO -> {
                    try {
                        if (newProductDTO.getBaseImage() != null && !newProductDTO.getBaseImage().isBlank()) {
                            String imageName = imageService.saveImage(newProductDTO.getImageName(),
                                    Base64.getDecoder().decode(newProductDTO.getBaseImage()));
                            newProductDTO.setImageName(imageName);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return newProductMapper.toEntity(newProductDTO);
                }).collect(Collectors.toList());
        repository.saveAll(productEntities);
        log.info("Added {} products", productEntities.size());
    }
}
