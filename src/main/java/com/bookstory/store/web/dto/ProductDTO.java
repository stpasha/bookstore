package com.bookstory.store.web.dto;

import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ProductDTO {
    private Long id;
    private List<ItemDTO> items;
    @Size(max = 255, message = "Should be not greater than 255 symbols")
    @NotEmpty
    @NotNull
    private String title;
    @Size(max = 1000, message = "Should be not greater than 1000 symbols")
    private String description;
    @Size(max = 255, message = "Should be not greater than 255 symbols")
    private String imageUrl;
    private BigDecimal price;
    @Min(0)
    private Long quantityAvailable;
    @Min(0)
    private Long quantity;
}
