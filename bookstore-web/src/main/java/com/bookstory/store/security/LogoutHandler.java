package com.bookstory.store.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.logout.SecurityContextServerLogoutHandler;
import reactor.core.publisher.Mono;

public class LogoutHandler extends SecurityContextServerLogoutHandler {

    private final SessionRegistry sessionRegistry;

    public LogoutHandler(SessionRegistry sessionRegistry) {
        this.sessionRegistry = sessionRegistry;
    }

    @Override
    public Mono<Void> logout(WebFilterExchange exchange, Authentication authentication) {
        return super.logout(exchange, authentication).then(exchange.getExchange().getSession().flatMap(webSession -> {
            if (authentication != null) {
                sessionRegistry.unregisterSession(authentication.getName(), webSession.getId());
            }
            return webSession.invalidate();
        }));
    }
}
