package com.bookstory.store.web.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ItemDTO {
    private Long id;
    private OrderDTO order;
    private Long quantity;
    private ProductDTO product;
}
