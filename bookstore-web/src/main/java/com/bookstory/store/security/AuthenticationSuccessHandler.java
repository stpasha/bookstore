package com.bookstory.store.security;

import org.springframework.http.HttpStatus;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler;
import org.springframework.security.core.Authentication;
import reactor.core.publisher.Mono;

import java.net.URI;

public class AuthenticationSuccessHandler extends RedirectServerAuthenticationSuccessHandler {

    private final SessionRegistry sessionRegistry;

    public AuthenticationSuccessHandler(SessionRegistry sessionRegistry, String uri) {
        super(uri);
        this.sessionRegistry = sessionRegistry;
    }

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange exchange, Authentication authentication) {
        return super.onAuthenticationSuccess(exchange, authentication).then(exchange.getExchange().getSession()
                .flatMap(session -> {
                    String username = authentication.getName();
                    String sessionId = session.getId();

                    if (!sessionRegistry.isSessionAllowed(username, sessionId)) {
                        return session.invalidate().then(Mono.defer(() -> {
                            // Делаем редирект на страницу логина с параметром
                            var response = exchange.getExchange().getResponse();
                            response.setStatusCode(HttpStatus.SEE_OTHER);
                            response.getHeaders().setLocation(URI.create("/login?error=session_limit"));
                            return response.setComplete();
                        }));
                    }

                    sessionRegistry.registerSession(username, sessionId);
                    return Mono.empty();
                }));
    }
}
