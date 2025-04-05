package com.bookstory.store.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Mono;

@Controller
public class AdminController {
    @GetMapping("/admin")
    public Mono<String> adminPanel() {
        return Mono.just("admin");
    }
}
