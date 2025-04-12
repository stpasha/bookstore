package com.bookstory.store.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.*;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Table(name = "orders", schema = "storedata")
@Getter
@Setter
@ToString(exclude = "items")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Order {

    @Id
    @Column("order_id")
    @EqualsAndHashCode.Include
    private Long id;

    @Column("comment")
    @EqualsAndHashCode.Include
    @Size(max = 255, message = "Comment must be less than 255 characters")
    @NotNull(message = "Comment cannot be null")
    private String comment;

    @Column("created_at")
    @EqualsAndHashCode.Include
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column("updated_at")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column("total")
    private BigDecimal total;

    @Id
    @Column("user_id")
    private Long userId;

    @Transient
    private List<Item> items;


}
