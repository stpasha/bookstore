package com.bookstory.store.web;

import com.bookstory.store.service.OrderService;
import com.bookstory.store.service.ProductService;
import com.bookstory.store.web.dto.CartDTO;
import com.bookstory.store.web.dto.ItemDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@Controller
@RequestMapping("/items")
@Slf4j
@SessionAttributes("cart")
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
                                @ModelAttribute("cart") CartDTO cart) {
        log.info("Adding product {} to cart with quantity {}", id, quantity);

        cart.getItems().compute(id, (productId, existingItem) -> {
            if (existingItem == null) {
                // Если товара нет в корзине, добавляем новый
                return ItemDTO.builder()
                        .product(productService.getProduct(id).get())
                        .quantity(quantity)
                        .build();
            } else {
                // Если товар уже есть, увеличиваем его количество на переданное значение
                existingItem.setQuantity(existingItem.getQuantity() + quantity);
                return existingItem;
            }
        });

        return "redirect:/items"; // Перенаправляем на страницу с товарами
    }

    @PostMapping("/remove")
    public String removeItemFromCart(@RequestParam Long productId, @ModelAttribute("cart") CartDTO cart) {
        if (cart.getItems().containsKey(productId)) {
            ItemDTO item = cart.getItems().remove(productId);
            log.info("Removed product {} from cart", productId);
        } else {
            log.warn("Attempted to remove non-existing product {}", productId);
        }

        return "redirect:/items"; // Перенаправляем на страницу с товарами
    }

    @GetMapping
    public String viewCart(Model model, @ModelAttribute("cart") CartDTO cart) {
        model.addAttribute("cart", cart); // Передаем cart, а не cart.getItems().values()
        double totalPrice = cart.getItems().values().stream()
                .mapToDouble(i -> i.getProduct().getPrice().doubleValue() * i.getQuantity())
                .sum();
        model.addAttribute("totalPrice", totalPrice);
        log.info("Displaying cart: {}", cart);
        return "cart"; // Представление корзины
    }
}
