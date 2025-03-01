package com.bookstory.store.web.dto;

import com.bookstory.store.model.Item;
import java.math.BigDecimal;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ProductDTO {
    private Long id;
    private Item item;
    private String title;
    private String description;
    private String imageUrl;
    private BigDecimal price;
    private Long quantityAvailable;
}
