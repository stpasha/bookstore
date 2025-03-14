package com.bookstory.store.web.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private OrderDTO order;
    @Min(0)
    private Long quantity;
    @JsonIgnore
    private ProductDTO product;
}
