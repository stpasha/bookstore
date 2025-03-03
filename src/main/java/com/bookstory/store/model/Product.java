package com.bookstory.store.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;

import javax.validation.constraints.Min;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "products", schema = "storedata")
@Getter
@Setter
@ToString(exclude = "items")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    @EqualsAndHashCode.Include
    private Long id;

    @JsonIgnore
    @OneToMany(mappedBy = "product")
    private List<Item> items;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "item_description", nullable = false)
    private String description;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "price", precision = 10, scale = 2)
    @DecimalMin(value = "0.00", message = "Price must be greater than or equal to 0.00")
    private BigDecimal price;

    @Column(name = "quantity_available")
    @Min(value = 0, message = "Quantity available must be greater than or equal to 0")
    private Long quantityAvailable;
}
