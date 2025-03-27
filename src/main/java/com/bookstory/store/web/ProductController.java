package com.bookstory.store.web;

import com.bookstory.store.service.FileService;
import com.bookstory.store.service.ProductService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.reactive.result.view.Rendering;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.Charset;
import java.util.Map;

@Controller
@RequestMapping("/products")
@Slf4j
@AllArgsConstructor
public class ProductController {

    private final ProductService productService;

    private final FileService fileService;

    @GetMapping
    public Mono<Rendering> listProducts(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size,
                                     @RequestParam(defaultValue = "id") String sort,
                                     @RequestParam(value = "search", required = false, defaultValue = "")
                                     String title, ServerWebExchange exchange) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        log.info("list of products with params title {}, pageable {}", title, pageable);
        return productService.getAllProducts(title, pageable)
                .map(productPage -> Rendering.view("main").modelAttribute("products", productPage.getContent())
                        .modelAttribute("productPage", productPage)
                        .modelAttribute("search", title)
                        .build());
    }

    @GetMapping("/{id}")
    public Mono<Rendering> getProduct(@PathVariable Long id, Model model) {
        return productService.getProduct(id)
                .map(product -> {
                    log.info("products queried {}", product);
                    return Rendering.view("item").modelAttribute("product", product).build();
                });
    }

    @PostMapping("/upload")
    public Mono<String> uploadFile(@RequestPart("file") FilePart imageFile) {
        return fileService.getNewProductDtosFromFile(imageFile)
                .collectList()
                .flatMap(list -> productService
                        .addProducts(Flux.fromIterable(list)))
                .thenReturn("redirect:/products");
    }
}
