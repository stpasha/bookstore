package com.bookstory.store.tests.web;

import com.bookstory.store.annotations.StoreTestAnnotation;
import com.bookstory.store.service.OrderService;
import com.bookstory.store.service.ProductService;
import com.bookstory.store.tests.AbstractTest;
import com.bookstory.store.util.TestDataFactory;
import com.bookstory.store.web.dto.OrderDTO;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@StoreTestAnnotation
@SpringBootTest(webEnvironment = RANDOM_PORT)
class DefaultControllerIT extends AbstractTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private OrderService orderService;

    @Autowired
    private TestDataFactory testDataFactory;

    private static final Long ITEM_ID = 1L;

    @Nested
    class ProductControllerIT {
        @Test
        public void listProducts() {
            webTestClient.get().uri("/products")
                    .accept(MediaType.TEXT_HTML)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(String.class)
                    .consumeWith(response -> {
                        String body = response.getResponseBody();
                        assertNotNull(body);
                        assertTrue(body.contains("<title>Витрина товаров</title>"));
                        assertTrue(body.contains("<span>Страница: 1 из"));
                        assertTrue(body.contains("<a href=\"/products/"));
                    });
        }

        @Test
        public void getProduct() {

            webTestClient.get().uri("/products/{id}", 1L)
                    .accept(MediaType.TEXT_HTML)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(String.class)
                    .consumeWith(response -> {
                        String body = response.getResponseBody();
                        assertNotNull(body);
                        assertTrue(body.contains("Java: The Complete Reference"));
                    });
        }
    }

    @Nested
    class ItemControllerIT {
        @Test
        public void shouldAddItemToCart() {
            webTestClient.post()
                    .uri("/items/" + ITEM_ID + "/add")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .accept(MediaType.TEXT_HTML)
                    .body(BodyInserters.fromFormData("quantity", "1"))
                    .exchange()
                    .expectStatus().is3xxRedirection()
                    .returnResult(Void.class);
        }

        @Test
        public void shouldRemoveItemFromCart() {
            FluxExchangeResult<Void> addItemResult = webTestClient.post()
                    .uri("/items/" + ITEM_ID + "/add")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .accept(MediaType.TEXT_HTML)
                    .body(BodyInserters.fromFormData("quantity", "1"))
                    .exchange()
                    .expectStatus().is3xxRedirection()
                    .expectCookie().exists("SESSION")
                    .returnResult(Void.class);


            String sessionId = addItemResult.getResponseCookies()
                    .getFirst("SESSION")
                    .getValue();

            assertNotNull(sessionId, "SESSION должен быть установлен");

            webTestClient.mutate()
                    .defaultCookie("SESSION", sessionId)
                    .build()
                    .post()
                    .uri("/items/" + ITEM_ID + "/remove")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .accept(MediaType.TEXT_HTML)
                    .exchange()
                    .expectStatus().is3xxRedirection();
        }


        @Test
        public void shouldViewCart() {
            webTestClient.get().uri("/items")
                    .accept(MediaType.TEXT_HTML)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(String.class)
                    .consumeWith(response -> {
                        String body = response.getResponseBody();
                        assertNotNull(body);
                        assertTrue(body.contains("Корзина товаров"));
                    });
        }
    }

    @Nested
    class OrderControllerIT {
        @Test
        public void shouldReturnOrdersList() {
            webTestClient.get().uri("/orders")
                    .accept(MediaType.TEXT_HTML)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(String.class)
                    .consumeWith(response -> {
                        String body = response.getResponseBody();
                        assertNotNull(body);
                        assertTrue(body.contains("<title>Заказы</title>"));
                    });
        }

        @Test
        public void shouldReturnOrderDetails() {

            OrderDTO orderDTO = testDataFactory.createOrderDTO();
            OrderDTO order = orderService.createOrder(Mono.just(orderDTO)).blockOptional()
                    .orElseThrow(() -> new AssertionError("Ошибка создания заказа"));

            webTestClient.get().uri("/orders/{id}", order.getId())
                    .accept(MediaType.TEXT_HTML)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(String.class)
                    .consumeWith(response -> {
                        String body = response.getResponseBody();
                        assertNotNull(body);
                        assertTrue(body.contains("<title>Заказ</title>"));
                        assertTrue(body.contains("<table id=\"order-details\">"));
                    });
        }

        @Test
        public void shouldReturnErrorWhenOrderNotFound() {
            webTestClient.get().uri("/orders/{id}", 9999L)
                    .accept(MediaType.TEXT_HTML)
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody(String.class)
                    .consumeWith(response -> {
                        String body = response.getResponseBody();
                        assertNotNull(body);
                        assertTrue(body.contains("Запрашиваемая страница не найдена"));
                    });
        }
    }
}