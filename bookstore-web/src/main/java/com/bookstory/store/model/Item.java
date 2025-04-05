package com.bookstory.store.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.*;

import jakarta.validation.constraints.Min;

@Table(name = "items", schema = "storedata")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Item {

    @Id
    @Column("item_id")
    @EqualsAndHashCode.Include
    private Long id;

    @Column("order_id")
    private Long orderId;

    @Column("quantity")
    @Min(value = 0, message = "Quantity must be greater than or equal to 0")
    private Long quantity;

    @Column("product_id")
    private Long productId;

    @Transient
    private Product product;
}