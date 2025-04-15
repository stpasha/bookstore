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
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import com.bookstory.store.api.AccountControllerApi;
import com.bookstory.store.domain.MessageDTO;
import com.bookstory.store.domain.PaymentDTO;
import com.bookstory.store.domain.AccountDTO;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
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

    @MockitoBean
    private AccountControllerApi accountControllerApi;

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
        @WithUserDetails("user")
        public void shouldAddItemToCart() {

            when(accountControllerApi.getAccountByUserId(anyLong()))
                    .thenAnswer(invocation -> Mono.just(new AccountDTO().id(2L).userId(2L).amount(BigDecimal.valueOf(10000)).version(1)));
            when(accountControllerApi.getAccountById(anyLong()))
                    .thenAnswer(invocation -> Mono.just(new AccountDTO().id(2L).userId(2L).amount(BigDecimal.valueOf(10000)).version(1)));

            String csrfToken = webTestClient.get().uri("/products")
                    .exchange()
                    .returnResult(Void.class)
                    .getResponseCookies()
                    .getFirst("XSRF-TOKEN")
                    .getValue();

            webTestClient.post()
                    .uri("/items/" + ITEM_ID + "/add")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .accept(MediaType.TEXT_HTML)
                    .cookie("XSRF-TOKEN", csrfToken)
                    .header("X-XSRF-TOKEN", csrfToken)
                    .body(BodyInserters.fromFormData("quantity", "1"))
                    .exchange()
                    .expectStatus().is3xxRedirection()
                    .returnResult(Void.class);
        }

        @Test
        @WithUserDetails("user")
        public void shouldRemoveItemFromCart() {
            when(accountControllerApi.getAccountByUserId(anyLong()))
                    .thenAnswer(invocation -> Mono.just(new AccountDTO().id(2L).userId(2L).amount(BigDecimal.valueOf(10000)).version(1)));
            when(accountControllerApi.getAccountById(anyLong()))
                    .thenAnswer(invocation -> Mono.just(new AccountDTO().id(2L).userId(2L).amount(BigDecimal.valueOf(10000)).version(1)));

            String csrfToken = webTestClient.get().uri("/products")
                    .exchange()
                    .returnResult(Void.class)
                    .getResponseCookies()
                    .getFirst("XSRF-TOKEN")
                    .getValue();

            FluxExchangeResult<Void> addItemResult = webTestClient.post()
                    .uri("/items/" + ITEM_ID + "/add")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .accept(MediaType.TEXT_HTML)
                    .cookie("XSRF-TOKEN", csrfToken)
                    .header("X-XSRF-TOKEN", csrfToken)
                    .body(BodyInserters.fromFormData("quantity", "1").with("_csrf", csrfToken))
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
                    .delete()
                    .uri("/items/" + ITEM_ID + "/remove")
                    .cookie("XSRF-TOKEN", csrfToken)
                    .header("X-XSRF-TOKEN", csrfToken)
                    .accept(MediaType.TEXT_HTML)
                    .exchange()
                    .expectStatus().is3xxRedirection();
        }


        @Test
        @WithUserDetails("user")
        public void shouldViewCart() {
            when(accountControllerApi.getAccountById(anyLong()))
                    .thenAnswer(invocation -> Mono.just(new AccountDTO().id(2L).userId(2L).amount(BigDecimal.valueOf(10000)).version(1)));
            when(accountControllerApi.getAccountByUserId(anyLong()))
                    .thenAnswer(invocation -> Mono.just(new AccountDTO().id(2L).userId(2L).amount(BigDecimal.valueOf(10000)).version(1)));
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
        @WithUserDetails("user")
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
        @WithUserDetails("admin")
        public void shouldNotReturnOrdersList_forAdmin() {
            webTestClient.get().uri("/orders")
                    .accept(MediaType.TEXT_HTML)
                    .exchange()
                    .expectStatus().isForbidden();
        }

        @Test
        @WithUserDetails("user")
        public void shouldReturnOrderDetails() {

            when(accountControllerApi.createAccountPayment(anyLong(), any(PaymentDTO.class)))
                    .thenAnswer(invocation -> Mono.just(new MessageDTO().message("Платёжь создан")));
            when(accountControllerApi.getAccountByUserId(anyLong()))
                    .thenAnswer(invocation -> Mono.just(new AccountDTO().id(2L).userId(2L).amount(BigDecimal.valueOf(10000)).version(1)));
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
        public void shouldNotReturnOrderDetails_UnAuth() {

            when(accountControllerApi.createAccountPayment(anyLong(), any(PaymentDTO.class)))
                    .thenAnswer(invocation -> Mono.just(new MessageDTO().message("Платёжь создан")));
            when(accountControllerApi.getAccountByUserId(anyLong()))
                    .thenAnswer(invocation -> Mono.just(new AccountDTO().id(2L).userId(2L).amount(BigDecimal.valueOf(10000)).version(1)));
            OrderDTO orderDTO = testDataFactory.createOrderDTO();
            OrderDTO order = orderService.createOrder(Mono.just(orderDTO)).blockOptional()
                    .orElseThrow(() -> new AssertionError("Ошибка создания заказа"));

            webTestClient.get().uri("/orders/{id}", order.getId())
                    .accept(MediaType.TEXT_HTML)
                    .exchange()
                    .expectStatus().isFound();
        }

        @Test
        @WithUserDetails("user")
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

    @Nested
    class AdminControllerIT {
        @Test
        @WithUserDetails("admin")
        public void shouldOpenAdmin() {
            webTestClient.get().uri("/admin")
                    .accept(MediaType.TEXT_HTML)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(String.class)
                    .consumeWith(response -> {
                        String body = response.getResponseBody();
                        assertNotNull(body);
                        assertTrue(body.contains("<label for=\"file-input\">Число товаров: </label>"));
                        assertTrue(body.contains("<label for=\"username\">Логин:</label>"));
                        assertTrue(body.contains("<button type=\"submit\">Создать пользователя</button>"));
                    });
        }

        @Test
        @WithUserDetails("user")
        public void shouldNotOpenAdmin_ForUser() {
            webTestClient.get().uri("/admin")
                    .accept(MediaType.TEXT_HTML)
                    .exchange()
                    .expectStatus().isForbidden();
        }
    }
}