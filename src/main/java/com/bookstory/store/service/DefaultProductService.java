package com.bookstory.store.service;

import com.bookstory.store.model.Product;
import com.bookstory.store.repository.ProductRepository;
import com.bookstory.store.web.dto.NewProductDTO;
import com.bookstory.store.web.dto.ProductDTO;
import com.bookstory.store.web.mapper.NewProductMapper;
import com.bookstory.store.web.mapper.ProductMapper;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
@Slf4j
@Validated
public class DefaultProductService implements ProductService {

    private final ProductRepository repository;

    private final ProductMapper productMapper;

    private final NewProductMapper newProductMapper;

    private final FileService fileService;

    @Override
    @Transactional(readOnly = true)
    public Mono<Page<ProductDTO>> getAllProducts(String title, Pageable pageable) {
        int limit = pageable.getPageSize();
        long offset = pageable.getOffset();
        Mono<List<ProductDTO>> productDTOS;
        Mono<Long> count;
        if (title.isBlank()) {
            log.info("Fetching all products | Pageable: size={}, page={}", pageable.getPageSize(), pageable.getPageNumber());
            count = repository.count();
            productDTOS = repository.findAllBy(limit, offset)
                    .map(productMapper::toDto)
                    .collectList()
                    .defaultIfEmpty(Collections.emptyList());
        } else {
            log.info("Fetching products with filter '{}' pageable {}", title, pageable);
            count = repository.countByTitleContainingIgnoreCase(title);
            productDTOS = repository.findByTitleContainingIgnoreCase(title, limit, offset)
                    .map(productMapper::toDto)
                    .collectList();
        }

        return Mono.zip(productDTOS, count).map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()));
    }

    @Override
    public Mono<ProductDTO> getProduct(Long id) {
        log.info("Fetching product with id {}", id);

        return repository.findById(id)
                .map(productMapper::toDto)
                .doOnSuccess(product -> log.info("Found product: {}", product))
                .onErrorResume(e -> {
                    log.warn("Error in querying id {} {}", id, e.getMessage());
                    return Mono.empty();
                });
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Mono<List<Product>> addProducts(Flux<@Valid NewProductDTO> productList) {
        return productList
                .flatMap(newProductDTO -> {
                    if (newProductDTO.getBaseImage() != null && !newProductDTO.getBaseImage().isBlank()) {
                        return fileService.saveImage(Mono.zip(
                                        Mono.just(newProductDTO.getImageName()),
                                        Mono.just(Base64.getDecoder().decode(newProductDTO.getBaseImage()))
                                )).doOnSuccess(newProductDTO::setImageName)
                                .thenReturn(newProductDTO);
                    } else {
                        return Mono.just(newProductDTO);
                    }
                })
                .map(newProductMapper::toEntity)
                .collectList()
                .flatMap(products -> repository.saveAll(Flux.fromIterable(products)).collectList());
    }
}
