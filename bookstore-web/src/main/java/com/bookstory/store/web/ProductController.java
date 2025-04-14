package com.bookstory.store.web;

import com.bookstory.store.service.ProductService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Controller
@RequestMapping("/products")
@Slf4j
@AllArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public Mono<Rendering> listProducts(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size,
                                        @RequestParam(defaultValue = "id") String sort,
                                        @RequestParam(value = "search", required = false, defaultValue = "") String title) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        log.info("list of products with params title {}, pageable {}", title, pageable);

        Mono<String> roleMono = ReactiveSecurityContextHolder.getContext()
                .map(context -> context.getAuthentication().getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.joining(",")))
                .defaultIfEmpty("");

        return Mono.zip(productService.getAllProducts(title, pageable), roleMono)
                .map(tuple -> Rendering.view("main")
                        .modelAttribute("products", tuple.getT1().getContent())
                        .modelAttribute("productPage", tuple.getT1())
                        .modelAttribute("search", title)
                        .modelAttribute("role", tuple.getT2())
                        .build());
    }

    @GetMapping("/{id}")
    public Mono<Rendering> getProduct(@PathVariable Long id) {
        return productService.getProductCache(id)
                .map(product -> {
                    log.info("products queried {}", product);
                    return Rendering.view("item").modelAttribute("product", product).build();
                });
    }

}
