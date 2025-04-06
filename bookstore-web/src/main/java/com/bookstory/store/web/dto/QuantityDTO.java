package com.bookstory.store.web.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class QuantityDTO {
    private int quantity;
    private BigDecimal cartTotal;
    private BigDecimal balance;
    private String message;
}
