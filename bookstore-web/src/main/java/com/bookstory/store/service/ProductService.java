package com.bookstory.store.service;

import com.bookstory.store.model.Product;
import com.bookstory.store.web.dto.ItemDTO;
import com.bookstory.store.web.dto.NewProductDTO;
import com.bookstory.store.web.dto.ProductDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ProductService {
    Mono<Page<ProductDTO>> getAllProducts(String title, Pageable pageable);

    Mono<ProductDTO> getProduct(Long id);

    public Mono<ProductDTO> getProductCache(Long id);

    Mono<ProductDTO> updateProductQuantity(Mono<ItemDTO> itemDTOMono);

    Mono<List<Product>> addProducts(Flux<NewProductDTO> productList);
}
