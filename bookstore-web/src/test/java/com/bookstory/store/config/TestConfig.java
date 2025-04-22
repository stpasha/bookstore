package com.bookstory.store.config;

import com.bookstory.store.handler.AuthenticationSuccessHandler;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.session.InMemoryReactiveSessionRegistry;
import org.springframework.security.core.session.ReactiveSessionRegistry;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.*;
import org.springframework.security.web.server.authentication.logout.SecurityContextServerLogoutHandler;
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository;
import org.springframework.security.web.server.csrf.ServerCsrfTokenRequestAttributeHandler;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;

@TestConfiguration
@EnableWebFluxSecurity
@Slf4j
public class TestConfig {

    @Bean
    public Faker faker() {
        return new Faker();
    }

    @Bean
    public TestDataFactory testDataFactory(Faker faker) {
        return new TestDataFactory(faker);
    }

    @Bean
    public WebClient billingWebClient() {
        return WebClient.builder()
                .build();
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, ReactiveSessionRegistry registry) {
        ServerCsrfTokenRequestAttributeHandler tokenRequestAttributeHandler = new ServerCsrfTokenRequestAttributeHandler();
        tokenRequestAttributeHandler.setTokenFromMultipartDataEnabled(true);
        var registerSessionHandler = new RegisterSessionServerAuthenticationSuccessHandler(registry);
        var sessionLimitHandler = new ConcurrentSessionControlServerAuthenticationSuccessHandler(registry, new PreventLoginServerMaximumSessionsExceededHandler());
        var customSuccessHandler = new AuthenticationSuccessHandler(registry);
        sessionLimitHandler.setSessionLimit(SessionLimit.of(1));
        var delegatingSuccessHandler = new DelegatingServerAuthenticationSuccessHandler(
                List.of(
                        sessionLimitHandler,
                        registerSessionHandler,
                        customSuccessHandler
                )
        );
        return http
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/items/**").hasRole("USER")
                        .pathMatchers("/orders/**").hasRole("USER")
                        .pathMatchers("/admin/**").hasRole("ADMIN")
                        .pathMatchers("/products/**", "/login", "/logout", "/css/**", "/js/**", "/uploads/**").permitAll()
                        .anyExchange().authenticated()
                )
                .csrf(csrf -> csrf // переопределяем для multipart form data
                        .csrfTokenRepository(CookieServerCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(tokenRequestAttributeHandler)
                )
                .formLogin(login -> login
                        .authenticationSuccessHandler(delegatingSuccessHandler)
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutHandler(new SecurityContextServerLogoutHandler())
                        .logoutSuccessHandler((exchange, authentication) ->
                                exchange.getExchange().getSession()
                                        .flatMap(WebSession::invalidate)
                                        .then(Mono.defer(() -> {
                                            ServerHttpResponse response = exchange.getExchange().getResponse();
                                            response.setStatusCode(HttpStatus.SEE_OTHER);
                                            response.getHeaders().setLocation(URI.create("/login?logout"));
                                            return Mono.empty();
                                        }))))
                .exceptionHandling(handling -> handling
                        .accessDeniedHandler((exchange, denied) -> {
                            ServerHttpResponse response = exchange.getResponse();
                            response.setStatusCode(HttpStatus.FORBIDDEN);
                            return response.setComplete();
                        })
                )
                .build();
    }

    @Bean
    public UserDetailsRepositoryReactiveAuthenticationManager userDetailsRepositoryReactiveAuthenticationManager(
            ReactiveUserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {

        UserDetailsRepositoryReactiveAuthenticationManager manager =
                new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
        manager.setPasswordEncoder(passwordEncoder);
        return manager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ReactiveSessionRegistry reactiveSessionRegistry() {
        return new InMemoryReactiveSessionRegistry();
    }

}

