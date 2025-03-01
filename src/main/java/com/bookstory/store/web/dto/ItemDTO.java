package com.bookstory.store.web.dto;

import lombok.*;

import javax.validation.constraints.Min;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ItemDTO {
    private Long id;
    private OrderDTO order;
    @Min(0)
    private Long quantity;
    private ProductDTO product;
}
