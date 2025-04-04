package com.bookstory.store.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Method;
import java.util.Objects;

@Slf4j
public class PageableProductKeyGenerator implements KeyGenerator {
    @Override
    public Object generate(Object target, Method method, Object... params) {
        String title = (String) params[0];
        Pageable pageable = (Pageable) params[1];
        String hash = String.join("", "list-", String.valueOf(
                Objects.hash(title,
                        String.valueOf(pageable.getPageSize()),
                        String.valueOf(pageable.getPageNumber()),
                        String.valueOf(pageable.getSort()))));
        log.debug("Params title {} pageSize {} pageNumber {} sort {} resulted in {}",
                title,
                pageable.getPageSize(),
                pageable.getPageNumber(),
                pageable.getSort(), hash);
        return  hash;
    }
}
