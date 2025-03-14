package com.bookstory.store.model;

import jakarta.persistence.*;
import lombok.*;

import jakarta.validation.constraints.Min;

@Entity
@Table(name = "items", schema = "storedata")
@Getter
@Setter
@ToString(exclude = {"order", "product"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "quantity")
    @Min(value = 0, message = "Quantity must be greater than or equal to 0")
    private Long quantity;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}