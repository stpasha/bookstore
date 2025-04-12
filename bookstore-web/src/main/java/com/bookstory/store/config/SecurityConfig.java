package com.bookstory.store.config;

import com.bookstory.store.security.AuthenticationSuccessHandler;
import com.bookstory.store.security.LogoutHandler;
import com.bookstory.store.security.SessionRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.logout.RedirectServerLogoutSuccessHandler;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
//TODO Remove? Активация поддержки аннотаций для методов
@EnableReactiveMethodSecurity
public class SecurityConfig {
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, SessionRegistry sessionRegistry) {
        return http
                .authorizeExchange(exchanges -> exchanges
                        // Аналог requestMatchers()
                        .pathMatchers("/items").hasRole("USER")
                        .pathMatchers("/orders").hasRole("USER")
                        .pathMatchers("/admin").hasRole("ADMIN")
                        .pathMatchers("/product").permitAll()
                        // Аналог anyRequest()
                        .anyExchange().authenticated()
                )
                .formLogin(form -> form
                                .loginPage("/login")
                                .authenticationSuccessHandler(new AuthenticationSuccessHandler(sessionRegistry, "/products"))
//                                (WebFilterExchange webFilterExchange, Authentication authentication) -> {
//                            authentication.getAuthorities().stream().peek(grantedAuthority -> {
//                               if("ROLE_ADMIN".equals(grantedAuthority.getAuthority())) {
//                                   webFilterExchange.getExchange().transformUrl()
//                                }
//                            });
//                        })

                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutHandler(new LogoutHandler(sessionRegistry))
                        .logoutSuccessHandler(new RedirectServerLogoutSuccessHandler())
                )
                .exceptionHandling(handling -> handling
                        .accessDeniedHandler((exchange, denied) ->
                                Mono.error(new AccessDeniedException("Access Denied")))
                )
                .build();
    }

    @Bean
    public UserDetailsRepositoryReactiveAuthenticationManager userDetailsRepositoryReactiveAuthenticationManager(ReactiveUserDetailsService userDetailsService) {
        return new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
