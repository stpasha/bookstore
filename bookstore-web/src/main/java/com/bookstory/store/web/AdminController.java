package com.bookstory.store.web;

import com.bookstory.store.service.FileService;
import com.bookstory.store.service.ProductService;
import com.bookstory.store.service.UserDetailsService;
import com.bookstory.store.web.dto.UserDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Controller
@RequestMapping("/admin")
@Slf4j
public class AdminController {

    private final FileService fileService;

    private final ProductService productService;

    private final UserDetailsService userDetailsService;

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

    @PostMapping("/add/user")
    public Mono<Rendering> addUser(@Valid UserDTO user) {
        return userDetailsService.createUser(Mono.just(user))
                .thenReturn(Rendering.redirectTo("/admin").build())
                .onErrorResume(ex -> {
                    log.error("Error creating user", ex);
                    return Mono.just(Rendering.view("error")
                            .modelAttribute("message", ex.getMessage())
                            .build());
                });
    }
}
