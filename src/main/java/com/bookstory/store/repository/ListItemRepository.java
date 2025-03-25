package com.bookstory.store.repository;

import com.bookstory.store.model.Item;
import com.bookstory.store.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;

@Repository
@RequiredArgsConstructor
public class ListItemRepository {
    private static final String SELECT = """
            SELECT
                order_id,
                item_id,
                quantity,
                items.product_id as item_product_id,
                title,
                item_description,
                image_url,
                price,
                quantity_available
            FROM
                items  INNER JOIN
                    products
                ON (items.product_id = products.product_id)
            WHERE order_id = :id
            """;
    private final DatabaseClient databaseClient;

    public Flux<Item> findByOrdersId(Long orderId) {
        return databaseClient.sql(SELECT)
                .bind("id", orderId)
                .map(((row, rowMetadata) ->
                        Item.builder()
                                .orderId(row.get("order_id", Long.class))
                                .id(row.get("item_id", Long.class))
                                .quantity(row.get("quantity", Long.class))
                                .productId(row.get("item_product_id", Long.class))
                                .product(Product.builder()
                                        .id(row.get("item_product_id", Long.class))
                                        .title(row.get("title", String.class))
                                        .description(row.get("item_description", String.class))
                                        .imageUrl(row.get("image_url", String.class))
                                        .price(row.get("price", BigDecimal.class))
                                        .quantityAvailable(row.get("quantity_available", Long.class))
                                        .build())
                                .build()
                ))
                .all();
    }
}
