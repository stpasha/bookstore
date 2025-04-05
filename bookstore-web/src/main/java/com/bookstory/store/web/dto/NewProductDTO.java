package com.bookstory.store.web.dto;

import lombok.*;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class NewProductDTO {
    @NotEmpty
    @NotNull
    @Size(max = 255, message = "Should be not greater than 255 symbols")
    private String title;
    @Size(max = 1000, message = "Should be not greater than 1000 symbols")
    private String description;
    @Size(max = 255, message = "Should be not greater than 255 symbols")
    private String imageName;
    private BigDecimal price;
    @Min(0)
    private Long quantityAvailable;
    private String baseImage;
}
