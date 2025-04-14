package com.bookstory.store.util;

import com.bookstory.store.domain.AccountDTO;
import com.bookstory.store.model.Item;
import com.bookstory.store.model.Order;
import com.bookstory.store.model.Product;
import com.bookstory.store.web.dto.*;
import net.datafaker.Faker;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TestDataFactory {

    private final Faker faker;

    public TestDataFactory(Faker faker) {
        this.faker = faker;
    }

    public Product createProduct() {
        return getRandomProduct();
    }

    public ProductDTO createProductDTO() {
        return ProductDTO.builder()
                .id(ThreadLocalRandom.current().nextLong(1, 24))
                .title(faker.book().title())
                .description(faker.lorem().maxLengthSentence(255))
                .imageUrl(faker.internet().image())
                .price(BigDecimal.valueOf(faker.number().randomDouble(2, 5, 500)))
                .quantityAvailable(ThreadLocalRandom.current().nextLong(0, 101))
                .quantity(ThreadLocalRandom.current().nextLong(0, 50))
                .build();
    }

    public ProductDTO getProductDTO(int i) {
        Product product = PRODUCTS.get((int) i);
        return ProductDTO.builder()
                .id(product.getId())
                .quantityAvailable(product.getQuantityAvailable())
                .imageUrl(product.getImageUrl())
                .title(product.getTitle())
                .description(product.getDescription())
                .price(product.getPrice())
                .build();
    }

    public NewProductDTO createNewProductDTO() {
        return NewProductDTO.builder()
                .title(faker.book().title())
                .description(faker.lorem().maxLengthSentence(255))
                .imageName(faker.file().fileName())
                .price(BigDecimal.valueOf(faker.number().randomDouble(2, 5, 500)))
                .quantityAvailable(ThreadLocalRandom.current().nextLong(0, 101))
                .baseImage(faker.internet().image())
                .build();
    }

    public Order createOrder() {
        return Order.builder()
                .comment(faker.lorem().maxLengthSentence(255))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .items(new ArrayList<>())
                .build();
    }

    public OrderDTO createOrderDTO() {
        List<ItemDTO> items = createItemDTOs(3);
        return OrderDTO.builder().comment(faker.lorem().maxLengthSentence(255)).items(items).build();
    }

    public List<Order> createOrders(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> createOrder())
                .collect(Collectors.toList());
    }

    public Item createItem(Order order, Product product) {
        return Item.builder()
                .orderId(order.getId())
                .productId(product.getId())
                .quantity(ThreadLocalRandom.current().nextLong(1, 5))
                .build();
    }

    public ItemDTO createItemDTO(int i) {
        ProductDTO productDTO = getProductDTO(i);
        return ItemDTO.builder()
                .quantity(ThreadLocalRandom.current().nextLong(1, 5))
                .productId(productDTO.getId())
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
                .mapToObj(i -> Product.builder()
                        .title(faker.book().title())
                        .description(faker.lorem().maxLengthSentence(255))
                        .imageUrl(faker.internet().image())
                        .price(BigDecimal.valueOf(faker.number().randomDouble(2, 5, 500)))
                        .quantityAvailable(ThreadLocalRandom.current().nextLong(0, 101))
                        .build())
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
                .mapToObj(this::createItemDTO)
                .collect(Collectors.toList());
    }

    public CartDTO createCartDTO() {
        List<ItemDTO> items = createItemDTOs(3);
        return new CartDTO(items.stream().collect(Collectors.toMap(ItemDTO::getProductId, item -> item)),
                faker.lorem().maxLengthSentence(255), "user", new AccountDTO().id(1L).amount(BigDecimal.valueOf(10000)));
    }


    public static Product getRandomProduct() {
        if (PRODUCTS.isEmpty()) {
            throw new IllegalStateException("Список продуктов пуст!");
        }
        return PRODUCTS.get(new Random().nextInt(PRODUCTS.size()));
    }

    public static List<Product> PRODUCTS = Arrays.asList(
            Product.builder()
                    .id(1L)
                    .title("Java: The Complete Reference")
                    .description("Полное руководство по Java")
                    .imageUrl("/uploads/images/1.jpeg")
                    .price(BigDecimal.valueOf(1145.99))
                    .quantityAvailable(100L)
                    .build(),

            Product.builder()
                    .id(2L)
                    .title("Effective Java")
                    .description("Лучшие практики программирования на Java")
                    .imageUrl("/uploads/images/2.jpeg")
                    .price(BigDecimal.valueOf(3229.99))
                    .quantityAvailable(80L)
                    .build(),

            Product.builder()
                    .id(3L)
                    .title("Spring in Action")
                    .description("Глубокое погружение в Spring Framework")
                    .imageUrl("/uploads/images/3.png")
                    .price(BigDecimal.valueOf(49.99))
                    .quantityAvailable(60L)
                    .build(),

            Product.builder()
                    .id(4L)
                    .title("Clean Code")
                    .description("Руководство по написанию чистого кода")
                    .imageUrl("/uploads/images/4.jpg")
                    .price(BigDecimal.valueOf(421.50))
                    .quantityAvailable(120L)
                    .build(),

            Product.builder()
                    .id(5L)
                    .title("Moby-Dick")
                    .description("Классика американской литературы о капитане Ахаве")
                    .imageUrl("/uploads/images/5.png")
                    .price(BigDecimal.valueOf(2222.00))
                    .quantityAvailable(160L)
                    .build(),

            Product.builder()
                    .id(6L)
                    .title("Crime and Punishment")
                    .description("Достоевский о морали и наказании")
                    .imageUrl("/uploads/images/6.jpeg")
                    .price(BigDecimal.valueOf(1117.99))
                    .quantityAvailable(140L)
                    .build(),

            Product.builder()
                    .id(7L)
                    .title("The Catcher in the Rye")
                    .description("Роман Сэлинджера о взрослении")
                    .imageUrl("/uploads/images/7.jpg")
                    .price(BigDecimal.valueOf(1226.50))
                    .quantityAvailable(150L)
                    .build(),

            Product.builder()
                    .id(8L)
                    .title("The Great Gatsby")
                    .description("История Джея Гэтсби от Фицджеральда")
                    .imageUrl("/uploads/images/8.jpg")
                    .price(BigDecimal.valueOf(120.99))
                    .quantityAvailable(130L)
                    .build()
    );
}
