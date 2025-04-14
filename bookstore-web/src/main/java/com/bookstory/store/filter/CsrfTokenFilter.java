package com.bookstory.store.filter;

import org.springframework.security.web.server.csrf.CsrfToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class CsrfTokenFilter implements WebFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return Mono.defer(() -> {
            Mono<CsrfToken> csrfTokenMono = exchange.getAttribute(CsrfToken.class.getName());
            return csrfTokenMono.doOnNext(token ->
                exchange.getAttributes().put("csrfToken", token.getToken())
            ).then(chain.filter(exchange));
        });
    }
}
