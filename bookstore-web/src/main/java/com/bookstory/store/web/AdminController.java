package com.bookstory.store.web;

import com.bookstory.store.service.FileService;
import com.bookstory.store.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final FileService fileService;

    private final ProductService productService;

    @GetMapping
    public Mono<String> adminPanel() {
        return Mono.just("admin");
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
