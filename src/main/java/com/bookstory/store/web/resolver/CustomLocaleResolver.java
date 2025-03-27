package com.bookstory.store.web.resolver;

import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.SimpleLocaleContext;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.i18n.LocaleContextResolver;

import java.util.Locale;

public class CustomLocaleResolver implements LocaleContextResolver {

    private final Locale defaultLocale = Locale.forLanguageTag("ru-RU"); // Устанавливаем локаль


    @Override
    public LocaleContext resolveLocaleContext(ServerWebExchange exchange) {
        return new SimpleLocaleContext(defaultLocale);
    }

    @Override
    public void setLocaleContext(ServerWebExchange exchange, org.springframework.context.i18n.LocaleContext localeContext) {
    }
}
