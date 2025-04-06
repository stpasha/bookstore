package com.bookstory.store.web;

import com.bookstory.store.api.AccountControllerApi;
import com.bookstory.store.domain.AccountDTO;
import com.bookstory.store.service.ProductService;
import com.bookstory.store.web.dto.CartDTO;
import com.bookstory.store.web.dto.ItemDTO;
import com.bookstory.store.web.dto.QuantityDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.HashMap;

@Controller
@RequestMapping("/items")
@Slf4j
@SessionAttributes("cart")
@AllArgsConstructor
public class ItemController {

    private final ProductService productService;
    private final AccountControllerApi accountControllerApi;

    @ModelAttribute("cart")
    public Mono<CartDTO> initializeCart() {
        //TODO временно используется ID 1L, нужно заменить на аккаунт авторизованного пользователя
        return accountControllerApi.getAccountById(1L)
                .map(accountDTO -> new CartDTO(new HashMap<>(), "", accountDTO))
                .onErrorResume(throwable -> {
                    log.warn("Биллинг сервис не доступен, продолжаем работу", throwable);
                    return Mono.just(new CartDTO(new HashMap<>(), "", new AccountDTO().id(1L)));
                });
    }

    @PostMapping("/{id}/add")
    public Mono<String> addItemToCart(ServerWebExchange exchange,
                                      @PathVariable("id") Long id,
                                      @ModelAttribute("cart") Mono<CartDTO> cartMono) {
        return exchange.getFormData().flatMap(formData -> {
            String quantityStr = formData.getFirst("quantity");
            if (quantityStr == null || quantityStr.equals("0")) {
                return Mono.just("redirect:/products");
            }

            long quantity;
            try {
                quantity = Long.parseLong(quantityStr);
                if (quantity < 1) return Mono.just("redirect:/products");
            } catch (NumberFormatException e) {
                log.warn("Invalid quantity input: {}", quantityStr);
                return Mono.just("redirect:/products");
            }

            return productService.getProduct(id)
                    .flatMap(productDTO ->
                            cartMono.flatMap(cartDTO ->
                                    accountControllerApi.getAccountById(cartDTO.getAccountDTO().getId())
                                            .doOnNext(cartDTO::setAccountDTO)
                                            .then(Mono.fromRunnable(() -> {
                                                cartDTO.getItems().compute(id, (productId, existingItem) -> {
                                                    if (existingItem == null) {
                                                        return ItemDTO.builder()
                                                                .productId(productDTO.getId())
                                                                .product(productDTO)
                                                                .quantity(quantity)
                                                                .build();
                                                    } else {
                                                        existingItem.setQuantity(existingItem.getQuantity() + quantity);
                                                        return existingItem;
                                                    }
                                                });
                                            })).thenReturn("redirect:/products")
                            )
                    )
                    .onErrorResume(e -> {
                        log.error("Failed to add product to cart", e);
                        return Mono.just("redirect:/products");
                    })
                    .defaultIfEmpty("redirect:/products");
        });
    }

    @DeleteMapping("/{id}/remove")
    public Mono<String> removeItemFromCart(@PathVariable("id") Long id,
                                           @ModelAttribute("cart") Mono<CartDTO> cartMono) {
        return cartMono.flatMap(cartDTO ->
                accountControllerApi.getAccountById(cartDTO.getAccountDTO().getId())
                        .doOnNext(cartDTO::setAccountDTO)
                        .then(Mono.fromCallable(() -> {
                            if (cartDTO.getItems().remove(id) != null) {
                                log.info("Removed product {} from cart", id);
                            } else {
                                log.warn("Attempted to remove non-existing product {}", id);
                            }
                            return cartDTO.getItems().isEmpty() ? "redirect:/products" : "redirect:/items";
                        }))
        ).onErrorResume(e -> {
            log.error("Error removing item from cart", e);
            return Mono.just("redirect:/products");
        });
    }

    @GetMapping
    public Mono<String> viewCart(Model model, @ModelAttribute("cart") Mono<CartDTO> cartMono) {
        return cartMono
                .flatMap(cartDTO ->
                        accountControllerApi.getAccountById(cartDTO.getAccountDTO().getId())
                                .doOnNext(cartDTO::setAccountDTO)
                                .thenReturn(cartDTO)
                )
                .flatMap(cartDTO -> {
                    model.addAttribute("cart", cartDTO);
                    return Flux.fromIterable(cartDTO.getItems().entrySet())
                            .flatMap(entry -> {
                                Long productId = entry.getKey();
                                Long quantity = entry.getValue().getQuantity();
                                return productService.getProduct(productId)
                                        .map(productDTO -> productDTO.getPrice().multiply(BigDecimal.valueOf(quantity)))
                                        .onErrorResume(e -> {
                                            log.warn("Failed to get product {} for total calculation", productId, e);
                                            return Mono.just(BigDecimal.ZERO);
                                        });
                            })
                            .reduce(BigDecimal.ZERO, BigDecimal::add)
                            .doOnNext(totalPrice -> model.addAttribute("totalPrice", totalPrice))
                            .thenReturn("cart");
                })
                .doOnNext(cartPage -> log.info("Displaying cart"))
                .onErrorResume(e -> {
                    log.error("Error viewing cart", e);
                    return Mono.just("error");
                });
    }

    @PutMapping("/{id}/product/{quantity}")
    @ResponseBody
    public Mono<ResponseEntity<?>> modifyQuantity(@ModelAttribute("cart") Mono<CartDTO> cartMono,
                                                  @PathVariable("id") Long id,
                                                  @PathVariable("quantity") int quantity) {
        return cartMono.flatMap(cart -> {
            ItemDTO item = cart.getItems().get(id);
            if (item == null) {
                log.warn("Attempted to modify quantity for non-existing product {}", id);
                return Mono.just(ResponseEntity.badRequest().build());
            }

            long newQuantity = item.getQuantity() + quantity;

            if (newQuantity < 1) {
                cart.getItems().remove(id);
                log.info("Product {} removed from cart due to quantity <= 0", id);
            } else {
                item.setQuantity(newQuantity);
                log.info("Product {} quantity updated to {}", id, newQuantity);
            }

            BigDecimal total = cart.getItems().values().stream()
                    .map(itemDTO -> itemDTO.getProduct().getPrice()
                            .multiply(BigDecimal.valueOf(itemDTO.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            return accountControllerApi.getAccountById(cart.getAccountDTO().getId())
                    .map(accountDTO -> ResponseEntity.ok(
                            QuantityDTO.builder()
                                    .balance(accountDTO.getAmount())
                                    .quantity((int) Math.max(newQuantity, 0))
                                    .cartTotal(total)
                                    .build()));
        }).onErrorResume(e -> {
            log.error("Error modifying quantity", e);
            return Mono.just(ResponseEntity.internalServerError().body(
                    QuantityDTO.builder().message("Произошла неизвестная ошибка").build()));
        });
    }

}
