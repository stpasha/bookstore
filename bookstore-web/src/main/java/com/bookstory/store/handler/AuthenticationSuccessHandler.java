package com.bookstory.store.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.session.ReactiveSessionRegistry;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationSuccessHandler extends RedirectServerAuthenticationSuccessHandler {

    private final ReactiveSessionRegistry reactiveSessionRegistry;

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange exchange, Authentication authentication) {
        return super.onAuthenticationSuccess(exchange, authentication).then(exchange.getExchange().getSession()
                .flatMap(session -> {
                    session.getAttributes().remove("cart");
                    return Mono.empty();
                }));
    }
}
