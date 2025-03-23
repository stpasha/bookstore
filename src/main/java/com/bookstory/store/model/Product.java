package com.bookstory.store.model;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

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
    @Column("product_id")
    @EqualsAndHashCode.Include
    private Long id;

    @Transient
    private List<Item> items;

    @Column("title")
    private String title;

    @Column("item_description")
    private String description;

    @Column("image_url")
    private String imageUrl;

    @Column("price")
    @DecimalMin(value = "0.00", message = "Price must be greater than or equal to 0.00")
    private BigDecimal price;

    @Column("quantity_available")
    @Min(value = 0, message = "Quantity available must be greater than or equal to 0")
    private Long quantityAvailable;
}
