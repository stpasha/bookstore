package com.bookstory.store.util;

import com.bookstory.store.model.Item;
import com.bookstory.store.model.Order;
import com.bookstory.store.model.Product;
import com.bookstory.store.web.dto.*;
import net.datafaker.Faker;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TestDataFactory {

    private final Faker faker;

    public TestDataFactory(Faker faker) {
        this.faker = faker;
    }

    public Product createProduct() {
        return Product.builder()
                .title(faker.book().title())
                .description(faker.lorem().sentence())
                .imageUrl(faker.internet().image())
                .price(BigDecimal.valueOf(faker.number().randomDouble(2, 5, 500)))
                .quantityAvailable(ThreadLocalRandom.current().nextLong(0, 101))
                .build();
    }

    public ProductDTO createProductDTO() {
        return ProductDTO.builder()
                .id(ThreadLocalRandom.current().nextLong(1, 1000))
                .title(faker.book().title())
                .description(faker.lorem().sentence())
                .imageUrl(faker.internet().image())
                .price(BigDecimal.valueOf(faker.number().randomDouble(2, 5, 500)))
                .quantityAvailable(ThreadLocalRandom.current().nextLong(0, 101))
                .quantity(ThreadLocalRandom.current().nextLong(0, 50))
                .build();
    }

    public NewProductDTO createNewProductDTO() {
        return NewProductDTO.builder()
                .title(faker.book().title())
                .description(faker.lorem().sentence())
                .imageName(faker.file().fileName())
                .price(BigDecimal.valueOf(faker.number().randomDouble(2, 5, 500)))
                .quantityAvailable(ThreadLocalRandom.current().nextLong(0, 101))
                .baseImage(faker.internet().image())
                .build();
    }

    public Order createOrder() {
        return Order.builder()
                .comment(faker.lorem().sentence())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .items(new ArrayList<>())
                .build();
    }

    public OrderDTO createOrderDTO() {
        List<ItemDTO> items = createItemDTOs(3);
        return new OrderDTO(
                ThreadLocalRandom.current().nextLong(1, 1000),
                faker.lorem().sentence(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                items,
                items.stream()
                        .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                        .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, BigDecimal.ROUND_HALF_UP)
        );
    }

    public List<Order> createOrders(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> createOrder())
                .collect(Collectors.toList());
    }

    public Item createItem(Order order, Product product) {
        return Item.builder()
                .order(order)
                .product(product)
                .quantity(ThreadLocalRandom.current().nextLong(1, 11))
                .build();
    }

    public ItemDTO createItemDTO() {
        ProductDTO productDTO = createProductDTO();
        return ItemDTO.builder()
                .id(ThreadLocalRandom.current().nextLong(1, 1000))
                .order(null)
                .quantity(ThreadLocalRandom.current().nextLong(1, 11))
                .product(productDTO)
                .build();
    }

    public List<Item> createItems(int count, List<Order> orders, List<Product> products) {
        if (orders.isEmpty() || products.isEmpty()) {
            throw new IllegalArgumentException("Orders and products must not be empty.");
        }

        return IntStream.range(0, count)
                .mapToObj(i -> {
                    Order order = orders.get(ThreadLocalRandom.current().nextInt(orders.size()));
                    Product product = products.get(ThreadLocalRandom.current().nextInt(products.size()));
                    return createItem(order, product);
                })
                .collect(Collectors.toList());
    }

    public List<Product> createProducts(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> createProduct())
                .collect(Collectors.toList());
    }

    public List<ProductDTO> createProductDTOs(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> createProductDTO())
                .collect(Collectors.toList());
    }

    public List<NewProductDTO> createNewProductDTOs(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> createNewProductDTO())
                .collect(Collectors.toList());
    }

    public List<OrderDTO> createOrderDTOs(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> createOrderDTO())
                .collect(Collectors.toList());
    }

    public List<ItemDTO> createItemDTOs(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> createItemDTO())
                .collect(Collectors.toList());
    }

    public CartDTO createCartDTO() {
        List<ItemDTO> items = createItemDTOs(5);
        return new CartDTO(items.stream().collect(Collectors.toMap(ItemDTO::getId, item -> item)));
    }
}
