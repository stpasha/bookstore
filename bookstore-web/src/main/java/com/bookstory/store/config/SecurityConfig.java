package com.bookstory.store.config;

import com.bookstory.store.security.SessionRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.authentication.logout.SecurityContextServerLogoutHandler;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

import java.net.URI;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, RedirectServerAuthenticationSuccessHandler successHandler) {
        return http
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/items/**").hasRole("USER")
                        .pathMatchers("/orders/**").hasRole("USER")
                        .pathMatchers("/admin/**").hasRole("ADMIN")
                        .pathMatchers("/products/**", "/login", "/logout", "/css/**", "/js/**", "/uploads/**").permitAll()
                        .anyExchange().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .authenticationSuccessHandler(successHandler)
                        .authenticationFailureHandler((webFilterExchange, exception) -> {
                            ServerHttpResponse response = webFilterExchange.getExchange().getResponse();
                            response.setStatusCode(HttpStatus.SEE_OTHER);
                            response.getHeaders().setLocation(URI.create("/login?error=bad_credentials"));
                            return Mono.empty();
                        })
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
                                        }))
                        )
                )
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
    public RedirectServerAuthenticationSuccessHandler authenticationSuccessHandler() {
        return new RedirectServerAuthenticationSuccessHandler("/products");
    }

}
