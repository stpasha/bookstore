package com.bookstory.store.web;

import com.bookstory.store.service.ProductService;
import com.bookstory.store.web.dto.CartDTO;
import com.bookstory.store.web.dto.ItemDTO;
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
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/items")
@Slf4j
@SessionAttributes("cart")
@AllArgsConstructor
public class ItemController {

    private final ProductService productService;

    @ModelAttribute("cart")
    public Mono<CartDTO> initializeCart() {
        return Mono.just(new CartDTO(new HashMap<>(), ""));
    }

    @PostMapping("/{id}/add")
    public Mono<String> addItemToCart(ServerWebExchange exchange,
                                      @PathVariable("id") Long id,
                                      @ModelAttribute("cart") Mono<CartDTO> cartMono) {
        return exchange.getFormData().flatMap(formData -> {
            List<String> quantityStrings = formData.get("quantity");
            if (!quantityStrings.isEmpty() && !Objects.equals("0", quantityStrings.getFirst())) {
                Long quantity = Long.parseLong(quantityStrings.getFirst());
                return productService.getProduct(id)
                        .flatMap(productDTO -> cartMono.flatMap(cart -> {
                            cart.getItems().compute(id, (productId, existingItem) -> {
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
                            return Mono.just("redirect:/products");
                        }))
                        .defaultIfEmpty("redirect:/products");
            }
            return Mono.just("redirect:/products");
        });
    }

    @PostMapping("/{id}/remove")
    public Mono<String> removeItemFromCart(@PathVariable("id") Long id,
                                           @ModelAttribute("cart") Mono<CartDTO> cartMono) {
        return cartMono.flatMap(cart -> {
            if (cart.getItems().remove(id) != null) {
                log.info("Removed product {} from cart", id);
            } else {
                log.warn("Attempted to remove non-existing product {}", id);
            }
            return Mono.just(cart.getItems().isEmpty() ? "redirect:/products" : "redirect:/items");
        });
    }

    @GetMapping
    public Mono<String> viewCart(Model model, @ModelAttribute("cart") Mono<CartDTO> cart) {
        return cart.flatMap(cartDTO -> {
                    model.addAttribute("cart", cartDTO);
                    return Flux.fromIterable(cartDTO.getItems().entrySet())
                            .flatMap(entry -> {
                                Long productId = entry.getKey();
                                Long quantity = entry.getValue().getQuantity();
                                return productService.getProduct(productId)
                                        .map(productDTO -> productDTO.getPrice().multiply(BigDecimal.valueOf(quantity)));
                            })
                            .reduce(BigDecimal.ZERO, BigDecimal::add)
                            .doOnNext(totalPrice -> model.addAttribute("totalPrice", totalPrice));
                }).doOnNext(cartDTO -> log.info("Displaying cart: {}", cartDTO))
                .thenReturn("cart");
    }

    @PutMapping("/{id}/product/{quantity}")
    @ResponseBody
    public Mono<ResponseEntity<Long>> modifyQuantity(@ModelAttribute("cart") Mono<CartDTO> cartMono,
                                                     @PathVariable("id") Long id,
                                                     @PathVariable("quantity") int quantity) {
        return cartMono.flatMap(cart -> {
            if (!cart.getItems().containsKey(id)) {
                log.warn("Attempted to modify quantity for non-existing product {}", id);
                return Mono.just(ResponseEntity.badRequest().build());
            }

            ItemDTO item = cart.getItems().get(id);
            long newQuantity = item.getQuantity() + quantity;

            if (newQuantity < 1) {
                cart.getItems().remove(id);
                return Mono.just(ResponseEntity.ok(0L));
            }

            item.setQuantity(newQuantity);
            return Mono.just(ResponseEntity.ok(newQuantity));
        });
    }
}
