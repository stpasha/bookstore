package com.bookstory.store.web;

import com.bookstory.store.service.FileService;
import com.bookstory.store.service.ProductService;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/products")
@Slf4j
@AllArgsConstructor
public class ProductController {

    private final ProductService productService;

    private final FileService fileService;

    @GetMapping
    public Mono<String> listProducts(@PageableDefault(size = 10, sort = "id") Pageable pageable,
                                     @RequestParam(value = "search", required = false, defaultValue = "")
                                     @Size(max = 255, message = "Title must be less than 255 characters") String title,
                                     Model model) {
        log.info("list of products with params title {}, pageable {}", title, pageable);
        return productService.getAllProducts(title, pageable)
                .map(productPage -> {
                    model.addAttribute("products", productPage.getContent());
                    model.addAttribute("productPage", productPage);
                    model.addAttribute("search", title);
                    return "main";
                });
    }

    @GetMapping("/{id}")
    public Mono<String> getProduct(@PathVariable Long id, Model model) {
        return productService.getProduct(id)
                .map(product -> {
                    model.addAttribute("product", product);
                    log.info("products queried {}", product);
                    return "item";
                });
    }

    @PostMapping("/upload")
    public Mono<String> uploadFile(@RequestPart("file") FilePart imageFile) {
        return productService
                .addProducts(fileService.getNewProductDtosFromFile(imageFile))
                .thenReturn("redirect:/products");
    }
}
