package com.bookstory.store.web.config;

import com.bookstory.store.web.resolver.CustomLocaleResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.config.ResourceHandlerRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.i18n.LocaleContextResolver;
import org.thymeleaf.spring6.ISpringWebFluxTemplateEngine;
import org.thymeleaf.spring6.view.reactive.ThymeleafReactiveViewResolver;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

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

    @Bean
    public LocaleContextResolver localeContextResolver() {
        return new CustomLocaleResolver();
    }

    @Bean
    public ThymeleafReactiveViewResolver thymeleafReactiveViewResolver(ISpringWebFluxTemplateEngine templateEngine) {
        ThymeleafReactiveViewResolver resolver = new ThymeleafReactiveViewResolver();
        resolver.setTemplateEngine(templateEngine);
        resolver.setDefaultCharset(StandardCharsets.UTF_8);
        resolver.setSupportedMediaTypes(List.of(MediaType.parseMediaType("text/html; charset=UTF-8")));
        return resolver;
    }
}