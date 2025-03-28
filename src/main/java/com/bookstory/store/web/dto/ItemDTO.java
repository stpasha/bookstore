package com.bookstory.store.web.dto;

import lombok.*;

import jakarta.validation.constraints.Min;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ItemDTO {
    private Long id;
    @Min(1)
    private Long quantity;
    private ProductDTO product;
    private Long orderId;
    private Long productId;
}
