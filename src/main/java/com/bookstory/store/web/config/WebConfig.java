package com.bookstory.store.web.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.ResourceHandlerRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import java.net.URI;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.permanentRedirect;

@Configuration
public class WebConfig implements WebFluxConfigurer {

    @Value("${image.upload.resource}")
    private String uploadResource;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**").addResourceLocations(uploadResource);
    }

    @Bean
    public RouterFunction<ServerResponse> redirectRootToProducts() {
        return route()
                .GET("/", request -> permanentRedirect(URI.create("/products")).build())
                .build();
    }
}