package com.bookstory.store.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.session.ReactiveSessionRegistry;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.*;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public final class InvalidateSessionControlHandler
        implements ServerAuthenticationSuccessHandler {

    private final ReactiveSessionRegistry sessionRegistry;

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange exchange, Authentication authentication) {
        return handleConcurrency(exchange, authentication);
    }

    private Mono<Void> handleConcurrency(WebFilterExchange exchange, Authentication authentication) {
        return exchange.getExchange().getSession()
                .flatMap(currentSession ->
                        this.sessionRegistry.getAllSessions(authentication.getPrincipal())
                                .flatMap(sessionInfo -> {
                                    if (!sessionInfo.getSessionId().equals(currentSession.getId())) {
                                        return this.sessionRegistry.removeSessionInformation(sessionInfo.getSessionId())
                                                .then(sessionInfo.invalidate());
                                    } else {
                                        return Mono.empty();
                                    }
                                })
                                .then()
                );
    }


}
