package com.bookstory.store.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;

@Controller
public class LoginController {

    @GetMapping("/login")
    public Mono<Rendering> loginPage(@RequestParam(required = false) String error) {
        String errorMessage = null;
        if ("session_limit".equals(error)) {
            errorMessage = "Ваша сессия уже активна в другом месте. Повторите вход.";
        } else if ("bad_credentials".equals(error)) {
            errorMessage = "Неверный логин или пароль.";
        }
        return Mono.just(Rendering.view("login")
                .modelAttribute("errorMessage", errorMessage)
                .build());
    }
}
