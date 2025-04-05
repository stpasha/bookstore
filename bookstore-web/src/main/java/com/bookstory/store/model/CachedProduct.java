package com.bookstory.store.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.math.BigDecimal;

@RedisHash(value = "products")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CachedProduct {

    @Id
    @EqualsAndHashCode.Include
    private Long id;

    private String title;

    private String description;

    private String imageUrl;

    private BigDecimal price;

    private Long quantityAvailable;
}
