package com.bookstory.store.web;

import com.bookstory.store.service.OrderService;
import com.bookstory.store.service.ProductService;
import com.bookstory.store.web.dto.CartDTO;
import com.bookstory.store.web.dto.ItemDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.util.HashMap;

@Controller
@RequestMapping("/items")
@Slf4j
@SessionAttributes("cart")
@Validated
public class ItemController {

    private final OrderService orderService;
    private final ProductService productService;

    public ItemController(OrderService orderService, ProductService productService) {
        this.orderService = orderService;
        this.productService = productService;
    }

    @ModelAttribute("cart")
    public CartDTO initializeCart() {
        CartDTO cartDTO = new CartDTO();
        cartDTO.setItems(new HashMap<>());
        return cartDTO;
    }

    @PostMapping("/{id}/add")
    public String addItemToCart(@PathVariable("id") Long id,
                                @RequestParam long quantity,
                                @Valid @ModelAttribute("cart") CartDTO cart) {
        log.info("Adding product {} to cart with quantity {}", id, quantity);
        if (quantity < 1) {
            return "redirect:/products";
        }

        cart.getItems().compute(id, (productId, existingItem) -> {
            if (existingItem == null) {
                return ItemDTO.builder()
                        .product(productService.getProduct(id).get())
                        .quantity(quantity)
                        .build();
            } else {
                existingItem.setQuantity(existingItem.getQuantity() + quantity);
                return existingItem;
            }
        });

        return "redirect:/products";
    }

    @PostMapping("/remove")
    public String removeItemFromCart(@RequestParam Long productId, @Valid @ModelAttribute("cart") CartDTO cart) {
        if (cart.getItems().containsKey(productId)) {
            cart.getItems().remove(productId);
            log.info("Removed product {} from cart", productId);
        } else {
            log.warn("Attempted to remove non-existing product {}", productId);
        }
        if (cart.getItems().isEmpty()) {
            return "redirect:/products";
        }

        return "redirect:/items";
    }

    @GetMapping
    public String viewCart(Model model, @Valid @ModelAttribute("cart") CartDTO cart) {
        model.addAttribute("cart", cart); // Передаем cart, а не cart.getItems().values()
        double totalPrice = cart.getItems().values().stream()
                .mapToDouble(i -> i.getProduct().getPrice().doubleValue() * i.getQuantity())
                .sum();
        model.addAttribute("totalPrice", totalPrice);
        log.info("Displaying cart: {}", cart);
        return "cart";
    }

    @PutMapping("/{id}/product/{quantity}")
    @ResponseBody
    public ResponseEntity<Long> modifyQuantity(@Valid @ModelAttribute("cart") CartDTO cart,
                                               @PathVariable("id") Long id,
                                               @PathVariable("quantity") int quantity) {
        if (!cart.getItems().containsKey(id)) {
            log.warn("Attempted to modify quantity for non-existing product {}", id);
            return ResponseEntity.badRequest().build();
        }

        ItemDTO item = cart.getItems().get(id);
        long newQuantity = item.getQuantity() + quantity;

        if (newQuantity < 1) {
            log.warn("Attempted to set quantity below 1 for product {}", id);
            return ResponseEntity.badRequest().build();
        }

        item.setQuantity(newQuantity);
        return ResponseEntity.ok(newQuantity);
    }
}
