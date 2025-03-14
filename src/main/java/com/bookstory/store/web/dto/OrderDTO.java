package com.bookstory.store.web.dto;

import lombok.*;

import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrderDTO {
    private Long id;
    @Size(max = 255, message = "Should be not greater than 255 symbols")
    private String comment;
    @PastOrPresent
    private LocalDateTime createdAt;
    @PastOrPresent
    private LocalDateTime updatedAt;
    @ToString.Exclude
    private List<ItemDTO> items;
    private BigDecimal totalSum;

    public BigDecimal getTotalSum() {
        return items.stream()
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
    }
}
