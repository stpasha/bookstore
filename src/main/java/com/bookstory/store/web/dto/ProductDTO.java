package com.bookstory.store.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class ProductDTO {
    private Long id;
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
